package com.jiang.bbs_forum.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiang.bbs_forum.common.PageResponse;
import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.response.PostVO;
import com.jiang.bbs_forum.entity.Favorite;
import com.jiang.bbs_forum.entity.Post;
import com.jiang.bbs_forum.mapper.FavoriteMapper;
import com.jiang.bbs_forum.mapper.PostMapper;
import com.jiang.bbs_forum.service.user.FavoriteService;
import com.jiang.bbs_forum.service.user.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private NotificationService notificationService;

    /**
     * 收藏帖子 - 加上事务保证数据一致性
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Void> favorite(int userId, int postId) {

        // 1. 判断是否已收藏（幂等）
        Favorite exist = favoriteMapper.selectOne(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getPostId, postId)
        );

        if (exist != null) {
            return Response.success("已收藏", null);
        }

        // 2. 插入收藏记录
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setPostId(postId);
        favoriteMapper.insert(favorite);

        // 3. 发送收藏通知（统一入口）
        Post post = postMapper.selectById(postId);
        if (post != null && !Objects.equals(post.getUserId(), userId)) {
            notificationService.notifyFavorite(
                    userId,            // 操作人
                    post.getUserId(),  // 接收人
                    postId
            );
        }

        return Response.success("收藏成功", null);
    }

    /**
     * 取消收藏
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Void> unfavorite(int userId, int postId) {
        favoriteMapper.delete(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getPostId, postId)
        );
        return Response.success("取消收藏", null);
    }

    /**
     * 收藏列表 - 修复了 selectBatchIds 导致的乱序 Bug
     */
    @Override
    public Response<PageResponse<PostVO>> listFavorites(int userId, int page, int size) {

        int offset = (page - 1) * size;

        // 1. 查收藏 postId（此时是有序的，比如 [10, 5, 8]）
        List<Integer> postIds = favoriteMapper.selectObjs(
                        new LambdaQueryWrapper<Favorite>()
                                .select(Favorite::getPostId)
                                .eq(Favorite::getUserId, userId)
                                .orderByDesc(Favorite::getCreateTime)
                                .last("limit " + offset + "," + size)
                ).stream()
                .map(o -> (Integer) o)
                .toList();

        if (postIds.isEmpty()) {
            return Response.success(
                    new PageResponse<>(0L, List.of(), page, size, 0)
            );
        }

        // 2. 查帖子
        List<Post> posts = postMapper.selectBatchIds(postIds);

        //将帖子转为 Map，并按照原本有序的 postIds 重新排列 posts 的顺序
        Map<Integer, Post> postMap = posts.stream()
                .collect(Collectors.toMap(Post::getId, post -> post));

        List<Post> orderedPosts = postIds.stream()
                .map(postMap::get)
                .filter(Objects::nonNull) // 过滤掉可能已被管理员删除的帖子
                .toList();

        // 3. 映射为 VO
        List<PostVO> list = orderedPosts.stream().map(post -> {
            PostVO vo = new PostVO();
            vo.setId(post.getId());
            vo.setTitle(post.getTitle());
            vo.setContent(post.getContent());
            vo.setLikeCount(post.getLikeCount());
            vo.setCommentCount(post.getCommentCount());

            vo.setFavorited(true);
            vo.setLiked(false);

            return vo;
        }).toList();

        // 4. 总数
        Long total = favoriteMapper.selectCount(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
        );

        int pages = (int) Math.ceil(total * 1.0 / size);

        PageResponse<PostVO> result = new PageResponse<>(
                total,
                list,
                page,
                size,
                pages
        );

        return Response.success(result);
    }
}