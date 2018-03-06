package com.zinc.libimage.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @date 创建时间：2017/12/5
 * @author Jiang zinc
 * @description 文件夹实体
 *
 */

public class LocalMediaFolder implements Serializable {

    private String name;    //名称
    private String path;    //文件夹路径
    private String firstImagePath; //第一张图的路径
    private int imageNum;   //文件夹的图片张数
    private List<LocalMedia> images = new ArrayList<LocalMedia>();  //图片

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }

    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    public int getImageNum() {
        return imageNum;
    }

    public void setImageNum(int imageNum) {
        this.imageNum = imageNum;
    }

    public List<LocalMedia> getImages() {
        return images;
    }

    public void setImages(List<LocalMedia> images) {
        this.images = images;
    }
}
