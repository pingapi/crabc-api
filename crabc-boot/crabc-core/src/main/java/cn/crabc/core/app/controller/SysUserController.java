package cn.crabc.core.app.controller;

import cn.crabc.core.app.entity.BaseUser;
import cn.crabc.core.app.entity.param.UserParam;
import cn.crabc.core.app.service.system.IBaseUserService;
import cn.crabc.core.app.util.*;
import com.github.benmanes.caffeine.cache.Cache;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/box/sys/user")
public class SysUserController {


    @Autowired
    private IBaseUserService iBaseUserService;
    @Autowired
    @Qualifier("dataCache")
    Cache<String, Object> caffeineCache;

    /**
     * 登录
     *
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody UserParam UserParam) {
        BaseUser userInfo = iBaseUserService.getUserByName(UserParam.getUsername());
        if (userInfo == null) {
            return Result.error("账号或密码错误!");
        }
        String pwd = new String(Base64.decodeBase64(UserParam.getPassword()), StandardCharsets.UTF_8);
        String md5 = Md5Utils.hash(pwd).toUpperCase();
        if (!userInfo.getPassword().equals(md5)) {
            return Result.error("账号或密码错误!");
        }
        Map<String, Object> user = new HashMap<>();
        String token = JwtUtil.createToken(userInfo.getUserId(), userInfo.getUsername());
        if (token == null) {
            return Result.error("登录失败");
        }
        user.put("expires", 3600);
        user.put("access_token", token);
        user.put("refresh_token", UUID.randomUUID().toString().replace("-", ""));
        return Result.success(user);
    }

    /**
     * 注销
     *
     * @return
     */
    @PostMapping("/loginout")
    public Result loginout() {
        UserThreadLocal.remove();
        return Result.success();
    }

    /**
     * 当前用户的基本信息
     *
     * @return
     */
    @GetMapping("/info")
    public Result userInfo() {
        BaseUser userInfo = iBaseUserService.getUserById(Long.parseLong(UserThreadLocal.getUserId()));
        return Result.success(userInfo);
    }

    /**
     * 修改密码
     * @param user
     * @return
     */
    @PostMapping("/reset/pwd")
    public Result resetPassword(@RequestBody UserParam user){
        BaseUser userInfo = iBaseUserService.getUserById(Long.valueOf(UserThreadLocal.getUserId()));
        if (userInfo == null) {
            return Result.error("账号不存在!");
        }
        String pwd = new String(Base64.decodeBase64(user.getPassword()), StandardCharsets.UTF_8);
        String md5Pwd = Md5Utils.hash(pwd).toUpperCase();
        if (!userInfo.getPassword().equals(md5Pwd)) {
            return Result.error("原密码错误!");
        }
        String newPwd = new String(Base64.decodeBase64(user.getNewPassword()), StandardCharsets.UTF_8);
        String newMd5Pwd = Md5Utils.hash(newPwd).toUpperCase();
        userInfo.setPassword(newMd5Pwd);
        iBaseUserService.updateUser(userInfo);
        return Result.success();
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @PostMapping("/register")
    public Result register(@RequestBody UserParam user){
        String pwd = new String(Base64.decodeBase64(user.getPassword()), StandardCharsets.UTF_8);
        String md5 = Md5Utils.hash(pwd).toUpperCase();
        user.setPassword(md5);
        BaseUser baseUser = new BaseUser();
        BeanUtils.copyProperties(user, baseUser);
        iBaseUserService.addUser(baseUser);
        return Result.success();
    }

    /**
     * 获取公钥
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/pubKey")
    public Result getPublicKey() throws Exception {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        RSAUtils.RSAKeyPair rsaKeyPair = RSAUtils.getKey();
        caffeineCache.put("priKey_" + UserThreadLocal.getUserId(), rsaKeyPair.getPrivateKey());
        Map<String, String> map = new HashMap(3);
        map.put("public", rsaKeyPair.getPublicKey());
        map.put("key", uuid);
        return Result.success(map);
    }
}
