package com.jiang.bbs_forum.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiang.bbs_forum.common.PageResponse;
import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.CreateCommentRequest;
import com.jiang.bbs_forum.dto.request.UpdateCommentRequest;
import com.jiang.bbs_forum.dto.response.AtUserVO;
import com.jiang.bbs_forum.dto.response.CommentVO;
import com.jiang.bbs_forum.entity.Comment;
import com.jiang.bbs_forum.entity.Post;
import com.jiang.bbs_forum.entity.User;
import com.jiang.bbs_forum.mapper.CommentMapper;
import com.jiang.bbs_forum.mapper.LikeMapper;
import com.jiang.bbs_forum.mapper.PostMapper;
import com.jiang.bbs_forum.mapper.UserMapper;
import com.jiang.bbs_forum.service.user.CommentService;
import com.jiang.bbs_forum.service.user.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LikeMapper likeMapper;
    /**
     * 查询评论列表（树结构）
     */
    @Override
    public Response<PageResponse<CommentVO>> listComments(Integer userId, int postId, int page, int size) {

        List<Comment> comments = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getPostId, postId)
                        .eq(Comment::getIsDeleted, 0)
                        .orderByDesc(Comment::getCreateTime)
        );

        if (comments.isEmpty()) {
            PageResponse<CommentVO> empty = new PageResponse<>();
            empty.setList(new ArrayList<>());
            empty.setTotal(0L);
            empty.setPage(page);
            empty.setSize(size);
            return Response.success(empty);
        }

        List<Integer> userIds = comments.stream()
                .map(Comment::getUserId)
                .distinct()
                .collect(Collectors.toList());

        Map<Integer, User> userMap = userMapper.selectBatchIds(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        List<Integer> commentIds = comments.stream()
                .map(Comment::getId)
                .collect(Collectors.toList());

        Map<Integer, Boolean> likeMap = new HashMap<>();

        if (!commentIds.isEmpty()) {

            LambdaQueryWrapper<com.jiang.bbs_forum.entity.Like> wrapper =
                    new LambdaQueryWrapper<>();

            wrapper.eq(com.jiang.bbs_forum.entity.Like::getUserId, userId)
                    .eq(com.jiang.bbs_forum.entity.Like::getTargetType, 2)
                    .in(com.jiang.bbs_forum.entity.Like::getTargetId, commentIds);

            List<com.jiang.bbs_forum.entity.Like> likes = likeMapper.selectList(wrapper);

            Set<Integer> likedSet = likes.stream()
                    .map(com.jiang.bbs_forum.entity.Like::getTargetId)
                    .collect(Collectors.toSet());

            for (Integer id : commentIds) {
                likeMap.put(id, likedSet.contains(id));
            }
        }

        Map<Integer, CommentVO> voMap = new HashMap<>();
        List<CommentVO> allVO = new ArrayList<>();

        for (Comment c : comments) {

            User user = userMap.get(c.getUserId());

            CommentVO vo = new CommentVO();
            vo.setId(c.getId());
            vo.setPostId(c.getPostId());
            vo.setParentId(c.getParentId());
            vo.setContent(c.getContent());
            vo.setUserId(c.getUserId());

            vo.setUsername(user == null ? null : user.getUsername());
            vo.setNickname(user == null ? null : user.getNickname());
            vo.setAvatar(user == null ? "default.png" : user.getAvatar());

            vo.setLikeCount(c.getLikeCount());
            vo.setIsLiked(likeMap.getOrDefault(c.getId(), false));

            vo.setCreateTime(
                    c.getCreateTime() == null ? null :
                            c.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );

            vo.setChildren(new ArrayList<>());

            voMap.put(vo.getId(), vo);
            allVO.add(vo);
        }

        List<CommentVO> roots = new ArrayList<>();

        for (CommentVO vo : allVO) {
            if (vo.getParentId() == null) {
                roots.add(vo);
            } else {
                CommentVO parent = voMap.get(vo.getParentId());
                if (parent != null) {
                    parent.getChildren().add(vo);
                }
            }
        }

        int fromIndex = Math.min((page - 1) * size, roots.size());
        int toIndex = Math.min(fromIndex + size, roots.size());

        List<CommentVO> pageList = roots.subList(fromIndex, toIndex);

        PageResponse<CommentVO> result = new PageResponse<>();
        result.setList(pageList);
        result.setTotal((long) roots.size());
        result.setPage(page);
        result.setSize(size);

        return Response.success(result);
    }

    /**
     * 创建评论（核心：已接入通知系统）
     */
    @Override
    public Response<CommentVO> createComment(int userId, CreateCommentRequest request) {

        Post post = postMapper.selectById(request.getPostId());
        if (post == null) {
            return Response.error(404, "帖子不存在");
        }

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setPostId(request.getPostId());
        comment.setParentId(request.getParentId());
        comment.setContent(request.getContent());
        comment.setLikeCount(0);

        List<String> atUsernames = extractAtUsernames(request.getContent());

        List<User> atUsers = new ArrayList<>();
        List<String> invalidUsers = new ArrayList<>();

        if (!atUsernames.isEmpty()) {

            atUsers = userMapper.selectList(
                    new LambdaQueryWrapper<User>()
                            .in(User::getUsername, atUsernames)
            );

            Set<String> existNames = atUsers.stream()
                    .map(User::getUsername)
                    .collect(Collectors.toSet());

            invalidUsers = atUsernames.stream()
                    .filter(name -> !existNames.contains(name))
                    .collect(Collectors.toList());

            comment.setAtUsernames(String.join(",", existNames));
        }

        commentMapper.insert(comment);

        /**
         * 评论帖子通知（统一入口）
         */
        if (request.getParentId() != null) {
            // 【分支A】如果是回复别人的评论（楼中楼）
            Comment parentComment = commentMapper.selectById(request.getParentId());

            // 只通知被回复的层主，不轰炸楼主
            if (parentComment != null && !parentComment.getUserId().equals(userId)) {
                notificationService.notifyComment(
                        userId,
                        parentComment.getUserId(),
                        2,                          // targetType = 2 (评论)
                        parentComment.getId(),
                        request.getContent()        // 传入真实的回复内容！
                );
            }
        } else {
            // 【分支B】如果是直接评论帖子（根评论）
            // 只有当不是自己评论自己时，才通知楼主
            if (!post.getUserId().equals(userId)) {
                notificationService.notifyComment(
                        userId,
                        post.getUserId(),
                        1,                          // targetType = 1 (帖子)
                        post.getId(),
                        request.getContent()        // 传入真实的评论内容！
                );
            }
        }

        /**
         * ✔ @用户通知
         */
        for (User atUser : atUsers) {

            if (!atUser.getId().equals(userId)) {

                notificationService.notifyAt(
                        userId,
                        atUser.getId(),
                        2,
                        comment.getId()
                );
            }
        }

        // 更新帖子评论数
        Post update = new Post();
        update.setId(post.getId());
        update.setCommentCount(
                post.getCommentCount() == null ? 1 : post.getCommentCount() + 1
        );
        postMapper.updateById(update);

        User user = userMapper.selectById(userId);

        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setPostId(comment.getPostId());
        vo.setParentId(comment.getParentId());
        vo.setContent(comment.getContent());
        vo.setUserId(comment.getUserId());

        vo.setUsername(user == null ? null : user.getUsername());
        vo.setNickname(user == null ? null : user.getNickname());
        vo.setAvatar(user == null ? "default.png" : user.getAvatar());

        vo.setLikeCount(0);

        vo.setCreateTime(
                comment.getCreateTime() == null ? null :
                        comment.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );

        vo.setChildren(new ArrayList<>());

        List<AtUserVO> atUserVOList = atUsers.stream().map(u -> {
            AtUserVO item = new AtUserVO();
            item.setUserId(u.getId());
            item.setUsername(u.getUsername());
            return item;
        }).collect(Collectors.toList());

        vo.setAtUsers(atUserVOList);
        vo.setInvalidAtUsers(invalidUsers);

        return Response.success("评论成功", vo);
    }

    @Override
    public Response<CommentVO> updateComment(int userId, int commentId, UpdateCommentRequest request) {

        Comment comment = commentMapper.selectById(commentId);
        if (comment == null || comment.getIsDeleted() == 1) {
            return Response.error(404, "评论不存在");
        }

        if (!comment.getUserId().equals(userId)) {
            return Response.error(403, "无权限修改");
        }

        comment.setContent(request.getContent());
        commentMapper.updateById(comment);

        User user = userMapper.selectById(userId);

        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setPostId(comment.getPostId());
        vo.setParentId(comment.getParentId());
        vo.setContent(comment.getContent());
        vo.setUserId(comment.getUserId());

        vo.setUsername(user == null ? null : user.getUsername());
        vo.setNickname(user == null ? null : user.getNickname());
        vo.setAvatar(user == null ? "default.png" : user.getAvatar());

        vo.setLikeCount(comment.getLikeCount());

        vo.setCreateTime(
                comment.getCreateTime() == null ? null :
                        comment.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );

        vo.setChildren(new ArrayList<>());

        return Response.success("修改成功", vo);
    }

    @Override
    public Response<Void> deleteComment(int userId, int commentId) {

        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            return Response.error(404, "评论不存在");
        }

        if (!comment.getUserId().equals(userId)) {
            return Response.error(403, "无权限删除");
        }

        deleteCommentRecursively(commentId);

        commentMapper.update(null,
                new UpdateWrapper<Comment>()
                        .eq("id", commentId)
                        .set("is_deleted", 1));

        Post post = postMapper.selectById(comment.getPostId());
        if (post != null) {
            Post update = new Post();
            update.setId(post.getId());
            update.setCommentCount(
                    Math.max(0,
                            (post.getCommentCount() == null ? 0 : post.getCommentCount()) - 1
                    )
            );
            postMapper.updateById(update);
        }

        return Response.success("删除成功", null);
    }

    private void deleteCommentRecursively(Integer commentId) {

        List<Comment> children = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getParentId, commentId)
        );

        for (Comment child : children) {
            deleteCommentRecursively(child.getId());
            commentMapper.update(null,
                    new UpdateWrapper<Comment>()
                            .eq("id", child.getId())
                            .set("is_deleted", 1));
        }
    }

    private List<String> extractAtUsernames(String content) {

        List<String> result = new ArrayList<>();

        if (content == null || content.trim().isEmpty()) {
            return result;
        }

        Pattern pattern = Pattern.compile("@([a-zA-Z0-9_\\u4e00-\\u9fa5]+)");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            result.add(matcher.group(1));
        }

        return result.stream().distinct().toList();
    }

    @Override
    public Response<PageResponse<CommentVO>> listCommentsByUser(int userId, int page, int size) {
        Page<Comment> p = new Page<>(page, size);

        IPage<Comment> result = commentMapper.selectPage(
                p,
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getUserId, userId)
                        .orderByDesc(Comment::getCreateTime)
        );

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<CommentVO> list = result.getRecords().stream().map(c -> {
            CommentVO vo = new CommentVO();
            vo.setId(c.getId());
            vo.setPostId(c.getPostId());
            vo.setContent(c.getContent());
            vo.setCreateTime(c.getCreateTime() != null ? c.getCreateTime().format(dtf) : null);
            return vo;
        }).toList();

        return Response.success(new PageResponse<>(result.getTotal(), list, page, size));
    }
}