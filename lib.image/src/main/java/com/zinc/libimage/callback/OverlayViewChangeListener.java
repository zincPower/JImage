package com.zinc.libimage.callback;

import android.graphics.RectF;

/**
 *
 * @date 创建时间：2018/3/5
 * @author Jiang zinc
 * @description 遮罩区域变动回调
 *
 */

public interface OverlayViewChangeListener {

    void onCropRectUpdated(RectF cropRect);

}