package com.zinc.jimage.hugeImage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Jiang zinc
 * @date 创建时间：2018/2/8
 * @description 长图显示View
 */

public class PiiicView extends View implements GestureDetector.OnGestureListener, View.OnTouchListener {

    private static final String TAG = PiiicView.class.getSimpleName();
    //用于存储图片的原始宽、高
    int mImageWidth;
    int mImageHeight;
    BitmapRegionDecoder mDecoder;

    //view的宽、高
    int mViewWidth;

    int mViewHeight;
    //加载区域
    Rect mRect;

    //缩放因子 scale = mViewWidth/mImageWidth;
    //因为要按照宽缩放的比例，所以高其实到最终达到目的是： mViewHeight/h=mViewWidth/mImageWidth;===>
    float mScale;

    BitmapFactory.Options mOptions;

    Bitmap mBitmap;

    GestureDetector mGestureDetector;
    private Scroller mScroller;

    public PiiicView(Context context) {
        this(context, null, 0);
    }

    public PiiicView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PiiicView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mRect = new Rect();
        mOptions = new BitmapFactory.Options();

        //创建手势识别
        mGestureDetector = new GestureDetector(context, this);
        //接受触摸事件
        setOnTouchListener(this);
        //滑动帮助
        mScroller = new Scroller(context);
    }

    public void setImage(InputStream is) {

        //设置为只读取长、宽
        mOptions.inJustDecodeBounds = true;
        mBitmap = BitmapFactory.decodeStream(is, null, mOptions);
        mImageWidth = mOptions.outWidth;
        mImageHeight = mOptions.outHeight;

        //设置图片像素为RGB_565
        mOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        mOptions.inJustDecodeBounds = false;
        //设置为true可复用
        mOptions.inMutable = true;

        try {
            //第二个参数为是否为共享：true，会浅引用inputstream，即inputstream的关闭或操作会影响decoder；
            //                      false，深引用inputstream，即会拷贝一份（效率会高些）
            mDecoder = BitmapRegionDecoder.newInstance(is, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //绘制
        requestLayout();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //如果没有进行先设置图片，则返回
        if (mDecoder == null) {
            return;
        }

        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();

        //按照左上右下设置
        mRect.left = 0;
        mRect.top = 0;
        mRect.right = mImageWidth;   //此处应为图片宽，否则会无法在屏幕中显示全
        mScale = mViewWidth / (float) mImageWidth;
        mRect.bottom = (int) (mViewHeight / mScale);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //如果没有进行先设置图片，则返回
        if (mDecoder == null) {
            return;
        }

        //复用上一张
        mOptions.inBitmap = mBitmap;
        //指定解码区域
        mBitmap = mDecoder.decodeRegion(mRect, mOptions);
        Matrix matrix = new Matrix();
        matrix.setScale(mScale, mScale);

        canvas.drawBitmap(mBitmap, matrix, null);

    }

    /**
     * @date 创建时间 2018/2/14
     * @author Jiang zinc
     * @Description 手指按下屏幕的回调
     * @version
     */
    @Override
    public boolean onDown(MotionEvent e) {
        //如果滑动事件还未停止，当下一个按压事件到来时，进行强制停止
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
        //需要对事件进行拦截，才能进行后续操作
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    /**
     * @param e1        手指按下去的事件   ——  可用于获取手指按下去的时候的坐标
     * @param e2        当前事件   ——  可用于获取当前坐标
     * @param distanceX x轴移动的距离
     * @param distanceY y轴移动的距离
     * @date 创建时间 2018/2/14
     * @author Jiang zinc
     * @Description 手指不离开屏幕滑动
     * @version
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        Log.i(TAG, "onScroll y:" + distanceY);

        //改变图片加载区域
        mRect.offset(0, (int) distanceY);

        //加载区域需要在范围内
        //加载区域的最低需要高于图片最高
        if (mRect.bottom > mImageHeight) {  //TODO 这里感觉有bug,需要后期验证（用窄长图和宽长图试）
            mRect.top = mImageHeight - (int) (mViewHeight / mScale);
            mRect.bottom = mImageHeight;
        }
        //加载区域的最高需要小于0
        if (mRect.top < 0) {
            mRect.top = 0;
            mRect.bottom = (int) (mViewHeight / mScale);
        }
        //重绘
        invalidate();
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    /**
     * @param e1        手指按下去的事件   ——  可用于获取手指按下去的时候的坐标
     * @param e2        当前事件   ——  可用于获取当前坐标
     * @param velocityX 每秒x轴移动的像素
     * @param velocityY 每秒y轴移动的像素
     * @date 创建时间 2018/2/14
     * @author Jiang zinc
     * @Description 手指离开屏幕，带惯性
     * @version
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        /**
         int startX, 滑动开始x轴
         int startY, 滑动开始y轴
         int velocityX, x轴速度
         int velocityY, y轴速度
         int minX,  x轴最小值
         int maxX,  x轴最大值
         int minY,  y轴最小值
         int maxY   y轴最大值
         */
        //这里是针对竖图
        //此处只是作为计算器，用于计算数值，结果由computeScroll获取
        mScroller.fling(0, mRect.top,
                0, (int) -velocityY,
                0, 0,
                0, mImageHeight - (int) (mViewHeight / mScale));

        return false;
    }

    @Override
    public void computeScroll() {
        if (mScroller.isFinished()) {
            return;
        }
        //为true时，说明正在动画中
        if (mScroller.computeScrollOffset()) {
            mRect.top = mScroller.getCurrY();
            mRect.bottom = mScroller.getCurrY() + (int) (mViewHeight / mScale);
            invalidate();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //让手势来处理触摸事件，可以省去一些判断
        return mGestureDetector.onTouchEvent(event);
    }
}
