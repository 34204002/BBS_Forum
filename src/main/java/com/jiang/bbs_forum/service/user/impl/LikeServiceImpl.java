package com.jiang.bbs_forum.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jiang.bbs_forum.common.PageResponse;
import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.LikeBatchRequest;
import com.jiang.bbs_forum.dto.response.LikeBatchVO;
import com.jiang.bbs_forum.dto.response.PostVO;
import com.jiang.bbs_forum.entity.*;
import com.jiang.bbs_forum.mapper.*;
import com.jiang.bbs_forum.service.user.LikeService;
import com.jiang.bbs_forum.service.user.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private NotificationService notificationService;

    /**
     * 点赞（帖子 / 评论）- 加上事务确保多表操作要么同时成功，要么同时失败
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Void> like(int userId, int targetType, int targetId) {

        Integer ownerId = null;

        // ===== 1. 查目标所属用户 =====
        if (targetType == 1) {
            Post post = postMapper.selectById(targetId);
            if (post == null) {
                return Response.error(404, "帖子不存在");
            }
            ownerId = post.getUserId();
        } else if (targetType == 2) {
            Comment comment = commentMapper.selectById(targetId);
            if (comment == null) {
                return Response.error(404, "评论不存在");
            }
            ownerId = comment.getUserId();
        }

        // ===== 2. 判断是否已点赞 =====
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId)
                .eq(Like::getTargetType, targetType)
                .eq(Like::getTargetId, targetId);

        if (likeMapper.selectCount(wrapper) > 0) {
            return Response.success("已点赞", null);
        }

        // ===== 3. 插入点赞 =====
        Like like = new Like();
        like.setUserId(userId);
        like.setTargetType(targetType);
        like.setTargetId(targetId);
        likeMapper.insert(like);

        // ===== 4. 原子更新点赞数（抗并发） =====
        updateLikeCount(targetType, targetId, 1);

        // ===== 5. 触发通知 =====
        if (ownerId != null && !ownerId.equals(userId)) { // 用 .equals 更规范
            notificationService.notifyLike(userId, ownerId, targetType, targetId);
        }

        return Response.success("点赞成功", null);
    }

    /**
     * 取消点赞
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Void> unlike(int userId, int targetType, int targetId) {

        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId)
                .eq(Like::getTargetType, targetType)
                .eq(Like::getTargetId, targetId);

        int deleted = likeMapper.delete(wrapper);

        if (deleted > 0) {
            updateLikeCount(targetType, targetId, -1);
        }

        return Response.success("取消成功", null);
    }

    /**
     * 原子更新点赞数：直接利用 SQL 进行加减，防止并发时数据算错
     */
    private void updateLikeCount(int targetType, int targetId, int delta) {
        String operator = delta > 0 ? "+" : "-";

        if (targetType == 1) {
            UpdateWrapper<Post> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", targetId)
                    // 相当于 SQL: set like_count = max(0, like_count + 1) 防止减成负数
                    .setSql("like_count = greatest(0, coalesce(like_count, 0) " + operator + " 1)");
            postMapper.update(null, updateWrapper);
        }

        if (targetType == 2) {
            UpdateWrapper<Comment> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", targetId)
                    .setSql("like_count = greatest(0, coalesce(like_count, 0) " + operator + " 1)");
            commentMapper.update(null, updateWrapper);
        }
    }

    /**
     * 是否点赞
     */
    @Override
    public Response<Boolean> isLiked(int userId, int targetType, int targetId) {
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId)
                .eq(Like::getTargetType, targetType)
                .eq(Like::getTargetId, targetId);

        return Response.success(likeMapper.selectCount(wrapper) > 0);
    }

    /**
     * 批量获取状态
     */
    @Override
    public Response<LikeBatchVO> batchStatus(int userId, LikeBatchRequest request) {
        LikeBatchVO vo = new LikeBatchVO();
        Map<Integer, Boolean> map = new HashMap<>();

        // ===== 帖子点赞状态 =====
        if (request.getPostId() != null) {
            LambdaQueryWrapper<Like> postWrapper = new LambdaQueryWrapper<>();
            postWrapper.eq(Like::getUserId, userId)
                    .eq(Like::getTargetType, 1)
                    .eq(Like::getTargetId, request.getPostId());

            vo.setPostLiked(likeMapper.selectCount(postWrapper) > 0);
        }

        // ===== 评论点赞状态 =====
        if (request.getCommentIds() != null && !request.getCommentIds().isEmpty()) {
            LambdaQueryWrapper<Like> commentWrapper = new LambdaQueryWrapper<>();
            commentWrapper.eq(Like::getUserId, userId)
                    .eq(Like::getTargetType, 2)
                    .in(Like::getTargetId, request.getCommentIds());

            List<Like> likes = likeMapper.selectList(commentWrapper);
            Set<Integer> likedIds = likes.stream().map(Like::getTargetId).collect(Collectors.toSet());

            for (Integer id : request.getCommentIds()) {
                map.put(id, likedIds.contains(id));
            }
        }

        vo.setCommentLikeMap(map);
        return Response.success(vo);
    }

    @Override
    public Response<PageResponse<PostVO>> listLikedPosts(int userId, int page, int size) {
        int offset = (page - 1) * size;

        List<Integer> postIds = likeMapper.selectObjs(
                new LambdaQueryWrapper<Like>()
                        .select(Like::getTargetId)
                        .eq(Like::getUserId, userId)
                        .eq(Like::getTargetType, 1)
                        .orderByDesc(Like::getCreateTime)
                        .last("limit " + offset + "," + size)
        ).stream().map(o -> (Integer) o).toList();

        if (postIds.isEmpty()) {
            return Response.success(new PageResponse<>(0L, List.of(), page, size));
        }

        List<Post> posts = postMapper.selectBatchIds(postIds);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<PostVO> list = posts.stream().map(post -> {
            PostVO vo = new PostVO();
            vo.setId(post.getId());
            vo.setTitle(post.getTitle());
            vo.setContent(post.getContent());
            vo.setLikeCount(post.getLikeCount());
            vo.setCommentCount(post.getCommentCount());
            vo.setLiked(true);
            vo.setFavorited(favoriteMapper.selectCount(
                    new LambdaQueryWrapper<Favorite>()
                            .eq(Favorite::getUserId, userId)
                            .eq(Favorite::getPostId, post.getId())
            ) > 0);
            vo.setCreateTime(post.getCreateTime() != null ? post.getCreateTime().format(dtf) : null);
            return vo;
        }).toList();

        Long total = likeMapper.selectCount(
                new LambdaQueryWrapper<Like>()
                        .eq(Like::getUserId, userId)
                        .eq(Like::getTargetType, 1)
        );

        return Response.success(new PageResponse<>(total, list, page, size));
    }
}