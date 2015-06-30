package com.doouya.babyhero.utils;

import android.content.Context;
import android.util.TypedValue;

import java.util.HashMap;

/** 
 * 常用单位转换的辅助类 
 *  
 *  
 *  
 */  
public class DensityUtils  
{  
    private DensityUtils()  
    {  
        /* cannot be instantiated */  
        throw new UnsupportedOperationException("cannot be instantiated");
    }  
  
    /** 
     * dp转px 
     *  
     * @param context 上下文对象
     * @param dpVal dp值
     * @return px值
     */

    private static HashMap<Float,Integer> dpCache=new HashMap<>();
    public static int dp2px(Context context, float dpVal)
    {
        if(dpCache.containsKey(dpVal))
            return dpCache.get(dpVal);
        int px=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
        dpCache.put(dpVal,px);
        return px;
    }  
  
    /** 
     * sp转px 
     *  
     * @param context 上下文对象
     * @param spVal sp值
     * @return px值
     */  
    public static int sp2px(Context context, float spVal)
    {  
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());  
    }  
  
    /** 
     * px转dp 
     *  
     * @param context 上下文对象
     * @param pxVal px值
     * @return dp值
     */  
    public static float px2dp(Context context, float pxVal)
    {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (pxVal / scale);  
    }  
  
    /** 
     * px转sp 
     *  
     * @param context 上下文对象
     * @param pxVal px值
     * @return sp值
     */  
    public static float px2sp(Context context, float pxVal)
    {  
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);  
    }  
  
}  
