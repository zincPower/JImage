package com.zinc.libimage.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zinc.libimage.R;
import com.zinc.libimage.model.detatilControl.BaseDetailControlInfo;

import java.util.List;

/**
 * @author Jiang zinc
 * @date 创建时间：2018/3/8
 * @description
 */

public class ControlBarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<BaseDetailControlInfo> detailControlInfoList;
    private Context mContext;
    private LayoutInflater mInflater;

    public ControlBarAdapter(List<BaseDetailControlInfo> detailControlInfoList, Context context) {
        this.detailControlInfoList = detailControlInfoList;
        this.mContext = context;

        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == BaseDetailControlInfo.CROP) {
            return new CropViewHolder(mInflater.inflate(R.layout.j_widget_crop_item_view, parent, false));
        } else if (viewType == BaseDetailControlInfo.FILTER) {
            return new FilterViewHolder(mInflater.inflate(R.layout.j_widget_filter_item_view, parent, false));
        } else if (viewType == BaseDetailControlInfo.TOOLBOX) {
            return new ToolboxViewHolder(mInflater.inflate(R.layout.j_widget_toolbox_item_view, parent, false));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof CropViewHolder) {         //裁剪项

            final BaseDetailControlInfo item = detailControlInfoList.get(position);

            CropViewHolder cropViewHolder = (CropViewHolder) holder;
            cropViewHolder.funName.setText(mContext.getString(item.getTitle()));
            cropViewHolder.llContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(item.isTouch()){
                        detailControlInfoList.get(position).execute(holder.itemView.getRootView());
                        return;
                    }

                    if(!isCurrent(position)){
                        detailControlInfoList.get(position).execute(holder.itemView.getRootView());
                        initDataState(position);
                    }
                }
            });

            if (item.isSelect()) {
                cropViewHolder.icon.setImageDrawable(ContextCompat.getDrawable(mContext, item.getSelectIcon()));
                cropViewHolder.funName.setTextColor(ContextCompat.getColor(mContext, R.color.jimage_select_color));
            } else {
                cropViewHolder.icon.setImageDrawable(ContextCompat.getDrawable(mContext, item.getUnselecticon()));
                cropViewHolder.funName.setTextColor(ContextCompat.getColor(mContext, R.color.jimage_unselect_color));
            }

//            holder.itemView.getRootView().post(new Runnable() {
//                @Override
//                public void run() {
//                    detailControlInfoList.get(getCurrentPosition()).execute(holder.itemView.getRootView());
//                }
//            });

        } else if (holder instanceof FilterViewHolder) {      //滤镜

        } else if (holder instanceof ToolboxViewHolder) {     //编辑工具箱

        }

    }

    private void initDataState(int position) {
        for (BaseDetailControlInfo item : detailControlInfoList) {
            item.setSelect(false);
        }
        detailControlInfoList.get(position).setSelect(true);
        notifyDataSetChanged();
    }

    private boolean isCurrent(int position) {
        return position == getCurrentPosition();
    }

    private int getCurrentPosition(){
        int i = -1;
        for (BaseDetailControlInfo item : detailControlInfoList) {
            ++i;
            if (item.isSelect()) {
                break;
            }
        }
        return i;
    }

    @Override
    public int getItemViewType(int position) {
        return detailControlInfoList.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return detailControlInfoList.size();
    }

    class CropViewHolder extends RecyclerView.ViewHolder {

        private ImageView icon;
        private TextView funName;
        private LinearLayout llContent;

        public CropViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.image1);
            funName = itemView.findViewById(R.id.text1);
            llContent = itemView.findViewById(R.id.ll_content);
        }

    }

    class FilterViewHolder extends RecyclerView.ViewHolder {

        private ImageView icon;
        private TextView funName;
        private LinearLayout llContent;

        public FilterViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.image1);
            funName = itemView.findViewById(R.id.text1);
            llContent = itemView.findViewById(R.id.ll_content);
        }
    }

    class ToolboxViewHolder extends RecyclerView.ViewHolder {

        private ImageView icon;
        private TextView funName;
        private LinearLayout llContent;

        public ToolboxViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.image1);
            funName = itemView.findViewById(R.id.text1);
            llContent = itemView.findViewById(R.id.ll_content);
        }
    }

}
