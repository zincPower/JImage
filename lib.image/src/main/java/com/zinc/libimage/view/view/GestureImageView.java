package com.zinc.libimage.view.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.zinc.libimage.anim.ZoomImageToPosition;
import com.zinc.libimage.utils.gesture.RotationGestureDetector;

/**
 * @author Jiang zinc
 * @date 创建时间：2018/2/26
 * @description 带手势的ImageView
 */

public class GestureImageView extends CropImageView {

    private static final int DOUBLE_TAP_ZOOM_DURATION = 200;

    private float mMidPntX, mMidPntY;

    private ScaleGestureDetector mScaleDetector;
    private RotationGestureDetector mRotationGestureDetector;
    private GestureDetector mGestureDetector;

    private int mDoubleTapScaleSteps = 5;

    private ZoomImageToPosition mZoomImageToPositionRunnable;

    public GestureImageView(Context context) {
        this(context, null, 0);
    }

    public GestureImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    protected void init() {
        setupGestureListener();
    }

    /**
     * 设置侦听
     */
    private void setupGestureListener() {
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        mRotationGestureDetector = new RotationGestureDetector(new RotateListener());
        mGestureDetector = new GestureDetector(getContext(), new GestureListener(), null, true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getPointerCount() > 1) {
            // TODO: 2018/2/26 需要修复
            mMidPntX = (event.getX(0) + event.getX(1)) / 2;
            mMidPntY = (event.getY(0) + event.getY(1)) / 2;
        }

        mGestureDetector.onTouchEvent(event);

        if(mScaleDetector != null){
            mScaleDetector.onTouchEvent(event);
        }

        if (mRotationGestureDetector != null) {
            mRotationGestureDetector.onTouchEvent(event);
        }

        if(event.getActionMasked() == MotionEvent.ACTION_UP){
            setImageToWrapCropBounds();
        }

        return true;
    }

    protected float getDoubleTapTargetScale() {
        return getCurrentScale() * (float) Math.pow(getMaxScale() / getMinScale(), 1.0f / mDoubleTapScaleSteps);
    }

    /**
     * 基于缩放中心（x, y）按照 缩放比 scale 进行时长为durationMs进行缩放
     *
     * @param scale      - 缩放的比率
     * @param centerX    - 缩放中心X
     * @param centerY    - 缩放中心Y
     * @param durationMs - 动画时长
     */
    protected void zoomImageToPosition(float scale, float centerX, float centerY, long durationMs) {
        if (scale > getMaxScale()) {
            scale = getMaxScale();
        }

        final float oldScale = getCurrentScale();
        final float deltaScale = scale - oldScale;

        post(mZoomImageToPositionRunnable = new ZoomImageToPosition(GestureImageView.this,
                durationMs, oldScale, deltaScale, centerX, centerY));
    }

    /**
     *
     * @date 创建时间 2018/3/5
     * @author Jiang zinc
     * @Description 移除动画
     * @version
     *
     */
    private void cancelAllAnimations() {
        removeCallbacks(mZoomImageToPositionRunnable);
        removeCallbacks(mWrapCropBoundsRunnable);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            postScale(detector.getScaleFactor(), mMidPntX, mMidPntY);
            return true;
        }
    }

    private class RotateListener extends RotationGestureDetector.SimpleRotationGestureListener {
        @Override
        public boolean onRotation(RotationGestureDetector rotationGestureDetector) {
            postRotate(rotationGestureDetector.getmAngle(), mMidPntX, mMidPntY);
            return true;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        //在双击的第二下，Touch down时触发
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            zoomImageToPosition(getDoubleTapTargetScale(), e.getX(), e.getY(), DOUBLE_TAP_ZOOM_DURATION);
            return super.onDoubleTap(e);
        }

        //用于移动图像
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            postTranslate(-distanceX, -distanceY);
            return true;
        }
    }

}
