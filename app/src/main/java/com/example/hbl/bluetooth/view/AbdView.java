package com.example.hbl.bluetooth.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.hbl.bluetooth.R;

/**
 * Created by hbl on 2017/9/3.
 */

public class AbdView extends View{
    BitmapDrawable drawable;
    public AbdView(Context context) {
        super(context);
    }

    public AbdView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AbdView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AbdView, defStyleAttr,0);
        drawable =(BitmapDrawable)typedArray.getDrawable(R.styleable.AbdView_drawable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        LinearGradient linearGradient=new LinearGradient(0,0,getMeasuredWidth(),0,new int[]{Color.parseColor("#5abff0"),Color.parseColor("#2750ae")},null, LinearGradient.TileMode.CLAMP);
//        Paint paint=new Paint();
//        paint.setColor(Color.GREEN);
//        paint.setShader(linearGradient);
//        canvas.drawRect(0,0,getMeasuredWidth(),getMeasuredHeight(),paint);

//        canvas.drawBitmap(bitmap,10,10,new Paint());
    }
}
