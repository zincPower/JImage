package com.zinc.jimage.hugeImage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.zinc.jimage.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Jiang zinc
 * @date 创建时间：2018/2/8
 * @description 长图显示视图
 */

public class PiiicPreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piiic);
        InputStream is = null;
        try {
            is = getAssets().open("big.png");
            PiiicView piiicView = findViewById(R.id.piiicView);
//            BigView bigView = findViewById(R.id.bigView);
            piiicView.setImage(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(is !=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
