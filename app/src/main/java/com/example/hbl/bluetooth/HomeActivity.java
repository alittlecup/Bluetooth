package com.example.hbl.bluetooth;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.realtabcontent)
    FrameLayout realtabcontent;
    @BindView(android.R.id.tabcontent)
    FrameLayout tabcontent;
    @BindView(android.R.id.tabhost)
    FragmentTabHost tabhost;
    private Class<?>[] fragmentArr = {OperationFragment.class, ModelFragment.class, SettingFragment.class};

    private int[] tabImageResArr = {
             R.drawable.selector_op
            , R.drawable.selector_model
            , R.drawable.selector_setting};

    private int[] tabTextResArr = {R.string.op,R.string.model,R.string.setting};
    private ArrayList<String> tabTagList;
    private int currentIndex;
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        initHost();
    }

    private void initHost() {
        tabhost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        tabTagList = new ArrayList<>();

        for (int i = 0; i < fragmentArr.length; i++) {
            tabTagList.add(getResources().getString(tabTextResArr[i]));
            TabHost.TabSpec tabSpec = tabhost.newTabSpec(tabTagList.get(i));
            tabSpec.setIndicator(getTabItemView(i));
            tabhost.addTab(tabSpec, fragmentArr[i], null);
        }
        //隐藏默认分割线
        tabhost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);

        //同步currentIndex
        tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                currentIndex= tabTagList.indexOf(tabId);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        tabhost.setCurrentTab(currentIndex);
    }

    private View getTabItemView(int i) {
        View view = View.inflate(this, R.layout.tabspec_home, null);
        ImageView ivTab = (ImageView) view.findViewById(R.id.ivTab);
        ivTab.setImageResource(tabImageResArr[i]);
        TextView tvTab = (TextView) view.findViewById(R.id.tvTab);
        tvTab.setText(tabTextResArr[i]);
        return view;
    }
}
