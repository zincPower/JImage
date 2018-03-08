package com.zinc.libimage.model.detatilControl;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.zinc.libimage.R;
import com.zinc.libimage.view.view.CropImageView;
import com.zinc.libimage.view.view.GestureImageView;
import com.zinc.libimage.view.view.JCropView;
import com.zinc.libimage.view.view.OverlayView;

/**
 * @author Jiang zinc
 * @date 创建时间：2018/3/8
 * @description 编辑页面的工具栏项
 */

public abstract class BaseDetailControlInfo implements IDetailControlInfo {

    public static final int CROP = 1;
    public static final int FILTER = 2;
    public static final int TOOLBOX = 3;

    private int type;

    protected int itemLayout;
    protected int title;
    protected int unselecticon;
    protected int selectIcon;
    protected boolean isSelect;

    protected boolean isTouch = false;

    public BaseDetailControlInfo(int type, int title, int unselecticon, int selectIcon, boolean isSelect) {
        this.type = type;
        this.title = title;
        this.unselecticon = unselecticon;
        this.selectIcon = selectIcon;
        this.isSelect = isSelect;
    }

    public int getItemLayout() {
        return itemLayout;
    }

    public void setItemLayout(int itemLayout) {
        this.itemLayout = itemLayout;
    }

    public int getUnselecticon() {
        return unselecticon;
    }

    public void setUnselecticon(int unselecticon) {
        this.unselecticon = unselecticon;
    }

    public int getSelectIcon() {
        return selectIcon;
    }

    public void setSelectIcon(int selectIcon) {
        this.selectIcon = selectIcon;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isTouch() {
        return isTouch;
    }

    public void setTouch(boolean touch) {
        isTouch = touch;
    }

    protected JCropView getCropView(View view) {
        return view.findViewById(R.id.crop_view);
    }

    protected GestureImageView getGestureImageView(View view) {
        return getCropView(view).getCropImageView();
    }

    protected OverlayView getOverlayView(View view) {
        return getCropView(view).getOverlayView();
    }

    protected FrameLayout getDetailControlBar(View view) {
        return view.findViewById(R.id.fl_detail_control_bar);
    }

    protected RecyclerView getRecycleViewControlBar(View view) {
        return view.findViewById(R.id.rv_control_bar);
    }

}
