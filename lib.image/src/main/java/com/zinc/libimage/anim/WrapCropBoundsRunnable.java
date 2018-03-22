package com.zinc.libimage.anim;

import com.zinc.libimage.utils.gesture.CubicEasing;
import com.zinc.libimage.view.view.CropImageView;

import java.lang.ref.WeakReference;

public class WrapCropBoundsRunnable implements Runnable {

    private final WeakReference<CropImageView> mCropImageView;

    private final long mDurationMs, mStartTime;
    private final float mOldX, mOldY;
    private final float mCenterDiffX, mCenterDiffY;
    private final float mOldScale;
    private final float mDeltaScale;
    private final boolean mWillBeImageInBoundsAfterTranslate;

    /**
     *
     * @param cropImageView 裁剪视图
     * @param durationMs 动画时长
     * @param oldX 当前图片中心坐标x
     * @param oldY 当前图片中心坐标y
     * @param centerDiffX x轴偏移量
     * @param centerDiffY y轴偏移量
     * @param oldScale 原先缩放
     * @param deltaScale 缩放增量
     * @param willBeImageInBoundsAfterTranslate 裁剪区域是否超出图片区域
     */
    public WrapCropBoundsRunnable(CropImageView cropImageView,
                                  long durationMs,
                                  float oldX, float oldY,
                                  float centerDiffX, float centerDiffY,
                                  float oldScale, float deltaScale,
                                  boolean willBeImageInBoundsAfterTranslate) {

        mCropImageView = new WeakReference<>(cropImageView);

        mDurationMs = durationMs;
        mStartTime = System.currentTimeMillis();
        mOldX = oldX;
        mOldY = oldY;
        mCenterDiffX = centerDiffX;
        mCenterDiffY = centerDiffY;
        mOldScale = oldScale;
        mDeltaScale = deltaScale;
        mWillBeImageInBoundsAfterTranslate = willBeImageInBoundsAfterTranslate;
    }

    @Override
    public void run() {
        CropImageView cropImageView = mCropImageView.get();
        if (cropImageView == null) {
            return;
        }

        long now = System.currentTimeMillis();
        float currentMs = Math.min(mDurationMs, now - mStartTime);

        //计算这一刻，已经需要偏移的总量（包含之前）
        float newX = CubicEasing.easeOut(currentMs, 0, mCenterDiffX, mDurationMs);
        float newY = CubicEasing.easeOut(currentMs, 0, mCenterDiffY, mDurationMs);
        float newScale = CubicEasing.easeInOut(currentMs, 0, mDeltaScale, mDurationMs);

        if (currentMs < mDurationMs) {
            float[] currentImageCenter = cropImageView.getmCurrentImageCenter();
            //需要偏移的需要减去之前的偏移
            cropImageView.postTranslate(newX - (currentImageCenter[0] - mOldX), newY - (currentImageCenter[1] - mOldY));
            if (!mWillBeImageInBoundsAfterTranslate) {
                cropImageView.zoomInImage(mOldScale + newScale, cropImageView.getmCropRect().centerX(), cropImageView.getmCropRect().centerY());
            }
            if (!cropImageView.isImageWrapCropBounds()) {
                cropImageView.post(this);
            }
        }
    }
}