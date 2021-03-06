package com.zero.yoga;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zero.yoga.base.BaseActivity;
import com.zero.yoga.mine.MineFragment;
import com.zero.yoga.mine.UpdateInfoEvent;
import com.zero.yoga.stadiums.StadiumFragment;
import com.zero.yoga.stadiums.StadiumSearchActivity;
import com.zero.yoga.utils.BottomNavigationViewHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.Nullable;

/**
 * Created by zero on 2018/8/13.
 */

public class MainActivity extends BaseActivity {

    private ViewPager viewPager;
    private MenuItem menuItem;
    private BottomNavigationView bottomNavigationView;

    private TextView tvTitle;

    private ImageView ivSearch;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewpager);
        tvTitle = findViewById(R.id.tvTitle);
        ivSearch = findViewById(R.id.ivSearch);

        tvTitle.setText("场馆");

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StadiumSearchActivity.jump2StadiumSearchActivity(MainActivity.this);
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        //默认 >3 的选中效果会影响ViewPager的滑动切换时的效果，故利用反射去掉
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.stadiums:
                                viewPager.setCurrentItem(0);
                                tvTitle.setText("场馆");
                                ivSearch.setVisibility(View.VISIBLE);
                                break;
                            case R.id.mine:
                                viewPager.setCurrentItem(1);
                                tvTitle.setText("个人中心");
                                ivSearch.setVisibility(View.GONE);
                                break;
                        }
                        return false;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.setSelectedItemId(position);
                if (position == 0) {
                    tvTitle.setText("场馆");
                    ivSearch.setVisibility(View.VISIBLE);
                } else {
                    tvTitle.setText("个人中心");
                    ivSearch.setVisibility(View.GONE);
                }
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        setupViewPager(viewPager);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UpdateInfoEventBus(UpdateInfoEvent updateInfoEvent) {
        mineFragment.initData();
    }

    private MineFragment mineFragment = MineFragment.newInstance();

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(StadiumFragment.newInstance());
        adapter.addFragment(mineFragment);
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
