package com.example.szx.recycleviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * 这种布局下：如果grid为两列的话，第一次加载的时候，同一行的两列高度不一致： child.getBottom() 返回值相差 mDivider.getIntrinsicHeight()
 * 来回上下滑动RecyclerView后，同一行的两列高度一致
 * 暂时没有找到原因  ----完全限制item的宽高也无效
 */
//TODO 上面描述的bug
public class MyItemDecoration_Grid extends RecyclerView.ItemDecoration {
    private static final String TAG = "MyItemDecoration_Grid";

    private Drawable mDivider;
    private int mSpanCount;
    private boolean isArrivalBottom = false;

    private int[] ATTRS = new int[]{android.R.attr.listDivider};

    public MyItemDecoration_Grid(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        if (mDivider == null) {
            Log.w(TAG, "@android:attr/listDivider was not set in the theme used for this "
                    + "MyItemDecoration. Please set that attribute all call setDrawable()");
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


    //outRect去设置了绘制的范围
    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        super.getItemOffsets(outRect, itemPosition, parent);

        //
        RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (!(manager instanceof GridLayoutManager)) {
            throw new RuntimeException("Invalid LayoutManager. LayoutManager should be GridLayoutManager");
        }
        mSpanCount = ((GridLayoutManager) manager).getSpanCount();

        //
        if (mDivider == null) {
            outRect.set(0, 0, 0, 0);
            return;
        }

        int childCount = parent.getChildCount();
        //行数
        int line = childCount % mSpanCount == 0 ? childCount/mSpanCount : childCount/mSpanCount + 1;
        int lastLine_startIndex = (line - 1) * mSpanCount;  //下标大于lastLine_startIndex为最后一行元素

        //最后一行最后一列
        if (itemPosition > lastLine_startIndex && (itemPosition+1) % mSpanCount == 0) {
            outRect.set(0, 0, 0, 0);
            return;
        }
        if (itemPosition > lastLine_startIndex) {
            //最后一行
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);  //同样在代码上会绘制decoration，但是超出屏幕显示区域了
            return;
        }

        if ((itemPosition+1) % mSpanCount == 0) {
            //最后一列
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            return;
        }

        outRect.set(0, 0, mDivider.getIntrinsicWidth(), mDivider.getIntrinsicHeight());

    }
}
