package com.szu.yupao.common;

/**
 * 队伍状态枚举类
 *
 */
public enum TeamStatusCode {

    PRIVATE(1,"私有"),
    SECRET(2,"加密"),
    PUBLIC(0,"公开");

    private int value;

    private String text;

    TeamStatusCode(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public static TeamStatusCode getStatusByValue(Integer value){
        if (value == null){
            return null;
        }
        //枚举类的values获得的TeamStatusCode这个类下面的所有的枚举对象
        TeamStatusCode[] values = TeamStatusCode.values();
        for (TeamStatusCode teamStatusCode :values) {
            if (teamStatusCode.getValue() == value){
                return teamStatusCode;
            }
        }

        return null;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
