package com.example.hbl.bluetooth.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.hbl.bluetooth.BaseFragment;
import com.example.hbl.bluetooth.MsgFragment;
import com.example.hbl.bluetooth.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by huangbaole on 2017/12/5.
 */

public class DoubleFragment extends BaseFragment {
    @BindView(R.id.tabs)
    FrameLayout tabs;
    @BindView(R.id.bottom_navigation_bar)
    BottomNavigationBar bottomNavigationBar;
    @BindView(R.id.container)
    FrameLayout container;
    Unbinder unbinder;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBottom();
    }

    private void initBottom() {
        getFragments();
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.opeartor_on, "连接模块").setInactiveIconResource(R.drawable.operator_un))
                .addItem(new BottomNavigationItem(R.drawable.module_on, "加热模块").setInactiveIconResource(R.drawable.module_un))
                .setFirstSelectedPosition(0)
                .initialise();
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int i) {
                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                if (fragmentList.get(i).isAdded()) {
                    fragmentTransaction.show(fragmentList.get(i));
                } else {
                    fragmentTransaction.add(R.id.tabs, fragmentList.get(i));
                }
                fragmentTransaction.commitAllowingStateLoss();
            }

            @Override
            public void onTabUnselected(int i) {
                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                fragmentTransaction.hide(fragmentList.get(i));
                fragmentTransaction.commitAllowingStateLoss();
            }

            @Override
            public void onTabReselected(int i) {
            }
        });
        getChildFragmentManager().beginTransaction().replace(R.id.tabs, fragmentList.get(0)).commitAllowingStateLoss();
    }

    private List<Fragment> fragmentList = new ArrayList<>();

    private void getFragments() {
        DoubleOpFragment fragment=new DoubleOpFragment();
        MsgFragment fragment1=new MsgFragment();

        fragmentList.add(fragment);
        fragmentList.add(fragment1);

    }
}
