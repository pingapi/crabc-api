package cn.crabc.core.system.controller;

import cn.crabc.core.system.entity.BaseUser;
import cn.crabc.core.system.service.system.IBaseUserService;
import cn.crabc.core.system.util.*;
import com.alibaba.fastjson2.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
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
        String pwd = new String(Base64Utils.decodeFromString(password), "UTF-8");
        String md5 = Md5Utils.hash(pwd).toUpperCase();
        if (!userInfo.getPassword().equals(md5)) {
            return Result.error("账号或密码错误!");
        }
        Map<String, Object> user = new HashMap<>();
        String token = JwtUtil.createToken(userInfo.getUserId(), userInfo.getUsername());
        user.put("expires", 3600);
        user.put("access_token", token);
        user.put("refresh_token", UUID.randomUUID().toString());
        return Result.success(user);
    }

    /**
     * 注销
     *
     * @return
     */
    @PostMapping("/loginout")
    public Result loginout() {
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
     * 菜单
     *
     * @return
     */
    @GetMapping("/menus")
    public Result getMenus() {
        String json = "[{\"id\":1001,\"parentId\":11,\"name\":\"首页\",\"css\":\"layui-icon-senior\",\"url\":\"/home\",\"path\":null,\"sort\":1,\"isMenu\":1,\"hidden\":false,\"permissionId\":null,\"code\":\"Home\",\"subMenus\":null},{\"id\":1004,\"parentId\":1001,\"name\":\"接口开发\",\"css\":\"layui-icon-senior\",\"url\":\"/data-service\",\"path\":null,\"sort\":3,\"isMenu\":1,\"hidden\":false,\"permissionId\":null,\"code\":\"DataService\",\"subMenus\":null},{\"id\":2060,\"parentId\":1001,\"name\":\"个人中心\",\"css\":\"layui-icon-username\",\"url\":\"/account\",\"path\":\"\",\"sort\":6,\"isMenu\":1,\"hidden\":false,\"permissionId\":null,\"code\":\"Account\",\"subMenus\":[{\"id\":2065,\"parentId\":2060,\"name\":\"数据库\",\"css\":\"\",\"url\":\"/account/datasource\",\"path\":\"\",\"sort\":2,\"isMenu\":1,\"hidden\":true,\"permissionId\":null,\"code\":\"DataSource\",\"subMenus\":null},{\"id\":2066,\"parentId\":2060,\"name\":\"密码修改\",\"css\":\"\",\"url\":\"/account/edit-pwd\",\"path\":\"\",\"sort\":3,\"isMenu\":1,\"hidden\":true,\"permissionId\":null,\"code\":\"ChangePassword\",\"subMenus\":null},{\"id\":2067,\"parentId\":2060,\"name\":\"我的应用\",\"css\":\"\",\"url\":\"/account/application\",\"path\":\"\",\"sort\":4,\"isMenu\":1,\"hidden\":true,\"permissionId\":null,\"code\":\"Application\",\"subMenus\":null},{\"id\":2068,\"parentId\":2060,\"name\":\"我的API\",\"css\":\"\",\"url\":\"/account/my-api\",\"path\":\"\",\"sort\":5,\"isMenu\":1,\"hidden\":true,\"permissionId\":null,\"code\":\"MyApi\",\"subMenus\":null}]}]";
        List<Map> poetList = JSON.parseArray(json, Map.class);//转换
        return Result.success(poetList);
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
