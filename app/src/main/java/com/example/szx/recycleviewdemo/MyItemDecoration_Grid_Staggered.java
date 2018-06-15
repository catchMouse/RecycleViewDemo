package com.example.szx.recycleviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;


//TODO 瀑布流的item位置会切换
public class MyItemDecoration_Grid_Staggered extends RecyclerView.ItemDecoration {
    private static final String TAG = "MyItemDecoration_Stag";

    private Drawable mDivider;
    private int mSpanCount;

    private int[] ATTRS = new int[]{android.R.attr.listDivider};

    public MyItemDecoration_Grid_Staggered(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        if (mDivider == null) {
            Log.d(TAG, "Please set that attribute or call setDrawable()");
        }
        a.recycle();
    }

    public void setDividerDrawable(Drawable divider) {
        this.mDivider = divider;
    }
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (parent.getLayoutManager() == null || mDivider == null) {
            return;
        }
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    //横线分隔符
    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();   //布局参数
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin
                    + mDivider.getIntrinsicWidth();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            Log.d("szx_tag", "index:"+i+"==top:"+top+",child.getBottom:"+child.getBottom());
            mDivider.setBounds(left, top, right, bottom);   //在一个范围内去绘制divider
            mDivider.draw(c);
        }
    }

    //竖线分隔符
    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicWidth();
            //Log.d("szx_tag", "left:"+left+",right:"+right +",child.left:"+child.getLeft());
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

    }

    /**
     *最后一列
     * @param manager
     * @param spanCount 列数
     * @return
     */
    private boolean isLastRaw(RecyclerView.LayoutManager manager, int spanCount, RecyclerView parent, int itemPosition) {
        int childCount = parent.getChildCount();
        //行数
        int line = childCount % spanCount == 0 ? childCount/spanCount : childCount/spanCount + 1;
        if (manager instanceof GridLayoutManager) {
            if ((itemPosition+1) % spanCount == 0) {
                return true;
            }
        } else if (manager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) manager).getOrientation();
            if (orientation == OrientationHelper.VERTICAL) {   //跟GridLayoutManager 表现一样
                if ((itemPosition+1) % spanCount == 0) {
                    return true;
                }
            } else {  //水平方向上判断最后一列
                if (itemPosition >= (childCount - childCount % line)) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     *最后一行
     * @param manager
     * @param spanCount 列数
     * @return
     */
    private boolean isLastCol(RecyclerView.LayoutManager manager, int spanCount, RecyclerView parent, int itemPosition) {
        int childCount = parent.getChildCount();
        //行数
        int line = childCount % spanCount == 0 ? childCount/spanCount : childCount/spanCount + 1;
        int lastLine_startIndex = (line - 1) * spanCount;  //下标大于lastLine_startIndex为最后一行元素
        if (manager instanceof GridLayoutManager) {
            if (itemPosition > lastLine_startIndex) {
                return true;
            }
        } else if (manager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) manager).getOrientation();
            if (orientation == OrientationHelper.VERTICAL) {   //跟GridLayoutManager 表现一样
                if (itemPosition > lastLine_startIndex) {
                    return true;
                }
            } else {  //水平方向上判断最后一行
                if ((itemPosition + 1) % line == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    //outRect去设置了绘制的范围
    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        super.getItemOffsets(outRect, itemPosition, parent);

        //
        RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (!(manager instanceof GridLayoutManager) && !(manager instanceof StaggeredGridLayoutManager)) {
            throw new RuntimeException("Invalid LayoutManager. LayoutManager should be GridLayoutManager or StaggeredGridLayoutManager");
        }

        //列数
        if (manager instanceof GridLayoutManager) {
            mSpanCount = ((GridLayoutManager) manager).getSpanCount();
        } else if (manager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) manager).getOrientation();
            mSpanCount = ((StaggeredGridLayoutManager) manager).getSpanCount();
        } else {
            mSpanCount = 1;
        }
        Log.d("szx_tag", "mSpanCount:"+mSpanCount);

        if (mDivider == null) {
            outRect.set(0, 0, 0, 0);
            return;
        }

        //最后一行最后一列
        if (isLastCol(manager, mSpanCount, parent, itemPosition) && isLastRaw(manager, mSpanCount, parent, itemPosition)) {
            outRect.set(0, 0, 0, 0);
            return;
        }
        if (isLastCol(manager, mSpanCount, parent, itemPosition)) {
            //最后一行
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);  //同样在代码上会绘制decoration，但是超出屏幕显示区域了
            return;
        }
        //最后一列
        if (isLastRaw(manager, mSpanCount, parent, itemPosition)) {
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            return;
        }
        outRect.set(0, 0, mDivider.getIntrinsicWidth(), mDivider.getIntrinsicHeight());

    }


}
