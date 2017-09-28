package com.example.hbl.bluetooth.home;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.hbl.bluetooth.App;
import com.example.hbl.bluetooth.BaseFragment;
import com.example.hbl.bluetooth.ModelData;
import com.example.hbl.bluetooth.MyAdapter;
import com.example.hbl.bluetooth.R;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import java.util.List;

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
        if (adapter != null && adapter.getCheckedPosition() != -1) {
            return;
        }
        addData();
        adapter = new MyAdapter(getActivity(), App.getDatas());
        listview.setAdapter(adapter);
    }
    private void addData(){
        ModelData data=new ModelData();
        data.setUp("30");
        data.setDown("30");
        data.setTime("30");
        App.addData(data);
        App.addData(data);
        App.addData(data);
        App.addData(data);
        App.addData(data);
        App.addData(data);
        App.addData(data);
        App.addData(data);
        App.addData(data);
        App.addData(data);
        App.addData(data);
        App.addData(data);
        App.addData(data);
        App.addData(data);
        App.addData(data);
        App.addData(data);
        App.addData(data);
        App.addData(data);
        App.addData(data);
        App.addData(data);
        App.addData(data);
        App.addData(data);
        App.addData(data);
        App.addData(data);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
   protected int getLayoutId() {
        return R.layout.fragment_model;
    }

    @OnClick(R.id.btnSure)
    public void onClick() {

    }


}
