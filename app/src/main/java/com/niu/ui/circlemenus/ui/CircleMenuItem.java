package com.niu.ui.circlemenus.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.niu.ui.circlemenus.R;
import com.niu.ui.circlemenus.util.Utils;

/**
 * 中间菜单是一个圆形
 * Created by niuxiaowei on 16/6/25.
 */
public class CircleMenuItem extends View  {

    private int mRadius = Utils.dpToPx(30);
    private int mCenterX, mCenterY;
    private Paint mPaint;

    private int mTextColor = Color.BLACK;
    private int mTextSize = Utils.dpToPx(16);
    private Paint mTextPaint;

    private static final String OPEN_CIRCLEMENUS = "OPEN", CLOSE_CIRCLEMENUS = "CLOSE";
    private String mMenuItemContent = OPEN_CIRCLEMENUS;

    public CircleMenuItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleMenuItem);
        if (a != null) {
            mRadius = (int) a.getDimension(R.styleable.CircleMenuItem_radius, mRadius);

            ARCMenuItem.sInnerRadius = mRadius;
            a.recycle();
        }
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.CYAN);

        mTextPaint = new Paint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
    }

    /**
     * 通知圆形菜单的状态，状态有打开和关闭两种，根据不同的状态跟换不同的文案
     * @param isOpen
     */
    public void notifyCircleMenusState(boolean isOpen){
        if(isOpen){
            mMenuItemContent = CLOSE_CIRCLEMENUS;
        }else{
            mMenuItemContent = OPEN_CIRCLEMENUS;
        }
        invalidate();
    }



    public int getRadius() {
        return mRadius;
    }

    private int getMinHeight() {
        return getPaddingTop() + getPaddingBottom() + mRadius * 2;
    }

    private int getMinWidth() {
        return getPaddingLeft() + getPaddingRight() + mRadius * 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthResult = getMinWidth();
        int heightResult = getMinHeight();

        /*测量宽度*/
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthModel = MeasureSpec.getMode(widthMeasureSpec);
        if (widthModel == MeasureSpec.AT_MOST) {
            widthResult = Math.min(widthResult, widthSize);
        } else if (widthModel == MeasureSpec.EXACTLY) {
            widthResult = widthSize;
        }

        /*测量高度*/
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightModel = MeasureSpec.getMode(heightMeasureSpec);

        if (heightModel == MeasureSpec.EXACTLY) {
            heightResult = heightSize;
        } else if (heightModel == MeasureSpec.AT_MOST) {
            heightResult = Math.min(heightResult, heightSize);
        }

        setMeasuredDimension(widthResult, heightResult);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
        /*画文字*/
        if (!TextUtils.isEmpty(mMenuItemContent)) {

            int textXOffset = mCenterX - com.niu.ui.circlemenus.util.TextUtils.measureTextWidth(mTextPaint, mMenuItemContent)/2;
            int textYOffset = mCenterY + com.niu.ui.circlemenus.util.TextUtils.measureTextHeight(mTextPaint)/2;
            canvas.drawText(mMenuItemContent,textXOffset, textYOffset,  mTextPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h / 2;
    }

    /**
     * 判断点击的点是否在menuitem的范围内
     *
     * @param clickX
     * @param clickY
     * @return
     */
    private boolean checkIsClickMenuItem(float clickX, float clickY) {

        /*计算2个点之间的距离*/
        double disToCenter = Math.sqrt(Math.pow(Math.abs(clickX - mCenterX), 2) + Math.pow(Math.abs(clickY - mCenterY), 2));
        /*大于半径说明没有点击到menuitem*/
        if (disToCenter > mRadius) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                /*表明点中了，会消费该事件*/
                if (checkIsClickMenuItem(event.getX(), event.getY())) {
                    super.onTouchEvent(event);
                    return true;
                } else {
                    return false;
                }
        }
        return super.onTouchEvent(event);
    }


}
