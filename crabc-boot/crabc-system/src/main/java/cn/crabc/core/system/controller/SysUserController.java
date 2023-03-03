package cn.crabc.core.system.controller;

import cn.crabc.core.system.entity.BaseUser;
import cn.crabc.core.system.service.system.IBaseUserService;
import cn.crabc.core.system.util.JwtUtil;
import cn.crabc.core.system.util.Result;
import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/box/sys/user")
public class SysUserController {

    @Autowired
    private IBaseUserService iBaseUserService;

    @GetMapping("/params")
    public Result params(){
        return Result.success();
    }

    /**
     * 登录
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
        if (!userInfo.getPassword().equals(password)){
            return Result.error("账号或密码错误!");
        }
        Map<String, Object> user = new HashMap<>();
        String token = JwtUtil.createToken(userInfo.getUserId(), userInfo.getUsername());
        user.put("expires_in", 3600);
        user.put("access_token", token);
        user.put("refresh_token",UUID.randomUUID().toString());
        return Result.success(user);
    }

    /**
     * 注销
     * @return
     */
    @PostMapping("/loginout")
    public Result loginout() {
        return Result.success();
    }

    /**
     * 当前用户的基本信息
     * @return
     */
    @GetMapping("/info")
    public Result userInfo(){
        Map<String,Object> userInfo = new HashMap<>();
        Map<String,Object> users = new HashMap<>();
        users.put("id","1");
        users.put("username","admin");
        users.put("nickname","管理员");
        users.put("email","admin@example.com");
        users.put("depts",new ArrayList<>());
        users.put("sysRoles", new ArrayList<>());
        users.put("topOrgId",1);
        List<String> permissions = new ArrayList<>();
        permissions.add("ALL:/api/**");
        users.put("permissions",permissions);
        userInfo.put("user",users);
        List<Map<String,Object>> permissionMap = new ArrayList<>();
        userInfo.put("permissions",permissionMap);
//        BaseUser userInfo =  iBaseUserService.getUserById(Long.parseLong(UserThreadLocal.getUserId()));
        return Result.success(userInfo);
    }

