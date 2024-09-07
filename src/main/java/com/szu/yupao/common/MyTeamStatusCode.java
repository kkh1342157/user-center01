package com.szu.yupao.common;

public enum MyTeamStatusCode {
    //1、写中文以及需要的参数

    OPENING("公开", 1),

    PRIVATE("私有", 2),

    SECERT("加密", 3);

    //2、定义属性
    String text;

    int value;

    //3、自定义构造器
    MyTeamStatusCode(String text, int value) {
        this.text = text;
        this.value = value;
    }

    //4、自定义部分需要的方法
    public String getStatusByVal(int value){
        TeamStatusCode[] values = TeamStatusCode.values();
        return "";
    }


}
