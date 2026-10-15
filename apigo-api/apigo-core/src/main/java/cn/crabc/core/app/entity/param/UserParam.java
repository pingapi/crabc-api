package cn.crabc.core.app.entity.param;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户请求对象
 *
 * @author yuqf
 */
@Setter
@Getter
public class UserParam {

    private Long userId;

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

    private String newPassword;
}
