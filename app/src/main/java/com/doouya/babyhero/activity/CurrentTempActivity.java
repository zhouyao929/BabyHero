package com.doouya.babyhero.activity;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doouya.babyhero.R;
import com.doouya.babyhero.adapter.TempListAdapter;
import com.doouya.babyhero.utils.DensityUtils;

public class CurrentTempActivity extends BaseActivity implements View.OnClickListener{
    //头部背景图
    private ImageView iv_title;
    //标题
    private TextView tv_title;
    //退出按钮
    private ImageView iv_finish;
    //显示温度的布局
    private RelativeLayout rl_temp;
    //当前温度数据
    private TextView tv_temp;
    //常用温度列表
    private ListView listView;
    private TempListAdapter adapter;

    private String [][]data = new String[][]{{"合适泡澡水温","36~40℃"},{"舒适环境温","24~26℃"},
            {"合适奶温","40~60℃"},{"婴儿正常体温","36~37℃"}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_temp);
        iv_title = (ImageView) findViewById(R.id.iv_title);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_finish = (ImageView) findViewById(R.id.iv_finish);
        rl_temp = (RelativeLayout) findViewById(R.id.rl_temp);
        tv_temp = (TextView) findViewById(R.id.tv_temp);
        listView = (ListView) findViewById(R.id.lv_temp);

        titleAlphaAnimation(tv_title,0,1,1000);
        titleAlphaAnimation(iv_finish,0,1,1000);
        titleAlphaAnimation(rl_temp,0,1,1000);
        titleScaleAnimation(iv_title,4f,3f,10);

        //获取屏幕宽高
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        //设置title的初始布局参数
        RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.setMargins(width/3,0,0,0);
        iv_title.setLayoutParams(titleParams);
        //设置listview的布局参数
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(DensityUtils.dp2px(this,10),height/2-DensityUtils.dp2px(this,10),0,0);
        listView.setLayoutParams(params);
        adapter = new TempListAdapter(this,data);
        listView.setAdapter(adapter);

        iv_finish.setOnClickListener(this);
        iv_title.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_finish:
                finish();
                break;
            case R.id.iv_title:
                finish();
                break;
        }
    }
}
