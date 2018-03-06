package com.zinc.libimage.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zinc.libimage.R;
import com.zinc.libimage.adapter.ImagePickerAdapter;
import com.zinc.libimage.model.LocalMedia;
import com.zinc.libimage.model.LocalMediaFolder;
import com.zinc.libimage.utils.GridSpacingItemDecoration;
import com.zinc.libimage.utils.LocalMediaLoader;
import com.zinc.libimage.utils.UIUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.zinc.libimage.config.JImageConfig.TYPE_IMAGE;
import static com.zinc.libimage.utils.JImageTag.CUR_MEDIA;
import static com.zinc.libimage.utils.JImageTag.LOCAL_MEDIA;
import static com.zinc.libimage.utils.JImageTag.MAX_NUM;
import static com.zinc.libimage.utils.JImageTag.SELECTED_MEDIA;

/**
 * @author Jiang zinc
 * @date 创建时间：2018/1/23
 * @description
 */

public class ImagePickerActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = ImagePickerActivity.class.getSimpleName();
    private final int PREVIEW_REQUET = 1;

    private ImagePickerAdapter mImagePickerAdapter;
    private RecyclerView mRecyclerView;

    //当前页面显示的数据源
    private List<LocalMedia> mLocalMediaList;
    //以选择的图片数据
    private List<LocalMedia> mSelectMediaList;
    //本地图片的数据夹
    private List<LocalMediaFolder> mLocalMediaFolderList;

    private int maxSelect = 9;                  //最大选择
    private boolean isCanPreview = true;               //是否可预览
    private boolean isShowCamera = true;               //是否显示照相机

    private TextView tvPreview;     //预览

    private int spanCount = 4;  //一行个数

    @Override
    public void initView() {
        mRecyclerView = findViewById(R.id.recycleView);
        tvPreview = findViewById(R.id.tv_preview);

    }

    @Override
    public void initData() {

        getSupportActionBar().setTitle(R.string.jimage_image_title);
        mTvCommit.setText(getString(R.string.jimage_commit));
        mTvCommit.setOnClickListener(this);
        mTvCommit.setVisibility(View.VISIBLE);

        if (mSelectMediaList == null) {
            mSelectMediaList = new ArrayList<>();
        }

        mImagePickerAdapter = new ImagePickerAdapter(this, maxSelect, isCanPreview, isShowCamera);
        mImagePickerAdapter.setImagePickerListener(new ImagePickerAdapter.ImagePickerListener() {
            @Override
            public void onSelectImageChange() {
                changeCommitBtn();
            }

            @Override
            public void onClickItem(int position) {
                Log.i(TAG, "预览图片，选择了第" + position + "张");
                Intent intent = new Intent(ImagePickerActivity.this, ImagePreviewActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable(LOCAL_MEDIA, (Serializable) mLocalMediaList);
                bundle.putSerializable(SELECTED_MEDIA, (Serializable) mSelectMediaList);
                bundle.putInt(MAX_NUM, maxSelect);
                bundle.putInt(CUR_MEDIA, position);
                intent.putExtras(bundle);

                startActivityForResult(intent, PREVIEW_REQUET);
            }
        });

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));
        mRecyclerView.setAdapter(mImagePickerAdapter);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, UIUtil.dip2px(this, 2), false));

        LocalMediaLoader localMediaLoader = new LocalMediaLoader(this, TYPE_IMAGE);
        localMediaLoader.loadAllMedia(new LocalMediaLoader.LocalMediaLoadListener() {
            @Override
            public void loadComplete(List<LocalMediaFolder> folders) {
                mLocalMediaFolderList = folders;
                if (mLocalMediaFolderList.size() > 0) {
                    //获取第一个文件夹的图片作为默认显示
                    mLocalMediaList = mLocalMediaFolderList.get(0).getImages();
                } else {
                    mLocalMediaList = new ArrayList<>();
                }

                mImagePickerAdapter.setMediaData(mLocalMediaList, mSelectMediaList);
            }
        });

        tvPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mImagePickerAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PREVIEW_REQUET) {
                Bundle bundle = data.getExtras();
                mSelectMediaList = (List<LocalMedia>) bundle.getSerializable(SELECTED_MEDIA);
                mImagePickerAdapter.setSelectedMediaList(mSelectMediaList);
                changeCommitBtn();
                mImagePickerAdapter.notifyDataSetChanged();
            }
        }


    }

    /**
     * 转换提交按钮
     */
    private void changeCommitBtn(){
        if (mSelectMediaList.size() <= 0) {       //如果一张都还没选
            mTvCommit.setText(getString(R.string.jimage_commit));
        } else {
            mTvCommit.setText(String.format(getString(R.string.jimage_commit_with_num), mSelectMediaList.size(), maxSelect));
        }
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void initIntent(Intent intent) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.j_activity_image_picker;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_commit) {
            Toast.makeText(this, "提交", Toast.LENGTH_SHORT).show();
        }
    }
}
