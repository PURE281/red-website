package com.jbb.library_common.retrofit.other;


/***
 * 所有实体返回类继承它
 */
public class BaseBean {

    private int code;
    private String msg;
    private String tipMessage;


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    @Override
    public String toString() {
        return "BaseBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", tipMessage='" + tipMessage + '\'' +
                '}';
    }
}
