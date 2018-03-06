package com.zinc.libimage.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zinc.libimage.model.LocalMedia;
import com.zinc.libimage.view.fragment.ImagePreviewFragment;

import java.util.List;

/**
 * @author Jiang zinc
 * @date 创建时间：2017/12/21
 * @description
 */

public class ImagePreviewFragmentAdapter extends FragmentPagerAdapter {

    private Context context;
    private List<LocalMedia> mLocalMedia;

    public ImagePreviewFragmentAdapter(FragmentManager fm, Context context, List<LocalMedia> mLocalMedia) {
        super(fm);
        this.context = context;
        this.mLocalMedia = mLocalMedia;
    }

    @Override
    public Fragment getItem(int position) {
        return ImagePreviewFragment.newInstance(mLocalMedia.get(position).getPath());
    }

    @Override
    public int getCount() {
        return mLocalMedia.size();
    }

}
