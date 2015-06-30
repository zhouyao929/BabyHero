package com.doouya.babyhero;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by le on 2015/6/18.
 */
public class BabyHeroApplication extends Application {
    public static short sequenceId = 0;
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
