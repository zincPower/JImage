package com.zinc.libimage.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zinc.libimage.R;
import com.zinc.libimage.view.activity.ImagePreviewActivity;

import java.io.File;

import static com.zinc.libimage.utils.JImageTag.IMAGE_URL;

/**
 * @author Jiang zinc
 * @date 创建时间：2017/12/20
 * @description
 */

public class ImagePreviewFragment extends Fragment {

    private String imgPath;

    private ImageView ivPreview;

    public static ImagePreviewFragment newInstance(String imgPath) {

        Bundle args = new Bundle();
        args.putString(IMAGE_URL, imgPath);

        ImagePreviewFragment fragment = new ImagePreviewFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        imgPath = args.getString(IMAGE_URL);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.j_fragment_image_preview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivPreview = view.findViewById(R.id.iv_preview);

        Glide.with(getContext())
                .load(new File(imgPath))
                .into(ivPreview);

        ivPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
