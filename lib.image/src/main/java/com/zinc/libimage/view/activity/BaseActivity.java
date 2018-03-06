package com.zinc.libimage.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.zinc.libimage.R;

/**
 * @author Jiang zinc
 * @date 创建时间：2018/1/23
 * @description
 */

public abstract class BaseActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    protected TextView mTvCommit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        initToolbar();
        initView();
        initIntent(getIntent());
        initData();
        onCreate();
    }

    public void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        mTvCommit = findViewById(R.id.tv_commit);
        if(mToolbar != null ){
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public abstract int getLayoutId();

    public abstract void initView();

    public abstract void initData();

    public abstract void onCreate();

    public abstract void initIntent(Intent intent);
}
