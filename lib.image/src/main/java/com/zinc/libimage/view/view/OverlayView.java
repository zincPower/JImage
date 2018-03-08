package com.zinc.libimage.view.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.zinc.libimage.R;
import com.zinc.libimage.callback.OverlayViewChangeListener;
import com.zinc.libimage.utils.RectUtils;
import com.zinc.libimage.utils.UIUtil;

import java.util.Map;

/**
 * @author Jiang zinc
 * @date 创建时间：2018/3/1
 * @description 遮罩
 */

public class OverlayView extends View {

    //裁剪框的长宽比
    private float mTargetAspectRatio;

    //视图可用的空间
    private int mWidth, mHeight;

    //是否为圆形遮罩
    private boolean mIsCircleDimmedLayer;

    //圆形裁剪区
    private Path mCirclePath = new Path();
    //裁剪区域的范围
    private RectF mCropViewRect = new RectF();
    private RectF mTempRect = new RectF();
    //遮罩颜色
    private int mDimmedColor;

    private int mCropGridRowCount;
    private int mCropGridColCount;

    //用于画遮罩
    private Paint mDimmedStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //用于画网格
    private Paint mGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //用于画外围
    private Paint mFramePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //用于画角
    private Paint mFrameCornersPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int mGridWidth;
    private int mCornerWidth;

    private float mGridPoints[];
    private float[] mCropGridCorners;
    private float[] mCropGridCenter;

    //这是一个标记，为了防止该视图还未部署，导致遮罩未打开
    private boolean mShouldSetupCropBounds;
    private int mCropRectCornerTouchAreaLineLength;

    //操控截图区域四个角的阀值
    private int mTouchPointThreshold;
    //截图最小区域的尺寸
    private int mCropRectMinSize;
    //触碰点坐标
    private int mCurrentTouchCornerIndex;
    private float mPreviousTouchX;
    private float mPreviousTouchY;

    private boolean isFixTargetAspectRatio;

    private OverlayViewChangeListener mOverlayViewChangeListener;

    public OverlayView(Context context) {
        this(context, null, 0);
    }

    public OverlayView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverlayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDimmedColor = getResources().getColor(R.color.jimage_dimmed_color);
        mDimmedStrokePaint.setColor(mDimmedColor);
        mDimmedStrokePaint.setStyle(Paint.Style.STROKE);
        mDimmedStrokePaint.setStrokeWidth(mGridWidth);

        mCropGridRowCount = 2;
        mCropGridColCount = 2;

        mTouchPointThreshold = UIUtil.dip2px(getContext(), 30);
        mCropRectMinSize = UIUtil.dip2px(getContext(), 100);

        mGridWidth = UIUtil.dip2px(getContext(), 1);
        mCornerWidth = mGridWidth * 3;

        mCropRectCornerTouchAreaLineLength = UIUtil.dip2px(getContext(), 10);

        mFramePaint.setStrokeWidth(mGridWidth);
        mFramePaint.setColor(getResources().getColor(R.color.jimage_overlay_frame_color));
        mFramePaint.setStyle(Paint.Style.STROKE);

        mGridPaint.setStrokeWidth(mGridWidth);
        mGridPaint.setColor(getResources().getColor(R.color.jimage_overlay_grid_color));
        mGridPaint.setStyle(Paint.Style.STROKE);

