package com.zinc.libimage.anim;

import com.zinc.libimage.utils.gesture.CubicEasing;
import com.zinc.libimage.view.view.GestureImageView;

import java.lang.ref.WeakReference;

/**
 * 用于缩放时带动画缓冲效果
 *
 * This Runnable is used to animate an image zoom.
 * Given values are interpolated during the animation time.
 * Runnable can be terminated either vie {@link GestureImageView#cancelAllAnimations()} method
 * or when certain conditions inside {@link ZoomImageToPosition#run()} method are triggered.
 */
public class ZoomImageToPosition implements Runnable {

    private final WeakReference<GestureImageView> mCropImageView;

    private final long mDurationMs, mStartTime;
    private final float mOldScale;
    private final float mDeltaScale;
    private final float mDestX;
    private final float mDestY;

    public ZoomImageToPosition(GestureImageView gestureImageView,
                               long durationMs,
                               float oldScale, float deltaScale,
                               float destX, float destY) {

        mCropImageView = new WeakReference<>(gestureImageView);

        mStartTime = System.currentTimeMillis();
        mDurationMs = durationMs;
        mOldScale = oldScale;
        mDeltaScale = deltaScale;
        mDestX = destX;
        mDestY = destY;
    }

    @Override
    public void run() {
        GestureImageView gestureImageView = mCropImageView.get();
        if (gestureImageView == null) {
            return;
        }

        long now = System.currentTimeMillis();
        float currentMs = Math.min(mDurationMs, now - mStartTime);
        float newScale = CubicEasing.easeInOut(currentMs, 0, mDeltaScale, mDurationMs);

        if (currentMs < mDurationMs) {
            gestureImageView.zoomInImage(mOldScale + newScale, mDestX, mDestY);
            gestureImageView.post(this);
        } else {
//                gestureImageView.setImageToWrapCropBounds();
        }
    }

}