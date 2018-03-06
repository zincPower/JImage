package com.zinc.libimage.view.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.zinc.libimage.anim.WrapCropBoundsRunnable;
import com.zinc.libimage.utils.RectUtils;
import com.zinc.libimage.utils.UIUtil;
import com.zinc.libimage.widget.FastBitmapDrawable;

import java.util.Arrays;

/**
 * @author Jiang zinc
 * @date 创建时间：2018/3/5
 * @description 带裁剪功能的imageview
 */

public class CropImageView extends TransformImageView {

    private static final String TAG = "CropImageView";

    public static final float SOURCE_IMAGE_ASPECT_RATIO = 0f;
    public static final long DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION = 500;

    private Matrix mTempMatrix = new Matrix();

    //裁剪区域
    private final RectF mCropRect = new RectF();

    public static final float DEFAULT_MAX_SCALE_MULTIPLIER = 10.0f;
    private float mMaxScaleMultiplier = DEFAULT_MAX_SCALE_MULTIPLIER;
    private float mMaxScale, mMinScale;

    //截图比例
    private float mTargetAspectRatio;

    protected WrapCropBoundsRunnable mWrapCropBoundsRunnable;

    private long mImageToWrapCropBoundsAnimDuration = DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION;

    public CropImageView(Context context) {
        this(context, null, 0);
    }

    public CropImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RectF getmCropRect() {
        return mCropRect;
    }

    @Override
    protected void onImageLaidOut() {
        super.onImageLaidOut();

        final Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        float w = drawable.getIntrinsicWidth();
        float h = drawable.getIntrinsicHeight();

        int height = (int) (mViewWidth / mTargetAspectRatio);

        mTargetAspectRatio = w / h;

        //判断标准：mViewWidth/mViewHeight > w/h
        if (height > mViewHeight) {         //竖图
            int width = (int) (mViewHeight * mTargetAspectRatio);
            int halfDiff = (mViewWidth - width) / 2;
            mCropRect.set(halfDiff, 0, width + halfDiff, mViewHeight);
        } else {                            //横图
            int halfDiff = (mViewHeight - height) / 2;
            mCropRect.set(0, halfDiff, mViewWidth, height + halfDiff);
        }

        //获取横竖缩放权
        float widthScale = mCropRect.width() / drawable.getIntrinsicWidth();
        float heightScale = mCropRect.height() / drawable.getIntrinsicHeight();

        //取横竖大的为缩放值，否则会超出可是范围
        float initialMinScale = Math.max(widthScale, heightScale);
        //第一步： 进行缩放
        mCurrentImageMatrix.postScale(initialMinScale, initialMinScale);

        calculateImageScaleBounds(w, h);

    }

    public boolean isImageWrapCropBounds() {
        return isImageWrapCropBounds(mCurrentImageCorners);
    }

    /**
     * 检测传入的图片是否有包含裁剪区域
     *
     * @param imageCorners 变动的图片角
     * @return
     */
    protected boolean isImageWrapCropBounds(float[] imageCorners) {
        mTempMatrix.reset();
        mTempMatrix.setRotate(-getCurrentAngle());

        //将图片摆正
        float[] unrotatedImageCorners = Arrays.copyOf(imageCorners, imageCorners.length);
        mTempMatrix.mapPoints(unrotatedImageCorners);

        //让截图反旋转 TODO 这里还有疑惑，需要在看
        float[] unrotatedCropBoundsCorners = RectUtils.getCornersFromRect(mCropRect);
        mTempMatrix.mapPoints(unrotatedCropBoundsCorners);

        return RectUtils.trapToRect(unrotatedImageCorners).contains(RectUtils.trapToRect(unrotatedCropBoundsCorners));

    }

    //===================================缩放方法 start===================================

    /**
     * @date 创建时间 2018/2/18
     * @author Jiang zinc
     * @Description 以图片的中心进行放大
     * @version
     */
    public void zoomInImage(float deltaScale) {
        zoomInImage(deltaScale, mCropRect.centerX(), mCropRect.centerY());
    }

