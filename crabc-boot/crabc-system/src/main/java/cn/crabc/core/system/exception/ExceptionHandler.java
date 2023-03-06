package cn.crabc.core.system.exception;

import cn.crabc.core.app.exception.CustomException;
import cn.crabc.core.system.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

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
     * 处理空指针的异常
     *
     * @param req
     * @param e
     * @return
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public Result exceptionHandler(HttpServletRequest req, NullPointerException e) {
        log.error("发生空指针异常！原因是:", e);
        return Result.error(50001, "空指针异常");
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
        log.error("参数校验异常-json转换异常:", e);
        return Result.error(50002, "json转换异常");
    }
}
