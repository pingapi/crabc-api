package cn.crabc.core.system.filter;

import cn.crabc.core.app.exception.CustomException;
import cn.crabc.core.system.component.BaseCache;
import cn.crabc.core.system.entity.BaseApp;
import cn.crabc.core.system.entity.dto.ApiInfoDTO;
import cn.crabc.core.system.enums.ApiAuthEnum;
import cn.crabc.core.system.util.ApiThreadLocal;
import cn.crabc.core.system.util.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * API开放接口鉴权过滤 拦截器
 *
 * @author yuqf
 */

public class AuthInterceptor implements HandlerInterceptor {
    // API开放接口前缀
    private final static String API_PRE = "/api/web/";
    @Autowired
    private BaseCache baseCache;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        ApiInfoDTO apiInfo = baseCache.getCacheApiInfo(method + "_" + path.replace(API_PRE, ""));
        if (ApiAuthEnum.CODE.getName().equalsIgnoreCase(apiInfo.getAuthType())) {
            Boolean auth = checkAppCode(request, apiInfo);
            if (auth == false) {
                throw new CustomException(53001, "您没有访问该API的权限");
            }
        }else if (ApiAuthEnum.APP_SECRET.getName().equalsIgnoreCase(apiInfo.getAuthType())){
            // TODO
        }
        // 放入上下文
        ApiThreadLocal.set(apiInfo);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) {
        // 清除上下文
        ApiThreadLocal.remove();
    }

    /**
     * 验证接口访问权限
     *
     * @param request
     * @param apiInfo
     * @return
     */
    private Boolean checkAppCode(HttpServletRequest request, ApiInfoDTO apiInfo) {
        String appCode = RequestUtils.getAppCode(request);
        if (appCode == null || "".equals(appCode)) {
            return false;
        }
        List<BaseApp> appList = apiInfo.getAppList();
        if (appList == null || appList.size() == 0) {
            return false;
        }
        for (BaseApp app : appList) {
            if (app.getAppCode().equals(appCode)) {
                return true;
            }
        }
        return false;
    }
}
