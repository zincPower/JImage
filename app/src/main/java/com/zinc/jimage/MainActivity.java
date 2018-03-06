package com.zinc.jimage;

import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zinc.libimage.view.view.GestureImageView;

public class MainActivity extends AppCompatActivity {

    String url = "content://com.android.providers.media.documents/document/image%3A56248";

    GestureImageView transformImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        transformImageView = findViewById(R.id.transform);
//        try {
//            transformImageView.setImageUri(Uri.parse(url), null);
//            transformImageView.setRotation(-90);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        new AlertDialog.Builder(this).setTitle("123").show();

    }
}
