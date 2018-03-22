//package com.zinc.libimage.model.detatilControl;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.zinc.libimage.R;
//import com.zinc.libimage.widget.HorizontalProgressWheelView;
//
///**
// * @author Jiang zinc
// * @date 创建时间：2018/3/8
// * @description 随意旋转
// */
//
//public class RotateDetailControlInfo extends BaseDetailControlInfo {
//
//    public static final int ROTATE_WIDGET_SENSITIVITY_COEFFICIENT = 10;
//
//    public RotateDetailControlInfo(int type, int title, int unselecticon, int selectIcon, boolean isSelect) {
//        super(type, title, unselecticon, selectIcon, isSelect);
//
//    }
//
//    @Override
//    public void execute(final View view) {
//
//        FrameLayout frameLayout = getDetailControlBar(view);
//        frameLayout.removeAllViews();
//
//        View rotateView = LayoutInflater.from(view.getContext()).inflate(R.layout.j_widget_rotate_detail_view, frameLayout, true);
//
//        ImageView reset = rotateView.findViewById(R.id.reset_rotate);
//        reset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getGestureImageView(view).resetRotate();
//            }
//        });
//
//        TextView rotateNum = rotateView.findViewById(R.id.rotate_num);
//
//        HorizontalProgressWheelView horizontalProgressWheelView = rotateView.findViewById(R.id.progress_wheel_rotate);
//        horizontalProgressWheelView.setScrollingListener(new HorizontalProgressWheelView.ScrollingListener() {
//            @Override
//            public void onScrollStart() {
//                getGestureImageView(view).cancelAllAnimations();
//            }
//
//            @Override
//            public void onScroll(float delta, float totalDistance) {
//                getGestureImageView(view).postRotate(delta / ROTATE_WIDGET_SENSITIVITY_COEFFICIENT);
//            }
//
//            @Override
//            public void onScrollEnd() {
//                getGestureImageView(view).setImageToWrapCropBounds();
//            }
//        });
//
//    }
//}
