package com.example.hbl.bluetooth;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by hbl on 2017/6/7.
 */

public abstract class BaseFragment extends Fragment {
    View RootView;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (RootView == null) {
            RootView = inflater.inflate(getLayoutId(), container, false);
        }
        ViewGroup parent = (ViewGroup) RootView.getParent();
        if (parent != null) {
            parent.removeView(RootView);
        }
        return RootView;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (unbinder == null) {
            unbinder = ButterKnife.bind(this, view);
        }
        super.onViewCreated(view, savedInstanceState);
    }

    protected abstract int getLayoutId();

    @Override
    public void onDestroyView() {
        if (unbinder != null && unbinder != Unbinder.EMPTY) {
            unbinder.unbind();
            unbinder = null;
        }
        super.onDestroyView();
    }


}