    /**
     * @date 创建时间 2018/2/18
     * @author Jiang zinc
     * @Description 以图片的中心进行缩小
     * @version
     */
    public void zoomOutImage(float deltaScale) {
        zoomOutImage(deltaScale, mCropRect.centerX(), mCropRect.centerY());
    }

    /**
     * @date 创建时间 2018/2/18
     * @author Jiang zinc
     * @Description 按照centerX，centerY进行缩小
     * @version
     */
    public void zoomOutImage(float scale, float centerX, float centerY) {
        if (scale >= getMinScale()) {
            postScale(scale / getCurrentScale(), centerX, centerY);
        }
    }

    /**
     * @date 创建时间 2018/2/18
     * @author Jiang zinc
     * @Description 按照centerX，centerY进行放大
     * @version
     */
    public void zoomInImage(float scale, float centerX, float centerY) {
        if (scale <= getMaxScale()) {
            postScale(scale / getCurrentScale(), centerX, centerY);
        }
    }

    /**
     * 以点（px,py）为中心，进行缩放，同时需要在缩放的最大和最小值中
     *
     * @param deltaScale - scale value
     * @param px         - scale center X
     * @param py         - scale center Y
     */
    public void postScale(float deltaScale, float px, float py) {
        if (deltaScale > 1 && getCurrentScale() * deltaScale <= getMaxScale()) {
            postResultScale(deltaScale, px, py);
        } else if (deltaScale < 1 && getCurrentScale() * deltaScale >= getMinScale()) {
            postResultScale(deltaScale, px, py);
        }
    }

    //===================================缩放方法 end===================================
    //===================================旋转方法 start=================================

    /**
     * @date 创建时间 2018/2/18
     * @author Jiang zinc
     * @Description 设置旋转角度
     * @version
     */
    public void postRotate(float deltaAngle) {
        postRotate(deltaAngle, mCropRect.centerX(), mCropRect.centerY());
    }

    public void postRotate(float deltaAngle, float px, float py) {
        if (deltaAngle != 0) {
            mCurrentImageMatrix.postRotate(deltaAngle, px, py);
            setImageMatrix(mCurrentImageMatrix);
            if (mTransformImageListener != null) {
                mTransformImageListener.onRotate(getMatrixAngle(mCurrentImageMatrix));
            }
        }
    }

    /**
     * @date 创建时间 2018/2/18
     * @author Jiang zinc
     * @Description 重置度数
     * @version
     */
    public void resetRotate() {
        postRotate(-getMatrixAngle(mCurrentImageMatrix));
    }

    //===================================旋转方法 end===================================

