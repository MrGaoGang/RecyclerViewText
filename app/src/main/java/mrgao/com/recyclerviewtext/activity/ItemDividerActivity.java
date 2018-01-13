package mrgao.com.recyclerviewtext.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import mrgao.com.recyclerviewtext.R;
import mrgao.com.recyclerviewtext.adapter.RecyclerAdapter;
import mrgao.com.recyclerviewtext.divider.MyGridDividerItem;


public class ItemDividerActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    Button mAddBtn, deleteBtn;
    private RecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_divider);
        mRecyclerView = $(R.id.dividerRecycler);
        mAddBtn = $(R.id.addBtn);
        deleteBtn = $(R.id.deleteBtn);
        onClick();
        /*LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);*/
        GridLayoutManager layoutManager=new GridLayoutManager(this,4);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //mRecyclerView.addItemDecoration(new LinnerItemDivider(LinnerItemDivider.VERTICAL_LIST, Color.RED,1));
        mRecyclerView.addItemDecoration(new MyGridDividerItem(this));
        mAdapter = new RecyclerAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        initData();
    }


    private void initData() {
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            strings.add("数据" + i);
        }
        mAdapter.addAll(strings);
    }

    private void onClick() {
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.add("添加的数据"+mAdapter.getItemCount()+"",2);

            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.remove(2);
            }
        });
    }

    public <T extends View> T $(int id) {
        return (T) findViewById(id);
    }

}
