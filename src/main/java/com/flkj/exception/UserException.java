package com.flkj.exception;

import com.flkj.result.ResultEnum;
import lombok.Data;

/**
 * @description:
 * @Author: www
 * @Date: 2020-03-26 15:13
 */
@Data
public class UserException extends RuntimeException {


    /**
     * 我们希望定位的错误更准确，
     * 希望不同的错误可以返回不同的错误码，所以可以自定义一个Exception
     *
     *
     * 注意要继承自RuntimeException，底层RuntimeException继承了Exception,
     * spring框架只对抛出的异常是RuntimeException才会进行事务回滚，
     * 如果是抛出的是Exception，是不会进行事物回滚的
     */
    public UserException(ResultEnum resultEnum) {
        super(resultEnum.getMsg());
        this.code = resultEnum.getCode();
    }

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
