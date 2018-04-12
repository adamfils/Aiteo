package com.adamapps.aiteo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.adamapps.aiteo.adapter.SectionStateAdapter;
import com.adamapps.aiteo.fragments.AccountFragment;
import com.adamapps.aiteo.fragments.HomeFragment;
import com.adamapps.aiteo.fragments.MessageFragment;
import com.mypopsy.widget.FloatingSearchView;

import java.util.ArrayList;

import devlight.io.library.ntb.NavigationTabBar;

public class Home extends AppCompatActivity {
    ViewPager viewPager;
    FloatingSearchView floatingSearchView;
    RecyclerView recyclerView;
    NavigationTabBar tabBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        viewPager = findViewById(R.id.vp_horizontal_ntb);
        //floatingSearchView = findViewById(R.id.search);
        recyclerView = findViewById(R.id.home_post_list);
        tabBar = findViewById(R.id.ntb_horizontal);
        /*recyclerView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = MotionEventCompat.getActionMasked(motionEvent);
                switch (action){
                    case(MotionEvent.ACTION_UP):
                        floatingSearchView.setVisibility(View.GONE);
                        break;
                    case(MotionEvent.ACTION_DOWN):
                        floatingSearchView.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });*/
        initUi();
    }

    private void initUi() {
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.toolbarSmall);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        String[] colors = {"#efb435", "#bb3f3f", "#7a687f", "#39ad48", "#13bbaf"};
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_home),
                        Color.parseColor(colors[0])
                ).title("Home")
                        .badgeTitle("8")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_message),
                        Color.parseColor(colors[1])
                ).title("Chat")
                        .badgeTitle("new")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_person_pin),
                        Color.parseColor(colors[2])
                ).title("Account")
                        .badgeTitle("2")
                        .build()
        );

        tabBar.setModels(models);
        tabBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < tabBar.getModels().size(); i++) {
                    final NavigationTabBar.Model model = tabBar.getModels().get(i);
                    tabBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            model.showBadge();
                        }
                    }, i * 100);
                }
            }
        }, 500);

        final CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.toolbar);
        collapsingToolbarLayout.setExpandedTitleColor(Color.parseColor("#009F90AF"));
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#9f90af"));
        setupViewPager(viewPager);
        tabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(NavigationTabBar.Model model, int index) {
                switch (index) {
                    case 0:
                        setViewPager(index);
                        break;
                    case 1:
                        setViewPager(index);
                        break;
                    case 2:
                        setViewPager(index);
                        break;
                }
            }

            @Override
            public void onEndTabSelected(NavigationTabBar.Model model, int index) {

            }
        });
        tabBar.setModelIndex(0);


    }

    private void setupViewPager(ViewPager viewPager) {
        SectionStateAdapter adapter = new SectionStateAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "Fragment 1");
        adapter.addFragment(new MessageFragment(), "Fragment 2");
        adapter.addFragment(new AccountFragment(), "Fragment 3");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabBar.setModelIndex(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setViewPager(int fragmentNumber) {
        viewPager.setCurrentItem(fragmentNumber);
    }
}

