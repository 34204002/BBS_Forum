package com.jiang.bbs_forum.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
public class PostServiceImpl implements PostService {

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
    private CommentService commentService;

    @Override
    public Response<PageResponse<PostVO>> listPosts(Integer boardId, String keyword, int page, int size, String orderBy) {
        // TODO: 多条件分页查询帖子
        // TODO: 支持按板块、关键词筛选，支持排序（createTime/likeCount/commentCount）
        return null;
    }

    @Override
    public Response<List<PostVO>> getHotPosts(int size) {
        // TODO: 热门帖子算法：
        // TODO: 热度 = 浏览量*0.3 + 点赞数*0.3 + 评论数*0.4
        // TODO: 取近7天内的帖子，按热度降序排列，取前N条
        return null;
    }

    @Override
    public Response<PostVO> getPostById(int id) {
        // TODO: 1. 查询帖子详情（关联板块名、用户信息）
        // TODO: 2. 增加浏览量（view_count + 1）
        return null;
    }

    @Override
    public Response<PostDetailVO> getPostDetail(Integer userId, int postId, int page, int size) {
        Post post = postMapper.selectById(postId);
        if (post == null || post.getIsDeleted() == 1) {
            return Response.error(404, "帖子不存在");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        PostVO postVO = new PostVO();
        postVO.setId(post.getId());
        postVO.setTitle(post.getTitle());
        postVO.setContent(post.getContent());
        postVO.setLikeCount(post.getLikeCount());
        postVO.setCommentCount(post.getCommentCount());
        postVO.setCreateTime(post.getCreateTime() == null ? null : post.getCreateTime().format(formatter));
        postVO.setUpdateTime(post.getUpdateTime() == null ? null : post.getUpdateTime().format(formatter));
        postVO.setLiked(false);
        postVO.setFavorited(false);

        if (userId != null) {
            postVO.setLiked(likeMapper.selectCount(
                    new LambdaQueryWrapper<Like>()
                            .eq(Like::getUserId, userId)
                            .eq(Like::getTargetType, 1)
                            .eq(Like::getTargetId, postId)
            ) > 0);

            postVO.setFavorited(favoriteMapper.selectCount(
                    new LambdaQueryWrapper<Favorite>()
                            .eq(Favorite::getUserId, userId)
                            .eq(Favorite::getPostId, postId)
            ) > 0);
        }

        if (post.getUserId() != null) {
            User user = userMapper.selectById(post.getUserId());
            postVO.setUserId(post.getUserId());
            postVO.setUsername(user == null ? null : user.getUsername());
            postVO.setNickname(user == null ? null : user.getNickname());
            postVO.setAvatar(user == null ? null : user.getAvatar());
        }

        if (post.getBoardId() != null) {
            Board board = boardMapper.selectById(post.getBoardId());
            postVO.setBoardId(post.getBoardId());
            postVO.setBoardName(board == null ? null : board.getName());
        }

        Response<PageResponse<CommentVO>> commentResp =
                commentService.listComments(userId, postId, page, size);

        PostDetailVO detailVO = new PostDetailVO();
        detailVO.setPost(postVO);
        detailVO.setComments(commentResp.getData());

        return Response.success(detailVO);
    }

    @Override
    public Response<PostVO> createPost(int userId, CreatePostRequest request) {
        // TODO: 1. 如果是需求帖(isDemand=1)，检查积分是否足够
        // TODO: 2. 需求帖扣积分，记录消耗记录
        // TODO: 3. 插入帖子
        // TODO: 4. 更新板块post_count
        // TODO: 5. 普通帖发帖奖励积分(+10)
        return null;
    }

    @Override
    public Response<PostVO> updatePost(int userId, int postId, UpdatePostRequest request) {
        // TODO: 1. 校验帖子存在且未删除
        // TODO: 2. 校验是否为帖子作者（非作者返回403）
        // TODO: 3. 更新标题和内容
        return null;
    }

    @Override
    public Response<PageResponse<PostVO>> listPostsByUser(int userId, int page, int size) {
        Page<Post> p = new Page<>(page, size);

        IPage<Post> result = postMapper.selectPage(
                p,
                new LambdaQueryWrapper<Post>()
                        .eq(Post::getUserId, userId)
                        .orderByDesc(Post::getCreateTime)
        );

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

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<PostVO> list = posts.stream().map(post -> {
            PostVO vo = new PostVO();
            vo.setId(post.getId());
            vo.setTitle(post.getTitle());
            vo.setContent(post.getContent());
            vo.setLikeCount(post.getLikeCount());
            vo.setCommentCount(post.getCommentCount());
            vo.setLiked(likedSet.contains(post.getId()));
            vo.setFavorited(favSet.contains(post.getId()));
            vo.setCreateTime(post.getCreateTime() != null ? post.getCreateTime().format(dtf) : null);
            return vo;
        }).toList();

        return Response.success(new PageResponse<>(result.getTotal(), list, page, size));
    }

    @Override
    public Response<Void> deletePost(int userId, int postId) {
        // TODO: 1. 校验帖子存在
        // TODO: 2. 校验是否为帖子作者或管理员
        // TODO: 3. 逻辑删除（级联删除回复、点赞记录）
        // TODO: 4. 更新板块post_count
        return null;
    }

    @Override
    public Response<StatusVO> toggleTop(int postId, int isTop) {
        // TODO: 更新帖子is_top字段
        return null;
    }

    @Override
    public Response<StatusVO> toggleEssence(int postId, int isEssence) {
        // TODO: 更新帖子is_essence字段
        return null;
    }
}
