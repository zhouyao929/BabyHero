package com.doouya.babyhero.activity;

import android.os.Bundle;
import android.os.DropBoxManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doouya.babyhero.R;
import com.doouya.babyhero.utils.DensityUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SleepTimeActivity extends BaseActivity implements View.OnClickListener {
    //头部背景,返回按钮
    private ImageView iv_title,iv_finish;
    //睡眠时间布局
    private RelativeLayout rl_sleep_time;
    private TextView tv_sleep_time;
    //踢被模式提示，踢被模式按钮
    private TextView tv_mode_tip,tv_mode_button;
    //睡眠提示
    private TextView tv_tip;
    //睡眠时间图表
    private LineChart lc_timeChart;
    //向前、向后按钮
    private ImageView iv_forward;
    private ImageView iv_next;
    //图表时间
    private TextView tv_time_bar;
    //是否开启踢被模式
    private Boolean isTrue = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_time);
        initView();
        getCurrentData();
        //title相关动画
        titleScaleAnimation(iv_title,4f,2.5f,10);
        titleAlphaAnimation(iv_finish,0,1,1000);
        titleAlphaAnimation(rl_sleep_time,0,1,1000);
        titleAlphaAnimation(tv_mode_tip,0,1,1000);
        titleAlphaAnimation(tv_mode_button,0,1,1000);

        iv_finish.setOnClickListener(this);
        tv_mode_button.setOnClickListener(this);
        iv_forward.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        iv_title.setOnClickListener(this);
    }

    private void initView(){
        iv_title = (ImageView) findViewById(R.id.iv_title);
        iv_finish = (ImageView) findViewById(R.id.iv_finish);
        rl_sleep_time = (RelativeLayout) findViewById(R.id.rl_sleep_time);
        tv_sleep_time = (TextView) findViewById(R.id.tv_sleep_time);
        tv_mode_tip = (TextView) findViewById(R.id.tv_mode_tip);
        tv_mode_button = (TextView) findViewById(R.id.tv_mode_button);
        tv_tip = (TextView) findViewById(R.id.tv_tip);
        lc_timeChart = (LineChart) findViewById(R.id.lc_timeChart);
        iv_forward = (ImageView) findViewById(R.id.iv_forward);
        iv_next = (ImageView) findViewById(R.id.iv_next);
        tv_time_bar = (TextView) findViewById(R.id.tv_time_bar);

        //获取屏幕宽高
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
        params.setMargins(DensityUtils.dp2px(this, 5),height/2-DensityUtils.dp2px(this,25),DensityUtils.dp2px(this, 5),0);
        tv_tip.setLayoutParams(params);

    }

    private void showLineChart(LineData lineData){
        lc_timeChart.setDescription("");//数据描述
        lc_timeChart.setNoDataTextDescription("暂无数据");//没有数据的时候显示
        lc_timeChart.setDrawBorders(false);//是否在折线图上添加边框
        lc_timeChart.setTouchEnabled(true);//设置是否可以触摸
        lc_timeChart.setDragEnabled(true);//设置是否可以拖拽
        lc_timeChart.setScaleEnabled(true);//是否可以缩放
        lc_timeChart.setPinchZoom(true);//为false则x，y轴可以独立缩放
        XAxis x = lc_timeChart.getXAxis();
        x.setEnabled(true);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawGridLines(false); //不显示网格线
        x.setSpaceBetweenLabels(12);
        YAxis leftY = lc_timeChart.getAxisLeft();
        leftY.setDrawGridLines(false);
        leftY.setEnabled(false);
        YAxis rightY = lc_timeChart.getAxisRight();
        rightY.setDrawGridLines(false);
        rightY.setEnabled(false);

        lc_timeChart.setData(lineData); //设置数据
        lc_timeChart.animateX(2000);
        lc_timeChart.invalidate();
    }

    private LineData getLineData(int count ,float range){
        ArrayList<String> xValues = new ArrayList<>();
        for (int i = 0;i<count;i++)
            xValues.add(""+i); //x轴显示的数据
        //y轴显示的数据
        ArrayList<Entry> yValues = new ArrayList<>();
        for(int i = 0;i<count;i++){
            float value = (float)(Math.random() * range);
            yValues.add(new Entry(value,i));
        }
        //y轴的数据集合
        LineDataSet lineDataSet = new LineDataSet(yValues,"测试数据");
        //用y轴的集合来设置参数
        lineDataSet.setDrawValues(false);//不显示曲线数据
        lineDataSet.setLineWidth(1f);//线宽
        lineDataSet.setDrawFilled(true);//设置允许填充
        lineDataSet.setFillColor(getResources().getColor(R.color.time_blue));//填充的颜色
        lineDataSet.setDrawCircles(false);//图标上的数据点不用小圆圈表示
        lineDataSet.setDrawCubic(true);//允许曲线平滑
        lineDataSet.setCubicIntensity(0.1f);//设置折线的平滑度
        lineDataSet.setColor(getResources().getColor(R.color.time_blue));
        ArrayList<LineDataSet> lineDataSets = new ArrayList<>();
        lineDataSets.add(lineDataSet);

        LineData lineData = new LineData(xValues,lineDataSets);
        return lineData;
    }

    //踢被子次数
    private void kickQuiltNum(){
        int num = 0;

        if(num == 0)
            tv_mode_tip.setText("宝贝暂无踢被情况");
        else{
            tv_mode_tip.setText("宝贝踢被"+num+"次");
        }

    }

    private void getCurrentData(){
        //获取当前日期
        SimpleDateFormat sdFormat = new SimpleDateFormat("MM月dd日");
        Date currentDate = new Date(System.currentTimeMillis());
        String date = sdFormat.format(currentDate);
        tv_time_bar.setText(date);

        LineData mLineData = getLineData(24,60);
        showLineChart(mLineData);

        /**
         * 添加获取当前模式代码
         */
        if(isTrue) {
            kickQuiltNum();
        }else{
            tv_mode_tip.setText("未开启踢被模式");
        }
        tv_sleep_time.setText("7");
        tv_tip.setText("深睡眠3小时，浅睡眠4小时");
        tv_mode_button.setVisibility(View.VISIBLE);
        iv_forward.setVisibility(View.VISIBLE);
        iv_next.setVisibility(View.GONE);
    }

    //获取昨天的数据
    private void getYesterDayData(){
        SimpleDateFormat sdFormat = new SimpleDateFormat("MM月dd日");
        tv_time_bar.setText(sdFormat.format(new Date(System.currentTimeMillis() - 1000*60*60*24)));
        int num = 0;
        tv_mode_tip.setText("宝贝踢被"+num+"次");
        tv_sleep_time.setText("9.2");
        tv_tip.setText("深睡眠4小时，浅睡眠5.2小时");
        tv_mode_button.setVisibility(View.GONE);
        iv_forward.setVisibility(View.INVISIBLE);
        iv_next.setVisibility(View.VISIBLE);

        LineData mLineData = getLineData(24,60);
        showLineChart(mLineData);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_finish:
                finish();
                break;
            case R.id.tv_mode_button:
                if(!isTrue){
                    isTrue = true;
                    tv_mode_button.setText("关闭踢被模式");
                    tv_mode_button.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_red));
                    kickQuiltNum();
                }else{
                    isTrue = false;
                    tv_mode_button.setText("开启踢被模式");
                    tv_mode_button.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_white));
                    tv_mode_tip.setText("未开启踢被模式");
                }
                break;
            case R.id.iv_forward:
                getYesterDayData();
                break;
            case R.id.iv_next:
                getCurrentData();
                break;
            case R.id.iv_title:
                finish();
                break;
        }
    }
}
