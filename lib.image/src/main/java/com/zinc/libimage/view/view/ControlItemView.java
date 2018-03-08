package com.zinc.libimage.view.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zinc.libimage.R;

/**
 * @author Jiang zinc
 * @date 创建时间：2018/3/7
 * @description 编辑页面控制项
 */

public class ControlItemView extends LinearLayout {

    private TextView mText;
    private ImageView mImage;

    private Integer unselectImage;
    private Integer selectImage;

    public ControlItemView(Context context) {
        this(context, null);
    }

    public ControlItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControlItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.j_control_item_view, this, true);

        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);

        mText = findViewById(R.id.text1);
        mImage = findViewById(R.id.image1);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.jimage_ControlItem);
        mText.setText(a.getString(R.styleable.jimage_ControlItem_title));
        mText.setTextColor(ContextCompat.getColor(context, R.color.jimage_unselect_color));

        unselectImage = a.getResourceId(R.styleable.jimage_ControlItem_unselect_image,0);
        selectImage = a.getResourceId(R.styleable.jimage_ControlItem_select_image,0);

        mImage.setImageDrawable(ContextCompat.getDrawable(context, unselectImage));

        a.recycle();

    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            mImage.setImageDrawable(ContextCompat.getDrawable(getContext(), selectImage));
            mText.setTextColor(ContextCompat.getColor(getContext(), R.color.jimage_select_color));
        } else {
            mImage.setImageDrawable(ContextCompat.getDrawable(getContext(), unselectImage));
            mText.setTextColor(ContextCompat.getColor(getContext(), R.color.jimage_unselect_color));
        }
    }

    public View getView() {
        return mImage;
    }
}