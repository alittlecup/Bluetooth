package com.example.hbl.bluetooth;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hbl on 2017/6/4.
 */

public class MyAdapter extends BaseAdapter {
    Context context;
    List<ModelData> list;
    SparseBooleanArray array = new SparseBooleanArray();
    int curPosition = -1;

    public MyAdapter(Context context, List<ModelData> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.checkbox.setTag(position);
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int pos = (int) buttonView.getTag();
                if (isChecked) {
                    array.clear();
                    array.put(pos, true);
                    curPosition=pos;
                    //do something
                } else {
                    array.delete(pos);
                    //do something else
                }
                notifyDataSetChanged();
            }
        });
        holder.checkbox.setChecked(array.get(position, false));
        holder.tvModel.setText(list.get(position).getString());
        holder.checkbox.setText("模式" + position + "  ");

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.checkbox)
        CheckBox checkbox;
        @BindView(R.id.tvModel)
        TextView tvModel;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

   public int getCheckedPosition(){
        return curPosition;
   }
    public void setChecked(int pos){
        curPosition=pos;
        array.put(pos, true);
    }
}
