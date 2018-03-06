package com.zinc.libimage.view.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import com.zinc.libimage.utils.BitmapLoadCallback;
import com.zinc.libimage.utils.BitmapLoadUtils;
import com.zinc.libimage.utils.ExifInfo;
import com.zinc.libimage.utils.RectUtils;
import com.zinc.libimage.widget.FastBitmapDrawable;

/**
 * @author Jiang zinc
 * @date 创建时间：2018/2/10
 * @description 基础的图片操作，包括旋转、缩放、平移，同时获取matrix的一些状态
 */

public class TransformImageView extends android.support.v7.widget.AppCompatImageView {

    private static final String TAG = "CropImageView";

    private static final int RECT_CORNER_POINTS_COORDS = 8;
    private static final int RECT_CENTER_POINT_COORDS = 2;
    protected final float[] mCurrentImageCorners = new float[RECT_CORNER_POINTS_COORDS];

    protected TransformImageListener mTransformImageListener;

    protected Matrix mCurrentImageMatrix = new Matrix();
    private static final int MATRIX_VALUES_COUNT = 9;
    private final float[] mMatrixValues = new float[MATRIX_VALUES_COUNT];
    protected final float[] mCurrentImageCenter = new float[RECT_CENTER_POINT_COORDS];

    private float[] mInitialImageCorners;
    private float[] mInitialImageCenter;

    private int mMaxBitmapSize = 0;

    protected int mViewWidth;
    protected int mViewHeight;

    protected boolean mBitmapLaidOut = false;
    private boolean mBitmapDecoded = false;

    public TransformImageView(Context context) {
        this(context, null, 0);
    }

