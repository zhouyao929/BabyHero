package com.doouya.babyhero.fragment;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doouya.babyhero.R;
import com.doouya.babyhero.activity.CalActivity;
import com.doouya.babyhero.activity.CurrentTempActivity;
import com.doouya.babyhero.activity.SecurityActivity;
import com.doouya.babyhero.activity.SettingActivity;
import com.doouya.babyhero.activity.SleepTimeActivity;
import com.doouya.babyhero.ble.BabyHeroProtocol;
import com.doouya.babyhero.ble.BluetoothLeService;
import com.doouya.babyhero.utils.DensityUtils;
import com.doouya.babyhero.utils.UserInfoUtils;
import com.nvanbenschoten.motion.ParallaxImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ParallaxFragment extends Fragment implements View.OnClickListener{
    //视差效果控件
    private ParallaxImageView mBackground;
    private Context mContext;
    private RelativeLayout rl_operation,rl_line;
    //对温度、时间、开关、安全、卡路里、设置的布局
    private RelativeLayout rl_temp,rl_time,rl_security,rl_cal,rl_setting;
    //温度、时间、安全、卡路里数据显示
    private TextView tv_temp,tv_time,tv_security,tv_cal,tv_temp_unit,tv_time_unit,tv_security_tip,tv_cal_unit;
    //温度、时间、安全、卡路里、设置图标
    private ImageView iv_temp,iv_time,iv_security,iv_cal,iv_setting;
    //踢被开关是否打开
    private Boolean isSleepOn = false;
    //防丢开关是否打开
    private Boolean isProtectOn = false;
    //baby动漫照片
    private ImageView iv_avatar;
    //baby姓名
    private TextView tv_name;
    //防丢开关、踢被开关
    private ImageView iv_protect,iv_sleep;

    //存储用户信息
    private SharedPreferences sp;
    //获取屏幕的宽高
    private int width,height;

    //BLE设备列表
    private ArrayList<BluetoothDevice> mLeDevices = new ArrayList<>();
    //BLE服务
    private BluetoothLeService mBluetoothLeService;
    private final static String TAG = ParallaxFragment.class.getSimpleName();
    //BLE设备的MAC地址
    private String mDeviceAddress;

    //ble搜索
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private boolean mScanning;
    // 10秒后停止查找搜索.
    private static final long SCAN_PERIOD = 10000;

    public ParallaxFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mContext = this.getActivity();
        sp = mContext.getSharedPreferences(UserInfoUtils.LoginSPKey.LOGIN_SP, ActionBarActivity.MODE_PRIVATE);
        //获取屏幕宽高
        WindowManager wm = getActivity().getWindowManager();
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        /**以下为BLE相关服务的初始化
         * */
        mHandler = new Handler();

        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        final BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            height = height - getNavigationBarHeight();
        }
        //获取连接上的BLE设备、地址
        mLeDevices = getActivity().getIntent().getParcelableArrayListExtra("bleDevices");
        try{
            mDeviceAddress = mLeDevices.get(0).getAddress();
        }catch(Exception e){
            scanLeDevice(true);
        }

        Intent gattServiceIntent = new Intent(mContext, BluetoothLeService.class);
        getActivity().bindService(gattServiceIntent, mServiceConnection, mContext.BIND_AUTO_CREATE);

    }

    private int getNavigationBarHeight() {
        Resources resources = getActivity().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height","dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        Log.v("dbw", "Navi height:" + height);
        return height;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_parallax, container, false);
        if (rootView == null) return null;

        mBackground = (ParallaxImageView) rootView.findViewById(android.R.id.background);
        rl_operation = (RelativeLayout) rootView.findViewById(R.id.rl_operation);
        rl_line = (RelativeLayout) rootView.findViewById(R.id.rl_line);
        iv_avatar = (ImageView) rootView.findViewById(R.id.iv_avatar);
        tv_name = (TextView) rootView.findViewById(R.id.tv_name);

        rl_line.setBackgroundColor(Color.TRANSPARENT);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //avatar的布局
        RelativeLayout.LayoutParams avatarParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,height*2/5);
        avatarParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
        avatarParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
        avatarParams.setMargins(0,0,0, DensityUtils.dp2px(mContext,20));
        iv_avatar.setLayoutParams(avatarParams);
        //baby姓名布局
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width/6,height/10);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        params.setMargins(0, 0, DensityUtils.dp2px(mContext, 30), DensityUtils.dp2px(mContext, 20));
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        tv_name.setLayoutParams(params);

    }

    //初始化数据
    private void initData(){

        if(sp.getString(UserInfoUtils.LoginSPKey.BABY_GENDER,"M").equals("M"))
            iv_avatar.setImageResource(R.mipmap.bigboy);
        else
            iv_avatar.setImageResource(R.mipmap.biggirl);

        tv_name.setText(sp.getString(UserInfoUtils.LoginSPKey.BABY_NAME,"豪豪"));
        //踢被开关是否打开
        if(isSleepOn){
            iv_sleep.setBackground(getResources().getDrawable(R.mipmap.sleep_on));
        }else{
            iv_sleep.setBackground(getResources().getDrawable(R.mipmap.sleep_off));
        }
        //防丢开关是否打开
        if(isProtectOn){
            iv_protect.setBackground(getResources().getDrawable(R.mipmap.protect_on));
        }else{
            iv_protect.setBackground(getResources().getDrawable(R.mipmap.protect_off));
        }
    }

    //初始化布局
    private void initView(View menuView){
        rl_temp = (RelativeLayout) menuView.findViewById(R.id.rl_temp);
        rl_time = (RelativeLayout) menuView.findViewById(R.id.rl_time);
        iv_protect = (ImageView) menuView.findViewById(R.id.iv_protect);
        rl_security = (RelativeLayout) menuView.findViewById(R.id.rl_security);
        rl_cal = (RelativeLayout) menuView.findViewById(R.id.rl_cal);
        rl_setting = (RelativeLayout) menuView.findViewById(R.id.rl_setting);
        tv_temp = (TextView) menuView.findViewById(R.id.tv_temp);
        tv_time = (TextView) menuView.findViewById(R.id.tv_time);
        iv_sleep = (ImageView) menuView.findViewById(R.id.iv_sleep);
        tv_security = (TextView) menuView.findViewById(R.id.tv_security);
        tv_cal = (TextView) menuView.findViewById(R.id.tv_cal);

        tv_cal_unit = (TextView) menuView.findViewById(R.id.tv_cal_unit);
        tv_security_tip = (TextView) menuView.findViewById(R.id.tv_security_tip);
        tv_temp_unit = (TextView) menuView.findViewById(R.id.tv_temp_unit);
        tv_time_unit = (TextView) menuView.findViewById(R.id.tv_time_unit);
        iv_cal = (ImageView) menuView.findViewById(R.id.iv_cal);
        iv_setting = (ImageView) menuView.findViewById(R.id.iv_setting);
        iv_temp = (ImageView) menuView.findViewById(R.id.iv_temp);
        iv_time = (ImageView) menuView.findViewById(R.id.iv_time);

        rl_temp.setOnClickListener(this);
        rl_time.setOnClickListener(this);
        iv_protect.setOnClickListener(this);
        iv_sleep.setOnClickListener(this);
        rl_security.setOnClickListener(this);
        rl_cal.setOnClickListener(this);
        rl_setting.setOnClickListener(this);

    }

    //进入主界面的初始动画
    private void animationView(){
        long durationTime = 2000;
        long alphaTime = 3000;
        titleTranslateAnimation(rl_security,width-DensityUtils.dp2px(mContext,115),0,
                DensityUtils.dp2px(mContext,-172),0,durationTime);
        titleTranslateAnimation(iv_protect,width-DensityUtils.dp2px(mContext,260),0,
                DensityUtils.dp2px(mContext,-75), 0,durationTime);
        titleTranslateAnimation(rl_temp,width-DensityUtils.dp2px(mContext,200),0,
                DensityUtils.dp2px(mContext,-30),0,durationTime);
        titleTranslateAnimation(rl_cal,DensityUtils.dp2px(mContext,-220),0,
                DensityUtils.dp2px(mContext,30),0,durationTime);
        titleTranslateAnimation(rl_time,DensityUtils.dp2px(mContext,-162),0,
                DensityUtils.dp2px(mContext,30),0,durationTime);
        titleTranslateAnimation(iv_sleep,DensityUtils.dp2px(mContext,-65),0,
                DensityUtils.dp2px(mContext,30),0,durationTime);
        titleTranslateAnimation(rl_setting,DensityUtils.dp2px(mContext,20),0,
                DensityUtils.dp2px(mContext,-10),0,durationTime);
        titleTranslateAnimation(iv_avatar,0,0,height*2/3,0,1500);
        titleTranslateAnimation(rl_line,0,0,height*2/3,0,1500);

        titleAlphaAnimation(tv_cal,0,1,alphaTime);
        titleAlphaAnimation(tv_cal_unit,0,1,alphaTime);
        titleAlphaAnimation(iv_cal,0,1,alphaTime);
        titleAlphaAnimation(tv_security,0,1,alphaTime);
        titleAlphaAnimation(tv_security_tip,0,1,alphaTime);
        titleAlphaAnimation(tv_temp,0,1,alphaTime);
        titleAlphaAnimation(tv_temp_unit,0,1,alphaTime);
        titleAlphaAnimation(iv_temp,0,1,alphaTime);
        titleAlphaAnimation(tv_time,0,1,alphaTime);
        titleAlphaAnimation(tv_time_unit,0,1,alphaTime);
        titleAlphaAnimation(iv_time,0,1,alphaTime);
        titleAlphaAnimation(iv_setting,0,1,alphaTime);
        titleAlphaAnimation(tv_name,0,1,alphaTime);
    }

    //反转动画--点击气球菜单后执行的动画
    private void animationReverse(){
        long durationTime = 2000;
        long alphaTime = 2000;
        titleTranslateAnimation(rl_security,0,width,
                0,DensityUtils.dp2px(mContext,-172),durationTime);
        titleTranslateAnimation(iv_protect,0,width-DensityUtils.dp2px(mContext,260),
                0, DensityUtils.dp2px(mContext,-75),durationTime);
        titleTranslateAnimation(rl_temp,0,width-DensityUtils.dp2px(mContext,200),
                0,DensityUtils.dp2px(mContext,-30),durationTime);
        titleTranslateAnimation(rl_cal,0,DensityUtils.dp2px(mContext,-220),
                0,DensityUtils.dp2px(mContext,30),durationTime);
        titleTranslateAnimation(rl_time,0,DensityUtils.dp2px(mContext,-162),
                0,DensityUtils.dp2px(mContext,30),durationTime);
        titleTranslateAnimation(iv_sleep,0,DensityUtils.dp2px(mContext,-65),
                0,DensityUtils.dp2px(mContext,30),durationTime);
        titleTranslateAnimation(rl_setting,0,DensityUtils.dp2px(mContext,60),
                0,DensityUtils.dp2px(mContext,-10),durationTime);

        titleTranslateAnimation(iv_avatar,0,0,0,height*2/3,1500);
        titleTranslateAnimation(rl_line,0,0,0,height*2/3,1500);

        titleAlphaAnimation(tv_cal,1,0,alphaTime);
        titleAlphaAnimation(tv_cal_unit,1,0,alphaTime);
        titleAlphaAnimation(iv_cal,1,0,alphaTime);
        titleAlphaAnimation(tv_security,1,0,alphaTime);
        titleAlphaAnimation(tv_security_tip,1,0,alphaTime);
        titleAlphaAnimation(tv_temp,1,0,alphaTime);
        titleAlphaAnimation(tv_temp_unit,1,0,alphaTime);
        titleAlphaAnimation(iv_temp,1,0,alphaTime);
        titleAlphaAnimation(tv_time,1,0,alphaTime);
        titleAlphaAnimation(tv_time_unit,1,0,alphaTime);
        titleAlphaAnimation(iv_time,1,0,alphaTime);
        titleAlphaAnimation(iv_setting,1,0,alphaTime);
        titleAlphaAnimation(tv_name,1,0,alphaTime);
    }

    //透明效果动画
    public void titleAlphaAnimation(View view,float fromAlpha,float toAlpha,long durationTime){
        AlphaAnimation animation = new AlphaAnimation(fromAlpha,toAlpha);
        animation.setDuration(durationTime);
        view.setAnimation(animation);
        animation.startNow();
    }

    //移动效果动画
    public void titleTranslateAnimation(View view ,float fromXDelta, float toXDelta, float fromYDelta, float toYDelta,long durationTime){
        TranslateAnimation translateAnimation = new TranslateAnimation(fromXDelta,toXDelta,fromYDelta,toYDelta);
        translateAnimation.setDuration(durationTime);
        translateAnimation.setFillAfter(true);
        view.setAnimation(translateAnimation);
        translateAnimation.startNow();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        mBackground.registerSensorManager();
        super.onResume();
        // Adjust the Parallax forward tilt adjustment
        mBackground.setForwardTiltOffset(.35f);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View menuView = inflater.inflate(R.layout.fragment_main, null);
        initView(menuView);

        mBackground.setParallaxIntensity(1.025f);
        mBackground.setImageResource(R.mipmap.bg_default);
        rl_operation.removeAllViews();
        rl_operation.addView(menuView);
        initData();
        animationView();

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.LTGRAY);
        Path path = new Path();
        //线段起点
        path.moveTo(width - DensityUtils.dp2px(mContext,265)*720/width , DensityUtils.dp2px(mContext,160) + DensityUtils.dp2px(mContext, 140f) * 9 / 10);
        //线段终点
        path.lineTo(width*width/720 / 3, (float)(height * 2.3 / 3));
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//设置为透明，画布也是透明
        canvas.drawPath(path,paint);
        Drawable drawable = new BitmapDrawable(bitmap);
        rl_line.setBackground(drawable);
        titleAlphaAnimation(rl_line,0,1,3000);

        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
            System.out.println("######result="+result);
        }

    }


    @Override
    public void onPause() {
        mBackground.unregisterSensorManager();
        super.onPause();
        scanLeDevice(false);
        getActivity().unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //解绑蓝牙服务
        getActivity().unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }


    private void clearAnimation(){
        rl_temp.clearAnimation();
        rl_security.clearAnimation();
        rl_cal.clearAnimation();
        rl_line.clearAnimation();
        rl_setting.clearAnimation();
        iv_sleep.clearAnimation();
        iv_protect.clearAnimation();
        tv_time_unit.clearAnimation();
        tv_time.clearAnimation();
        iv_time.clearAnimation();
        tv_temp_unit.clearAnimation();
        tv_temp.clearAnimation();
        iv_temp.clearAnimation();
        tv_cal.clearAnimation();
        tv_cal_unit.clearAnimation();
        iv_cal.clearAnimation();
        tv_security.clearAnimation();
        tv_security_tip.clearAnimation();
        iv_setting.clearAnimation();
        tv_name.clearAnimation();

    }

    @Override
    public void onClick(View view) {
        final Intent intent = new Intent();
        AnimationSet set = new AnimationSet(true);
        switch (view.getId()){
            case R.id.rl_temp:
                clearAnimation();
                animationReverse();
                rl_temp.clearAnimation();

                set.addAnimation(new TranslateAnimation(0,-50,0,-60));
                set.addAnimation(new ScaleAnimation(0f,4f,0f,3f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f));
                set.setFillAfter(true);
                set.setDuration(2000);
                rl_temp.startAnimation(set);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        intent.setClass(mContext, CurrentTempActivity.class);
                        startActivity(intent);
                    }
                },1500);

                break;
            case R.id.rl_time:
                clearAnimation();
                animationReverse();
                rl_time.clearAnimation();
                set.addAnimation(new TranslateAnimation(0,width/8,0,-width/8));
                set.addAnimation(new ScaleAnimation(0f,4f,0f,2.5f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f));
                set.setFillAfter(true);
                set.setDuration(2000);
                rl_time.startAnimation(set);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        intent.setClass(mContext, SleepTimeActivity.class);
                        startActivity(intent);
                    }
                },1500);

                break;
            case R.id.iv_sleep:
                //踢被开关是否打开
                if(!isSleepOn){
                    isSleepOn = true;
                    iv_sleep.setBackground(getResources().getDrawable(R.mipmap.sleep_on));
                }else{
                    isSleepOn = false;
                    iv_sleep.setBackground(getResources().getDrawable(R.mipmap.sleep_off));
                }
                break;
            case R.id.iv_protect:
                //防丢开关是否打开
                if(!isProtectOn){
                    isProtectOn = true;
                    iv_protect.setBackground(getResources().getDrawable(R.mipmap.protect_on));
                }else{
                    isProtectOn = false;
                    iv_protect.setBackground(getResources().getDrawable(R.mipmap.protect_off));
                }
                break;
            case R.id.rl_security:
                clearAnimation();
                animationReverse();
                rl_security.clearAnimation();