        mFrameCornersPaint.setStrokeWidth(mCornerWidth);
        mFrameCornersPaint.setColor(getResources().getColor(R.color.jimage_overlay_frame_color));
        mFrameCornersPaint.setStyle(Paint.Style.STROKE);

    }

    public float getmTargetAspectRatio() {
        return mTargetAspectRatio;
    }

    public int getmDimmedColor() {
        return mDimmedColor;
    }

    public void setmDimmedColor(int mDimmedColor) {
        this.mDimmedColor = mDimmedColor;
    }

    public void setOverlayViewChangeListener(OverlayViewChangeListener mOverlayViewChangeListener) {
        this.mOverlayViewChangeListener = mOverlayViewChangeListener;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            //确定视图可用空间
            left = getPaddingLeft();
            top = getPaddingTop();
            right = getWidth() - getPaddingRight();
            bottom = getHeight() - getPaddingBottom();
            mWidth = right - left;
            mHeight = bottom - top;

            if (mShouldSetupCropBounds) {
                mShouldSetupCropBounds = false;
                setTargetAspectRatio(mTargetAspectRatio);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDimmedLayer(canvas);
    }

    //画遮罩
    private void drawDimmedLayer(Canvas canvas) {
        canvas.save();
        if (mIsCircleDimmedLayer) {     //圆形遮罩
            canvas.clipPath(mCirclePath, Region.Op.DIFFERENCE);
        } else {                          //长方形遮罩
            canvas.clipRect(mCropViewRect, Region.Op.DIFFERENCE);
        }
        canvas.drawColor(mDimmedColor);
        canvas.restore();

        if (mIsCircleDimmedLayer) {
            drawCropCircle(canvas);
        } else {
            drawCropGrid(canvas);
        }

    }

    //画网格
    private void drawCropGrid(Canvas canvas) {

        //mGridPoints还未初始化且mCropViewRect范围不为空
        if (mGridPoints == null && !mCropViewRect.isEmpty()) {
            mGridPoints = new float[mCropGridRowCount * 4 + mCropGridColCount * 4];
            int index = 0;

            float rectWidth = mCropViewRect.width();
            float rectHeight = mCropViewRect.height();

            for (int i = 0; i < mCropGridColCount; ++i) {       //添加列分割线坐标
                mGridPoints[index++] = rectWidth * ((float) i + 1.0f) / (float) (mCropGridColCount + 1) + mCropViewRect.left;
                mGridPoints[index++] = mCropViewRect.top;
                mGridPoints[index++] = rectWidth * ((float) i + 1.0f) / (float) (mCropGridColCount + 1) + mCropViewRect.left;
                mGridPoints[index++] = mCropViewRect.bottom;
            }
            for (int i = 0; i < mCropGridRowCount; ++i) {       //添加横分割线坐标
                mGridPoints[index++] = mCropViewRect.left;
                mGridPoints[index++] = rectHeight * ((float) i + 1.0f) / (float) (mCropGridRowCount + 1) + mCropViewRect.top;
                mGridPoints[index++] = mCropViewRect.right;
                mGridPoints[index++] = rectHeight * ((float) i + 1.0f) / (float) (mCropGridRowCount + 1) + mCropViewRect.top;
            }

        }

        //画九宫格
        if (mGridPoints != null) {
            canvas.drawLines(mGridPoints, mGridPaint);
        }

        //绘制外框
        canvas.drawRect(mCropViewRect, mFramePaint);

        //当开启随意裁剪时，设置四个角
//        if (mFreestyleCropMode != FREESTYLE_CROP_MODE_DISABLE) {
        canvas.save();

        mTempRect.set(mCropViewRect);
        mTempRect.inset(mCropRectCornerTouchAreaLineLength, -mCropRectCornerTouchAreaLineLength);
        canvas.clipRect(mTempRect, Region.Op.DIFFERENCE);

        mTempRect.set(mCropViewRect);
        mTempRect.inset(-mCropRectCornerTouchAreaLineLength, mCropRectCornerTouchAreaLineLength);
        canvas.clipRect(mTempRect, Region.Op.DIFFERENCE);

        canvas.drawRect(mCropViewRect, mFrameCornersPaint);

        canvas.restore();
//        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mCropViewRect.isEmpty()) {
            return false;
        }

        float x = event.getX();
        float y = event.getY();

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {     //按下

            //获取触碰点
            mCurrentTouchCornerIndex = getCurrentTouchIndex(x, y);
            //是否需要拦截，当为-1时，落在不用操作的范围
            boolean shouldHandle = mCurrentTouchCornerIndex != -1;
            if (shouldHandle) {
                mPreviousTouchX = -1;
                mPreviousTouchY = -1;
            } else {
                mPreviousTouchX = x;
                mPreviousTouchY = y;
            }
            return shouldHandle;

        }
        if (event.getActionMasked() == MotionEvent.ACTION_MOVE) { //移动

            //只有一个触碰点且触碰的是有效范围
            if (mCurrentTouchCornerIndex != -1 && event.getPointerCount() == 1) {

                //让x和y在padding内
                x = Math.min(Math.max(x, getPaddingLeft()), getWidth() - getPaddingRight());
                y = Math.min(Math.max(y, getPaddingTop()), getHeight() - getPaddingBottom());

                updateCropViewRect(x, y);

                mPreviousTouchX = x;
                mPreviousTouchY = y;

                return true;
            }

        }
        if (event.getActionMasked() == MotionEvent.ACTION_UP) { //抬起
            mPreviousTouchX = -1;
            mPreviousTouchY = -1;
            mCurrentTouchCornerIndex = -1;

            if (mOverlayViewChangeListener != null) {
                mOverlayViewChangeListener.onCropRectUpdated(mCropViewRect);
            }

        }

        return false;
    }

    /**
     * @date 创建时间 2018/3/3
     * @author Jiang zinc
     * @Description 更新裁剪区域
     * 0------->1
     * ^        |
     * |   4    |
     * |        v
     * 3<-------2
     * @version
     */
    private void updateCropViewRect(float touchX, float touchY) {
        mTempRect.set(mCropViewRect);

        float resultTouchX = touchX;
        float resultTouchY = touchY;

        float deltaX;
        float deltaY;

        //如果是固定比例，则以横坐标为参考比例值
        if (isFixTargetAspectRatio) {
            switch (mCurrentTouchCornerIndex) {
                case 0:
                    deltaX = mCropViewRect.right - touchX;
                    deltaY = deltaX / mTargetAspectRatio;
                    resultTouchY = mCropViewRect.bottom - deltaY;
                    break;
                case 3:
                    deltaX = mCropViewRect.right - touchX;
                    deltaY = deltaX / mTargetAspectRatio;
                    resultTouchY = mCropViewRect.top + deltaY;
                    break;
                case 1:
                    deltaX = Math.abs(touchX - mCropViewRect.left);
                    deltaY = deltaX / mTargetAspectRatio;
                    resultTouchY = mCropViewRect.bottom - deltaY;
                    break;
                case 2:
                    deltaX = touchX - mCropViewRect.left;
                    deltaY = deltaX / mTargetAspectRatio;
                    resultTouchY = mCropViewRect.top + deltaY;
                    break;
            }
        }

        Log.i("overlay", "x:" + resultTouchX + ";  y:" + resultTouchY + "; ratio:" + mTargetAspectRatio);

        switch (mCurrentTouchCornerIndex) {
            case 0:
                mTempRect.set(resultTouchX, resultTouchY, mCropViewRect.right, mCropViewRect.bottom);
                break;
            case 1:
                mTempRect.set(mCropViewRect.left, resultTouchY, resultTouchX, mCropViewRect.bottom);
                break;
            case 2:
                mTempRect.set(mCropViewRect.left, mCropViewRect.top, resultTouchX, resultTouchY);
                break;
            case 3:
                mTempRect.set(resultTouchX, mCropViewRect.top, mCropViewRect.right, resultTouchY);
                break;
        }

        boolean heightChange;
        boolean widthChange;
        if(isFixTargetAspectRatio){
            boolean temp = (mTempRect.width()/mTempRect.height() == mTargetAspectRatio) && mTempRect.height()>=mCropRectMinSize && mTempRect.width()>= mCropRectMinSize;
            widthChange = heightChange = temp;
        }else{
            //高和宽在阀值内
            heightChange = mTempRect.height() >= mCropRectMinSize;
            widthChange = mTempRect.width() >= mCropRectMinSize;
        }

        mCropViewRect.set(
                widthChange ? mTempRect.left : mCropViewRect.left,
                heightChange ? mTempRect.top : mCropViewRect.top,
                widthChange ? mTempRect.right : mCropViewRect.right,
                heightChange ? mTempRect.bottom : mCropViewRect.bottom
        );

        if (heightChange || widthChange) {
            updateGridPoints();
            postInvalidate();
        }

    }

    /**
     * @date 创建时间 2018/3/3
     * @author Jiang zinc
     * @Description 获取当前触碰的点
     * 0------->1
     * ^        |
     * |   4    |
     * |        v
     * 3<-------2
     * @version
     */
    private int getCurrentTouchIndex(float touchX, float touchY) {
        int closestPointIndex = -1;

        double closestPointDistance = mTouchPointThreshold;

        for (int i = 0; i < 8; i += 2) {
            //通过勾股定理获取距离
            double distanceToCorner = Math.sqrt(Math.pow(touchX - mCropGridCorners[i], 2) + Math.pow(touchY - mCropGridCorners[i + 1], 2));
            if (distanceToCorner < closestPointDistance) {    //如果小于阀值，则保存此触碰点的下标和当前的距离
                closestPointDistance = distanceToCorner;
                closestPointIndex = i / 2;
            }
        }

        //如果触碰点不是四个角，但再截图范围内
        if (closestPointIndex < 0 && mCropViewRect.contains(touchX, touchY)) {
            return -1;
        }

        return closestPointIndex;
    }

    public void setTargetAspectRatio(final float targetAspectRatio) {
        mTargetAspectRatio = targetAspectRatio;

        if (mWidth > 0) {
            setupCropBounds();
            postInvalidate();
        } else {
            mShouldSetupCropBounds = true;
        }
    }

    /**
     * This method setups crop bounds rectangles for given aspect ratio and view size.
     * {@link #mCropViewRect} is used to draw crop bounds - uses padding.
     */
    public void setupCropBounds() {
        int height = (int) (mWidth / mTargetAspectRatio);
        if (height > mHeight) { //竖图
            int width = (int) (mHeight * mTargetAspectRatio);
            int halfDiff = (mWidth - width) / 2;
            mCropViewRect.set(getPaddingLeft() + halfDiff, getPaddingTop(),
                    getPaddingLeft() + width + halfDiff, getPaddingTop() + mHeight);
        } else {    //横图
            int halfDiff = (mHeight - height) / 2;
            mCropViewRect.set(getPaddingLeft(), getPaddingTop() + halfDiff,
                    getPaddingLeft() + mWidth, getPaddingTop() + height + halfDiff);
        }

        //更新在cropImageView中的截图区域
        if (mOverlayViewChangeListener != null) {
            mOverlayViewChangeListener.onCropRectUpdated(mCropViewRect);
        }

        updateGridPoints();
    }

    private void updateGridPoints() {
        mCropGridCorners = RectUtils.getCornersFromRect(mCropViewRect);
        mCropGridCenter = RectUtils.getCenterFromRect(mCropViewRect);

        mGridPoints = null;
        mCirclePath.reset();
        mCirclePath.addCircle(mCropViewRect.centerX(), mCropViewRect.centerY(),
                Math.min(mCropViewRect.width(), mCropViewRect.height()) / 2.f, Path.Direction.CW);
    }


    //画圆
    private void drawCropCircle(Canvas canvas) {
        canvas.drawCircle(mCropViewRect.centerX(), mCropViewRect.centerY(),
                Math.min(mCropViewRect.width(), mCropViewRect.height()) / 2.0f,
                mDimmedStrokePaint);
    }

    public void setFixTargetAspectRatio(boolean fixTargetAspectRatio) {
        isFixTargetAspectRatio = fixTargetAspectRatio;
    }
}
