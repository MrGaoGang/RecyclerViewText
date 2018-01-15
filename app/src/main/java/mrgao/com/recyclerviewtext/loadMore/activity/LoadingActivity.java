package mrgao.com.recyclerviewtext.loadMore.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import mrgao.com.recyclerviewtext.R;
import mrgao.com.recyclerviewtext.loadMore.adapter.DataAdapter;
import mrgao.com.recyclerviewtext.loadMore.recyclerview.YRecyclerView;
import mrgao.com.recyclerviewtext.loadMore.views.YRecyclerViewItemDivider;

public class LoadingActivity extends AppCompatActivity implements YRecyclerView.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    String TAG = "LoadingActivity";
    YRecyclerView mYRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    DataAdapter dataAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        mYRecyclerView = $(R.id.yRecyclerView);
        mSwipeRefreshLayout = $(R.id.swipe);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mYRecyclerView.setLoadMore(this);
        mYRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mYRecyclerView.addItemDecoration(new YRecyclerViewItemDivider(YRecyclerViewItemDivider.VERTICAL_LIST, Color.BLACK, 5));
     /* mYRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
       mYRecyclerView.addItemDecoration(new GridStaggerDivider(this));*/
        mYRecyclerView.setItemAnimator(new DefaultItemAnimator());

         dataAdapter=new DataAdapter();


        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            strings.add("数据" + i);
        }
        dataAdapter.addAll(strings);

        mYRecyclerView.setAdapter(dataAdapter);
        View empty = LayoutInflater.from(this).inflate(R.layout.view_empty,null,false);
        mYRecyclerView.setEmptyView(empty);
        mYRecyclerView.setEmptyView(empty);
    }


    public <T extends View> T $(int id) {
        return (T) findViewById(id);
    }


    @Override
    public void loadMore() {
        Log.i(TAG, "加载更多调用了");
        mYRecyclerView.setLoadMoreState(YRecyclerView.LOADING);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int count = dataAdapter.getItemCount()+1;
                if (count < 50) {
                    List<String> strings = new ArrayList<>();
                    for (int i = count - 1; i < 5 + count; i++) {
                        strings.add("数据" + i);
                    }
                    dataAdapter.addAll(strings);
                    mYRecyclerView.setLoadMoreState(YRecyclerView.LOADING_COMPLETE);
                } else {
                    mYRecyclerView.setLoadMoreState(YRecyclerView.LOADING_END);
                }


            }
        }, 2000);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dataAdapter.clearAll();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }
}
