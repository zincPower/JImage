package com.zinc.libimage.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zinc.libimage.R;
import com.zinc.libimage.model.LocalMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.zinc.libimage.config.JImageConfig.ITEM_CAMERA;
import static com.zinc.libimage.config.JImageConfig.ITEM_PICTURE;

/**
 * @author Jiang zinc
 * @date 创建时间：2018/1/23
 * @description 图片墙的适配器
 */

public class ImagePickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private int mMaxSelect;                      //最大选择
    private boolean mIsCanPreview;               //是否可预览
    private boolean mIsShowCamera;               //是否显示照相机
    private List<LocalMedia> mLocalMediaList = new ArrayList<>();    //文件夹下的图片
    private List<LocalMedia> mSelectedMediaList = new ArrayList<>(); //已选择的

    private ImagePickerListener mImagePickerListener;

    public ImagePickerAdapter(Context context, int maxSelect, boolean isCanPreview, boolean isShowCamera) {
        this.mContext = context;
        this.mMaxSelect = maxSelect;
        this.mIsCanPreview = isCanPreview;
        this.mIsShowCamera = isShowCamera;
    }

    public void setLocalMediaList(List<LocalMedia> localMediaList) {
        this.mLocalMediaList = localMediaList;
        notifyDataSetChanged();
    }

    public void setSelectedMediaList(List<LocalMedia> selectedMediaList) {
        this.mSelectedMediaList = selectedMediaList;
        notifyDataSetChanged();
    }

    public void setMediaData(List<LocalMedia> localMediaList, List<LocalMedia> selectedMediaList) {
        this.mLocalMediaList = localMediaList;
        this.mSelectedMediaList = selectedMediaList;
        notifyDataSetChanged();
    }

    public void setImagePickerListener(ImagePickerListener imagePickerListener) {
        mImagePickerListener = imagePickerListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (mIsShowCamera && position == 0) {      //需要显示照相机并且位置为1,返回照相机
            return ITEM_CAMERA;
        } else {
            return ITEM_PICTURE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ITEM_CAMERA) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.j_item_camera, parent, false);
            return new CameraViewHolder(view);
        } else if (viewType == ITEM_PICTURE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.j_item_media, parent, false);
            return new MediaViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof CameraViewHolder) {

            CameraViewHolder cameraViewHolder = (CameraViewHolder) holder;

            cameraViewHolder.rl_camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "相机", Toast.LENGTH_SHORT).show();
                }
            });

        } else if (holder instanceof MediaViewHolder) {

            MediaViewHolder mediaViewHolder = (MediaViewHolder) holder;
            final LocalMedia curLocalMedia = mLocalMediaList.get(mIsShowCamera ? position - 1 : position);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();
            requestOptions.dontAnimate();
            Glide.with(mContext)
                    .load(new File(curLocalMedia.getPath()))
                    .apply(requestOptions)
                    .thumbnail(0.5f)    //低解析度的图片，为原图5／10
                    .into(mediaViewHolder.image);

            if (isSelected(curLocalMedia.getPath())) {
                mediaViewHolder.check.setSelected(true);
                mediaViewHolder.image.setColorFilter(mContext.getResources().getColor(R.color.image_overlay2), PorterDuff.Mode.SRC_ATOP);
            } else {
                mediaViewHolder.check.setSelected(false);
                mediaViewHolder.image.setColorFilter(mContext.getResources().getColor(R.color.image_overlay), PorterDuff.Mode.SRC_ATOP);
            }

            mediaViewHolder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(mContext, "预览", Toast.LENGTH_SHORT).show();
                    if (mImagePickerListener != null) {
                        mImagePickerListener.onClickItem(mIsShowCamera ? position - 1 : position);
                    }
                }
            });

            mediaViewHolder.check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(mContext, "选择", Toast.LENGTH_SHORT).show();
                    selectImage(curLocalMedia, position);
                    if (mImagePickerListener != null) {
                        mImagePickerListener.onSelectImageChange();
                    }
                }
            });

        }

    }

    private boolean isSelected(String imagePath) {
        for (LocalMedia localMedia : mSelectedMediaList) {
            if (localMedia.getPath().equals(imagePath)) {
                return true;
            }
        }
        return false;
    }

    private void selectImage(LocalMedia localMedia, int position) {

        boolean isSelect = isSelected(localMedia.getPath());

        if (!isSelect && mSelectedMediaList.size() >= mMaxSelect) {
            Toast.makeText(mContext, String.format(mContext.getString(R.string.jimage_max_select_tip), mMaxSelect), Toast.LENGTH_SHORT).show();
            return;
        }

        if (isSelect) {   //已经选择了图片
            Iterator<LocalMedia> iterator = mSelectedMediaList.iterator();
            while (iterator.hasNext()) {
                LocalMedia curMedia = iterator.next();
                if (curMedia.getPath().equals(localMedia.getPath())) {
                    iterator.remove();
                    break;
                }
            }
        } else {
            mSelectedMediaList.add(localMedia);
        }

        notifyItemChanged(position);

    }

    @Override
    public int getItemCount() {
        return mIsShowCamera ? mLocalMediaList.size() + 1 : mLocalMediaList.size();
    }

    static class CameraViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rl_camera;

        public CameraViewHolder(View itemView) {
            super(itemView);
            rl_camera = itemView.findViewById(R.id.rl_camera);
        }
    }

    static class MediaViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private ImageView check;

        public MediaViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            check = itemView.findViewById(R.id.check);
        }
    }

    public interface ImagePickerListener {
        void onSelectImageChange();

        /**
         * 点击图片项事件
         *
         * @param position 当前的图片位置【已去除拍照项】
         */
        void onClickItem(int position);
    }

}
