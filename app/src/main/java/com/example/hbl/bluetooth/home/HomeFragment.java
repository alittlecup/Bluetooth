package com.example.hbl.bluetooth.home;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.hbl.bluetooth.BaseFragment;
import com.example.hbl.bluetooth.R;
import com.example.hbl.bluetooth.SingleDownFragment;
import com.example.hbl.bluetooth.SingleFragment;
import com.example.hbl.bluetooth.util.CommonFlexAdapter;
import com.example.hbl.bluetooth.util.GridItem;
import com.example.hbl.bluetooth.util.ItemData;
import com.example.hbl.bluetooth.view.UltraViewPagerAdapter;
import com.tmall.ultraviewpager.UltraViewPager;
import com.tmall.ultraviewpager.transformer.UltraScaleTransformer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by huangbaole on 2017/12/2.
 */

public class HomeFragment extends BaseFragment implements FlexibleAdapter.OnItemClickListener {
    @BindView(R.id.edit_home_search)
    EditText editHomeSearch;
    @BindView(R.id.ultra_viewpager)
    UltraViewPager ultraViewPager;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    List<GridItem> items = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView() {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initRecyclerView();
        initViewPager();
    }

    private List<Fragment> fragments = new ArrayList<>();

    private void initData() {
        List<ItemData> data = new ArrayList<>();
        data.add(new ItemData("冲锋衣", R.drawable.home_chongfenyi));
        data.add(new ItemData("内衣", R.drawable.home_neiyi));
        data.add(new ItemData("马甲", R.drawable.home_majia));
        data.add(new ItemData("护腰", R.drawable.home_huyao));
        data.add(new ItemData("护颈", R.drawable.home_hujing));
        data.add(new ItemData("聊天", R.drawable.home_chat));
        for (ItemData item : data) {
            items.add(new GridItem(item.getText(), item.getRes()));
        }
        fragments.add(new DoubleFragment());
        fragments.add(new DoubleFragment());
        fragments.add(new SingleFragment());
        fragments.add(new SingleDownFragment());
        fragments.add(new SingleFragment());
    }

    private void initRecyclerView() {

        CommonFlexAdapter commonFlexAdapter = new CommonFlexAdapter(items);
        recyclerview.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerview.setAdapter(commonFlexAdapter);
        commonFlexAdapter.addListener(this);

    }

    private void initViewPager() {
        List<Integer> draws = new ArrayList<>();
        draws.add(R.drawable.banner);
        draws.add(R.drawable.banner2);
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
//UltraPagerAdapter 绑定子view到UltraViewPager
        PagerAdapter adapter = new UltraViewPagerAdapter(draws);
        ultraViewPager.setAdapter(adapter);
        //内置indicator初始化
        ultraViewPager.initIndicator();
        //设置indicator样式
        ultraViewPager.getIndicator()
                .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                .setFocusColor(getResources().getColor(R.color.getcode))
                .setNormalColor(Color.WHITE)
                .setRadius((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
//设置indicator对齐方式
        ultraViewPager.getIndicator().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        ultraViewPager.getIndicator().setMargin(0, 0, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
//构造indicator,绑定到UltraViewPager
        ultraViewPager.getIndicator().build();

//设定页面循环播放
        ultraViewPager.setInfiniteLoop(true);
        ultraViewPager.setPageTransformer(false, new UltraScaleTransformer());
//设定页面自动切换  间隔2秒
        ultraViewPager.setAutoScroll(5000);
    }

    @Override
    public boolean onItemClick(int position) {
        Log.d("TAG", "onItemClick: " + position);

        ((HomeActivity) getActivity()).routeTo(fragments.get(position));
        return false;
    }
}
