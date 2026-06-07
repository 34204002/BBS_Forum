package com.jiang.bbs_forum.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiang.bbs_forum.common.PageResponse;
import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.CreatePostRequest;
import com.jiang.bbs_forum.dto.request.UpdatePostRequest;
import com.jiang.bbs_forum.dto.response.*;
import com.jiang.bbs_forum.entity.*;
import com.jiang.bbs_forum.mapper.*;
import com.jiang.bbs_forum.service.user.CommentService;
import com.jiang.bbs_forum.service.user.PointService;
import com.jiang.bbs_forum.service.user.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class PostServiceImpl implements PostService {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private PostMapper postMapper;
    @Autowired
    private PointService pointService;
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BoardMapper boardMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private CommentService commentService;

    @Override
    public Response<PageResponse<PostVO>> listPosts(Integer boardId, String keyword, int page, int size, String orderBy) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getIsDeleted, 0);

        if (boardId != null) {
            wrapper.eq(Post::getBoardId, boardId);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Post::getTitle, keyword);
        }

        wrapper.orderByDesc(Post::getIsTop)
              .orderByDesc(Post::getIsEssence);

        switch (orderBy) {
            case "likeCount" -> wrapper.orderByDesc(Post::getLikeCount);
            case "commentCount" -> wrapper.orderByDesc(Post::getCommentCount);
            default -> wrapper.orderByDesc(Post::getCreateTime);
        }

        Page<Post> p = new Page<>(page, size);
        IPage<Post> result = postMapper.selectPage(p, wrapper);

        List<PostVO> list = buildPostVOList(result.getRecords(), null);
        return Response.success(new PageResponse<>(result.getTotal(), list, page, size));
    }

    @Override
    public Response<List<PostVO>> getHotPosts(int size) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        List<Post> posts = postMapper.selectList(
                new LambdaQueryWrapper<Post>()
                        .eq(Post::getIsDeleted, 0)
                        .ge(Post::getCreateTime, sevenDaysAgo)
        );

        List<Post> hotPosts = posts.stream()
                .sorted(Comparator.comparingDouble((Post p) ->
                        (p.getViewCount() == null ? 0 : p.getViewCount()) * 0.3
                                + (p.getLikeCount() == null ? 0 : p.getLikeCount()) * 0.3
                                + (p.getCommentCount() == null ? 0 : p.getCommentCount()) * 0.4
                ).reversed())
                .limit(size)
                .toList();

        List<PostVO> list = buildPostVOList(hotPosts, null);
        return Response.success(list);
    }

    @Override
    public Response<PostVO> getPostById(int id) {
        Post post = postMapper.selectById(id);
        if (post == null || post.getIsDeleted() == 1) {
            return Response.error(404, "帖子不存在");
        }

        Post update = new Post();
        update.setId(id);
        update.setViewCount((post.getViewCount() == null ? 0 : post.getViewCount()) + 1);
        postMapper.updateById(update);

        post.setViewCount(update.getViewCount());
        PostVO vo = buildPostVO(post, null);

        return Response.success(vo);
    }

    @Override
    public Response<PostDetailVO> getPostDetail(Integer userId, int postId, int page, int size) {
        Post post = postMapper.selectById(postId);
        if (post == null || post.getIsDeleted() == 1) {
            return Response.error(404, "帖子不存在");
        }

        PostVO postVO = buildPostVO(post, userId);

        Response<PageResponse<CommentVO>> commentResp =
                commentService.listComments(userId, postId, page, size);

        PostDetailVO detailVO = new PostDetailVO();
        detailVO.setPost(postVO);
        detailVO.setComments(commentResp.getData());

        return Response.success(detailVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<PostVO> createPost(int userId, CreatePostRequest request) {
        User user = userMapper.selectById(userId);

        int isDemand = request.getIsDemand() != null && request.getIsDemand() == 1 ? 1 : 0;
        int rewardPoints = request.getRewardPoints() != null ? request.getRewardPoints() : 0;

        if (isDemand == 1) {
            if (rewardPoints <= 0) {
                return Response.error(400, "需求帖必须设置悬赏积分");
            }
            if (user.getPoints() < rewardPoints) {
                return Response.error(400, "积分不足");
            }
            pointService.consumePoints(userId, rewardPoints, "发布需求帖悬赏");
        }

        Post post = new Post();
        post.setUserId(userId);
        post.setBoardId(request.getBoardId());
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setIsDemand(isDemand);
        post.setRewardPoints(rewardPoints);
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        postMapper.insert(post);

        Board board = boardMapper.selectById(request.getBoardId());
        if (board != null) {
            Board boardUpdate = new Board();
            boardUpdate.setId(board.getId());
            boardUpdate.setPostCount((board.getPostCount() == null ? 0 : board.getPostCount()) + 1);
            boardMapper.updateById(boardUpdate);
        }

        if (isDemand == 0) {
            pointService.addPoints(userId, 10, "发布普通帖奖励");
        }

        PostVO vo = buildPostVO(post, userId);
        return Response.success("发帖成功", vo);
    }

    @Override
    public Response<PostVO> updatePost(int userId, int postId, UpdatePostRequest request) {
        Post post = postMapper.selectById(postId);
        if (post == null || post.getIsDeleted() == 1) {
            return Response.error(404, "帖子不存在");
        }
        if (!post.getUserId().equals(userId)) {
            return Response.error(403, "无权限修改");
        }

        if (request.getTitle() != null) post.setTitle(request.getTitle());
        if (request.getContent() != null) post.setContent(request.getContent());
        postMapper.updateById(post);

        PostVO vo = buildPostVO(post, userId);
        return Response.success("修改成功", vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Void> deletePost(int userId, int postId) {
        Post post = postMapper.selectById(postId);
        if (post == null || post.getIsDeleted() == 1) {
            return Response.error(404, "帖子不存在");
        }

        User user = userMapper.selectById(userId);
        if (!post.getUserId().equals(userId) && !"admin".equals(user.getRole())) {
            return Response.error(403, "无权限删除");
        }

        commentMapper.update(null,
                new UpdateWrapper<Comment>()
                        .eq("post_id", postId)
                        .set("is_deleted", 1));

        likeMapper.delete(new LambdaQueryWrapper<Like>()
                .eq(Like::getTargetType, 1)
                .eq(Like::getTargetId, postId));

        favoriteMapper.delete(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getPostId, postId));

        postMapper.update(null,
                new UpdateWrapper<Post>()
                        .eq("id", postId)
                        .set("is_deleted", 1));

        Board board = boardMapper.selectById(post.getBoardId());
        if (board != null) {
            Board boardUpdate = new Board();
            boardUpdate.setId(board.getId());
            boardUpdate.setPostCount(Math.max(0,
                    (board.getPostCount() == null ? 0 : board.getPostCount()) - 1));
            boardMapper.updateById(boardUpdate);
        }

        return Response.success("删除成功", null);
    }

    @Override
    public Response<PageResponse<PostVO>> listPostsByUser(int userId, int page, int size) {
        Page<Post> p = new Page<>(page, size);

        IPage<Post> result = postMapper.selectPage(p,
                new LambdaQueryWrapper<Post>()
                        .eq(Post::getUserId, userId)
                        .orderByDesc(Post::getCreateTime));

        List<Post> posts = result.getRecords();
        if (posts.isEmpty()) {
            return Response.success(new PageResponse<>(0L, List.of(), page, size));
        }

        List<Integer> postIds = posts.stream().map(Post::getId).toList();

        Set<Integer> likedSet = likeMapper.selectObjs(
                new LambdaQueryWrapper<Like>()
                        .select(Like::getTargetId)
                        .eq(Like::getUserId, userId)
                        .eq(Like::getTargetType, 1)
                        .in(Like::getTargetId, postIds)
        ).stream().map(o -> (Integer) o).collect(java.util.stream.Collectors.toSet());

        Set<Integer> favSet = favoriteMapper.selectObjs(
                new LambdaQueryWrapper<Favorite>()
                        .select(Favorite::getPostId)
                        .eq(Favorite::getUserId, userId)
                        .in(Favorite::getPostId, postIds)
        ).stream().map(o -> (Integer) o).collect(java.util.stream.Collectors.toSet());

        List<PostVO> list = posts.stream().map(post -> {
            PostVO vo = new PostVO();
            vo.setId(post.getId());
            vo.setTitle(post.getTitle());
            vo.setContent(post.getContent());
            vo.setLikeCount(post.getLikeCount());
            vo.setCommentCount(post.getCommentCount());
            vo.setLiked(likedSet.contains(post.getId()));
            vo.setFavorited(favSet.contains(post.getId()));
            vo.setCreateTime(post.getCreateTime() != null ? post.getCreateTime().format(DTF) : null);
            return vo;
        }).toList();

        return Response.success(new PageResponse<>(result.getTotal(), list, page, size));
    }

    @Override
    public Response<StatusVO> toggleTop(int postId, int isTop) {
        Post post = postMapper.selectById(postId);
        if (post == null || post.getIsDeleted() == 1) {
            return Response.error(404, "帖子不存在");
        }

        Post update = new Post();
        update.setId(postId);
        update.setIsTop(isTop);
        postMapper.updateById(update);

        StatusVO vo = StatusVO.builder()
                .id(postId)
                .isTop(isTop)
                .updateTime(LocalDateTime.now().format(DTF))
                .build();
        return Response.success(vo);
    }

    @Override
    public Response<StatusVO> toggleEssence(int postId, int isEssence) {
        Post post = postMapper.selectById(postId);
        if (post == null || post.getIsDeleted() == 1) {
            return Response.error(404, "帖子不存在");
        }

        Post update = new Post();
        update.setId(postId);
        update.setIsEssence(isEssence);
        postMapper.updateById(update);

        StatusVO vo = StatusVO.builder()
                .id(postId)
                .isEssence(isEssence)
                .updateTime(LocalDateTime.now().format(DTF))
                .build();
        return Response.success(vo);
    }

    private PostVO buildPostVO(Post post, Integer userId) {
        PostVO vo = new PostVO();
        vo.setId(post.getId());
        vo.setTitle(post.getTitle());
        vo.setContent(post.getContent());
        vo.setViewCount(post.getViewCount());
        vo.setLikeCount(post.getLikeCount());
        vo.setCommentCount(post.getCommentCount());
        vo.setIsTop(post.getIsTop());
        vo.setIsEssence(post.getIsEssence());
        vo.setIsDemand(post.getIsDemand());
        vo.setRewardPoints(post.getRewardPoints());
        vo.setCreateTime(post.getCreateTime() != null ? post.getCreateTime().format(DTF) : null);
        vo.setUpdateTime(post.getUpdateTime() != null ? post.getUpdateTime().format(DTF) : null);
        vo.setLiked(false);
        vo.setFavorited(false);

        if (userId != null) {
            vo.setLiked(likeMapper.selectCount(
                    new LambdaQueryWrapper<Like>()
                            .eq(Like::getUserId, userId)
                            .eq(Like::getTargetType, 1)
                            .eq(Like::getTargetId, post.getId())
            ) > 0);
            vo.setFavorited(favoriteMapper.selectCount(
                    new LambdaQueryWrapper<Favorite>()
                            .eq(Favorite::getUserId, userId)
                            .eq(Favorite::getPostId, post.getId())
            ) > 0);
        }

        if (post.getUserId() != null) {
            User u = userMapper.selectById(post.getUserId());
            vo.setUserId(post.getUserId());
            vo.setUsername(u != null ? u.getUsername() : null);
            vo.setNickname(u != null ? u.getNickname() : null);
            vo.setAvatar(u != null ? u.getAvatar() : null);
        }

        if (post.getBoardId() != null) {
            Board b = boardMapper.selectById(post.getBoardId());
            vo.setBoardId(post.getBoardId());
            vo.setBoardName(b != null ? b.getName() : null);
        }

        return vo;
    }

    private List<PostVO> buildPostVOList(List<Post> posts, Integer userId) {
        return posts.stream().map(p -> buildPostVO(p, userId)).toList();
    }
}
