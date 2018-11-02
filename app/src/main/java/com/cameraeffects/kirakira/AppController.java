/**
 * Created by Taylor Baker on 11/16/2017 10:02:26 AM
 */


package com.cameraeffects.kirakira;

import android.app.Application;
import android.content.Context;

import com.cameraeffects.kirakira.filter.helper.analog3_FilterContext;


public class AppController extends Application {

    public static Context context;


    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        analog3_FilterContext.context = this;
    }



}
