package cn.crabc.core.app.exception;

import cn.crabc.core.app.util.Result;
import cn.crabc.core.datasource.exception.CustomException;
import cn.crabc.core.spi.PluginException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 异常统一返回处理
 *
 * @author yuqf
 */
@RestControllerAdvice
public class ExceptionHandler {
    private final static Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

    /**
     * 处理自定义异常
     *
     * @param e
     * @return
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(value = CustomException.class)
    @ResponseBody
    public Result custom(CustomException e) {
        return Result.error(e.getCode(), e.getMsg());
    }

    /**
     * 处理插件异常
     *
     * @param e
     * @return
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(value = PluginException.class)
    @ResponseBody
    public Result custom(PluginException e) {
        return Result.error(e.getCode(), e.getMsg());
    }

    /**
     * 处理空指针的异常
     *
     * @param req
     * @param e
     * @return
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public Result exceptionHandler(HttpServletRequest req, NullPointerException e) {
        return Result.error(50001, "必传参数不能为空！");
    }

    /**
     * 运行时异常
     * @param req
     * @param e
     * @return
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(value = RuntimeException.class)
    @ResponseBody
    public Result exceptionHandler(HttpServletRequest req, RuntimeException e) {
        return Result.error(50005, "执行异常："+e.getMessage());
    }

    /**
     * 处理参数校验异常 --Json 转换异常
     *
     * @param req
     * @param e
     * @return
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseBody
    public Result exceptionHandler(HttpServletRequest req, HttpMessageNotReadableException e) {
        return Result.error(50002, "Body参数异常，请检查Body/json是否正确传参");
    }
}