//                set.addAnimation(new TranslateAnimation(0,-30,0,-30));
                set.addAnimation(new ScaleAnimation(0f,3f,0f,2f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f));
                set.setFillAfter(true);
                set.setDuration(2000);
                rl_security.startAnimation(set);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        intent.setClass(mContext, SecurityActivity.class);
                        startActivity(intent);
                    }
                },1500);

                break;
            case R.id.rl_cal:
                clearAnimation();
                animationReverse();
                rl_cal.clearAnimation();
                set.addAnimation(new TranslateAnimation(0,0,0,-height/9));
                set.addAnimation(new ScaleAnimation(0f,3.6f,0f,2.4f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f));
                set.setFillAfter(true);
                set.setDuration(2000);
                rl_cal.startAnimation(set);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        intent.setClass(mContext, CalActivity.class);
                        startActivity(intent);
                    }
                },1500);

                break;
            case R.id.rl_setting:
                clearAnimation();
                animationReverse();
                rl_setting.clearAnimation();
                set.addAnimation(new TranslateAnimation(0,-width/10,0,-height/10));
                set.addAnimation(new ScaleAnimation(0f,5f,0f,5f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f));
                set.setFillAfter(true);
                set.setDuration(2000);
                rl_setting.startAnimation(set);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        intent.setClass(mContext, SettingActivity.class);
                        startActivity(intent);
                    }
                },1500);

                break;
        }

    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                String uuid = null;
                List<BluetoothGattService> gattServices=mBluetoothLeService.getSupportedGattServices();
                for (BluetoothGattService gattService : gattServices){
                    if(gattService.getUuid().toString().substring(0,8).equals(BabyHeroProtocol.SERVICE_ACTIVITY_UUID)){
                        List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                        for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics){
                            uuid = gattCharacteristic.getUuid().toString().substring(0,8);
                            if(uuid.equals(BabyHeroProtocol.CHARACT_STATE_WRITE_UUID)){
                                byte [] value = BabyHeroProtocol.mergeL1L2(BabyHeroProtocol.createL1(BabyHeroProtocol.handShake()),BabyHeroProtocol.handShake());
                                gattCharacteristic.setValue(value);
                                mBluetoothLeService.writeCharacteristic(gattCharacteristic);
                            }
                        }
                    }
                }

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                System.out.println("######data="+data);
            }else if(BluetoothLeService.ACTION_GATT_WRITE.equals(action)){
                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                System.out.println("$$$$$$$$$$"+data);
                String []dataStr = data.split(" ");
                if(dataStr[10].equals("72")){
                    Log.i(TAG,"握手成功！");
                    String uuid = null;
                    List<BluetoothGattService> gattServices=mBluetoothLeService.getSupportedGattServices();
                    for (BluetoothGattService gattService : gattServices){
                        if(gattService.getUuid().toString().substring(0,8).equals(BabyHeroProtocol.SERVICE_ACTIVITY_UUID)){
                            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics){
                                uuid = gattCharacteristic.getUuid().toString().substring(0,8);
                                System.out.println("uuid="+uuid);
                                if(uuid.equals(BabyHeroProtocol.CHARACT_STATE_WRITE_UUID)){
                                    byte [] value = BabyHeroProtocol.mergeL1L2(BabyHeroProtocol.createL1(BabyHeroProtocol.syscTime()),BabyHeroProtocol.syscTime());
                                    gattCharacteristic.setValue(value);
                                    mBluetoothLeService.writeCharacteristic(gattCharacteristic);
                                }
                                if(uuid.equals(BabyHeroProtocol.CHARACT_STATE_NOTIFY_UUID)){
                                    byte [] value = BabyHeroProtocol.mergeL1L2(BabyHeroProtocol.createL1(BabyHeroProtocol.readCurrentTime()),BabyHeroProtocol.readCurrentTime());
                                    gattCharacteristic.setValue(value);
                                    mBluetoothLeService.setCharacteristicNotification(gattCharacteristic,true);
                                }
                            }
                        }
                    }
                }
                if(dataStr[10].equals("51")&& dataStr.length > 14){
                    Log.i(TAG,"设置时间成功！");
                    String uuid = null;
                    List<BluetoothGattService> gattServices=mBluetoothLeService.getSupportedGattServices();
                    for (BluetoothGattService gattService : gattServices){
                        if(gattService.getUuid().toString().substring(0,8).equals(BabyHeroProtocol.SERVICE_ACTIVITY_UUID)){
                            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics){
                                uuid = gattCharacteristic.getUuid().toString().substring(0,8);
                                if(uuid.equals(BabyHeroProtocol.CHARACT_STATE_WRITE_UUID)){
                                    byte [] value = BabyHeroProtocol.mergeL1L2(BabyHeroProtocol.createL1(BabyHeroProtocol.readCurrentTime()),BabyHeroProtocol.readCurrentTime());
                                    gattCharacteristic.setValue(value);
                                    mBluetoothLeService.writeCharacteristic(gattCharacteristic);
                                }
                            }
                        }
                    }
                }
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_WRITE);
        return intentFilter;
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                getActivity().finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (device.getName() != null) {
                        //只存留设备名称是apptest2的设备
                        if (device.getName().equals("apptest") && !mLeDevices.contains(device)) {
                            mLeDevices.add(device);
                            System.out.println("####name=" + device.getName() + "###address=" + device.getAddress());
                            mDeviceAddress = mLeDevices.get(0).getAddress();
                            mBluetoothLeService.connect(mDeviceAddress);
                        }
                    }
                }
            });
        }
    };
}
