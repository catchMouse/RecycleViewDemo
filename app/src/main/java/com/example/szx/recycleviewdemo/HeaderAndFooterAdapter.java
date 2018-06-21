package com.example.szx.recycleviewdemo;

import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//为recyclerview添加header,footer的适配器
/**
 *  思路:
 *  1.根据view type来具体加载不同的view, 即headerView, footerView, itemView具有不同的type值
 *  2.public int getItemViewType(int pos)方法可以返回对应位置的view的type
 *       pos=0; type=000; view=view01
 *       pos=1; type=111; view=view02
 *       pos=2; type=222; view=view03
 *       根据推演，可知三者之间存在映射关系
 *       比如: HashMap<K,V> headerViews; HashMap<K,V> footerViews; //K为integer类型, V为view类型
 *       SparseArray<E></>(SparseArrayCompat 兼容低版本)headerViews; put(int key, E value);  //键为int型, 该类为Android提供的，用于优化HashMap
 *       如果不嫌麻烦应可以使用HashMap来存储,其中key会有拆箱封箱的转换; 毕竟SparseArray<E></>(SparseArrayCompat兼容低版本)数据类型上更为合适一些.
 *  3. //需要根据viewType的值来判断当前的view是 header, item, footer
 *       那么可以分别为header,footer定义一个范围值
 *
 *  4. 针对布局管理器为 GridLayoutManager 的时候，headerView,footerView 应该占据单独一行或者一列 --重写onAttachedToRecyclerView
 *  5. 针对布局管理器为 StaggeredGridLayoutManager 的时候，headerView,footerView 应该占据单独一行或者一列
 */
public class HeaderAndFooterAdapter extends RecyclerView.Adapter {

    public static final String TAG = "HeaderAndFooterAdapter";

    SparseArrayCompat<View> headerViews = new SparseArrayCompat();
    SparseArrayCompat<View> footerViews = new SparseArrayCompat();

    //那么viewType的值1000-2000之间的就是header, viewType的值大于2000的就是footer
    public static final int HEADER_BASE_INDEX = 1000;
    public static final int FOOTER_BASE_INDEX = 2000;

    //装饰者模式---功能扩展时需要使用的设计模式
    RecyclerView.Adapter baseAdapter;
    public HeaderAndFooterAdapter(RecyclerView.Adapter adapter) {
        this.baseAdapter = adapter;
    }

    public void addHeaderView(View headerView) {
        headerViews.put(HEADER_BASE_INDEX + headerViews.size(), headerView);
    }
    public void addFooterView(View footerView) {
        footerViews.put(FOOTER_BASE_INDEX + footerViews.size(), footerView);
    }
    private boolean isHeaderView(int position) {
        if (position < headerViews.size()) {
            return true;
        }
        return false;
    }
    private boolean isFooterView(int position) {
        if (position >= (headerViews.size() + baseAdapter.getItemCount())) {
            return true;
        }
        return false;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //需要根据viewType的值来判断当前的view是 header, item, footer
        if (viewType >= FOOTER_BASE_INDEX) { //footer
            return new RecyclerView.ViewHolder(footerViews.get(viewType)) {};
        } else if (viewType >= HEADER_BASE_INDEX && viewType < FOOTER_BASE_INDEX) {  //header
            return new RecyclerView.ViewHolder(headerViews.get(viewType)) {};
        } else { //item
            return baseAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeaderView(position)) { //header
            return;
        } else if (isFooterView(position)) { //footer
            return;
        } else {
            baseAdapter.onBindViewHolder(holder, position - headerViews.size());
        }
    }

    public int getItemViewType(int position) {
        if (isHeaderView(position)) { //header
            return headerViews.keyAt(position);
        } else if (isFooterView(position)) { //footer
            return footerViews.keyAt(position - headerViews.size() - baseAdapter.getItemCount());
        } else {
            return 0;
        }
    }


    //原有items加上header,footer的数量
    @Override
    public int getItemCount() {
        return headerViews.size() + footerViews.size() + baseAdapter.getItemCount();
    }

    //为啥通过重写该方法来对recyclerView的不同不同管理器进行区别  ????
    //其实一个holder对象就是一个item的封装, 也就可以获取到item的位置等一系列信息,在onAttachedToRecyclerView中无法通过recyclerView做到
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        //下面两个值基本一样
        int layoutPos = holder.getLayoutPosition();  //基于item position做相关计算时应该采用该方法，且该方法也应用在在item延迟(动画)加载
        int adapterPos = holder.getAdapterPosition();  //在数据更新情况下notifyDataSetChanged,只有layout传递过来,才会返回位置的值
        Log.d(TAG, "onViewAttachedToWindow. layoutPos="+layoutPos+",adapterPos="+adapterPos);

        //采用layoutPos
        //通过StaggeredGridLayoutManager.LayoutParams的setFullSpan方法修改item所占的span
        if (isHeaderView(layoutPos) || isFooterView(layoutPos)) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                ((StaggeredGridLayoutManager.LayoutParams) lp).setFullSpan(true);
            }
        }

    }

    //为啥通过重写该方法来对recyclerView的不同不同管理器进行区别  ????
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        //如果布局管理器是GridLayoutManager类型,对header,footer单独一行或一列
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final int spanCount = ((GridLayoutManager) manager).getSpanCount();
            final GridLayoutManager.SpanSizeLookup lookup = ((GridLayoutManager) manager).getSpanSizeLookup();
            //GridLayoutManager 通过setSpanSizeLookup来设置每个item所占的span
            ((GridLayoutManager) manager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                //返回每个item占据的span
                @Override
                public int getSpanSize(int position) {  //默认一个item一个span
                    if (isHeaderView(position)) {
                        return spanCount;
                    } else if (isFooterView(position)) {
                        return spanCount;
                    }
                    if (lookup != null) {
                        return lookup.getSpanSize(position);   //默认是1
                    }
                    return 1;
                }
            });
        } else if (manager instanceof StaggeredGridLayoutManager) {
            //并没有相关API来控制对应position的item所占的span
        }

    }
}
