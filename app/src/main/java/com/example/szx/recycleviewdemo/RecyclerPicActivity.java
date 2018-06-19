package com.example.szx.recycleviewdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

public class RecyclerPicActivity extends Activity {

    public static final String TAG = "MainActivity";
    private DataAdapter3 mAdapter;
    private RecyclerView mRecyclerView;
    ImageView mCurrentPic;

    //本地图片显示
    int[] datas = new int[]{R.drawable.a1, R.drawable.a2, R.drawable.a3, R.drawable.a4, R.drawable.a5,
            R.drawable.a6, R.drawable.a7, R.drawable.a8, R.drawable.a9, R.drawable.a10,
            R.drawable.a11, R.drawable.a12, R.drawable.a13, R.drawable.a14, R.drawable.a15,
            R.drawable.a16, R.drawable.a17, R.drawable.a18, R.drawable.a19, R.drawable.a20,
            R.drawable.a21, R.drawable.a22, R.drawable.a23, R.drawable.a24, R.drawable.a25};
    private void initData() {
        Log.d(TAG,"size:"+datas.length);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_pic);

        initData(); //模拟数据
        initAdapter(); //创建Adapter
        mRecyclerView = (RecyclerView)findViewById(R.id.pic_recyclerview);
        initRecyclerView_line();

        mCurrentPic = (ImageView)findViewById(R.id.show_current_pic);
    }

    //线性布局方式
    private void initRecyclerView_line() {
        mRecyclerView.setItemAnimator(new DefaultItemAnimator()); //设置Item增加、移除动画
        mRecyclerView.setLayoutManager(new LinearLayoutManager(RecyclerPicActivity.this)); //设置布局管理器

        //如果布局管理器的方向是垂直的，那么分割线应该采用ividerItemDecoration.VERTICAL
        mRecyclerView.addItemDecoration(new DividerItemDecoration(
                RecyclerPicActivity.this, DividerItemDecoration.VERTICAL)); //添加分割线-系统提供的实现
        mRecyclerView.setAdapter(mAdapter); //绑定Adapter
    }

    //网格布局管理器
    private void initRecyclerView_grid() {
        mRecyclerView.setItemAnimator(new DefaultItemAnimator()); //设置Item增加、移除动画
        mRecyclerView.setLayoutManager(new GridLayoutManager(RecyclerPicActivity.this, 2)); //设置布局管理器

        //可以调用多次addItemDecoration 来添加装饰内容
        mRecyclerView.addItemDecoration(new MyItemDecoration_Grid(RecyclerPicActivity.this));
        mRecyclerView.setAdapter(mAdapter); //绑定Adapter
    }

    private void initAdapter() {
        mAdapter = new DataAdapter3();
        mAdapter.setOnViewClickListener(new OnViewClickListener() {
            @Override
            public void onItemClick(View v) {
                mCurrentPic.setImageDrawable(((ImageView)v).getDrawable());
            }

            @Override
            public boolean onItemLongClick(View v) {
                return false;
            }
        });
    }



    class DataAdapter3 extends RecyclerView.Adapter<MyViewHolder2> {
        private OnViewClickListener listener;

        public void setOnViewClickListener(OnViewClickListener listener) {
            this.listener = listener;
        }
        @Override
        public MyViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(RecyclerPicActivity.this).inflate(R.layout.layout_item_pic, parent,false);
            MyViewHolder2 holder = new MyViewHolder2(view);
            return holder;
        }
        @Override
        public void onBindViewHolder(MyViewHolder2 holder, int position) {
            Log.d(TAG,"position:"+position);
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

    class MyViewHolder2 extends RecyclerView.ViewHolder {
        ImageView img;
        public MyViewHolder2(View itemView) {
            super(itemView);
            img = (ImageView)itemView.findViewById(R.id.item_pic_content);
        }
    }

    interface OnViewClickListener{
        public void onItemClick(View v);
        public boolean onItemLongClick(View v);
    }

}
