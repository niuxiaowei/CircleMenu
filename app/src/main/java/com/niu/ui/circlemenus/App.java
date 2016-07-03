package com.niu.ui.circlemenus;

import android.app.Application;
import android.content.Context;

/**
 * Created by niuxiaowei on 16/6/22.
 */
public class App extends Application {

    public static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
    }

    public static Context getContext() {
        return sContext;
    }
}
