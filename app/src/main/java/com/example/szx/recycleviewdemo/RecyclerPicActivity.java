package com.example.szx.recycleviewdemo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class RecyclerPicActivity extends Activity {

    public static final String TAG = "MainActivity";
//    private DataAdapter3 mAdapter;
    private DataOnLineAdapter mAdapter;
    private RecyclerView mRecyclerView;
    ImageView mCurrentPic;

    ImageLruCache imageLruCache;

    //本地数据
    int[] datas = new int[]{R.drawable.a1, R.drawable.a2, R.drawable.a3, R.drawable.a4, R.drawable.a5,
            R.drawable.a6, R.drawable.a7, R.drawable.a8, R.drawable.a9, R.drawable.a10,
            R.drawable.a11, R.drawable.a12, R.drawable.a13, R.drawable.a14, R.drawable.a15,
            R.drawable.a16, R.drawable.a17, R.drawable.a18, R.drawable.a19, R.drawable.a20,
            R.drawable.a21, R.drawable.a22, R.drawable.a23, R.drawable.a24, R.drawable.a25};

    public static final String PREFIX = "https://raw.githubusercontent.com/catchMouse/RecycleViewDemo/master/app/src/main/res/drawable/a";
    public static final String SUFFIX = ".png";
    private ArrayList<String> mPngSites = new ArrayList<String>();    //在线数据
    private DiskLruCache mDiskLruCache;

    private void initData() {
        Log.d(TAG,"size:"+datas.length);

        mPngSites.clear();
        for (int i=1; i<=25; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append(i);
            sb.append(SUFFIX);
            Log.d(TAG, "site:" + sb.toString());
            mPngSites.add(sb.toString());
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_pic);

        initData(); //模拟数据
        imageLruCache = new ImageLruCache((int)Runtime.getRuntime().maxMemory() / 8);

        try {
            // 获取图片缓存路径
            File cacheDir = getDiskCacheDir(getApplicationContext(), "thumb");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            // 创建DiskLruCache实例，初始化缓存数据
            mDiskLruCache = DiskLruCache
                    .open(cacheDir, getAppVersion(getApplicationContext()), 1, 10 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }

        initAdapter(); //创建Adapter
        mRecyclerView = (RecyclerView)findViewById(R.id.pic_recyclerview);
        initRecyclerView_line();

        mCurrentPic = (ImageView)findViewById(R.id.show_current_pic);
    }

    /**
     * 根据传入的uniqueName获取硬盘缓存的路径地址。
     */
    public File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        Log.d(TAG, "getDiskCacheDir. cachePath=" + cachePath);
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 获取当前应用程序的版本号。
     */
    public int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
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
        //本地图片适配器
        /*mAdapter = new DataAdapter3();
        mAdapter.setOnViewClickListener(new OnViewClickListener() {
            @Override
            public void onItemClick(View v) {
                mCurrentPic.setImageDrawable(((ImageView)v).getDrawable());
            }

            @Override
            public boolean onItemLongClick(View v) {
                return false;
            }
        });*/

        //网络图片适配器
        mAdapter = new DataOnLineAdapter();
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


    //本地数据构建的adapter
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

    class DataOnLineAdapter extends RecyclerView.Adapter<MyViewHolder2> {
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
            String url = mPngSites.get(position);
            Bitmap source;
            //内存缓存 + 硬盘缓存 + 网络获取
            source = imageLruCache.get(url);//内存缓存
            holder.img.setImageResource(R.mipmap.ic_launcher);  //默认图片
            if (source != null) {
                Log.d(TAG,"内存缓存111111111");
                holder.img.setImageBitmap(source);
            } else {
                //进行硬盘缓存读取,最后网络下载
                new PngDownloadTask(holder.img).execute(url);
            }

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
            return mPngSites.size();
        }

    }

    class MyViewHolder2 extends RecyclerView.ViewHolder {
        ImageView img;
        public MyViewHolder2(View itemView) {
            super(itemView);
            img = (ImageView)itemView.findViewById(R.id.item_pic_content);
        }
    }

    //String 为image的url地址; Bitmap为缓存对象
    class ImageLruCache extends LruCache<String, Bitmap> {
        //其对外提供get put方法进行缓存对象的存取
        public ImageLruCache(int maxSize) {
            super(maxSize);
        }

        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }
    }

    interface OnViewClickListener{
        public void onItemClick(View v);
        public boolean onItemLongClick(View v);
    }

    /**
     * 使用MD5算法对传入的key进行加密并返回。
     */
    public String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
            Log.d(TAG, "hashKeyForDisk.key="+key+",\n,mDigest.digest()"+mDigest.digest()
                +",\n,cacheKey="+cacheKey);
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    //进行硬盘缓存读取,最后网络下载
    class PngDownloadTask extends AsyncTask<String, Integer, Bitmap> {
        ImageView view;
        DiskLruCache.Snapshot snapshot = null;
        DiskLruCache.Editor editor = null;
        OutputStream os = null;
        InputStream is = null;
        public PngDownloadTask(ImageView view) {
            this.view = view;
        }
        @Override
        protected Bitmap doInBackground(String[] urls) {
            Log.d(TAG, "doInBackground.url:"+urls[0]);
            Bitmap bitmap = null;
            try {
                //硬盘缓存检查
                String key = hashKeyForDisk(urls[0]);
                snapshot = mDiskLruCache.get(key);
                if (snapshot == null) {
                    editor = mDiskLruCache.edit(key);
                    if (editor != null) {
                        os = editor.newOutputStream(0);
                        if(downloadToStream(urls[0], os)) {  //硬盘输出流进行网络下载数据保存
                            editor.commit();  //磁盘缓存
                        } else {
                            editor.abort();
                        }
                    }
                    snapshot = mDiskLruCache.get(key);
                }
                //磁盘缓存上有数据
                if (snapshot != null) {
                    is = snapshot.getInputStream(0);
                    Log.d(TAG,"磁盘缓存22222222");
                    bitmap = BitmapFactory.decodeStream(is);  //获取数据源
                }
                //加入内存缓存
                if (bitmap != null) {
                    imageLruCache.put(urls[0], bitmap);
                }
                return bitmap;
            } catch (Exception e) {
                Log.d(TAG, "doInBackground.Exception:"+e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        private boolean downloadToStream(String downloadUrl, OutputStream os) {
            BufferedOutputStream out = null;
            BufferedInputStream in = null;
            HttpURLConnection conn = null;
            try {
                URL url = new URL(downloadUrl);
                conn = (HttpURLConnection)url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.connect();
                Log.d(TAG,"网络下载33333333");
                in = new BufferedInputStream(conn.getInputStream(), 8 * 1024);
                out = new BufferedOutputStream(os, 8 * 1024);
                int b;
                while ((b = in.read()) != -1) {
                    out.write(b);
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }


            return false;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                view.setImageBitmap(result);
            }
        }
    }

}
