package com.zinc.libimage.model.detatilControl;

import android.view.View;
import android.view.ViewGroup;

/**
 * @author Jiang zinc
 * @date 创建时间：2018/3/8
 * @description 90度旋转
 */

public class RightRotateDetailControlInfo extends BaseDetailControlInfo {

    public RightRotateDetailControlInfo(int type, int title, int unselecticon, int selectIcon, boolean isSelect) {
        super(type, title, unselecticon, selectIcon, isSelect);
        setTouch(true);
    }

    @Override
    public void execute(View view) {
        getGestureImageView(view).postRotate(-90);
    }

}
