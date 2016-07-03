package com.niu.ui.circlemenus.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.niu.ui.circlemenus.R;
import com.niu.ui.circlemenus.util.Utils;


/**
 * 弧形菜单
 * Created by niuxiaowei on 16/6/22.
 */
public class ARCMenuItem extends View implements View.OnClickListener {


    private Paint mPaint;
    /**
     * 所画扇形的度
     */
    private int mARCSweepDegree;
    private int mCenterX, mCenterY;
    /**
     * 外圆和内圆的半径，内圆指{@link CircleMenuItem}的半径
     */
    public static int sOutterRadius, sInnerRadius;
    private String mMenuItemContent;

    private int mTextColor = Color.BLACK;
    private int mTextSize = Utils.dpToPx(20);
    private Paint mTextPaint;
    private ARCMenuItemClickListener mARCMenuItemClickListener;

    public ARCMenuItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        int color = 0;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ARCMenuItem);
        if (typedArray != null) {
            color = typedArray.getColor(R.styleable.ARCMenuItem_menuItemColor, Color.BLUE);
            mMenuItemContent = typedArray.getString(R.styleable.ARCMenuItem_menuItemContent);
            typedArray.recycle();
        }
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);

        this.setOnClickListener(this);

    }


    public void setARCSweepDegree(int ARCSweepDegree) {
        mARCSweepDegree = ARCSweepDegree;
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.setPivotY(h);
        this.setPivotX(w / 2);
        mCenterX = w / 2;
        mCenterY = h;
        sOutterRadius = h;
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
        if (disToCenter > sOutterRadius) {
            return false;
        } else {
            /*查看点击的位置是否在指定的角度内，以中心点的12点钟为menuitem的中轴线，计算点击的点到中轴线的角度*/
            float x1 = mCenterX, y1 = clickY;
            double disToZhongZhou = Math.abs(clickX - mCenterX);
            double degree = Math.toDegrees(Math.asin(disToZhongZhou / disToCenter));
            /*大于0说明点中了*/
            return mARCSweepDegree / 2 - degree >= 0;
        }
    }


    private int getMinHeight() {
        return getPaddingTop() + getPaddingBottom();
    }

    private int getMinWidth() {
        return getPaddingLeft() + getPaddingRight();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                Log.i("test", "onTouchEvent  ACTION_DOWN this=" + mMenuItemContent);
                if (checkIsClickMenuItem(event.getX(), event.getY())) {
                    super.onTouchEvent(event);
                    /*必须得调用父类的方法，主要来处理点击，长按等事件,表明会消费此事件*/
                    return true;
                } else {
                    /*不消费此事件*/
                    return false;
                }

            case MotionEvent.ACTION_UP:
                Log.i("test", "onTouchEvent  ACTION_UP this=" + mMenuItemContent);


        }
//        switch (event.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                return true;
//        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF rectF = new RectF(0, 0, 2 * getHeight(), 2 * getHeight());
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);

        /*3点钟为0度，顺时针旋转画扇形状,所画的扇形是以12点钟为中轴线，12点钟的度数是270*/
        canvas.drawArc(rectF, 270 - mARCSweepDegree / 2, mARCSweepDegree, true, mPaint);

        /*画文字*/
        if (mMenuItemContent != null) {
            /*能画文本的高度,画字体高度是以baseline为基础画起*/
            int textHeightOffset = mCenterY - sInnerRadius-(sOutterRadius- sInnerRadius)/2+ com.niu.ui.circlemenus.util.TextUtils.measureTextHeight(mTextPaint)/2;
            int textWidthOffset = mCenterX - com.niu.ui.circlemenus.util.TextUtils.measureTextWidth(mTextPaint,mMenuItemContent)/2;

            canvas.drawText(mMenuItemContent, textWidthOffset, textHeightOffset, mTextPaint);
        }

    }


    /**
     * 弧形菜单点击事件
     */
    public static interface ARCMenuItemClickListener{
        void onClick(View v,String menuContent);
    }

    public void setARCMenuItemClickListener(ARCMenuItemClickListener ARCMenuItemClickListener) {
        mARCMenuItemClickListener = ARCMenuItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if(mARCMenuItemClickListener != null){
            mARCMenuItemClickListener.onClick(v,mMenuItemContent);
        }
    }
}
