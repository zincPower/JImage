package com.zinc.libimage.model.detatilControl;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zinc.libimage.R;
import com.zinc.libimage.view.view.JCropView;

import java.lang.ref.SoftReference;

/**
 * @author Jiang zinc
 * @date 创建时间：2018/3/8
 * @description 比例
 */

public class RatioDetailControlInfo extends BaseDetailControlInfo implements View.OnClickListener {

    private SoftReference<View> mSoftView;

    public RatioDetailControlInfo(int type, int title, int unselecticon, int selectIcon, boolean isSelect) {
        super(type, title, unselecticon, selectIcon, isSelect);
    }

    @Override
    public void execute(View view) {

        FrameLayout frameLayout = getDetailControlBar(view);
        frameLayout.removeAllViews();

        View ratioView = LayoutInflater.from(view.getContext()).inflate(R.layout.j_widget_ratio_detail_view, frameLayout, true);

        LinearLayout controlRatioBar = view.findViewById(R.id.control_ratio_bar);
        TextView ratioFreeBtn = view.findViewById(R.id.ratio_free_btn);
        TextView ratio11Btn = view.findViewById(R.id.ratio_1_1_btn);
        TextView ratio43Btn = view.findViewById(R.id.ratio_4_3_btn);
        TextView ratio32Btn = view.findViewById(R.id.ratio_3_2_btn);
        TextView ratio169Btn = view.findViewById(R.id.ratio_16_9_btn);
        TextView ratio75Btn = view.findViewById(R.id.ratio_7_5_btn);
        TextView ratio54Btn = view.findViewById(R.id.ratio_5_4_btn);

        ratioFreeBtn.setOnClickListener(this);
        ratio11Btn.setOnClickListener(this);
        ratio43Btn.setOnClickListener(this);
        ratio32Btn.setOnClickListener(this);
        ratio169Btn.setOnClickListener(this);
        ratio75Btn.setOnClickListener(this);
        ratio54Btn.setOnClickListener(this);

        mSoftView = new SoftReference<View>(view);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        View mView =  mSoftView.get();
        JCropView jCropView = mView.findViewById(R.id.crop_view);
        jCropView.getOverlayView().setFixTargetAspectRatio(true);
        if (i == R.id.ratio_free_btn) {
            jCropView.getOverlayView().setFixTargetAspectRatio(false);
        } else if (i == R.id.ratio_1_1_btn) {
            jCropView.getOverlayView().setTargetAspectRatio(1.0f/1.0f);
        } else if (i == R.id.ratio_4_3_btn) {
            jCropView.getOverlayView().setTargetAspectRatio(4.0f/3.0f);
        } else if (i == R.id.ratio_3_2_btn) {
            jCropView.getOverlayView().setTargetAspectRatio(3.0f/2.0f);
        } else if (i == R.id.ratio_16_9_btn) {
            jCropView.getOverlayView().setTargetAspectRatio(16.0f/9.0f);
        } else if (i == R.id.ratio_7_5_btn) {
            jCropView.getOverlayView().setTargetAspectRatio(7.0f/5.0f);
        } else if (i == R.id.ratio_5_4_btn) {
            jCropView.getOverlayView().setTargetAspectRatio(5.0f/4.0f);
        }
    }

}
