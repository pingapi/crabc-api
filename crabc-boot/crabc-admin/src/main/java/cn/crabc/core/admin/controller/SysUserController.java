package cn.crabc.core.admin.controller;

import cn.crabc.core.admin.entity.BaseUser;
import cn.crabc.core.admin.service.system.IBaseUserService;
import cn.crabc.core.admin.util.*;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/login")
    public Result login(String username, String password) throws Exception {
        BaseUser userInfo = iBaseUserService.getUserByName(username);
        if (userInfo == null) {
            return Result.error("账号或密码错误!");
        }
        String pwd = new String(Base64Utils.decodeFromString(password), StandardCharsets.UTF_8);
        String md5 = Md5Utils.hash(pwd).toUpperCase();
        if (!userInfo.getPassword().equals(md5)) {
            return Result.error("账号或密码错误!");
        }
        Map<String, Object> user = new HashMap<>();
        String token = JwtUtil.createToken(userInfo.getUserId(), userInfo.getUsername());
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
