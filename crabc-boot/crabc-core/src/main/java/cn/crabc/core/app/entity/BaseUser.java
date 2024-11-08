package cn.crabc.core.app.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户信息
 *
 * @author yuqf
 */
@Setter
@Getter
@JsonIgnoreProperties(value = {"password"})
public class BaseUser extends BaseEntity {

    /** 用户ID */
    private Long userId;

    /** 角色 */
    private String role;

    /** 用户账号 */
    private String username;

    /** 用户昵称 */
    private String nickname;

    /** 用户邮箱 */
    private String email;

    /** 手机号码 */
    private String phone;

    /** 用户性别 */
    private String sex;

    /** 用户头像 */
    private String picture;

    /** 密码 */
    private String password;

    /** 帐号状态（0正常 1停用） */
    private String status;

    private String remark;
}