    public TransformImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransformImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //重要！！ 如果没有设置为matrix，后续操作会没有作用
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType == ScaleType.MATRIX) {
            super.setScaleType(scaleType);
        } else {
            Log.w(TAG, "初始化 ScaleType. 只能使用ScaleType.Matrix");
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        setImageDrawable(new FastBitmapDrawable(bm));
    }

    public void setmTransformImageListener(TransformImageListener mTransformImageListener) {
        this.mTransformImageListener = mTransformImageListener;
    }

    public float[] getmCurrentImageCenter() {
        return mCurrentImageCenter;
    }

    /**
     * This method takes an Uri as a parameter, then calls method to decode it into Bitmap with specified size.
     *
     * @param imageUri - image Uri
     * @throws Exception - can throw exception if having problems with decoding Uri or OOM.
     */
    public void setImageUri(@NonNull Uri imageUri, @Nullable Uri outputUri) throws Exception {
        int maxBitmapSize = getMaxBitmapSize();

        BitmapLoadUtils.decodeBitmapInBackground(getContext(), imageUri, outputUri, maxBitmapSize, maxBitmapSize,
                new BitmapLoadCallback() {

                    @Override
                    public void onBitmapLoaded(@NonNull Bitmap bitmap, @NonNull ExifInfo exifInfo,
                                               @NonNull String imageInputPath, @Nullable String imageOutputPath) {

                        mBitmapDecoded = true;
                        setImageBitmap(bitmap);
                    }

                    @Override
                    public void onFailure(@NonNull Exception bitmapWorkerException) {
                        Log.e(TAG, "onFailure: setImageUri", bitmapWorkerException);
                        if (mTransformImageListener != null) {
                            mTransformImageListener.onLoadFailure(bitmapWorkerException);
                        }
                    }
                });
    }

    /**
     * @param changed This is a new size or position for this view
     * @param left    Left position, relative to parent
     * @param top     Top position, relative to parent
     * @param right   Right position, relative to parent
     * @param bottom  Bottom position, relative to parent
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        /**
         * 此处需要重绘的条件（满足其一）：
         * 1、视图尺寸和位置发生变化
         * 2、已经有图片加载、未显示至ImageView（这个条件必须有，否则只会第一次显示时使用此逻辑）
         */
        if (changed || (mBitmapDecoded && !mBitmapLaidOut)) {

            left = getPaddingLeft();
            top = getPaddingTop();
            right = getWidth() - getPaddingRight();
            bottom = getHeight() - getPaddingBottom();
            //获取视图的宽、高
            mViewWidth = right - left;
            mViewHeight = bottom - top;

            onImageLaidOut();
        }

    }

    protected void onImageLaidOut() {
        final Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        float w = drawable.getIntrinsicWidth();
        float h = drawable.getIntrinsicHeight();

        Log.d(TAG, String.format("Image size: [%d:%d]", (int) w, (int) h));

        //获取图片的四个角的坐标、中心坐标
        RectF initialImageRect = new RectF(0, 0, w, h);
        mInitialImageCorners = RectUtils.getCornersFromRect(initialImageRect);
        mInitialImageCenter = RectUtils.getCenterFromRect(initialImageRect);

        mBitmapLaidOut = true;

        if (mTransformImageListener != null) {
            mTransformImageListener.onLoadComplete();
        }

    }

    public int getMaxBitmapSize() {
        if (mMaxBitmapSize <= 0) {
            mMaxBitmapSize = BitmapLoadUtils.calculateMaxBitmapSize(getContext());
        }
        return mMaxBitmapSize;
    }

    /**
     * 获取matrix的旋转角度
     * <p>
     * todo 这里的公式不太懂
     */
    public float getMatrixAngle(@NonNull Matrix matrix) {
        return (float) -(Math.atan2(getMatrixValue(matrix, Matrix.MSKEW_X),
                getMatrixValue(matrix, Matrix.MSCALE_X)) * (180 / Math.PI));
    }

    /**
     * 返回对应矩阵值
     * <p>
     * --                                       --
     * |    MSCALE_X    MSKEW_X     MTRANS_X     |
     * |    MSKEW_Y     MSCALE_Y    MTRANS_Y     |
     * |    MPERSP_0    MPERSP_1    MPERSP_2     |
     * --                                       --
     *
     * @param matrix     - valid Matrix object
     * @param valueIndex - index of needed value. See {@link Matrix#MSCALE_X} and others.
     * @return - matrix value for index
     */
    protected float getMatrixValue(@NonNull Matrix matrix, @IntRange(from = 0, to = MATRIX_VALUES_COUNT) int valueIndex) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[valueIndex];
    }

    /**
     * 设置image的matrix
     *
     * @param matrix
     */
    @Override
    public void setImageMatrix(Matrix matrix) {
        super.setImageMatrix(matrix);
        mCurrentImageMatrix.set(matrix);
        updateCurrentImagePoints();
    }

    /**
     * 刷新图片的角和中心 坐标
     */
    private void updateCurrentImagePoints() {
        /**
         * mapPoints(float[] dst, float[] src)  函数意义是：
         *  将src按照matrix的缩放、平移、斜切等操作映射至dst
         */
        mCurrentImageMatrix.mapPoints(mCurrentImageCorners, mInitialImageCorners);
        mCurrentImageMatrix.mapPoints(mCurrentImageCenter, mInitialImageCenter);
    }

    /**
     * 用于平移
     *
     * @param deltaX - horizontal shift
     * @param deltaY - vertical shift
     */
    public void postTranslate(float deltaX, float deltaY) {
        if (deltaX != 0 || deltaY != 0) {
            mCurrentImageMatrix.postTranslate(deltaX, deltaY);
            setImageMatrix(mCurrentImageMatrix);
        }
    }

    /**
     * 缩放图片，同时更新matrix
     *
     * @param deltaScale - scale value
     * @param px         - scale center X
     * @param py         - scale center Y
     */
    public void postResultScale(float deltaScale, float px, float py) {
        if (deltaScale != 0) {
            mCurrentImageMatrix.postScale(deltaScale, deltaScale, px, py);
            setImageMatrix(mCurrentImageMatrix);
            if (mTransformImageListener != null) {
                mTransformImageListener.onScale(getMatrixScale(mCurrentImageMatrix));
            }
        }
    }

    /**
     * @return - 当前图片的缩放值.
     * [1.0f - 原图, 2.0f - 200%的图片, etc.]
     */
    public float getCurrentScale() {
        return getMatrixScale(mCurrentImageMatrix);
    }

    /**
     * @return - 当前图片的旋转角度
     */
    public float getCurrentAngle() {
        return getMatrixAngle(mCurrentImageMatrix);
    }

    /**
     * @date 创建时间 2018/2/18
     * @author Jiang zinc
     * @Description 获取矩阵的缩放值
     * @version
     */
    public float getMatrixScale(@NonNull Matrix matrix) {
        return (float) Math.sqrt(Math.pow(getMatrixValue(matrix, Matrix.MSCALE_X), 2)
                + Math.pow(getMatrixValue(matrix, Matrix.MSKEW_Y), 2));
    }


    public interface TransformImageListener {

        void onLoadComplete();

        void onLoadFailure(@NonNull Exception e);

        void onRotate(float currentAngle);

        void onScale(float currentScale);

    }

}
