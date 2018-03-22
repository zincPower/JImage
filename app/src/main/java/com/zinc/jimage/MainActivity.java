package com.zinc.jimage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zinc.libimage.view.activity.ImageEditActivity;
import com.zinc.libimage.view.view.GestureImageView;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnEdit;
    private Button btnPalette;

//    private MyHandler mHandler = new MyHandler(this);
//
//    private void loadData() {
//        //do request
//        Message message = Message.obtain();
//        mHandler.sendMessage(message);
//    }
//
//    private static class MyHandler extends Handler {
//
//        private WeakReference<Context> reference;
//
//        public MyHandler(Context context) {
//            reference = new WeakReference<Context>(context);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            MainActivity mainActivity = (MainActivity) reference.get();
//            if (mainActivity != null) {
//                //do something to update UI via mainActivity
//            }
//        }
//    }
//
//    String url = "content://com.android.providers.media.documents/document/image%3A56248";
//
//    GestureImageView transformImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_edit).setOnClickListener(this);
        findViewById(R.id.btn_palette).setOnClickListener(this);

//        loadData();
//        transformImageView = findViewById(R.id.transform);
//        try {
//            transformImageView.setImageUri(Uri.parse(url), null);
//            transformImageView.setRotation(-90);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


//        new AlertDialog.Builder(this).setTitle("123").show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_edit:
                Intent intent1 = new Intent(this, ImageEditActivity.class);
                startActivity(intent1);
                break;
            case R.id.btn_palette:
                Intent intent2 = new Intent(this, PaletteActivity.class);
                startActivity(intent2);
                break;
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mHandler.removeCallbacksAndMessages(null);
//    }
}
