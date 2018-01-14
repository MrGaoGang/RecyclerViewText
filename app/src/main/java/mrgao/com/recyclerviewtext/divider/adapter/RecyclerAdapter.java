package mrgao.com.recyclerviewtext.divider.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mrgao.com.recyclerviewtext.R;


/**
 * Created by mr.gao on 2018/1/12.
 * Package:    mrgao.com.recyclerviewtext.dividerModule.adapter
 * Create Date:2018/1/12
 * Project Name:RecyclerViewText
 * Description:zhge
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    List<String> mStringList;
    Context mContext;

    public RecyclerAdapter(Context context) {
        mContext = context;
        mStringList = new ArrayList<>();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mTextView.setText(mStringList.get(position));
    }

    @Override
    public int getItemCount() {
        return mStringList.size();
    }


    public void addAll(List<String> stringList) {
        if (mStringList != null) {
            mStringList.addAll(stringList);
        }
    }

    public void add(String s,int position) {
        mStringList.add(position,s);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        mStringList.remove(position);
        notifyItemRemoved(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTextView = getView(itemView, R.id.item);
        }
    }

    public <T extends View> T getView(View view, int id) {
        return (T) view.findViewById(id);

    }
}
