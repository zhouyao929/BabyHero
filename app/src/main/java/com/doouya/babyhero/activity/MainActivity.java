package com.doouya.babyhero.activity;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.doouya.babyhero.R;
import com.doouya.babyhero.fragment.ParallaxFragment;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null){
            getFragmentManager().beginTransaction()
                    .add(R.id.container,new ParallaxFragment()).commit();
        }
    }

}
