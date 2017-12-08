package com.example.hbl.bluetooth.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hbl.bluetooth.R;

/**
 * Created by huangbaole on 2017/12/2.
 */

public class ItemView extends LinearLayout {
    private LinearLayout rootView;

    public ItemView(Context context) {
        this(context,null);
    }

    public ItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public ItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GirdItem, 0, 0);
        String string = typedArray.getString(R.styleable.GirdItem_text);
        Drawable image = typedArray.getDrawable(R.styleable.GirdItem_girdDrawble);
        ((TextView) rootView.findViewById(R.id.text)).setText(string);
        ((ImageView) rootView.findViewById(R.id.top_image)).setImageDrawable(image);
        typedArray.recycle();
    }

    private void initView(Context context) {
        rootView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.gird_item, this,true);
    }
}
