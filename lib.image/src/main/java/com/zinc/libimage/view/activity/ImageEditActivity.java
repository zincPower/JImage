package com.zinc.libimage.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zinc.libimage.R;
import com.zinc.libimage.view.view.GestureImageView;
import com.zinc.libimage.view.view.JCropView;
import com.zinc.libimage.view.view.OverlayView;
import com.zinc.libimage.view.view.TransformImageView;
import com.zinc.libimage.widget.HorizontalProgressWheelView;

import java.util.Locale;

/**
 * @author Jiang zinc
 * @date 创建时间：2018/2/9
 * @description
 */

public class ImageEditActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout controlBar;
    private ImageView crop;
    private ImageView rotate;
    private RelativeLayout controlRotateBar;
    private ImageView resetRotate;
    private ImageView rightAngleRotate;
    private TextView rotateNum;
    private HorizontalProgressWheelView progressWheelRotate;
//    private CropImageView cropImageView;

    private JCropView mCropView;
    private GestureImageView mCropImageView;
    private OverlayView mOverlayView;

    //缩放控制
    private RelativeLayout controlScaleBar;
    private ImageView resetScale;
    private TextView scaleNum;
    private HorizontalProgressWheelView progressWheelScale;

    //旋转灵敏系数
    private int ROTATE_WIDGET_SENSITIVITY_COEFFICIENT = 30;
    //缩放的权重
    private static final int SCALE_WIDGET_SENSITIVITY_COEFFICIENT = 15000;


    @Override
    public int getLayoutId() {
        return R.layout.j_activity_image_edit;
    }

    @Override
    public void initView() {
        controlBar = findViewById(R.id.control_bar);
        crop = findViewById(R.id.crop);
        rotate = findViewById(R.id.rotate);
        controlRotateBar = findViewById(R.id.control_rotate_bar);
        resetRotate = findViewById(R.id.reset_rotate);
        rightAngleRotate = findViewById(R.id.right_angle_rotate);
        rotateNum = findViewById(R.id.rotate_num);
        progressWheelRotate = findViewById(R.id.progress_wheel_rotate);

        mCropView = findViewById(R.id.crop_view);
        mCropImageView = mCropView.getmGestureImageView();
        mOverlayView = mCropView.getmOverlayView();

        controlScaleBar = findViewById(R.id.control_scale_bar);
        resetScale = findViewById(R.id.reset_scale);
        scaleNum = findViewById(R.id.scale_num);
        progressWheelScale = findViewById(R.id.progress_wheel_scale);

    }

    @Override
    public void initData() {

        rightAngleRotate.setOnClickListener(this);
        resetRotate.setOnClickListener(this);
        resetScale.setOnClickListener(this);

        progressWheelRotate.setScrollingListener(new HorizontalProgressWheelView.ScrollingListener() {
            @Override
            public void onScrollStart() {

            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onScroll(float delta, float totalDistance) {
                mCropImageView.postRotate(delta / ROTATE_WIDGET_SENSITIVITY_COEFFICIENT);
            }

            @Override
            public void onScrollEnd() {

            }
        });

        progressWheelScale.setScrollingListener(new HorizontalProgressWheelView.ScrollingListener() {
            @Override
            public void onScrollStart() {

            }

            @Override
            public void onScroll(float delta, float totalDistance) {
                if (delta > 0) {
                    mCropImageView.zoomInImage(mCropImageView.getCurrentScale()
                            + delta * ((mCropImageView.getMaxScale() - mCropImageView.getMinScale()) / SCALE_WIDGET_SENSITIVITY_COEFFICIENT));
                } else {
                    mCropImageView.zoomOutImage(mCropImageView.getCurrentScale()
                            + delta * ((mCropImageView.getMaxScale() - mCropImageView.getMinScale()) / SCALE_WIDGET_SENSITIVITY_COEFFICIENT));
                }
            }

            @Override
            public void onScrollEnd() {

            }
        });

//        String url = "content://com.android.providers.media.documents/document/image%3A56248";
        String url = "content://media/external/images/media/469781";
        try {
            mCropImageView.setImageUri(Uri.parse(url), null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mCropImageView.setmTransformImageListener(mImageListener);
        mOverlayView.setTargetAspectRatio(1.77f);

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void initIntent(Intent intent) {

    }

    /**
     * @date 创建时间 2018/2/9
     * @author Jiang zinc
     * @Description
     * @version
     */
    private void setAngleText(float angle) {
        if (rotateNum != null) {
            rotateNum.setText(String.format(Locale.getDefault(), "%.1f°", angle));
        }
    }

    private void setScaleText(float scale) {
        if (scaleNum != null) {
            scaleNum.setText(String.format(Locale.getDefault(), "%d%%", (int) (scale * 100)));
        }
    }

    private TransformImageView.TransformImageListener mImageListener = new TransformImageView.TransformImageListener() {
        @Override
        public void onRotate(float currentAngle) {
            setAngleText(currentAngle);
        }

        @Override
        public void onScale(float currentScale) {
            setScaleText(currentScale);
        }

        @Override
        public void onLoadComplete() {

            supportInvalidateOptionsMenu();
        }

        @Override
        public void onLoadFailure(@NonNull Exception e) {
//            setResultError(e);
            finish();
        }

    };

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.right_angle_rotate) { //90度旋转
            mCropImageView.postRotate(90);
        } else if (i == R.id.reset_rotate) {   //重置度数
            mCropImageView.resetRotate();
        } else if (i == R.id.reset_scale) {   //重置缩放
//            mCropImageView.resetScale();
            mCropView.cropAndSave();
        }
    }
}
