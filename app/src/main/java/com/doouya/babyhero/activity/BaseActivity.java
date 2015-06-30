package com.doouya.babyhero.activity;

import android.app.Activity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * Created by le on 2015/6/13.
 */
public class BaseActivity extends Activity{

    //缩放动画
    public void titleScaleAnimation(View view,float toX,float toY, long durationTime){
        Animation scaleAnimation = new ScaleAnimation(0f,toX,0f,toY,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setDuration(durationTime);
        scaleAnimation.setFillAfter(true);
        view.setAnimation(scaleAnimation);
        scaleAnimation.startNow();
    }
    //透明效果动画
    public void titleAlphaAnimation(View view,float fromAlpha,float toAlpha,long durationTime){
        AlphaAnimation animation = new AlphaAnimation(0,1);
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

}
