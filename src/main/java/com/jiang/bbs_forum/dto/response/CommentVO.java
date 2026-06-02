package com.jiang.bbs_forum.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/** 回复响应（含children楼中楼） */
public class CommentVO {
    /**
     * 回复ID
     */
    private Integer id;
    /**
     * 帖子ID
     */
    private Integer postId;
    /**
     * 帖子标题
     */
    private String postTitle;
    /**
     * 父回复ID（null为一级回复）
     */
    private Integer parentId;
    /**
     * 内容
     */
    private String content;
    /**
     * 回复者ID
     */
    private Integer userId;
    /**
     * 回复者用户名
     */
    private String username;
    /**
     * 回复者昵称
     */
    private String nickname;
    /**
     * 回复者头像
     */
    private String avatar;
    /**
     * 点赞数
     */
    private Integer likeCount;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     *  无效@用户
     */
    private List<String> invalidAtUsers;
    /**
     *  @用户列表
     */
    private List<AtUserVO> atUsers;
    /**
     * 子回复列表
     */
    private List<CommentVO> children;
    private Boolean isLiked;
}
