package com.example.szx.recycleviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public final class MyItemDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "MyItemDecoration";

    public static final int STREET_DIRECTION = 2;
    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;

    public static final int STREET = STREET_DIRECTION;   //台阶式方向绘制
    private int mColNum;  //每一行几个台阶

    private int mOrientation;

    private final Rect mBounds = new Rect();

    private Drawable mDivider;
    //framework Themes.xml中 <item name="listDivider">@drawable/divider_horizontal_dark</item>
    private static final int[] ATTRS = new int[]{ android.R.attr.listDivider };

    public MyItemDecoration(Context context, int orientation, int colNum) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        if (mDivider == null) {
            Log.w(TAG, "@android:attr/listDivider was not set in the theme used for this "
                    + "MyItemDecoration. Please set that attribute all call setDrawable()");
        }
        a.recycle();
        setOrientation(orientation);
        mColNum = colNum;
    }

    public void setOrientation(int orientation) {
        //参数校验
        if (orientation != HORIZONTAL && orientation != VERTICAL && orientation != STREET) {
            throw new IllegalArgumentException(
                    "Invalid orientation. It should be either HORIZONTAL or VERTICAL");
        }
        mOrientation = orientation;
    }
    public void setDrawable(@NonNull Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException("Drawable cannot be null.");
        }
        mDivider = drawable;
    }

    //在RecyclerView的drawChildren之前执行
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() == null || mDivider == null) {
            return;
        }
        if (mOrientation == VERTICAL) {
            drawVertical(c, parent);
        } else if (mOrientation == VERTICAL){
            drawHorizontal(c, parent);
        } else {
            drawStreet(c, parent);
        }
    }

    //在RecyclerView的drawChildren之后执行
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    private void drawStreet(Canvas canvas, RecyclerView parent) {

    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int left;
        final int right;
        //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
        Log.d("szx", "parent.getClipToPadding():"+parent.getClipToPadding());
        if (parent.getClipToPadding()) {   //是否支持padding, 只显示clipRect部分画布
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }
        //以上完成left,right的设置

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            Log.d("szx", "child.getTranslationY():"+child.getTranslationY());
            final int bottom = mBounds.bottom + Math.round(child.getTranslationY());  //child.getTranslationY()默认返回0
            final int top = bottom - mDivider.getIntrinsicHeight();  //默认Drawable的IntrinsicHeight为2
            //Log.d("szx", "bottom:"+bottom+",top:"+top+",mDivider.getIntrinsicHeight():"+mDivider.getIntrinsicHeight());
            mDivider.setBounds(left, top, right, bottom);  //分割线的范围
            mDivider.draw(canvas);   //绘制分割线
        }
        canvas.restore();
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int top;
        final int bottom;
        //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            canvas.clipRect(parent.getPaddingLeft(), top,
                    parent.getWidth() - parent.getPaddingRight(), bottom);
        } else {
            top = 0;
            bottom = parent.getHeight();
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getLayoutManager().getDecoratedBoundsWithMargins(child, mBounds);
            //mBounds就是这个child的范围；其中如果垂直方向则加上了分割线的高度；水平方向则加上了分割线的宽度

            //View.getTranslationX()计算的是该View在X轴的偏移量。初始值为0，向左偏移值为负，向右偏移值为正。
            //View.getTranslationY()计算的是该View在Y轴的偏移量。初始值为0，向上偏移为负，向下偏移为证。
            final int right = mBounds.right + Math.round(child.getTranslationX());
            final int left = right - mDivider.getIntrinsicWidth();
            mDivider.setBounds(left, top, right, bottom);  //分割线的范围
            mDivider.draw(canvas);
        }
        canvas.restore();
    }


    //outRect的上下左右就是 final Rect insets = lp.mDecorInsets;
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        if (mDivider == null) {
            outRect.set(0, 0, 0, 0);
            return;
        }
        if (mOrientation == VERTICAL) {
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        }
    }

}
