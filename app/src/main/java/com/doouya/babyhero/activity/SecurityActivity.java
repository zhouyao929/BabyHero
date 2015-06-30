package com.doouya.babyhero.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doouya.babyhero.R;

public class SecurityActivity extends BaseActivity implements View.OnClickListener{
    //头部背景图
    private ImageView iv_title;
    //标题
    private TextView tv_title;
    //退出按钮
    private ImageView iv_finish;
    //安全状态
    private TextView tv_security;
    //安全状态图
    private ImageView iv_security;
    //tip
    private TextView tv_tip;
    //开关按钮
    private TextView tv_protect_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        initView();
    }

    private void initView(){
        iv_title = (ImageView) findViewById(R.id.iv_title);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_finish = (ImageView) findViewById(R.id.iv_finish);
        tv_security = (TextView) findViewById(R.id.tv_security);
        iv_security = (ImageView) findViewById(R.id.iv_security);
        tv_tip = (TextView) findViewById(R.id.tv_tip);
        tv_protect_btn = (TextView) findViewById(R.id.tv_protect_btn);

        //获取屏幕宽高
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width/3, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,height/2,0,0);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
        iv_security.setLayoutParams(params);

        titleAlphaAnimation(tv_title,0,1,1000);
        titleAlphaAnimation(iv_finish,0,1,1000);
        titleAlphaAnimation(tv_security,0,1,1000);
        titleScaleAnimation(iv_title,3f,2f,10);

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
