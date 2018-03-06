package com.zinc.libimage.view.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.zinc.libimage.R;
import com.zinc.libimage.callback.OverlayViewChangeListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Jiang zinc
 * @date 创建时间：2018/3/1
 * @description
 */

public class JCropView extends FrameLayout {

    private OverlayView mOverlayView;
    private GestureImageView mGestureImageView;

    public JCropView(@NonNull Context context) {
        this(context, null, 0);
    }

    public JCropView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JCropView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.j_crop_view, this, true);
        mOverlayView = findViewById(R.id.overlay_view);
        mGestureImageView = findViewById(R.id.crop_image_view);

        setListenersToViews();

    }

    private void setListenersToViews(){
        mOverlayView.setOverlayViewChangeListener(new OverlayViewChangeListener() {
            @Override
            public void onCropRectUpdated(RectF cropRect) {
                mGestureImageView.setCropRect(cropRect);
            }
        });
    }

    public void cropAndSave(){

        Bitmap bitmap = mGestureImageView.cropAndSave();

        File PHOTO_DIR = new File(Environment.getExternalStorageDirectory(),"image");//设置保存路径
        File avaterFile = new File(PHOTO_DIR, System.currentTimeMillis()+"image.jpg");//设置文件名称

        if(!PHOTO_DIR.exists()){
            PHOTO_DIR.mkdir();
        }

        try {
            avaterFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(avaterFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public OverlayView getmOverlayView() {
        return mOverlayView;
    }

    public GestureImageView getmGestureImageView() {
        return mGestureImageView;
    }
}
