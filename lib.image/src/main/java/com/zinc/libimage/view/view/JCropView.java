package com.zinc.libimage.view.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.zinc.libimage.R;
import com.zinc.libimage.callback.OverlayViewChangeListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * @author Jiang zinc
 * @date 创建时间：2018/3/1
 * @description
 */

public class JCropView extends FrameLayout {

    public static final String JPG = ".jpg";
    public static final String PNG = ".png";
    public static final String WEBP = ".webp";

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

    private void setListenersToViews() {
        mOverlayView.setOverlayViewChangeListener(new OverlayViewChangeListener() {
            @Override
            public void onCropRectUpdated(RectF cropRect) {
                mGestureImageView.setCropRect(cropRect);

            }
        });
    }

    public void cropAndSave(){
        cropAndSave("JImage");
    }

    public void cropAndSave(String folderName) {
        cropAndSave(folderName, null);
    }

    public void cropAndSave(String folderName, String fileName) {
        cropAndSave(folderName, fileName, 100, PNG);
    }

    public void cropAndSave(String folderName, String fileName, int quality, @NonNull String type) {

        Bitmap bitmap = mGestureImageView.crop();

        File folder = new File(Environment.getExternalStorageDirectory(), folderName);//设置保存路径
        File file = null;
        if (TextUtils.isEmpty(fileName)) {
            Random random = new Random(34);
            file = new File(folder, "JImage_" + random.nextInt(10000) + "_" + System.currentTimeMillis() + type);//设置文件名称
        }

        if (!folder.exists()) {
            folder.mkdir();
        }

        FileOutputStream fos = null;
        try {
            file.createNewFile();
            fos = new FileOutputStream(file);
            if (type.equals(PNG)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, quality, fos);
            } else if (type.equals(JPG)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            } else if (type.equals(WEBP)) {
                bitmap.compress(Bitmap.CompressFormat.WEBP, quality, fos);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public OverlayView getOverlayView() {
        return mOverlayView;
    }

    public GestureImageView getCropImageView() {
        return mGestureImageView;
    }
}
