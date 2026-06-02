package com.jiang.bbs_forum.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/** 帖子信息响应 */
public class PostVO {
    /**
     * 帖子ID
     */
    private Integer id;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 所属板块ID
     */
    private Integer boardId;
    /**
     * 所属板块名称
     */
    private String boardName;
    /**
     * 作者ID
     */
    private Integer userId;
    /**
     * 作者用户名
     */
    private String username;
    /**
     * 作者昵称
     */
    private String nickname;
    /**
     * 作者头像
     */
    private String avatar;
    /**
     * 是否置顶（0-否, 1-是）
     */
    private Integer isTop;
    /**
     * 是否加精（0-否, 1-是）
     */
    private Integer isEssence;
    /**
     * 是否需求帖（0-否, 1-是）
     */
    private Integer isDemand;
    /**
     * 需求帖奖励积分
     */
    private Integer rewardPoints;
    /**
     * 浏览量
     */
    private Integer viewCount;
    /**
     * 点赞数
     */
    private Integer likeCount;
    /**
     * 回复数
     */
    private Integer commentCount;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 更新时间
     */
    private String updateTime;
    private Boolean liked = false;
    private Boolean favorited = false;
}
