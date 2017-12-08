package com.example.hbl.bluetooth.util;

import android.support.annotation.DrawableRes;
import android.support.annotation.IntegerRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hbl.bluetooth.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by huangbaole on 2017/12/2.
 */

public class GridItem extends AbstractFlexibleItem<GridItem.GridViewHolder> {

    String text;
    @DrawableRes
    int res;

    public GridItem(String text, @DrawableRes int res) {
        this.text = text;
        this.res = res;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof GridItem && ((GridItem) o).getText().equalsIgnoreCase(text);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.gird_item;
    }

    @Override
    public GridViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new GridViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, GridViewHolder holder, int position, List payloads) {
        holder.text.setText(text);
        holder.topImage.setImageResource(res);
    }

    public class GridViewHolder extends FlexibleViewHolder {
        @BindView(R.id.top_image)
        ImageView topImage;
        @BindView(R.id.text)
        TextView text;

        public GridViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }

    }
}
