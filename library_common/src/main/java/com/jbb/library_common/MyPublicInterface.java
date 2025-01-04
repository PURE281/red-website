package com.jbb.library_common;


public interface MyPublicInterface {

    /**
     * 从tim返回的数据
     * @param type 该消息类型
     * @param userId 发送的用户TIMId
     * @param data 数据内容
     */
    void showJubakInfo(int type,String userId,Object data);

}
