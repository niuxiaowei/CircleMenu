package com.niu.ui.circlemenus.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.WindowManager;

import com.niu.ui.circlemenus.App;

/**
 * Created by niuxiaowei on 16/6/22.
 */
public class Utils {

    /**
     * 把dp转化为px
     * @param dp
     * @return
     */
    public static int dpToPx(float dp) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, App.getContext().getResources().getDisplayMetrics());
        return (int) px;
    }

    /**
     * 获取屏幕宽度
     * @return
     */
    public static int getScreenWidth(){
        WindowManager wm = (WindowManager) App.getContext().getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();

    }

}