    /**
     * @date 创建时间 2018/3/5
     * @author Jiang zinc
     * @Description 设置裁剪区域，由{@link JCropView#setListenersToViews()}调用
     * @version
     */
    public void setCropRect(RectF rectF) {
        //1、计算截图比例
        mTargetAspectRatio = rectF.width() / rectF.height();
        //2、更新截图区域
        mCropRect.set(rectF.left - getPaddingLeft(),
                rectF.top - getPaddingTop(),
                rectF.right - getPaddingRight(),
                rectF.bottom - getPaddingBottom());
        //3、刷新缩放范围
        if (getDrawable() == null) {
            return;
        }
        calculateImageScaleBounds(getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
        //4、进行平移、缩放；但不用旋转
        setImageToWrapCropBounds(true);
    }

    /**
     * 获取最大和最小的缩放值
     *
     * @param drawableWidth  - image width
     * @param drawableHeight - image height
     */
    private void calculateImageScaleBounds(float drawableWidth, float drawableHeight) {
        float widthScale = Math.min(mCropRect.width() / drawableWidth, mCropRect.width() / drawableHeight);
        float heightScale = Math.min(mCropRect.height() / drawableHeight, mCropRect.height() / drawableWidth);

        mMinScale = Math.min(widthScale, heightScale);
        mMaxScale = mMinScale * mMaxScaleMultiplier;
    }

    public void setImageToWrapCropBounds() {
        setImageToWrapCropBounds(true);
    }

    /**
     * @date 创建时间 2018/2/18
     * @author Jiang zinc
     * @Description 计算平移距离使其居中
     * @version
     */
    public void setImageToWrapCropBounds(boolean animate) {

        //图片已加载完且图片超出范围
        if (mBitmapLaidOut && !isImageWrapCropBounds()) {

            //获取当前图片的中心点坐标、缩放
            float currentX = mCurrentImageCenter[0];
            float currentY = mCurrentImageCenter[1];
            float currentScale = getCurrentScale();

            //计算增量
            float deltaX = mCropRect.centerX() - currentX;
            float deltaY = mCropRect.centerY() - currentY;
            float deltaScale = 0;

            mTempMatrix.reset();
            mTempMatrix.setTranslate(deltaX, deltaY);

            //对当前的图片进行平移
            float[] tempCurrentImageCorners = Arrays.copyOf(mCurrentImageCorners, mCurrentImageCorners.length);
            mTempMatrix.mapPoints(tempCurrentImageCorners);

            //判断平移后的图片是否包含了截图区域
            boolean willImageWrapCropBoundsAfterTranslate = isImageWrapCropBounds(tempCurrentImageCorners);

            if (willImageWrapCropBoundsAfterTranslate) {      //新区域包含截图区域
                float[] imageIndents = calculateImageIndents();
                deltaX = -(imageIndents[0] + imageIndents[2]);
                deltaY = -(imageIndents[1] + imageIndents[3]);
            } else {
                RectF tempCropRect = new RectF(mCropRect);
                mTempMatrix.reset();
                mTempMatrix.setRotate(getCurrentAngle());
                mTempMatrix.mapRect(tempCropRect);

                float[] currentImageSides = RectUtils.getRectSidesFromCorners(mCurrentImageCorners);

                deltaScale = Math.max(tempCropRect.width() / currentImageSides[0],
                        tempCropRect.height() / currentImageSides[1]);

                deltaScale = deltaScale * currentScale - currentScale;
            }

            if (animate) {
                post(mWrapCropBoundsRunnable = new WrapCropBoundsRunnable(CropImageView.this, mImageToWrapCropBoundsAnimDuration,
                        currentX, currentY, deltaX, deltaY,
                        currentScale, deltaScale, willImageWrapCropBoundsAfterTranslate));
            } else {
                // 第二步： 图片居中
                postTranslate(deltaX, deltaY);
                if (!willImageWrapCropBoundsAfterTranslate) {
                    zoomInImage(currentScale + deltaScale, mCropRect.centerX(), mCropRect.centerY());
                }
            }

        }

    }

    /**
     * @return 图像缩进浮点数组
     * @date 创建时间 2018/3/5
     * @author Jiang zinc
     * @Description 获取图像缩进浮点数组
     * 第一步：将图片和截图区域反向旋转
     * 第二步：计算偏移
     * 第三步：将大于零的偏移放进数组，小于就将零放进数组
     * 第四步：旋转数组
     * @version
     */
    private float[] calculateImageIndents() {
        mTempMatrix.reset();
        mTempMatrix.setRotate(-getCurrentAngle());

        float[] unrotatedImageCorners = Arrays.copyOf(mCurrentImageCorners, mCurrentImageCorners.length);
        float[] unrotatedCropBoundsCorners = RectUtils.getCornersFromRect(mCropRect);

        mTempMatrix.mapPoints(unrotatedImageCorners);
        mTempMatrix.mapPoints(unrotatedCropBoundsCorners);

        RectF unrotatedImageRect = RectUtils.trapToRect(unrotatedImageCorners);
        RectF unrotatedCropRect = RectUtils.trapToRect(unrotatedCropBoundsCorners);

        float deltaLeft = unrotatedImageRect.left - unrotatedCropRect.left;
        float deltaTop = unrotatedImageRect.top - unrotatedCropRect.top;
        float deltaRight = unrotatedImageRect.right - unrotatedCropRect.right;
        float deltaBottom = unrotatedImageRect.bottom - unrotatedCropRect.bottom;

        float indents[] = new float[4];
        indents[0] = (deltaLeft > 0) ? deltaLeft : 0;
        indents[1] = (deltaTop > 0) ? deltaTop : 0;
        indents[2] = (deltaRight < 0) ? deltaRight : 0;
        indents[3] = (deltaBottom < 0) ? deltaBottom : 0;

        mTempMatrix.reset();
        mTempMatrix.setRotate(getCurrentAngle());
        mTempMatrix.mapPoints(indents);

        return indents;
    }

    /**
     * @return - 获取缩放的最大值
     */
    public float getMaxScale() {
        return mMaxScale;
    }

    /**
     * @return - 获取缩放的最小值
     */
    public float getMinScale() {
        return mMinScale;
    }

    public void resetScale() {
        //获取横竖缩放权
        float widthScale = mCropRect.width() / getDrawable().getIntrinsicWidth();
        float heightScale = mCropRect.height() / getDrawable().getIntrinsicHeight();

        //取横竖大的为缩放值，否则会超出可是范围
        float initialMinScale = Math.max(widthScale, heightScale);
        //第一步： 进行缩放
        mCurrentImageMatrix.postScale(initialMinScale, initialMinScale);

        setImageMatrix(mCurrentImageMatrix);
    }

    public Bitmap cropAndSave() {

        mTempMatrix.reset();
        mTempMatrix.setRotate(-getCurrentAngle());

        float[] unrotatedImageCorners = Arrays.copyOf(mCurrentImageCorners, mCurrentImageCorners.length);
        mTempMatrix.mapPoints(unrotatedImageCorners);

        float[] unrotatedCropBoundsCorners = RectUtils.getCornersFromRect(mCropRect);
        float orgX = unrotatedCropBoundsCorners[0];     //原来X
        float orgY = unrotatedCropBoundsCorners[1];     //原来Y
        mTempMatrix.mapPoints(unrotatedCropBoundsCorners);

        float offsetX = orgX - mCurrentImageCorners[0]; //偏移的x
        float offsetY = orgY - mCurrentImageCorners[1]; //偏移的y

        Bitmap bitmap = ((FastBitmapDrawable) getDrawable()).getBitmap();
        Bitmap output = Bitmap.createBitmap((int) mCropRect.width(), (int) mCropRect.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.translate(-offsetX, -offsetY);
        canvas.save();
        canvas.rotate(getCurrentAngle());

        canvas.drawARGB(0, 0, 0, 0);

        Paint paint = new Paint();

        Path path = new Path();
        path.moveTo(unrotatedCropBoundsCorners[0] - unrotatedImageCorners[0], unrotatedCropBoundsCorners[1] - unrotatedImageCorners[1]);
        path.lineTo(unrotatedCropBoundsCorners[2] - unrotatedImageCorners[0], unrotatedCropBoundsCorners[3] - unrotatedImageCorners[1]);
        path.lineTo(unrotatedCropBoundsCorners[4] - unrotatedImageCorners[0], unrotatedCropBoundsCorners[5] - unrotatedImageCorners[1]);
        path.lineTo(unrotatedCropBoundsCorners[6] - unrotatedImageCorners[0], unrotatedCropBoundsCorners[7] - unrotatedImageCorners[1]);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawPath(path, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        Rect orgRect = new Rect(0, 0, getWidth(), getHeight());
        RectF resultRect = new RectF(0, 0, getWidth() * getCurrentScale(), getHeight() * getCurrentScale());

        canvas.drawBitmap(bitmap, orgRect, resultRect, paint);

        canvas.restore();

        return output;
    }

}