package com.czf.blog.enums;

/**
 * @description: 角色类型枚举
 * @author czf
 * @date 2026-03-31
 */
public enum RoleTypeEnum {
    /**
     * 博主角色，拥有写权限
     */
    OWNER,
    /**
     * 游客角色，OAuth 登录用户，仅只读
     */
    VISITOR,
    /**
     * 匿名角色，未登录用户，仅只读
     */
    ANONYMOUS
}
