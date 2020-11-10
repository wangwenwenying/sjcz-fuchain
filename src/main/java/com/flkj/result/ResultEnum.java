package com.flkj.result;

public enum ResultEnum {
    USER_ERROR("00016","链上用户创建失败"),
    INPUT_ERROR("00015","输入值不能为空"),
    METADATA_ERROR("00014","元数据标志已存在"),
    DATANULL_ERROR("00013","输入数据为空"),
    PROOF_ERRORS("00012","证据不存在或者证据异常"),
    PROOF_ERROR("00012","证据文件异常"),
    FILE_NULLERROR("00011","文件不能为空"),
    LOGIN_ERROR("00010","你的登录信息异常"),
    TOKEN_ERROR("00009","无token，请重新登录"),
    FTP_ERROR("00008","FTP 连接失败"),
    ZH_REERO("00007","帐号已存在"),
    ZHMM_ERROR("00006","帐号密码错误或者帐号已过期"),
    ZHMMNLL_ERROR("00005","帐号密码为空"),
    GET_ERROR("00004","领取任务失败，任务已被领取"),
    DELETE_ERROR("00003","删除失败"),
    UNKNOW_ERROR("00002","网络异常"),
    NOTFOUND("00001","未找到该对象"),
    SUCCESS("00000","成功");
    private String code;
    private String msg;

    ResultEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}