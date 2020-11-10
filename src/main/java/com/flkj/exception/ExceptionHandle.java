package com.flkj.exception;

/**
 * @description:
 * @Author: www
 * @Date: 2020-03-26 15:14
 */

import com.flkj.result.Result;
import com.flkj.result.ResultEnum;
import com.flkj.result.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常处理
 */
@Slf4j
@ControllerAdvice
public class ExceptionHandle {
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result handle(Exception e) {
        if (e instanceof UserException) {
            UserException userException = (UserException) e;
            log.error(e.getMessage());
            return ResultUtil.error(userException);
        } else {
            e.printStackTrace();
            log.error(e.getMessage());
            return ResultUtil.error(ResultEnum.UNKNOW_ERROR);
        }
    }
}
