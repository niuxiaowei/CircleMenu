package com.niu.ui.circlemenus.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.niu.ui.circlemenus.R;
import com.niu.ui.circlemenus.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by niuxiaowei on 16/6/22.
 */
public class CircleMenu extends ViewGroup implements View.OnClickListener{


    private int mCircleMenusRadius = Utils.dpToPx(80);


    private static final int DEGREE_360 = 360;

    /**
     * 超过该值就认为是要移动圆形菜单了
     */
    private static final int MOVE_MIN_DISTANCE = 10;

    /**
     * 上一次touch事件的x，y坐标值
     */
    private float mLastTouchX, mLastTouchY;
    private boolean mCanRotate;

    private int mCenterX, mCenterY;

    private CircleMenuItem mCircleMenuItem;


    /**
     * 菜单是否打开
     */
    private boolean mIsCircleMenusOpen = true;

    private static final float sCenterCircleMenuItemScaleRadio = 0.4f;



    public CircleMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleMenu);
        if (a != null) {
            mCircleMenusRadius = (int) a.getDimension(R.styleable.CircleMenu_circleMenusRadius, mCircleMenusRadius);
            ARCMenuItem.sOutterRadius = mCircleMenusRadius;
            a.recycle();
        }



    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int centerX = getMeasuredWidth() / 2;
        int centerY = getMeasuredHeight() / 2;





        int startDegree = 0;
        int visibleChildCount = getVisibleARCChildCount();
        if(visibleChildCount == 0){
            return;
        }
        int stepDegree = DEGREE_360 / visibleChildCount;
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() != View.GONE && childView instanceof ARCMenuItem) {
                childView.layout(centerX - childView.getMeasuredWidth() / 2, centerY - childView.getMeasuredHeight(), centerX + childView.getMeasuredWidth() / 2, centerY);
                childView.setRotation(startDegree);
                startDegree += stepDegree;
            } else if (isCenterCircleMenuItem(childView)) {
                childView.layout(centerX - childView.getMeasuredWidth() / 2, centerY - childView.getMeasuredHeight() / 2, centerX + childView.getMeasuredWidth() / 2, centerY + childView.getMeasuredHeight() / 2);

            }

        }

        int visibleARCChildCount = getVisibleARCChildCount();
        if(visibleARCChildCount != 0){

            int itemARCDegree = DEGREE_360 / visibleARCChildCount;
            for (int i = 0; i < getChildCount(); i++) {
                View childView = getChildAt(i);
                if (childView.getVisibility() != View.GONE && childView instanceof ARCMenuItem) {
                    ((ARCMenuItem) childView).setARCSweepDegree(itemARCDegree);
                }
            }
        }


    }

    /**
     * 获取可显示的{@link ARCMenuItem}的个数
     *
     * @return
     */
    private int getVisibleARCChildCount() {
        int result = 0;
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof ARCMenuItem && getChildAt(i).getVisibility() != View.GONE) {
                result++;
            }
        }
        return result;
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

        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() != View.GONE) {
                if (childView instanceof ARCMenuItem) {
                    LayoutParams lp = new LayoutParams(2 * mCircleMenusRadius, mCircleMenusRadius);
                    childView.setLayoutParams(lp);
                } else if (isCenterCircleMenuItem(childView)) {
                    CircleMenuItem circleMenuItem = (CircleMenuItem) childView;
                    LayoutParams lp = new LayoutParams(circleMenuItem.getRadius() * 2, circleMenuItem.getRadius() * 2);
                    childView.setLayoutParams(lp);
                }

            }

        }

        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    private boolean isCenterCircleMenuItem(View childView){
        return childView instanceof CircleMenuItem;
    }

    private int getMinHeight() {
        return getPaddingTop() + getPaddingBottom() + mCircleMenusRadius * 2 + 50;
    }

    private int getMinWidth() {
        return getPaddingLeft() + getPaddingRight() + mCircleMenusRadius * 2 + 50;
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h / 2;
        this.setPivotY(mCenterY);
        this.setPivotX(mCenterX);
        mCircleMenuItem = findCenterCircleMenuItem();
        if(mCircleMenuItem != null){

            mCircleMenuItem.setOnClickListener(this);
        }
    }


    private CircleMenuItem findCenterCircleMenuItem(){
        for (int i = 0; i < getChildCount(); i++) {
            if(isCenterCircleMenuItem(getChildAt(i))){
                return ((CircleMenuItem)getChildAt(i));
            }
        }
        return null;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
//        Log.i("test", "dispatchToucEvent");
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchX = event.getX();
                mLastTouchY = event.getY();
                reset();
            case MotionEvent.ACTION_MOVE:


                if (caculate2PointsDistance(mLastTouchX,mLastTouchY,event.getX(),event.getY()) > MOVE_MIN_DISTANCE) {
                    mCanRotate = true;
                }
                rotate(event);
                mLastTouchY = event.getY();
                mLastTouchX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                reset();
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private void reset(){
        mCanRotate = false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (mCanRotate) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }




    /**
     * 计算两个点直角的长度
     *
     * @param x
     * @param y
     * @param x1
     * @param y1
     * @return
     */
    private float caculate2PointsDistance(float x, float y, float x1, float y1) {
        return (float) Math.sqrt(Math.pow(x - x1, 2) + Math.pow(y - y1, 2));
    }


    /**
     * 计算以a，b，c三点组成的三角形的c点与b点，a点所形成的角度的大小，计算公式  C = arccos((a^2+b^2-c^2)/(2ab))
     * @param aX a点x坐标
     * @param aY a点y坐标
     * @param bX
     * @param bY
     * @param cX
     * @param cY
     * @return
     */
    private float calculateCDegree4Triangle(float aX,float aY ,float bX, float bY,float cX, float cY){

        float disA2B = caculate2PointsDistance(aX,aY,bX,bY);
        float disA2C = caculate2PointsDistance(aX,aY,cX,cY);
        float disC2B = caculate2PointsDistance(cX,cY,bX,bY);

        return (float) Math.toDegrees(Math.acos((Math.pow(disA2C, 2) + Math.pow(disC2B, 2) - Math.pow(disA2B, 2)) / (2 * disA2C * disC2B)));

    }

    /**
     * 旋转
     * @param event
     */
    private void rotate(MotionEvent event) {
        /*不能旋转*/
        if (!mCanRotate) {
            return;
        }


        float touchX = event.getX(), touchY = event.getY();
        /*中轴线是一条贯穿圆中心点，垂直水平面的直线*/
        /*当前点与圆中心点，圆顶点组成一个三角形，计算中心点与剩余2点组成的角的角度*/
        float touchDegree = calculateCDegree4Triangle(touchX,touchY,mCenterX,0,mCenterX,mCenterY);
        /*若当前点位于中轴线的左侧，计算出的角度值是degree ＝ 360 - degree*/
        touchDegree = touchX < mCenterX?DEGREE_360- touchDegree:touchDegree;

        float lastDegree = calculateCDegree4Triangle(mLastTouchX,mLastTouchY,mCenterX,0,mCenterX,mCenterY);
        lastDegree = mLastTouchX < mCenterX ? DEGREE_360 - lastDegree: lastDegree;

        /*若2个点刚好在中轴线上半部分2侧，则需要特殊处理*/
        if((touchX - mCenterX)*(mLastTouchX - mCenterX) < 0 && (touchY <mCenterY) && (mLastTouchY < mCenterY)){
            /*把相对小的角度＋360*/
            if(touchDegree < lastDegree){
                touchDegree += DEGREE_360;
            }else {
                lastDegree += DEGREE_360;
            }
        }
        float rotateDegree = touchDegree - lastDegree;


//        /*非数字返回*/
        if(Float.isNaN(rotateDegree)){
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE || isCenterCircleMenuItem(child)) {
                continue;
            }
            child.setRotation(child.getRotation() + rotateDegree);


        }
    }

    public void dismiss(){
        /*让所有的弧形菜单消失*/
        int visibleChildCount = getVisibleARCChildCount();
        AnimatorSet animatorSet1 = new AnimatorSet();
        AnimatorSet animatorSet = new AnimatorSet();
        List<Animator> anis = new ArrayList<>(visibleChildCount);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if ( isCenterCircleMenuItem(child)) {
                continue;
            }
            ObjectAnimator ani = ObjectAnimator.ofFloat(child, "alpha", 1, 0);
            ani.setDuration(1000);
            anis.add(ani);


        }
        animatorSet.playTogether(anis);

        /*让处于中心的圆形菜单缩小*/
        PropertyValuesHolder propertyValuesHolder = PropertyValuesHolder.ofFloat("scaleX",1,sCenterCircleMenuItemScaleRadio);
        PropertyValuesHolder propertyValuesHolder1 = PropertyValuesHolder.ofFloat("scaleY",1,sCenterCircleMenuItemScaleRadio);
        PropertyValuesHolder rotate = PropertyValuesHolder.ofFloat("rotation",0,3*DEGREE_360);
        PropertyValuesHolder transX = PropertyValuesHolder.ofFloat("translationX",0,getWidth()- mCenterX- mCircleMenuItem.getWidth()*sCenterCircleMenuItemScaleRadio);

        ObjectAnimator scaleAni = ObjectAnimator.ofPropertyValuesHolder(mCircleMenuItem,propertyValuesHolder,propertyValuesHolder1,rotate,transX);
        scaleAni.setDuration(500);
        scaleAni.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mCircleMenuItem.notifyCircleMenusState(false);
            }
        });
        animatorSet1.play(animatorSet).before(scaleAni);
        animatorSet1.start();

        /*让弧形菜单消失*/
        animatorSet1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    if(!isCenterCircleMenuItem(child)){
                        child.setVisibility(View.GONE);
                    }
                }
            }
        });


    }

    private void open(){

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if(!isCenterCircleMenuItem(child)){
                child.setVisibility(View.VISIBLE);
            }
        }

        /*让所有的弧形菜单打开*/
        int visibleChildCount = getVisibleARCChildCount();
        AnimatorSet animatorSet1 = new AnimatorSet();
        AnimatorSet animatorSet = new AnimatorSet();
        List<Animator> anis = new ArrayList<>(visibleChildCount);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if ( isCenterCircleMenuItem(child)) {
                continue;
            }
            ObjectAnimator ani = ObjectAnimator.ofFloat(child, "alpha", 0, 1);
            ani.setDuration(1000);
            anis.add(ani);


        }
        animatorSet.playTogether(anis);

        /*让处于中心的圆形菜单缩小*/
        PropertyValuesHolder propertyValuesHolder = PropertyValuesHolder.ofFloat("scaleX",sCenterCircleMenuItemScaleRadio,1);
        PropertyValuesHolder propertyValuesHolder1 = PropertyValuesHolder.ofFloat("scaleY",sCenterCircleMenuItemScaleRadio,1);
        PropertyValuesHolder rotate = PropertyValuesHolder.ofFloat("rotation",3*DEGREE_360,0);
        PropertyValuesHolder transX = PropertyValuesHolder.ofFloat("translationX",getWidth()-mCenterX- mCircleMenuItem.getWidth()*sCenterCircleMenuItemScaleRadio,0);

        ObjectAnimator scaleAni = ObjectAnimator.ofPropertyValuesHolder(mCircleMenuItem,propertyValuesHolder,propertyValuesHolder1,rotate,transX);
        scaleAni.setDuration(500);
        scaleAni.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mCircleMenuItem.notifyCircleMenusState(true);
            }
        });
        animatorSet1.play(animatorSet).after(scaleAni);
        animatorSet1.start();


    }

    @Override
    public void onClick(View v) {
        if(mIsCircleMenusOpen){

            dismiss();
        }else{
            open();
        }
        mIsCircleMenusOpen = !mIsCircleMenusOpen;
    }


}