    /**
     * 菜单
     * @return
     */
    @GetMapping("/menus")
    public Result getMenus(){
        String json ="[{\"id\":1004,\"parentId\":1001,\"name\":\"服务开发\",\"css\":\"layui-icon-senior\",\"url\":\"/data-service\",\"path\":null,\"sort\":3,\"createTime\":\"2022-07-15 09:33:24\",\"updateTime\":\"2022-08-12 10:09:28\",\"isMenu\":1,\"hidden\":false,\"permissionId\":null,\"code\":\"DataService\",\"description\":null,\"subMenus\":[{\"id\":2104,\"parentId\":1004,\"name\":\"服务开发权限\",\"css\":\"\",\"url\":\"/\",\"path\":\"\",\"sort\":1,\"createTime\":\"2022-10-20 10:29:37\",\"updateTime\":\"2022-10-20 10:38:52\",\"isMenu\":2,\"hidden\":true,\"permissionId\":308,\"code\":\"\",\"description\":null,\"subMenus\":null,\"roleId\":null,\"menuIds\":null}],\"roleId\":null,\"menuIds\":null},{\"id\":2060,\"parentId\":1001,\"name\":\"个人中心\",\"css\":\"layui-icon-username\",\"url\":\"/account\",\"path\":\"\",\"sort\":6,\"createTime\":\"2022-07-26 15:53:43\",\"updateTime\":\"2022-08-22 13:54:09\",\"isMenu\":1,\"hidden\":false,\"permissionId\":null,\"code\":\"Account\",\"description\":null,\"subMenus\":[{\"id\":2064,\"parentId\":2060,\"name\":\"基本资料\",\"css\":\"\",\"url\":\"/account/info\",\"path\":\"\",\"sort\":1,\"createTime\":\"2022-08-01 11:24:55\",\"updateTime\":\"2022-08-15 09:14:54\",\"isMenu\":1,\"hidden\":true,\"permissionId\":null,\"code\":\"BasicInfo\",\"description\":null,\"subMenus\":null,\"roleId\":null,\"menuIds\":null},{\"id\":2065,\"parentId\":2060,\"name\":\"消息中心\",\"css\":\"\",\"url\":\"/account/message\",\"path\":\"\",\"sort\":2,\"createTime\":\"2022-08-01 11:25:29\",\"updateTime\":\"2022-08-04 18:18:02\",\"isMenu\":1,\"hidden\":true,\"permissionId\":null,\"code\":\"MessageCenter\",\"description\":null,\"subMenus\":null,\"roleId\":null,\"menuIds\":null},{\"id\":2066,\"parentId\":2060,\"name\":\"密码修改\",\"css\":\"\",\"url\":\"/account/edit-pwd\",\"path\":\"\",\"sort\":3,\"createTime\":\"2022-08-01 11:28:27\",\"updateTime\":\"2022-08-04 18:18:09\",\"isMenu\":1,\"hidden\":true,\"permissionId\":null,\"code\":\"ChangePassword\",\"description\":null,\"subMenus\":null,\"roleId\":null,\"menuIds\":null},{\"id\":2067,\"parentId\":2060,\"name\":\"我的应用\",\"css\":\"\",\"url\":\"/account/my-app\",\"path\":\"\",\"sort\":4,\"createTime\":\"2022-08-01 11:29:17\",\"updateTime\":\"2022-08-08 11:35:49\",\"isMenu\":1,\"hidden\":true,\"permissionId\":null,\"code\":\"MyApp\",\"description\":null,\"subMenus\":[{\"id\":2079,\"parentId\":2067,\"name\":\"我的应用\",\"css\":\"\",\"url\":\"/account/my-app/list\",\"path\":\"\",\"sort\":1,\"createTime\":\"2022-08-08 11:35:21\",\"updateTime\":\"2022-08-23 14:02:49\",\"isMenu\":1,\"hidden\":true,\"permissionId\":null,\"code\":\"\",\"description\":null,\"subMenus\":null,\"roleId\":null,\"menuIds\":null},{\"id\":2078,\"parentId\":2067,\"name\":\"应用修改\",\"css\":\"\",\"url\":\"/account/my-app/edit-app\",\"path\":\"\",\"sort\":2,\"createTime\":\"2022-08-08 11:05:17\",\"updateTime\":\"2022-08-08 11:35:41\",\"isMenu\":1,\"hidden\":true,\"permissionId\":null,\"code\":\"\",\"description\":null,\"subMenus\":null,\"roleId\":null,\"menuIds\":null},{\"id\":2086,\"parentId\":2067,\"name\":\"查看应用\",\"css\":\"\",\"url\":\"/account/my-app/app-details\",\"path\":\"\",\"sort\":3,\"createTime\":\"2022-08-09 15:28:18\",\"updateTime\":\"2022-08-09 15:28:18\",\"isMenu\":1,\"hidden\":false,\"permissionId\":null,\"code\":\"\",\"description\":null,\"subMenus\":null,\"roleId\":null,\"menuIds\":null}],\"roleId\":null,\"menuIds\":null},{\"id\":2068,\"parentId\":2060,\"name\":\"我的API\",\"css\":\"\",\"url\":\"/account/my-api\",\"path\":\"\",\"sort\":5,\"createTime\":\"2022-08-01 11:29:55\",\"updateTime\":\"2022-08-04 18:18:18\",\"isMenu\":1,\"hidden\":true,\"permissionId\":null,\"code\":\"MyApi\",\"description\":null,\"subMenus\":null,\"roleId\":null,\"menuIds\":null},{\"id\":2069,\"parentId\":2060,\"name\":\"调用日志\",\"css\":\"\",\"url\":\"/account/use-log\",\"path\":\"\",\"sort\":6,\"createTime\":\"2022-08-01 11:30:36\",\"updateTime\":\"2022-08-04 18:18:24\",\"isMenu\":1,\"hidden\":true,\"permissionId\":null,\"code\":\"UseLog\",\"description\":null,\"subMenus\":[{\"id\":2084,\"parentId\":2069,\"name\":\"调用日志\",\"css\":\"\",\"url\":\"/account/use-log/list\",\"path\":\"\",\"sort\":1,\"createTime\":\"2022-08-08 16:22:38\",\"updateTime\":\"2022-08-23 14:04:39\",\"isMenu\":1,\"hidden\":false,\"permissionId\":null,\"code\":\"\",\"description\":null,\"subMenus\":null,\"roleId\":null,\"menuIds\":null},{\"id\":2083,\"parentId\":2069,\"name\":\"调用日志详情\",\"css\":\"\",\"url\":\"/account/use-log/detail-user-Log\",\"path\":\"\",\"sort\":2,\"createTime\":\"2022-08-08 16:21:35\",\"updateTime\":\"2022-08-08 16:22:48\",\"isMenu\":1,\"hidden\":false,\"permissionId\":null,\"code\":\"\",\"description\":null,\"subMenus\":null,\"roleId\":null,\"menuIds\":null}],\"roleId\":null,\"menuIds\":null},{\"id\":2070,\"parentId\":2060,\"name\":\"调用监控\",\"css\":\"\",\"url\":\"/account/traffic-statistics\",\"path\":\"\",\"sort\":7,\"createTime\":\"2022-08-01 11:31:34\",\"updateTime\":\"2022-08-23 14:04:53\",\"isMenu\":1,\"hidden\":true,\"permissionId\":null,\"code\":\"TrafficStatistics\",\"description\":null,\"subMenus\":null,\"roleId\":null,\"menuIds\":null},{\"id\":2071,\"parentId\":2060,\"name\":\"我的申请\",\"css\":\"\",\"url\":\"/account/my-apply\",\"path\":\"\",\"sort\":8,\"createTime\":\"2022-08-01 11:32:05\",\"updateTime\":\"2022-08-04 18:18:51\",\"isMenu\":1,\"hidden\":true,\"permissionId\":null,\"code\":\"MyApply\",\"description\":null,\"subMenus\":[{\"id\":2085,\"parentId\":2071,\"name\":\"我的申请\",\"css\":\"\",\"url\":\"/account/my-apply/list\",\"path\":\"\",\"sort\":1,\"createTime\":\"2022-08-08 16:23:37\",\"updateTime\":\"2022-08-23 14:05:06\",\"isMenu\":1,\"hidden\":false,\"permissionId\":null,\"code\":\"\",\"description\":null,\"subMenus\":null,\"roleId\":null,\"menuIds\":null},{\"id\":2082,\"parentId\":2071,\"name\":\"我的申请详情\",\"css\":\"\",\"url\":\"/account/my-apply/detail-my-apply\",\"path\":\"\",\"sort\":2,\"createTime\":\"2022-08-08 16:20:39\",\"updateTime\":\"2022-08-08 16:23:44\",\"isMenu\":1,\"hidden\":false,\"permissionId\":null,\"code\":\"\",\"description\":null,\"subMenus\":null,\"roleId\":null,\"menuIds\":null}],\"roleId\":null,\"menuIds\":null},{\"id\":2087,\"parentId\":2060,\"name\":\"我的数据篮\",\"css\":\"\",\"url\":\"/account/my-data-basket\",\"path\":\"\",\"sort\":9,\"createTime\":\"2022-08-11 10:13:42\",\"updateTime\":\"2022-08-11 10:14:11\",\"isMenu\":1,\"hidden\":true,\"permissionId\":null,\"code\":\"\",\"description\":null,\"subMenus\":null,\"roleId\":null,\"menuIds\":null}],\"roleId\":null,\"menuIds\":null}]";
        List<Map> poetList = JSON.parseArray(json, Map.class);//转换
        return Result.success(poetList);
    }

    @GetMapping("/statistics")
    public Result getStatistics(){
        return Result.success();
    }
}
