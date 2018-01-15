package mrgao.com.recyclerviewtext.loadMore.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import mrgao.com.recyclerviewtext.R;
import mrgao.com.recyclerviewtext.loadMore.adapter.DataAdapter;
import mrgao.com.recyclerviewtext.loadMore.recyclerview.YRecyclerView;

public class LoadingActivity extends AppCompatActivity implements YRecyclerView.OnLoadMoreListener, View.OnClickListener {
    String TAG = "LoadingActivity";
    YRecyclerView mYRecyclerView;

    DataAdapter dataAdapter;
    Button mEmptyBtn, mErrorBtn, mRestoreBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        mYRecyclerView = $(R.id.yRecyclerView);
        mEmptyBtn = $(R.id.showEmpty);
        mErrorBtn = $(R.id.showError);
        mRestoreBtn = $(R.id.restore);
        mErrorBtn.setOnClickListener(this);
        mEmptyBtn.setOnClickListener(this);
        mRestoreBtn.setOnClickListener(this);

        mYRecyclerView.setLoadMore(this);
        mYRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //封装好了的线性分割线
        mYRecyclerView.addLinearDivider(YRecyclerView.VERTICAL_LIST);
        mYRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加错误ss的View
        mYRecyclerView.setErrorView(R.layout.error_view);

        dataAdapter = new DataAdapter();


        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            strings.add("数据" + i);
        }
        dataAdapter.addAll(strings);
        mYRecyclerView.setAdapter(dataAdapter);

        /*//这个是添加空View
        View empty = LayoutInflater.from(this).inflate(R.layout.view_empty, null, false);
        mYRecyclerView.setEmptyView(empty);*/

        mYRecyclerView.setEmptyView(R.layout.view_empty);

       mYRecyclerView.addHeaderView(R.layout.header_view);
    }


    public <T extends View> T $(int id) {
        return (T) findViewById(id);
    }


    @Override
    public void loadMore() {
        mYRecyclerView.setLoading();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int count = dataAdapter.getItemCount() + 1;
                if (count < 50) {
                    List<String> strings = new ArrayList<>();
                    for (int i = count - 1; i < 5 + count; i++) {
                        strings.add("数据" + i);
                    }
                    dataAdapter.addAll(strings);
                    mYRecyclerView.setLoadingComplete();
                } else {

                    mYRecyclerView.setLoadingNoMore("唉呀妈呀，没数据咯");
                }


            }
        }, 2000);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.showEmpty:
                dataAdapter.clearAll();
                break;

            case R.id.showError:
                mYRecyclerView.showError(true);
                break;
            case R.id.restore:
                dataAdapter.clearAll();
                List<String> strings = new ArrayList<>();
                for (int i = 0; i < 30; i++) {
                    strings.add("数据" + i);
                }

                mYRecyclerView.reset();
                dataAdapter.addAll(strings);
                break;
        }
    }



  /*  @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dataAdapter.clearAll();
                mYRecyclerView.setRefreshing(false);
            }
        }, 2000);
    }*/
}
