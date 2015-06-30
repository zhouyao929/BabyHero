package com.doouya.babyhero.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doouya.babyhero.R;
import com.doouya.babyhero.adapter.TempListAdapter;
import com.doouya.babyhero.utils.DensityUtils;
import com.facebook.drawee.view.SimpleDraweeView;

public class SettingActivity extends BaseActivity implements View.OnClickListener{
    //头部背景图
    private ImageView iv_title;
    //标题
    private TextView tv_title;
    //退出按钮
    private ImageView iv_finish;
    //设置图标
    private ImageView iv_setting;
    //常用温度列表
    private ListView listView;
    //list的adapter
    private TempListAdapter adapter;
    //list数据
    private String data[][] = {{"硬件升级","请升级"},{"使用手册",""},{"意见反馈",""},{"给我们评价",""},{"关于我们",""}};
    //是否隐藏关于我们
    private boolean isHide = false;
    //关于我们布局
    private RelativeLayout rl_about_us;
    private TextView tv_us;
    private SimpleDraweeView sdv_us;
    private String aboutUsStr = "云豆科技是位于美丽的西子湖畔的亲子领域创业公司，在母婴这一垂直领域公允超过两年时间，积累了身后的软硬件研发、生产经验，研发的多款" +
            "产品皆受到用户好评。团队中既有沉稳老练的CEO、CTO、硬件总监等，又有80后、90后的新一代年轻员工，在两种风格的碰撞中形成了产品品质过硬，创新动力强劲的公司风格。";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initData();

    }

    private void initView(){
        iv_title = (ImageView) findViewById(R.id.iv_title);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_finish = (ImageView) findViewById(R.id.iv_finish);
        listView = (ListView) findViewById(R.id.lv_setting);
        rl_about_us = (RelativeLayout) findViewById(R.id.rl_aboutus);
        tv_us = (TextView) findViewById(R.id.tv_description);
        sdv_us = (SimpleDraweeView) findViewById(R.id.sdv_us);
        iv_setting = (ImageView) findViewById(R.id.iv_setting);

        //获取屏幕宽高
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        //list布局
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width,height/2-DensityUtils.dp2px(this,35));
        params.setMargins(DensityUtils.dp2px(this,10),height/4,0,0);
        listView.setLayoutParams(params);
        titleAlphaAnimation(tv_title,0,1,1000);
        titleAlphaAnimation(iv_finish,0,1,1000);
        titleAlphaAnimation(iv_setting,0,1,1000);
        titleScaleAnimation(iv_title,4f,1.5f,100);
    }

    private void initData(){
        adapter = new TempListAdapter(SettingActivity.this,data);
        listView.setAdapter(adapter);
        tv_us.setText(aboutUsStr);
        sdv_us.setImageResource(R.mipmap.about_us);
        sdv_us.setAspectRatio(1.8f);

        iv_finish.setOnClickListener(this);
        iv_title.setOnClickListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch(position){
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        if(!isHide){
                            isHide = true;
                            view.findViewById(R.id.iv_arrow).setBackgroundResource(R.mipmap.arrow_down);
                            rl_about_us.setVisibility(View.VISIBLE);
                        }else{
                            isHide = false;
                            view.findViewById(R.id.iv_arrow).setBackgroundResource(R.mipmap.arrow_up);
                            rl_about_us.setVisibility(View.GONE);
                        }
                        break;
                }
            }
        });
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
