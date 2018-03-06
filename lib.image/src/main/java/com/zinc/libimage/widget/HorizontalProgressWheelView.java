package com.zinc.libimage.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.zinc.libimage.R;

/**
 *
 * @date 创建时间：2018/2/9
 * @author Oleksii Shliama (https://github.com/shliama).
 * @description 横向刻度 todo 需要处理中轴线问题
 * @modify Jiang zinc 56002982@qq.com
 */

public class HorizontalProgressWheelView extends View {

    private final Rect mCanvasClipBounds = new Rect();

    private ScrollingListener mScrollingListener;
    private float mLastTouchedPosition;

    private Paint mProgressLinePaint;
    private int mProgressLineWidth, mProgressLineHeight;
    private int mProgressLineMargin;

    private boolean mScrollStarted;
    private float mTotalScrollDistance;

    private int mMiddleLineColor;

    public HorizontalProgressWheelView(Context context) {
        this(context, null);
    }

    public HorizontalProgressWheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalProgressWheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HorizontalProgressWheelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setScrollingListener(ScrollingListener scrollingListener) {
        mScrollingListener = scrollingListener;
    }

    public void setMiddleLineColor(@ColorInt int middleLineColor) {
        mMiddleLineColor = middleLineColor;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchedPosition = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                if (mScrollingListener != null) {
                    mScrollStarted = false;
                    mScrollingListener.onScrollEnd();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //获取一次触控的距离
                float distance = event.getX() - mLastTouchedPosition;
                if (distance != 0) {
                    //第一次滑动调
                    if (!mScrollStarted) {
                        mScrollStarted = true;
                        if (mScrollingListener != null) {
                            mScrollingListener.onScrollStart();
                        }
                    }
                    onScrollEvent(event, distance);
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //获取canvas可画区域rect
        canvas.getClipBounds(mCanvasClipBounds);

        //通过  canvas区域／（刻度宽度+刻度margin）  获取刻度数
        int linesCount = mCanvasClipBounds.width() / (mProgressLineWidth + mProgressLineMargin);

        float deltaX = (mTotalScrollDistance) % (float) (mProgressLineMargin + mProgressLineWidth);

        //设置刻度颜色
        mProgressLinePaint.setColor(getResources().getColor(R.color.jimage_progress_wheel_line_color));

        //绘制刻度
        //渐变效果：
        //      前1／4 => 255（即透明度alpha）* ( i（第几条）/ linesCount/4 )
        //      中间2／4 => 255
        //      后1／4 => 255（即透明度alpha）*（ linesCount-i（最后1／4的第几条） ／ linesCount/4 ）
        for (int i = 0; i < linesCount; i++) {
            if (i < (linesCount / 4)) {
                mProgressLinePaint.setAlpha((int) (255 * (i / (float) (linesCount / 4))));
            } else if (i > (linesCount * 3 / 4)) {
                mProgressLinePaint.setAlpha((int) (255 * ((linesCount - i) / (float) (linesCount / 4))));
            } else {
                mProgressLinePaint.setAlpha(255);
            }
            canvas.drawLine(
                    -deltaX + mCanvasClipBounds.left + i * (mProgressLineWidth + mProgressLineMargin),
                    mCanvasClipBounds.centerY() - mProgressLineHeight / 4.0f,
                    -deltaX + mCanvasClipBounds.left + i * (mProgressLineWidth + mProgressLineMargin),
                    mCanvasClipBounds.centerY() + mProgressLineHeight / 4.0f, mProgressLinePaint);
        }

        //画中轴线
        mProgressLinePaint.setColor(mMiddleLineColor);
        canvas.drawLine(mCanvasClipBounds.centerX(),
                mCanvasClipBounds.centerY() - mProgressLineHeight / 2.0f,
                mCanvasClipBounds.centerX(),
                mCanvasClipBounds.centerY() + mProgressLineHeight / 2.0f, mProgressLinePaint);

    }

    private void onScrollEvent(MotionEvent event, float distance) {
        mTotalScrollDistance -= distance;
        postInvalidate();
        mLastTouchedPosition = event.getX();
        if (mScrollingListener != null) {
            mScrollingListener.onScroll(-distance, mTotalScrollDistance);
        }
    }

    private void init() {
        //中轴刻度颜色
        mMiddleLineColor = ContextCompat.getColor(getContext(), R.color.jimage_progress_wheel_line_color);

        //刻度宽
        mProgressLineWidth = getContext().getResources().getDimensionPixelSize(R.dimen.jimage_width_horizontal_wheel_progress_line);
        //刻度高
        mProgressLineHeight = getContext().getResources().getDimensionPixelSize(R.dimen.jimage_height_horizontal_wheel_progress_line);
        //刻度margin
        mProgressLineMargin = getContext().getResources().getDimensionPixelSize(R.dimen.jimage_margin_horizontal_wheel_progress_line);

        //设置画笔
        mProgressLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressLinePaint.setStyle(Paint.Style.STROKE);
        mProgressLinePaint.setStrokeWidth(mProgressLineWidth);
    }

    public interface ScrollingListener {

        void onScrollStart();

        void onScroll(float delta, float totalDistance);

        void onScrollEnd();
    }

}
