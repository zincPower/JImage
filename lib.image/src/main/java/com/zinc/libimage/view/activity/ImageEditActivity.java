package com.zinc.libimage.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zinc.libimage.R;
import com.zinc.libimage.adapter.ControlBarAdapter;
import com.zinc.libimage.config.JImageConfig;
import com.zinc.libimage.model.ControlTabItemVO;
import com.zinc.libimage.model.detatilControl.BaseDetailControlInfo;
import com.zinc.libimage.view.view.ControlItemView;
import com.zinc.libimage.view.view.GestureImageView;
import com.zinc.libimage.view.view.JCropView;
import com.zinc.libimage.view.view.OverlayView;
import com.zinc.libimage.view.view.TransformImageView;
import com.zinc.libimage.widget.HorizontalProgressWheelView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Jiang zinc
 * @date 创建时间：2018/2/9
 * @description
 */

public class ImageEditActivity extends BaseActivity implements View.OnClickListener {

    private JCropView mCropView;
    private FrameLayout mFlDetailControlBar;
    private RecyclerView mRvControlBar;
    private LinearLayout mLlControlTypeBar;
    private ControlItemView mCropControlBtn;
    private ControlItemView mFilterControlBtn;
    private ControlItemView mToolboxControlBtn;

    private GestureImageView mCropImageView;
    private OverlayView mOverlayView;

    private ControlBarAdapter mAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.j_activity_image_edit;
    }

    @Override
    public void initView() {

        mCropView = findViewById(R.id.crop_view);
        mFlDetailControlBar = findViewById(R.id.fl_detail_control_bar);
        mRvControlBar = findViewById(R.id.rv_control_bar);
        mLlControlTypeBar = findViewById(R.id.ll_control_type_bar);
        mCropControlBtn = findViewById(R.id.crop_control_btn);
        mFilterControlBtn = findViewById(R.id.filter_control_btn);
        mToolboxControlBtn = findViewById(R.id.toolbox_control_btn);

        mCropImageView = mCropView.getCropImageView();
        mOverlayView = mCropView.getOverlayView();

    }

    @Override
    public void initData() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvControlBar.setLayoutManager(linearLayoutManager);
        mAdapter = new ControlBarAdapter(JImageConfig.cropDetailControlInfoList, ImageEditActivity.this);
        mRvControlBar.setAdapter(mAdapter);


//        String url = "content://com.android.providers.media.documents/document/image%3A56248";
        String url = "content://media/external/images/media/469781";
        try {
            mCropImageView.setImageUri(Uri.parse(url), null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mCropImageView.setmTransformImageListener(mImageListener);
        mOverlayView.setTargetAspectRatio(1.77f);
        mCropImageView.setImageToWrapCropBounds();

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
//        if (rotateNum != null) {
//            rotateNum.setText(String.format(Locale.getDefault(), "%.1f°", angle));
//        }
    }

    private TransformImageView.TransformImageListener mImageListener = new TransformImageView.TransformImageListener() {
        @Override
        public void onRotate(float currentAngle) {
            setAngleText(currentAngle);
        }

        @Override
        public void onScale(float currentScale) {
        }

        @Override
        public void onLoadComplete() {
            mCropImageView.animate().alpha(1).setDuration(300).setInterpolator(new AccelerateInterpolator()).start();
            supportInvalidateOptionsMenu();
        }

        @Override
        public void onLoadFailure(@NonNull Exception e) {
            Toast.makeText(ImageEditActivity.this, "图片加载失败", Toast.LENGTH_SHORT).show();
            finish();
        }

    };

    @Override
    public void onClick(View v) {
        int i = v.getId();
//        if (i == R.id.right_angle_rotate) { //90度旋转
//            mCropImageView.postRotate(-90);
//            mCropImageView.setImageToWrapCropBounds();
//        } else if (i == R.id.reset_rotat/e) {   //重置度数
//            mCropImageView.resetRotate();
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCropImageView.cancelAllAnimations();
    }

    public void cropAndSave() {
        mCropView.cropAndSave();
    }

}
