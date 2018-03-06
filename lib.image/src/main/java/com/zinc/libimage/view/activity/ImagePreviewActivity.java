package com.zinc.libimage.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zinc.libimage.R;
import com.zinc.libimage.adapter.ImagePreviewFragmentAdapter;
import com.zinc.libimage.model.LocalMedia;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.zinc.libimage.utils.JImageTag.CUR_MEDIA;
import static com.zinc.libimage.utils.JImageTag.LOCAL_MEDIA;
import static com.zinc.libimage.utils.JImageTag.MAX_NUM;
import static com.zinc.libimage.utils.JImageTag.SELECTED_MEDIA;

/**
 * @author Jiang zinc
 * @date 创建时间：2018/1/26
 * @description 图片预览
 */

public class ImagePreviewActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = ImagePreviewActivity.class.getSimpleName();

    private ViewPager mViewPager;
    private TextView mTvEdit;
    private LinearLayout mllOriginal;
    private LinearLayout mllSelect;
    private Checkable mCbSelect;

    //当前页面显示的数据源
    private List<LocalMedia> mLocalMediaList;
    //以选择的图片数据
    private List<LocalMedia> mSelectMediaList;

    private int mMaxSelect = 9;                  //最大选择
    private int mCurPosition = 0;                  //当前选择项

    @Override
    public int getLayoutId() {
        return R.layout.j_activity_image_preview;
    }

    @Override
    public void initView() {
        mViewPager = findViewById(R.id.viewPager);
        mTvEdit = findViewById(R.id.tv_edit);
        mllOriginal = findViewById(R.id.ll_original);
        mllSelect = findViewById(R.id.ll_select);
        mCbSelect = findViewById(R.id.cb_select);
    }

    @Override
    public void initData() {

        changeCommitBtn();
        getSupportActionBar().setTitle(String.format("%1$o/%2$o", mCurPosition + 1, mLocalMediaList.size()));
        mTvCommit.setOnClickListener(this);
        mTvCommit.setVisibility(View.VISIBLE);

        mViewPager.setAdapter(new ImagePreviewFragmentAdapter(getSupportFragmentManager(), this, mLocalMediaList));
        mViewPager.setOffscreenPageLimit(mLocalMediaList.size() >= 5 ? 5 : mLocalMediaList.size());
        mViewPager.setCurrentItem(mCurPosition, false);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                boolean isSelect = isSelected(mLocalMediaList.get(position).getPath());
                mCbSelect.setChecked(isSelect);
            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, String.format("选择了第%1$o张，共%2$o张。", position, mLocalMediaList.size()));
                getSupportActionBar().setTitle(String.format("%1$o/%2$o", position + 1, mLocalMediaList.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mllSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalMedia localMedia = mLocalMediaList.get(mViewPager.getCurrentItem());
                selectImage(localMedia);
            }
        });

        mTvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(SELECTED_MEDIA, (Serializable) mSelectMediaList);
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void initIntent(Intent intent) {

        Bundle bundle = intent.getExtras();
        mLocalMediaList = (List<LocalMedia>) bundle.getSerializable(LOCAL_MEDIA);
        mSelectMediaList = (List<LocalMedia>) bundle.getSerializable(SELECTED_MEDIA);
        mMaxSelect = bundle.getInt(MAX_NUM);
        mCurPosition = bundle.getInt(CUR_MEDIA, 0);

    }

    private void selectImage(LocalMedia localMedia) {

        boolean isSelect = isSelected(localMedia.getPath());

        if (!isSelect && mSelectMediaList.size() >= mMaxSelect) {
            Toast.makeText(ImagePreviewActivity.this, String.format(ImagePreviewActivity.this.getString(R.string.jimage_max_select_tip), mMaxSelect), Toast.LENGTH_SHORT).show();
            return;
        }

        if (isSelect) {   //已经选择了图片
            Iterator<LocalMedia> iterator = mSelectMediaList.iterator();
            while (iterator.hasNext()) {
                LocalMedia curMedia = iterator.next();
                if (curMedia.getPath().equals(localMedia.getPath())) {
                    mCbSelect.setChecked(false);
                    iterator.remove();
                    break;
                }
            }
        } else {
            mCbSelect.setChecked(true);
            mSelectMediaList.add(localMedia);
        }

        changeCommitBtn();

    }

    private void changeCommitBtn(){
        if (mSelectMediaList.size() <= 0) {       //如果一张都还没选
            mTvCommit.setText(getString(R.string.jimage_commit));
        } else {
            mTvCommit.setText(String.format(getString(R.string.jimage_commit_with_num), mSelectMediaList.size(), mMaxSelect));
        }
    }

    private boolean isSelected(String imagePath) {
        for (LocalMedia localMedia : mSelectMediaList) {
            if (localMedia.getPath().equals(imagePath)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_commit) {
            Toast.makeText(this, "提交", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(SELECTED_MEDIA, (Serializable) mSelectMediaList);
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish();
    }
}
