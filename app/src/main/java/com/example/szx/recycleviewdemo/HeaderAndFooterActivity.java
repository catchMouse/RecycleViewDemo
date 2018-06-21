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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

//关于在展示过程中，部分divider被遮挡的问题: 由于绘制divider是在onDraw中，它是在drawChildren之前绘制的，所以可能被child的绘制内容所覆盖;
//可以在onDrawOver中进行绘制，这样divider就会盖在child之上
public class HeaderAndFooterActivity extends Activity {

    public static final String TAG = "HeaderAndFooterActivity";
    private RecyclerView mRecyclerView;
    private DataAdapter mAdapter;
    //本地数据
    private int[] datas = new int[]{R.drawable.a1, R.drawable.a2, R.drawable.a3, R.drawable.a4, R.drawable.a5,
            R.drawable.a6, R.drawable.a7, R.drawable.a8, R.drawable.a9, R.drawable.a10,
            R.drawable.a11, R.drawable.a12, R.drawable.a13, R.drawable.a14, R.drawable.a15,
            R.drawable.a16, R.drawable.a17, R.drawable.a18, R.drawable.a19, R.drawable.a20,
            R.drawable.a21, R.drawable.a22, R.drawable.a23, R.drawable.a24, R.drawable.a25};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_header_and_footer);

        initAdapter(); //创建Adapter
        mRecyclerView = (RecyclerView)findViewById(R.id.my_recyclerview);
//        initRecyclerView_line();
//        initRecyclerView_grid();
        initRecyclerView_stag();

    }

    //线性布局方式
    private void initRecyclerView_line() {
        mRecyclerView.setItemAnimator(new DefaultItemAnimator()); //设置Item增加、移除动画
        mRecyclerView.setLayoutManager(new LinearLayoutManager(HeaderAndFooterActivity.this)); //设置布局管理器

        mRecyclerView.addItemDecoration(new DividerItemDecoration(
                HeaderAndFooterActivity.this, DividerItemDecoration.VERTICAL)); //添加分割线-系统提供的实现

        HeaderAndFooterAdapter myAdapter = new HeaderAndFooterAdapter(mAdapter);
        myAdapter.addHeaderView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_item_header, null));
        myAdapter.addHeaderView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_item_header, null));
        myAdapter.addFooterView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_item_footer, null));
        myAdapter.addFooterView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_item_footer, null));

        mRecyclerView.setAdapter(myAdapter); //绑定Adapter
    }

    //网格布局管理器
    private void initRecyclerView_grid() {
        mRecyclerView.setItemAnimator(new DefaultItemAnimator()); //设置Item增加、移除动画
        //mRecyclerView.setLayoutManager(new GridLayoutManager(HeaderAndFooterActivity.this, 2)); //设置布局管理器 垂直
        mRecyclerView.setLayoutManager(new GridLayoutManager(HeaderAndFooterActivity.this, 2, GridLayoutManager.HORIZONTAL, false)); //设置布局管理器 水平

        mRecyclerView.addItemDecoration(new MyItemDecoration_Grid(HeaderAndFooterActivity.this));

        HeaderAndFooterAdapter myAdapter = new HeaderAndFooterAdapter(mAdapter);
        myAdapter.addHeaderView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_item_header, null));
        myAdapter.addHeaderView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_item_header, null));
        myAdapter.addFooterView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_item_footer, null));
        myAdapter.addFooterView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_item_footer, null));

        mRecyclerView.setAdapter(myAdapter); //绑定Adapter

    }

    //瀑布流布局管理器
    private void initRecyclerView_stag() {
        mRecyclerView.setItemAnimator(new DefaultItemAnimator()); //设置Item增加、移除动画
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)); //设置布局管理器 垂直

        mRecyclerView.addItemDecoration(new MyItemDecoration_Grid_Staggered(HeaderAndFooterActivity.this));

        HeaderAndFooterAdapter myAdapter = new HeaderAndFooterAdapter(mAdapter);
        myAdapter.addHeaderView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_item_header, null));
        myAdapter.addHeaderView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_item_header, null));
        myAdapter.addFooterView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_item_footer, null));
        myAdapter.addFooterView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_item_footer, null));

        mRecyclerView.setAdapter(myAdapter); //绑定Adapter

    }

    private void initAdapter() {
        //本地图片适配器
        mAdapter = new DataAdapter();
        mAdapter.setOnViewClickListener(new OnViewClickListener() {
            @Override
            public void onItemClick(View v) {

            }

            @Override
            public boolean onItemLongClick(View v) {
                return false;
            }
        });

    }

    //本地数据构建的adapter
    class DataAdapter extends RecyclerView.Adapter<HeaderAndFooterActivity.MyViewHolder> {
        private HeaderAndFooterActivity.OnViewClickListener listener;

        public void setOnViewClickListener(HeaderAndFooterActivity.OnViewClickListener listener) {
            this.listener = listener;
        }
        @Override
        public HeaderAndFooterActivity.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(HeaderAndFooterActivity.this).inflate(R.layout.layout_item_pic, parent,false);
            HeaderAndFooterActivity.MyViewHolder holder = new HeaderAndFooterActivity.MyViewHolder(view);
            return holder;
        }
        @Override
        public void onBindViewHolder(HeaderAndFooterActivity.MyViewHolder holder, int position) {
            Log.d(TAG,"onBindViewHolder position:"+position);
            holder.img.setImageResource(datas[position]);

            //GridLayoutManager, LinearLayoutManager 可以注释以下部分
            int width = ((Activity) holder.img.getContext()).getWindowManager().getDefaultDisplay().getWidth();
            //高度随机----针对瀑布流
            ViewGroup.LayoutParams params = holder.img.getLayoutParams();
            //设置图片的相对于屏幕的宽高比
            params.width = width/2;
            params.height =  (int) (200 + Math.random() * 200) ;
            holder.img.setLayoutParams(params);

            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(v);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return datas.length;
        }

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        public MyViewHolder(View itemView) {
            super(itemView);
            img = (ImageView)itemView.findViewById(R.id.item_pic_content);
        }
    }

    interface OnViewClickListener{
        public void onItemClick(View v);
        public boolean onItemLongClick(View v);
    }

}
