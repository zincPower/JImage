package com.zinc.jimage;

import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.zinc.libimage.view.view.PaletteImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Jiang zinc
 * @date 创建时间：2018/3/22
 * @description
 */

public class PaletteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette);

//        String url = "/storage/emulated/0/DCIM/Camera/IMG_20151210_004611.jpg";
        String url = "content://media/external/images/media/469781";

//        InputStream is = null;
//        try {
//            is = new FileInputStream(new File(url));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        Uri uri = Uri.parse(url);
//        InputStream strem = null;
//        try {
//            strem = getContentResolver().openInputStream(uri);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
        PaletteImageView paletteImageView = findViewById(R.id.palette_view);
        /**
         * bottom = 632.0
         left = 32.0
         right = 624.8974
         top = 276.0
         */
        paletteImageView.setImage(uri, new Rect(32, 276, 624, 632));
        paletteImageView.setClickable(true);

    }
}
