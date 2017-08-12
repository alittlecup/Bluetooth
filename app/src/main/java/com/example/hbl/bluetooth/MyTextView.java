package com.example.hbl.bluetooth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * Created by hbl on 2017/8/12.
 */

public class MyTextView extends android.support.v7.widget.AppCompatTextView {
    private Paint paint;

    public MyTextView(Context context) {
        super(context);
        init();
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.colorAccent));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(new RectF(0, 0, width, height), height / 3f, height / 3f, paint);
        super.onDraw(canvas);

    }

    private int width;
    private int height;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }
    public void setColor(int color){
        if(color>6){
            paint.setColor(Color.RED);
        }else if(color>3){
            paint.setColor(Color.BLUE);
        }else {
            paint.setColor(Color.GREEN);
        }
        invalidate();
    }
}
