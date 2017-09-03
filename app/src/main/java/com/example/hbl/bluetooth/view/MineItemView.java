package com.example.hbl.bluetooth.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hbl.bluetooth.R;

/**
 * Created by hbl on 2017/9/3.
 */

public class MineItemView extends LinearLayout {
    private String string;
    private LinearLayout rootView;
    private TextView textView;
    private EditText mineItemEdit;

    public MineItemView(Context context) {
        this(context,null);
    }
    public MineItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,-1);
        init(context);
    }

    public MineItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MineItemView, 0, 0);
        string = typedArray.getString(R.styleable.MineItemView_lefttext);
        typedArray.recycle();
    }

    private void init(Context context) {
        rootView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.mine_item, this,true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        textView= (TextView) rootView.findViewById(R.id.lift);
        mineItemEdit= (EditText) rootView.findViewById(R.id.mineItemEdit);
        if(!TextUtils.isEmpty(string)){
            textView.setText(string);
        }
    }
    public String getEditText(){
        return  mineItemEdit.getText().toString().trim();
    }
    public void setEditText(String text){
        mineItemEdit.setText(text);
    }
}
