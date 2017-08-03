package com.example.hbl.bluetooth;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

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
        adapter = new MyAdapter(getActivity(), App.getDatas());
        listview.setAdapter(adapter);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    int getLayoutId() {
        return R.layout.fragment_model;
    }

    @OnClick(R.id.btnSure)
    public void onClick() {

    }
}
