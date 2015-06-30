package com.doouya.babyhero.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.doouya.babyhero.R;
import com.doouya.babyhero.utils.UserInfoUtils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends BaseActivity {
    private ImageView iv_logo;
    //计时器
    private Timer timer;
    //时间任务
    private TimerTask task;
    //存储用户信息
    private SharedPreferences sp;
    //baby姓名、生日、性别
    private String babyName,babyBirthday,babyGender;

    private Intent intent;
    private Boolean newActi,newTi;

    //ble搜索
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private static final int REQUEST_ENABLE_BT = 1;
    // 10秒后停止查找搜索.
    private static final long SCAN_PERIOD = 10000;
    //BLE设备列表
    private ArrayList<BluetoothDevice> mLeDevices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);
        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        //获取屏幕宽高
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width*2/3, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
        params.setMargins(0,height/3,0,0);
        iv_logo.setLayoutParams(params);
        //logo渐变动画
        titleAlphaAnimation(iv_logo,0,1,1000);

        /**以下为BLE相关服务的初始化
         * */
        mHandler = new Handler();

        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // 检查设备上是否支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        getBabyInfo();
        timerIntent();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        scanLeDevice(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    //扫描BLE设备
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    // BLE设备扫描回调函数
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (device.getName() != null) {
                        //只存留设备名称是apptest2的设备
                        if (device.getName().equals("apptest") && !mLeDevices.contains(device)) {
                            mLeDevices.add(device);
                            System.out.println("####name=" + device.getName() + "###address=" + device.getAddress());
                        }
                    }
                }
            });
        }
    };

    //获取baby信息
    private void getBabyInfo(){
        sp = getSharedPreferences(UserInfoUtils.LoginSPKey.LOGIN_SP,ActionBarActivity.MODE_PRIVATE);
        if(sp.getBoolean(UserInfoUtils.LoginSPKey.IS_LOGIN,false)){
            babyName = sp.getString(UserInfoUtils.LoginSPKey.BABY_NAME,"");
            babyBirthday = sp.getString(UserInfoUtils.LoginSPKey.BABY_BIRTHDAY,"");
            babyGender = sp.getString(UserInfoUtils.LoginSPKey.BABY_GENDER,"M");
            intent = new Intent(this,MainActivity.class);
            intent.putExtra("babyName",babyName);
            intent.putExtra("babyBirthday",babyBirthday);
            intent.putExtra("babyGender",babyGender);
        }else{
            intent = new Intent(this,LoginActivity.class);
        }
        intent.putParcelableArrayListExtra("bleDevices",mLeDevices);

    }

    //定时跳转
    private void timerIntent(){
        timer = new Timer();
        newActi = false;
        newTi = false;
        task = new TimerTask() {
            @Override
            public void run() {
                if(!newActi){
                    newActi = true;
                    timer.cancel();//取消定时器
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                    WelcomeActivity.this.finish();
                }
            }
        };
        if(!newTi){
            newTi = true;
            timer.schedule(task,3000);//设置执行任务时间
        }
    }

//    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            newActi = true;
//            startActivity(intent);
//            overridePendingTransition( android.R.anim.slide_in_left,android.R.anim.slide_out_right);
//            WelcomeActivity.this.finish();
//
//        }
//        return super.onTouchEvent(event);
//    }

}
