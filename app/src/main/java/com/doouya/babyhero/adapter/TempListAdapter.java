package com.doouya.babyhero.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.doouya.babyhero.R;
import com.doouya.babyhero.activity.SettingActivity;
import com.doouya.babyhero.utils.DensityUtils;


/**
 * Created by zhouyao on 2015/6/12.
 */
public class TempListAdapter extends BaseAdapter{

    private Context mContext;
    private String data[][] = null;
    //布局适配器
    private ViewHolder viewHolder = null;

    public TempListAdapter(Context context,String data[][]){
        this.mContext = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position ) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_temp_list,parent,false);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tv_temp = (TextView) convertView.findViewById(R.id.tv_temp);
            viewHolder.iv_arrow = (ImageView) convertView.findViewById(R.id.iv_arrow);
            viewHolder.line = convertView.findViewById(R.id.line);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_title.setText(data[position][0]);
        viewHolder.tv_temp.setText(data[position][1]);
        if(mContext.getClass().equals(SettingActivity.class) && position == 0){
            viewHolder.tv_temp.setTextColor(mContext.getResources().getColor(R.color.setting_blue));
            viewHolder.tv_temp.setTextSize(DensityUtils.sp2px(mContext,8));
        }
        if(position == data.length-1){
            viewHolder.line.setVisibility(View.GONE);
        }
        if(mContext.getClass().equals(SettingActivity.class) && position == data.length-1){
            viewHolder.iv_arrow.setVisibility(View.VISIBLE);
            viewHolder.tv_temp.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder{
        TextView tv_title;
        TextView tv_temp;
        ImageView iv_arrow;
        View line;
    }
}
