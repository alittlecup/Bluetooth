package com.example.hbl.bluetooth;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class ModelFragment extends BaseFragment {


    @BindView(R.id.listview)
    ListView listview;
    @BindView(R.id.btnSure)
    Button btnSure;
    MyAdapter adapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initOnce();
    }

    protected void initOnce() {
        if(adapter!=null&&adapter.getCheckedPosition()!=-1){
            return;
        }
        ArrayList<ModelData> strings=new ArrayList<>();
        for(int i=0;i<30;i++){
            ModelData data=new ModelData();
            data.setString("加热时长30分钟 \n强度80%");
            strings.add(data);
        }
        adapter=new MyAdapter(getActivity(),strings);
        listview.setAdapter(adapter);
    }


    @Override
    int getLayoutId() {
        return R.layout.fragment_model;
    }
    @OnClick(R.id.btnSure)
    public void onClick() {

    }
}
