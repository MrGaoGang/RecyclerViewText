package mrgao.com.recyclerviewtext.divider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by mr.gao on 2018/1/13.
 * Package:    mrgao.com.recyclerviewtext.divider
 * Create Date:2018/1/13
 * Project Name:RecyclerViewText
 * Description:
 */

public class MyGridDividerItem extends RecyclerView.ItemDecoration {

    private String TAG = "MyGridDividerItem";
    private int[] ATTRS = new int[]{android.R.attr.listDivider};
    private Drawable mDrawable;
    private Paint mPaint;
    private int lineHeight = 1;


    public MyGridDividerItem(Context context) {
        TypedArray typedArray = context.obtainStyledAttributes(ATTRS);
        mDrawable = typedArray.getDrawable(0);
        typedArray.recycle();
    }

    public MyGridDividerItem(int color, int lineHeight) {
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setStrokeWidth(lineHeight);
        this.lineHeight = lineHeight;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        drawHorizontal(c, parent);

        drawVertal(c, parent);
    }

    /**
     * 绘制竖着的线
     *
     * @param canvas
     * @param parent
     */
    private void drawVertal(Canvas canvas, RecyclerView parent) {

        RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            int spanCount = ((GridLayoutManager) manager).getSpanCount();
            int count = parent.getChildCount();
            for (int i = 0; i < count; i++) {
                if (!isLastColumn(spanCount, i)) {//如果是最后一列的话，那么不绘制垂直的边框

                    View child = parent.getChildAt(i);
                    RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
                    int left = child.getRight() + layoutParams.rightMargin;
                    int top = child.getTop() - layoutParams.topMargin;
                    if (mDrawable != null) {
                        int right = left + mDrawable.getIntrinsicHeight();
                        int bottom = child.getBottom() + layoutParams.bottomMargin + mDrawable.getIntrinsicHeight();

                        mDrawable.setBounds(left, top, right, bottom);
                        mDrawable.draw(canvas);

                    } else if (mPaint != null) {
                        int right = left + lineHeight;
                        int bottom = child.getBottom() + layoutParams.bottomMargin + lineHeight;
                        canvas.drawRect(left, top, right, bottom, mPaint);
                    }
                }
            }
        }

    }


    /**
     * 绘制横着的线
     *
     * @param canvas
     * @param parent
     */
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            int count = parent.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
                int left = child.getLeft() - layoutParams.leftMargin;
                int top = child.getBottom() + layoutParams.bottomMargin;


                if (mDrawable != null) {
                    int bottom = top + mDrawable.getIntrinsicHeight();
                    int right = child.getRight() + layoutParams.rightMargin;
                    mDrawable.setBounds(left, top, right, bottom);
                    mDrawable.draw(canvas);

                } else if (mPaint != null) {
                    int bottom = top + lineHeight;
                    int right = child.getRight() + layoutParams.rightMargin ;

                    canvas.drawRect(left, top, right, bottom, mPaint);
                }

            }
        }
    }

    /**
     * 判断是否是最后一列
     *
     * @param spanCount
     * @param position
     * @return
     */
    private boolean isLastColumn(int spanCount, int position) {

        if ((position + 1) % spanCount == 0) {
            return true;
        }

        return false;
    }

    /**
     * 判断是否是最后一行
     *
     * @param spanCount
     * @param childCount
     * @param position
     * @return
     */
    private boolean isLastRow(int spanCount, int childCount, int position) {

        if (childCount % spanCount == 0) {
            if ((position + spanCount) / spanCount == childCount / spanCount) {
                return true;
            }
        } else {
            if (position / spanCount == childCount / spanCount) {
                return true;
            }
        }

        return false;
    }

    /**
     * 通过这个方法在每一个Item绘制的时候回提前调用，那么首先必须要获取到这个view的position；
     * 获取位置的时候 切记不要使用for循环，不然会调用很多次；
     *
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            int spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
            int count = parent.getAdapter().getItemCount();
            //获取到对应表的位置很重要，切记不要在这个里面使用for(int i=0;i<parent.getchildCount;i++),
            //因为这个方法是每一个Item回执单额时候 调用的
            int i = parent.getChildAdapterPosition(view);

            //最后一行切实最后一列，设置item偏移量都为0，那么就不会显示边框
            if (isLastColumn(spanCount, i) && isLastRow(spanCount, count, i)) {
                outRect.set(0, 0, 0, 0);
            } else {
                //否则：如果是最后一行的话，设置item偏移量 右边都为itemHeight，那么其他的就不会显示边框
                //如果是最后一列：设置item偏移量 下方为itemHeight，那么其他的就不会显示边框
                //否则：设置右边和下边的偏移量
                if (isLastRow(spanCount, count, i)) {
                    if (mDrawable != null) {
                        outRect.set(0, 0, mDrawable.getIntrinsicWidth(), 0);
                    } else if (mPaint != null) {
                        outRect.set(0, 0, lineHeight, 0);
                    }
                } else if (isLastColumn(spanCount, i)) {
                    if (mDrawable != null) {
                        outRect.set(0, 0, 0, mDrawable.getIntrinsicWidth());
                    } else if (mPaint != null) {
                        outRect.set(0, 0, 0, lineHeight);
                    }

                } else {
                    if (mDrawable != null) {
                        outRect.set(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
                    } else if (mPaint != null) {
                        outRect.set(0, 0, lineHeight, lineHeight);
                    }
                }
            }

        }
    }


}
