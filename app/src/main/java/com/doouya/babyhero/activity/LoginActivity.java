package com.doouya.babyhero.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doouya.babyhero.R;
import com.doouya.babyhero.utils.DensityUtils;
import com.doouya.babyhero.utils.UserInfoUtils;

import java.util.ArrayList;
import java.util.Calendar;

public class LoginActivity extends BaseActivity implements View.OnClickListener{
    //logo
    private ImageView iv_logo;
    //完善宝贝信息部分
    private RelativeLayout rl_babyInfo;
    //男女宝贝
    private RadioGroup rg_babyIcon;
    private RadioButton rb_boy,rb_girl;
    //姓名
    private EditText et_name;
    //出生日期
    private TextView tv_birthday;
    //确定按钮
    private TextView tv_ok;
    //性别
    private String genderStr = "M";
    //获取生日
    private String birthdayStr;
    //存储用户信息
    private SharedPreferences sp;
    //BLE设备列表
    private ArrayList<BluetoothDevice> mLeDevices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLeDevices = getIntent().getParcelableArrayListExtra("bleDevices");
        initView();
        chooseBabyIcon();
    }

    private void initView(){
        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        rl_babyInfo = (RelativeLayout) findViewById(R.id.rl_babyInfo);
        rg_babyIcon = (RadioGroup) findViewById(R.id.rg_babyIcon);
        rb_boy = (RadioButton) findViewById(R.id.rb_boy);
        rb_girl = (RadioButton) findViewById(R.id.rb_girl);
        et_name = (EditText) findViewById(R.id.et_name);
        tv_birthday = (TextView) findViewById(R.id.tv_birthday);
        tv_ok = (TextView) findViewById(R.id.tv_ok);

        //获取屏幕宽高
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        //设置logo位置
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width*2/3, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(width/6,height/5- DensityUtils.dp2px(this,10),width/6,0);
        iv_logo.setLayoutParams(params);

        titleAlphaAnimation(iv_logo,0,1,1000);
        titleAlphaAnimation(rl_babyInfo,0,1,1000);

        tv_birthday.setOnClickListener(this);
        tv_ok.setOnClickListener(this);
        rg_babyIcon.check(R.id.rb_boy);
    }
    //切换性别
    private void chooseBabyIcon(){
        rg_babyIcon.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id){
                    case R.id.rb_boy:
                        rb_boy.setBackground(getResources().getDrawable(R.mipmap.boy_tapped));
                        rb_boy.setTextColor(getResources().getColor(R.color.boy_blue));
                        rb_girl.setBackground(getResources().getDrawable(R.mipmap.girl));
                        rb_girl.setTextColor(getResources().getColor(R.color.tap_color));
                        genderStr = "M";
                        break;
                    case R.id.rb_girl:
                        rb_girl.setBackground(getResources().getDrawable(R.mipmap.girl_tapped));
                        rb_girl.setTextColor(getResources().getColor(R.color.girl_red));
                        rb_boy.setBackground(getResources().getDrawable(R.mipmap.boy));
                        rb_boy.setTextColor(getResources().getColor(R.color.tap_color));
                        genderStr = "F";
                        break;
                }
            }
        });
    }
    //选择生日
    private void chooseBirthday(){
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        birthdayStr = year + "-" + (monthOfYear + 1) + "-"
                                + dayOfMonth;
                        tv_birthday.setText(birthdayStr);
                    }
                }, c.get(Calendar.YEAR), // 传入年份
                c.get(Calendar.MONTH), // 传入月份
                c.get(Calendar.DAY_OF_MONTH) // 传入天数
        ).show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_birthday:
                chooseBirthday();
                break;
            case R.id.tv_ok:
                if(et_name.getText().toString().trim().equals("")){
                    et_name.setError("名字不能为空");
                    return;
                }
                if(tv_birthday.getText().toString().trim().equals("")){
                    tv_birthday.setError("生日不能为空");
                    return;
                }
                sp = getSharedPreferences(UserInfoUtils.LoginSPKey.LOGIN_SP,ActionBarActivity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean(UserInfoUtils.LoginSPKey.IS_LOGIN, true);
                editor.putString(UserInfoUtils.LoginSPKey.BABY_NAME, et_name.getText().toString());
                editor.putString(UserInfoUtils.LoginSPKey.BABY_BIRTHDAY, tv_birthday.getText().toString());
                editor.putString(UserInfoUtils.LoginSPKey.BABY_GENDER, genderStr);
                editor.commit();
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                intent.putParcelableArrayListExtra("bleDevices",mLeDevices);
                startActivity(intent);
                LoginActivity.this.finish();
                break;
        }

    }
}
