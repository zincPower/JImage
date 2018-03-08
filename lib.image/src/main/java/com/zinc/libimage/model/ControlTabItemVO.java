package com.zinc.libimage.model;

/**
 * @author Jiang zinc
 * @date 创建时间：2018/3/7
 * @description
 */

public class ControlTabItemVO {

    private String name;
    private int image;

    public ControlTabItemVO(String name, int image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
