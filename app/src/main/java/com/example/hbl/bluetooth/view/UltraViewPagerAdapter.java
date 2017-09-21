package com.example.hbl.bluetooth.view;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.hbl.bluetooth.R;

import java.util.List;

/**
 * Created by hbl on 2017/9/21.
 */

public class UltraViewPagerAdapter extends PagerAdapter {
    List<Integer> mDrawResours;
    public UltraViewPagerAdapter(List<Integer> drawResours){
            this.mDrawResours=drawResours;
    }
    @Override
    public int getCount() {
        return mDrawResours.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = (ImageView) LayoutInflater.from(container.getContext()).inflate(R.layout.viewpager_item, null);
        imageView.setImageResource(mDrawResours.get(position));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
       ImageView imageView= (ImageView) object;
       container.removeView(imageView);
    }
}
