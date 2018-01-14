package mrgao.com.recyclerviewtext.loadMore.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mrgao.com.recyclerviewtext.R;
import mrgao.com.recyclerviewtext.loadMore.views.TwoFishView;

/**
 * Created by mr.gao on 2018/1/14.
 * Package:    mrgao.com.recyclerviewtext.loadMore.recyclerview
 * Create Date:2018/1/14
 * Project Name:RecyclerViewText
 * Description:
 */

public class YRecyclerView extends RecyclerView {

    //正在加载
    public static final int LOADING = -1;
    //加载完成
    public static final int LOADING_COMPLETE = -2;
    //加载到底
    public static final int LOADING_END = -3;
    //默认情况是加载完成
    int mFooterState = LOADING_COMPLETE;


    public String TAG = "YRecyclerView";

    private OnScrollCallback mOnScrollCallback;

    private boolean mIsSlowUp = false;//是否是向上滑动

    private OnLoadMoreListener mLoadMoreListener;

    private LoadMoreWrapAdapter mWrapAdapter;

    private final RecyclerView.AdapterDataObserver mDataObserver = new DataObserver();

    private View mEmptyView;

    public YRecyclerView(Context context) {
        super(context);

    }

    public YRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);


    }

    public YRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);


    }

    private void addOnScrollListener() {
        this.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int lastItemPosition = -1;
                int itemCount = 0;
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LayoutManager manager = recyclerView.getLayoutManager();
                    if (manager instanceof LinearLayoutManager) {
                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) manager;

                        lastItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                        itemCount = linearLayoutManager.getItemCount();

                    } else if (manager instanceof GridLayoutManager) {
                        GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;
                        //GridLayoutManager判断上拉加载还没有做
                        lastItemPosition = gridLayoutManager.findLastVisibleItemPosition();
                        itemCount = gridLayoutManager.getItemCount();

                    } else if (manager instanceof StaggeredGridLayoutManager) {
                        StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) manager;
                        int[] into = new int[((StaggeredGridLayoutManager) staggeredGridLayoutManager).getSpanCount()];
                        staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(into);
                        lastItemPosition = findMax(into);
                        itemCount = staggeredGridLayoutManager.getItemCount();
                    }


                    // 判断是否滑动到了最后一个item，并且是向上滑动
                    if (lastItemPosition == (itemCount - 1) && mIsSlowUp) {
                        //加载更多
                        if (mLoadMoreListener != null) {
                            mLoadMoreListener.loadMore();
                        }

                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mIsSlowUp = dy > 0;

            }
        });
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    /**
     * 滑动监听事件
     */
    private void addScrollListener() {
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mOnScrollCallback.onStateChanged(YRecyclerView.this, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy >= 0) {
                    mOnScrollCallback.onScrollDown(YRecyclerView.this, dy);
                } else {
                    mOnScrollCallback.onScrollUp(YRecyclerView.this, dy);
                }
            }

        });
    }

    /**
     * 设置监听事件
     *
     * @param loadMore
     */

    public void setLoadMore(OnLoadMoreListener loadMore) {
        if (loadMore != null) {
            mLoadMoreListener = loadMore;
            addOnScrollListener();
        }
    }

    /**
     * 监听滑动状态
     *
     * @param onScrollCallback
     */
    public void setOnScrollCallback(OnScrollCallback onScrollCallback) {
        if (onScrollCallback != null) {
            this.mOnScrollCallback = onScrollCallback;
            addScrollListener();
        }

    }

    /**
     * 设置适配器
     *
     * @param adapter
     */

    public void setAdapter(Adapter adapter) {
        mWrapAdapter = new LoadMoreWrapAdapter(adapter);
        super.setAdapter(mWrapAdapter);
        adapter.registerAdapterDataObserver(mDataObserver);
        mDataObserver.onChanged();

    }

    /**
     * 获取头部的数量
     *
     * @return
     */
    public int getHeaderViewCount() {
        if (mWrapAdapter != null) {
            return mWrapAdapter.getHeaderCount();
        } else {
            return 0;
        }
    }

    /**
     * 添加头部
     *
     * @param view
     */
    public void addHeaderView(View view) {
        if (mWrapAdapter != null) {
            mWrapAdapter.addHeaderView(view);
        }
    }

    /**
     * 添加为空的时候的方法
     *
     * @param view
     */
    public void setEmptyView(View view) {
        if (mWrapAdapter != null) {
            mWrapAdapter.setEmptyView(view);
            mDataObserver.onChanged();
            checkIfEmpty();
        }
    }

    /**
     * 设置底部的状态
     *
     * @param footerState
     */
    public void setLoadMoreState(int footerState) {
        if (mWrapAdapter != null) {
            mFooterState = footerState;
            mWrapAdapter.notifyDataSetChanged();
        }

    }

    /**
     * 检查是否应该为空
     */
    private void checkIfEmpty() {
        if (mEmptyView != null && mWrapAdapter != null) {
            final boolean emptyViewVisible =
                    mWrapAdapter.getItemCount() == mWrapAdapter.getHeaderCount();
            mEmptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
            setVisibility(emptyViewVisible ? GONE : VISIBLE);
        }
    }

    /**
     * 设置加载更多的接口回调
     */
    public interface OnLoadMoreListener {
        void loadMore();
    }

    /**
     * 滑动监听回调
     */
    public interface OnScrollCallback {

        void onStateChanged(YRecyclerView recycler, int state);

        void onScrollUp(YRecyclerView recycler, int dy);

        void onScrollDown(YRecyclerView recycler, int dy);
    }


    /**
     * Created by mr.gao on 2018/1/14.
     * Package:    mrgao.com.recyclerviewtext.loadMore.adapter
     * Create Date:2018/1/14
     * Project Name:RecyclerViewText
     * Description:
     */

    private class DataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            if (mWrapAdapter != null) {
                mWrapAdapter.notifyDataSetChanged();
            }
            checkIfEmpty();
        }
    }

    private class LoadMoreWrapAdapter extends Adapter<ViewHolder> {

        private RecyclerView.Adapter mAdapter;
        //普通的item
        public final int NORMAL_TYPE = -1;
        //底部的Item
        public final int FOOTER_TYPE = -2;
        //每个header必须有不同的type,不然滚动的时候顺序会变化
        private List<Integer> mHeaderTypes = new ArrayList<>();
        //底部的view
        private View mFooterView;
        //头部的view
        private List<View> mHeaderViews;


        public LoadMoreWrapAdapter(Adapter adapter) {
            mAdapter = adapter;
            mHeaderViews = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == FOOTER_TYPE) {
                mFooterView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_view, parent, false);
                return new FooterHolder(mFooterView);
            } else if (viewType == NORMAL_TYPE) {
                return mAdapter.onCreateViewHolder(parent, viewType);
            } else {
                return new HeaderViewHolder(getHeaderViewByType(viewType));
            }

        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (holder instanceof FooterHolder) {
                FooterHolder footerHolder = (FooterHolder) holder;
                System.out.println("Wraper:" + mFooterState);
                if (mFooterState == LOADING_COMPLETE) {
                    footerHolder.mProgressBar.setVisibility(GONE);
                    footerHolder.mTextView.setText("加载完成");
                } else if (mFooterState == LOADING) {
                    footerHolder.mProgressBar.setVisibility(VISIBLE);
                    footerHolder.mTextView.setText("正在加载....");

                } else if (mFooterState == LOADING_END) {
                    footerHolder.mProgressBar.setVisibility(GONE);
                    footerHolder.mTextView.setText("无更多数据");
                }

            } else if (holder instanceof HeaderViewHolder) {

            } else {
                mAdapter.onBindViewHolder(holder, position);
            }


        }

        @Override
        public int getItemCount() {
            if (mAdapter.getItemCount() > 0) {
                return mAdapter.getItemCount() + 1 + mHeaderViews.size();
            } else {
                return mHeaderViews.size();
            }

        }

        @Override
        public int getItemViewType(int position) {
            if (getItemCount() == mHeaderViews.size() && mHeaderViews.size() != 0) {
                return getHeaderTypeByPosition(position);
            } else {
                if (position + 1 == getItemCount()) {
                    return FOOTER_TYPE;
                } else if (mHeaderViews.size() != 0 && position < mHeaderViews.size()) {
                    return getHeaderTypeByPosition(position);
                } else {
                    return NORMAL_TYPE;
                }
            }

        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            mAdapter.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                final int spanCount = gridLayoutManager.getSpanCount();
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        // 如果当前是footer的位置，那么该item占据一行的个单元格，正常情况下占据1个单元格
                        return getItemViewType(position) == FOOTER_TYPE ? spanCount : 1;
                    }
                });
            }
            mAdapter.onAttachedToRecyclerView(recyclerView);
        }


        //根据header的ViewType判断是哪个header
        private View getHeaderViewByType(int itemType) {
            if (!isHeaderType(itemType)) {
                return null;
            }
            return mHeaderViews.get(itemType);
        }

        //判断一个type是否为HeaderType
        private boolean isHeaderType(int itemViewType) {
            return mHeaderViews.size() > 0 && mHeaderTypes.contains(itemViewType);
        }

        /**
         * 根据位置得到type
         *
         * @param position
         * @return
         */
        private int getHeaderTypeByPosition(int position) {
            if (position < mHeaderTypes.size()) {
                return mHeaderTypes.get(position);
            } else {
                throw new IndexOutOfBoundsException("YRecyclerView get header type by position out index");
            }
        }


        /**
         * 获取到头部的数量
         *
         * @return
         */
        public int getHeaderCount() {
            return mHeaderViews.size();
        }

        /**
         * 添加头部
         *
         * @param view
         */
        public void addHeaderView(View view) {
            if (view != null) {
                mHeaderTypes.add(mHeaderViews.size());
                mHeaderViews.add(view);
                notifyDataSetChanged();
            }
        }

        /**
         * 设置空布局
         *
         * @param view
         */
        public void setEmptyView(View view) {
            if (view != null) {
                mEmptyView = view;
            }
        }


        public class FooterHolder extends ViewHolder {
            TwoFishView mProgressBar;
            TextView mTextView;

            public FooterHolder(View itemView) {
                super(itemView);
                mProgressBar = getView(itemView, R.id.progressBar);
                mTextView = getView(itemView, R.id.footer);
            }
        }

        public class HeaderViewHolder extends ViewHolder {
            public HeaderViewHolder(View itemView) {
                super(itemView);
            }
        }

        private <T extends View> T getView(View view, int id) {
            return (T) view.findViewById(id);
        }


    }
}
