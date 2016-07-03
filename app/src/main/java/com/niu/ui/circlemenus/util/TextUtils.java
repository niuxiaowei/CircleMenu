package com.niu.ui.circlemenus.util;

import android.graphics.Paint;

/**
 * Created by niuxiaowei on 16/6/26.
 */
public class TextUtils {

    /**
     * 测量文字宽度
     * @param paint
     * @param text
     * @return
     */
    public static int measureTextWidth(Paint paint, String text) {
        if (!android.text.TextUtils.isEmpty(text) && paint != null) {
            return (int)paint.measureText(text);
        }
        return 0;

    }

    /**
     * 测量文字高度
     * @param paint
     * @return
     */
    public static int measureTextHeight(Paint paint){
        return (int)Math.ceil(paint.descent() - paint.ascent());
    }
}
