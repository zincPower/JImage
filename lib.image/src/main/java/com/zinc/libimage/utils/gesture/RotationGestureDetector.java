package com.zinc.libimage.utils.gesture;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * @author Jiang zinc
 * @date 创建时间：2018/2/26
 * @description 角度的旋转手势
 */

public class RotationGestureDetector {

    private static final int INVALID_POINTER_INDEX = -1;
    private RotationGestureListener mRotateListener;

    //第一个触点的坐标（x，y）、第二个触点的坐标（x，y）
    private float fx, fy, sx, sy;
    //两次触点的标
    private int mPointerIndex1, mPointerIndex2;
    private boolean mIsFirstTouch;

    private float mAngle;

    public RotationGestureDetector(RotationGestureListener listener) {
        mRotateListener = listener;
        mPointerIndex1 = INVALID_POINTER_INDEX;
        mPointerIndex2 = INVALID_POINTER_INDEX;
    }

    public float getmAngle() {
        return mAngle;
    }

    public boolean onTouchEvent(@Nullable MotionEvent event) {

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:       //第一个触点（初始化）

//                fx = event.getX();
//                fy = event.getY();

                resetParams();
                mPointerIndex1 = event.findPointerIndex(event.getPointerId(0));

                break;
            case MotionEvent.ACTION_POINTER_DOWN://非第一触点 （初始化）

//                sx = event.getX();
//                sy = event.getY();

                resetParams();
                mPointerIndex2 = event.findPointerIndex(event.getPointerId(event.getActionIndex()));

                break;
            case MotionEvent.ACTION_MOVE:

                //TODO 比uCrop少 && event.getPointerCount() > mPointerIndex2
                //当两个触碰点都合法时，出发此逻辑，进行旋转
                if (mPointerIndex1 != INVALID_POINTER_INDEX && mPointerIndex2 != INVALID_POINTER_INDEX) {

                    float nfx, nfy, nsx, nsy;
                    nfx = event.getX(mPointerIndex1);
                    nfy = event.getY(mPointerIndex1);
                    nsx = event.getX(mPointerIndex2);
                    nsy = event.getY(mPointerIndex2);

                    //必须增加第一次判断，
                    if (mIsFirstTouch) {
                        mAngle = 0;
                        mIsFirstTouch = false;
                    } else {
                        calculateAngleBetweenLines(fx, fy, sx, sy, nfx, nfy, nsx, nsy);
                    }

                    if (mRotateListener != null) {
                        mRotateListener.onRotation(this);
                    }

                    fx = nfx;
                    fy = nfy;
                    sx = nsx;
                    sy = nsy;
                }

                break;
            case MotionEvent.ACTION_UP:         //抬起第一个触点 （复原）
                mPointerIndex1 = INVALID_POINTER_INDEX;
                break;
            case MotionEvent.ACTION_POINTER_UP: //抬起非第一个触点 （复原）
                mPointerIndex2 = INVALID_POINTER_INDEX;
                break;
        }
        return true;
    }

    //计算两条线间的角度
    private float calculateAngleBetweenLines(float fx, float fy, float sx, float sy, float nfx, float nfy, float nsx, float nsy) {

        Log.i("calculateAngle", "PointOne:["+fx+","+fy+"]"+",PointTwo:["+sx+","+sy+"]");

        //Math.toDegrees将弧度制转为角度
        return calculateAngleDelta((float) Math.toDegrees((float)Math.atan2(sy - fy, sx - fx)),
                (float) Math.toDegrees((float)Math.atan2(nsy - nfy, nsx - nfx)));

    }

    //计算每次旋转的角度
    private float calculateAngleDelta(float angleFrom, float angleTo) {

        Log.i("calculateAngle", "【angleFrom】:" + angleFrom + ";【angleTo】:" + angleTo + ";【mAngle】:" + mAngle);

        mAngle = angleTo % 360.0f - angleFrom % 360.0f;

        // TODO: 2018/2/26 这段代码感觉没什么可用
//        if (mAngle < -180) {
//            mAngle += 360;
//        } else if (mAngle > 180) {
//            mAngle -= 360;
//        }

        return mAngle;
    }

    private void resetParams() {
        mIsFirstTouch = true;
        mAngle = 0;
    }

    public static class SimpleRotationGestureListener implements RotationGestureListener {
        @Override
        public boolean onRotation(RotationGestureDetector rotationGestureDetector) {
            return false;
        }
    }

    public interface RotationGestureListener {
        boolean onRotation(RotationGestureDetector rotationGestureDetector);
    }
}
