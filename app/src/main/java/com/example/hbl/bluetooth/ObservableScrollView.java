package com.example.hbl.bluetooth;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by hbl on 2017/8/14.
 */

public class ObservableScrollView extends ScrollView {
    private ScrollViewListener scrollViewListener = null;

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void addOnScrollChangeListener(ScrollViewListener listener){
        this.scrollViewListener=listener;
    }
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(scrollViewListener!=null){
            scrollViewListener.onScrollChanged(this,l,t,oldl,oldt);
        }
    }

    public interface ScrollViewListener {
       void onScrollChanged(ScrollView view,int x,int y,int oldx,int oldy);
    }
}
