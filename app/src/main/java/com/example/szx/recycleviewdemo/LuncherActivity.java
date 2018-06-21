package com.example.szx.recycleviewdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LuncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luncher);
    }

    public void startBaseStudy(View v) {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
    }
    public void startUseForPic(View v){
        Intent intent = new Intent();
        intent.setClass(this, RecyclerPicActivity.class);
        startActivity(intent);
    }
    public void ExtendUseForRecyclerView(View v){
        Intent intent = new Intent();
        intent.setClass(this, HeaderAndFooterActivity.class);
        startActivity(intent);
    }

}
