package com.example.szx.recycleviewdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.recyclerview.adapter.AlphaAnimatorAdapter;
import it.gmariotti.recyclerview.itemanimator.ScaleInOutItemAnimator;
import it.gmariotti.recyclerview.itemanimator.SlideInOutLeftItemAnimator;
import it.gmariotti.recyclerview.itemanimator.SlideInOutRightItemAnimator;

public class MainActivity extends Activity {

    public static final String TAG = "MainActivity";
    private List<String> mDatas;
    private DataAdapter3 adapter;

    private RecyclerView mRecyclerView;

    public static final int RAW = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData(); //模拟数据
        initAdapter(); //创建Adapter
        mRecyclerView = (RecyclerView)findViewById(R.id.view_recyclerview);
        //initRecyclerView_line();   //线性布局
        //initRecyclerView_grid();  //网格布局
        //initRecyclerView_staggeredGrid();  //瀑布流式的布局
        initRecyclerView_staggeredGrid_Anim();  //瀑布流式的布局 + 动画
    }

    //线性布局方式
    private void initRecyclerView_line() {
        mRecyclerView.setItemAnimator(new DefaultItemAnimator()); //设置Item增加、移除动画
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this)); //设置布局管理器

        //如果布局管理器的方向是垂直的，那么分割线应该采用ividerItemDecoration.VERTICAL
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(
//                MainActivity.this, DividerItemDecoration.VERTICAL)); //添加分割线-系统提供的实现

        mRecyclerView.addItemDecoration(new MyItemDecoration(
                MainActivity.this, DividerItemDecoration.VERTICAL, 3)); //添加分割线-自定义

        mRecyclerView.setAdapter(adapter); //绑定Adapter
    }

    //网格布局管理器
    private void initRecyclerView_grid() {
        mRecyclerView.setItemAnimator(new DefaultItemAnimator()); //设置Item增加、移除动画
        mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2)); //设置布局管理器

        //可以调用多次addItemDecoration 来添加装饰内容
        mRecyclerView.addItemDecoration(new MyItemDecoration_Grid(MainActivity.this));

        mRecyclerView.setAdapter(adapter); //绑定Adapter
    }

    //瀑布流式的布局 + 默认动画
    //如果是水平方向，那么数据 以列的方式水平推进. 假设两行(0,0)-A  (1,0)-B  (0,1)-C (1,1)-D
    //根据方向适当调整RecyclerView的宽高以及item布局的宽高
    private void initRecyclerView_staggeredGrid() {
        mRecyclerView.setItemAnimator(new DefaultItemAnimator()); //设置Item增加、移除动画
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(RAW, StaggeredGridLayoutManager.VERTICAL)); //设置布局管理器
        //mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(RAW, StaggeredGridLayoutManager.HORIZONTAL)); //设置布局管理器

        //可以调用多次addItemDecoration 来添加装饰内容
        mRecyclerView.addItemDecoration(new MyItemDecoration_Grid_Staggered(MainActivity.this));

        mRecyclerView.setAdapter(adapter); //绑定Adapter
    }

    //瀑布流式的布局 + github提供的动画
    private void initRecyclerView_staggeredGrid_Anim() {
        //Appearance animations:
        //01 入场-退场的透明度变化动画
//        AlphaAnimatorAdapter myAdapter = new AlphaAnimatorAdapter(adapter, mRecyclerView);
//        mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, RAW)); //设置布局管理器
//        //可以调用多次addItemDecoration 来添加装饰内容
//        mRecyclerView.addItemDecoration(new MyItemDecoration_Grid(MainActivity.this));
//        mRecyclerView.setAdapter(myAdapter); //绑定Adapter

        //02 入场-退场的透明度变化动画
//        AlphaAnimatorAdapter myAdapter = new AlphaAnimatorAdapter(adapter, mRecyclerView);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
//        //可以调用多次addItemDecoration 来添加装饰内容
//        mRecyclerView.addItemDecoration(new MyItemDecoration(MainActivity.this, MyItemDecoration.VERTICAL, 1));
//        mRecyclerView.setAdapter(myAdapter); //绑定Adapter

        //03 error  找不到类 android.support.v4.animation.AnimatorCompatHelper
        /*mRecyclerView.setItemAnimator(new SlideInOutRightItemAnimator(mRecyclerView));
        mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, RAW)); //设置布局管理器
        //可以调用多次addItemDecoration 来添加装饰内容
        //mRecyclerView.addItemDecoration(new MyItemDecoration_Grid(MainActivity.this));
        mRecyclerView.setAdapter(adapter); //绑定Adapter*/

        //04. 入场-退场的透明度变化动画 + 监听item增删
        AlphaAnimatorAdapter myAdapter = new AlphaAnimatorAdapter(adapter, mRecyclerView);
        myAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            //监听item的增删动作
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                Log.d(TAG, "onItemRangeInserted positionStart:"+positionStart+",itemCount:"+itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                Log.d(TAG, "onItemRangeRemoved positionStart:"+positionStart+",itemCount:"+itemCount);

            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        //可以调用多次addItemDecoration 来添加装饰内容
        mRecyclerView.addItemDecoration(new MyItemDecoration(MainActivity.this, MyItemDecoration.VERTICAL, 1));
        mRecyclerView.setAdapter(myAdapter); //绑定Adapter

    }

    private void initAdapter() {
        //adapter = new DataAdapter(); //方式1 使用RecyclerView.ViewHolder
        //adapter = new DataAdapter2(); //方式2 使用MyViewHolder extends RecyclerView.ViewHolder

        //方式3 使用MyViewHolder extends RecyclerView.ViewHolder
        //同时将自定义itme布局中的view控件设置为MyViewHolder2的成员
        adapter = new DataAdapter3();
    }

    private void initData() {
        mDatas = new ArrayList<String>();
        for (int i = 'A'; i < 'z'; i++) {
            mDatas.add("字母==" + (char) i);
        }
        Log.d(TAG,"size:"+mDatas.size());
    }

    //默认Adapter的泛型是RecyclerView.ViewHolder的子类
    //方式1 使用RecyclerView.ViewHolder
    class DataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        //ViewHolder对象由View构造生成, view可以是自己创建的或者是通过xml布局文件加载的，
        //ViewHolder对象通过onBindViewHolder方法用来展示Adapter所绑定的数据的items
        //不需要想BaseAdapter那样去做缓存，ViewHolder已经实现缓存机制
        //其实一个ViewHolder 代表了RecyclerView中一行数据显示(view布局+数据item)
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_item, parent,false);
            RecyclerView.ViewHolder holder = new RecyclerView.ViewHolder(view){};
            return holder;
        }

        //由RecyclerView调用来展示position位置的数据显示(即更新ViewHolder的显示)
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Log.d(TAG,"position:"+position);
            TextView tv = (TextView)(holder.itemView.findViewById(R.id.item_content));
            tv.setText(mDatas.get(position));
        }

        //返回Adapter绑定的数据总量
        @Override
        public int getItemCount() {
            Log.d(TAG,"getItemCount:"+mDatas.size());
            return mDatas.size();
        }

        //返回对应position位置的数据的类型type
        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }
    }

    //默认Adapter的泛型是RecyclerView.ViewHolder的子类
    //方式2 使用MyViewHolder extends RecyclerView.ViewHolder
    class DataAdapter2 extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_item, parent,false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Log.d(TAG,"position:"+position);
            TextView tv = (TextView)(holder.itemView.findViewById(R.id.item_content));
            tv.setText(mDatas.get(position));
        }
        @Override
        public int getItemCount() {
            return mDatas.size();
        }
    }

    //默认Adapter的泛型是RecyclerView.ViewHolder的子类
    //方式3 使用MyViewHolder extends RecyclerView.ViewHolder
    //同时将自定义itme布局中的view控件设置为MyViewHolder2的成员
    class DataAdapter3 extends RecyclerView.Adapter<MyViewHolder2> {
        ClickListener listener;  //外部监听

        @Override
        public MyViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_item, parent,false);
            MyViewHolder2 holder = new MyViewHolder2(view);
            return holder;
        }
        @Override
        public void onBindViewHolder(MyViewHolder2 holder, int position) {
            Log.d(TAG,"position:"+position);
            holder.tv.setText(mDatas.get(position));

            //GridLayoutManager, LinearLayoutManager 可以注释以下部分
            int width = ((Activity) holder.tv.getContext()).getWindowManager().getDefaultDisplay().getWidth();
            //高度随机----针对瀑布流
            ViewGroup.LayoutParams params = holder.tv.getLayoutParams();
            //设置图片的相对于屏幕的宽高比
            params.width = width/RAW;
            params.height =  (int) (200 + Math.random() * 200) ;
            holder.tv.setLayoutParams(params);

            //内部监听-单击-长按等动作; 外部可以通过接口调用获取点击/长按事件
            holder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO 回调给外部
                    if (listener != null) {
                        listener.onClick(v);
                    }
                }
            });

            holder.tv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //TODO 回调给外部
                    if (listener != null) {
                        return listener.onLongClick(v);
                    }
                    return false;
                }
            });
        }

        public void setClickListener(ClickListener listener) {
            this.listener = listener;
        }
        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        //为动画增加测试方法
        public void addData(int position) {
            mDatas.add(position, "Insert One");
            notifyItemInserted(position);
        }

        public void removeData(int position) {
            mDatas.remove(position);
            notifyItemRemoved(position);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
    class MyViewHolder2 extends RecyclerView.ViewHolder {
        TextView tv;
        public MyViewHolder2(View itemView) {
            super(itemView);
            tv = (TextView)itemView.findViewById(R.id.item_content);
        }
    }

    interface ClickListener {
        public void onClick(View v);
        public boolean onLongClick(View v);
        //...others
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_action_add:
                adapter.addData(1);  //test
                break;
            case R.id.id_action_delete:
                adapter.removeData(1);  //test
                break;
        }
        return true;
    }

}
