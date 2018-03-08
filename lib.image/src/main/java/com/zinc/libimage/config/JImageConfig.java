package com.zinc.libimage.config;

import android.support.v4.content.ContextCompat;

import com.zinc.libimage.R;
import com.zinc.libimage.model.ControlTabItemVO;
import com.zinc.libimage.model.detatilControl.BaseDetailControlInfo;
import com.zinc.libimage.model.detatilControl.RatioDetailControlInfo;
import com.zinc.libimage.model.detatilControl.RightRotateDetailControlInfo;
import com.zinc.libimage.model.detatilControl.RotateDetailControlInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jiang zinc
 * @date 创建时间：2018/1/23
 * @description
 */

public class JImageConfig {

    //照相机
    public static int ITEM_CAMERA = 1;
    //图片
    public static int ITEM_PICTURE = 2;

    //媒体类型
    public static final int TYPE_IMAGE = 3;     //图片
    public static final int TYPE_VIDEO = 4;     //视频

    //工具栏
//    public static final List<ControlTabItemVO> controlTabItemList = new ArrayList<>();
//    static {
//        controlTabItemList.add(new ControlTabItemVO("裁剪", R.mipmap.j_ic_crop));
//        controlTabItemList.add(new ControlTabItemVO("滤镜", R.mipmap.j_ic_color_filter));
//        controlTabItemList.add(new ControlTabItemVO("编辑工具箱", R.mipmap.j_ic_toolbox));
//    }

    //用于存储裁剪的子操作
    public static List<BaseDetailControlInfo> cropDetailControlInfoList = new ArrayList<>();

    static {
        cropDetailControlInfoList.add(new RotateDetailControlInfo(BaseDetailControlInfo.CROP,
                R.string.jimage_rotate,
                R.mipmap.j_ic_rotate,
                R.mipmap.j_ic_rotate_select,
                true));

        cropDetailControlInfoList.add(new RatioDetailControlInfo(BaseDetailControlInfo.CROP,
                R.string.jimage_ratio,
                R.mipmap.j_ic_ratio,
                R.mipmap.j_ic_ratio_select,
                false));

        cropDetailControlInfoList.add(new RightRotateDetailControlInfo(BaseDetailControlInfo.CROP,
                R.string.jimage_rotate_90,
                R.mipmap.j_ic_rotate_90,
                R.mipmap.j_ic_rotate_90_select,
                false));
    }

    //用于存储滤镜的子操作
    private List<BaseDetailControlInfo> filterDetailControlInfoList;
    //用于存储编辑工具箱的子操作
    private List<BaseDetailControlInfo> toolboxDetailControlInfoList;

}
