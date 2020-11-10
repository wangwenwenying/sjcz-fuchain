package com.flkj.result;


import com.flkj.exception.UserException;

/**
 * @description:
 * @Author: www
 * @Date: 2020-03-26 15:05
 */
public class ResultUtil {
    public static Result success(Object object) {
        Result result = new Result();
        result.setCode(ResultEnum.SUCCESS.getCode());
        result.setMsg(ResultEnum.SUCCESS.getMsg());
        result.setData(object);
        return result;
    }

    public static Result success() {
        return success(null);
    }

    public static Result error(String code, String msg) {
        Result result = new Result(code,msg);
        return result;
    }
    public static Result error(ResultEnum resultEnum) {
        return error(resultEnum.getCode(),resultEnum.getMsg());
    }
    public static Result error(UserException userException) {
        return error(userException.getCode(),userException.getMessage());
    }
}
