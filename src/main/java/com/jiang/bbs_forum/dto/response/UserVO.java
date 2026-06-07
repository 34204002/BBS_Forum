package com.jiang.bbs_forum.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/** 用户信息响应 */
public class UserVO {
    /**
     * 用户ID
     */
    private Integer id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码（BCrypt加密）
     */
    private String password;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 头像URL
     */
    private String avatar;
    /**
     * 角色（user/admin）
     */
    private String role;
    /**
     * 状态（0-禁用, 1-正常）
     */
    private Integer status;
    /**
     * 积分
     */
    private Integer points;
    /**
     * 最后登录时间
     */
    private String lastLoginTime;
    /**
     * 登录次数
     */
    private Integer loginCount;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 个人资料
     */
    private ProfileVO profile;
}
