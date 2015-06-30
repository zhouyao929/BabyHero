package com.doouya.babyhero.activity;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doouya.babyhero.R;
import com.doouya.babyhero.utils.DensityUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CalActivity extends BaseActivity implements View.OnClickListener{
    //头部背景,返回按钮
    private ImageView iv_title,iv_finish;
    //睡眠时间布局
    private RelativeLayout rl_cal;
    private TextView tv_cal;
    //运动量图表
    private BarChart bc_calChart;
    //向前、向后按钮
    private ImageView iv_forward;
    private ImageView iv_next;
    //图表时间
    private TextView tv_time_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cal);
        initView();
        getCurrentData();
        titleScaleAnimation(iv_title, 3.6f, 2.4f, 10);
        titleAlphaAnimation(iv_finish, 0, 1, 1000);
        titleAlphaAnimation(rl_cal, 0, 1, 1000);

        iv_finish.setOnClickListener(this);
        iv_forward.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        iv_title.setOnClickListener(this);
    }

    private void initView(){
        iv_title = (ImageView) findViewById(R.id.iv_title);
        iv_finish = (ImageView) findViewById(R.id.iv_finish);
        rl_cal = (RelativeLayout) findViewById(R.id.rl_cal);
        tv_cal = (TextView) findViewById(R.id.tv_cal);
        bc_calChart = (BarChart) findViewById(R.id.bc_calChart);
        iv_forward = (ImageView) findViewById(R.id.iv_forward);
        iv_next = (ImageView) findViewById(R.id.iv_next);
        tv_time_bar = (TextView) findViewById(R.id.tv_time_bar);

        //获取屏幕宽高
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(width/3-DensityUtils.dp2px(this,20),0,0,0);
        iv_title.setLayoutParams(params);

        RelativeLayout.LayoutParams chartParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        chartParams.addRule(RelativeLayout.ABOVE,R.id.tv_time_bar);
        chartParams.setMargins(DensityUtils.dp2px(this, 5),height/2-DensityUtils.dp2px(this,35),DensityUtils.dp2px(this, 5),10);
        bc_calChart.setLayoutParams(chartParams);


    }

    private void showBarChart(BarData barData){
        bc_calChart.setDescription("");
        bc_calChart.setNoDataTextDescription("暂无数据");
        bc_calChart.setDrawBorders(false);
        bc_calChart.setTouchEnabled(true);
        bc_calChart.setDragEnabled(true);//设置是否可以拖拽
        bc_calChart.setScaleEnabled(true);//是否可以缩放
        bc_calChart.setPinchZoom(true);//为false则x，y轴可以独立缩放
        XAxis x = bc_calChart.getXAxis();
        x.setEnabled(true);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawGridLines(false); //不显示网格线
        x.setSpaceBetweenLabels(12);
        x.setAxisLineWidth(3);
        x.setAxisLineColor(getResources().getColor(R.color.cal_yellow));

        YAxis leftY = bc_calChart.getAxisLeft();
        leftY.setDrawGridLines(false);
        leftY.setEnabled(false);
        YAxis rightY = bc_calChart.getAxisRight();
        rightY.setDrawGridLines(false);
        rightY.setEnabled(false);

        bc_calChart.setData(barData);
        bc_calChart.animateXY(2000,2000);
        bc_calChart.invalidate();
    }

    private BarData getBarData(int count,float range){
        ArrayList<String> xValues = new ArrayList<>();
        for (int i = 0;i<count;i++)
            xValues.add(""+i); //x轴显示的数据
        //y轴显示的数据
        ArrayList<BarEntry> yValues = new ArrayList<>();
        for(int i = 0;i<count;i++){
            float value = (float)(Math.random() * range);
            yValues.add(new BarEntry(value,i));
        }
        //y轴的数据集合
        BarDataSet barDataSet = new BarDataSet(yValues,"测试数据");
        //用y轴的集合来设置参数
        barDataSet.setDrawValues(false);//不显示曲线数据

        barDataSet.setColor(getResources().getColor(R.color.cal_yellow));
        ArrayList<BarDataSet> barDataSets = new ArrayList<>();
        barDataSets.add(barDataSet);

        BarData barData = new BarData(xValues,barDataSets);
        return barData;
    }

    private void getCurrentData(){
        //获取当前日期
        SimpleDateFormat sdFormat = new SimpleDateFormat("MM月dd日");
        Date currentDate = new Date(System.currentTimeMillis());
        String date = sdFormat.format(currentDate);
        tv_time_bar.setText(date);

        tv_cal.setText("420");

        BarData mBarData = getBarData(24, 100);
        showBarChart(mBarData);

        iv_forward.setVisibility(View.VISIBLE);
        iv_next.setVisibility(View.GONE);
    }

    //获取昨天的数据
    private void getYesterDayData(){
        SimpleDateFormat sdFormat = new SimpleDateFormat("MM月dd日");
        tv_time_bar.setText(sdFormat.format(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24)));
        iv_forward.setVisibility(View.INVISIBLE);
        iv_next.setVisibility(View.VISIBLE);

        tv_cal.setText("486");

        BarData mBarData = getBarData(24,100);
        showBarChart(mBarData);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_finish:
                finish();
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
