package com.mutlcolorloadingview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * description:
 * author chaojiong.zhang
 * data: 2020/4/29
 * copyright TCL-MIBC
 */
public class MutlColorLoadingView extends View {

    // 画笔
    private Paint mPaint;
    //动画进度更新器
    private ValueAnimator mLoadingAnimator;
    //圆弧路径，被截取
    private Path mArcPath;

    // 测量Path 并截取部分的工具
    private PathMeasure mMeasure;
    private float mMeasureLength;

    // 默认的动效周期 2s
    private int defaultDuration = 2000;

    // 动画数值(用于控制动画状态,因为同一时间内只允许有一种状态出现,具体数值处理取决于当前状态)
    private float mAnimatorValue = 0;
    //存储宽高
    private RectF mLayer;

    // 分 3个阶段,以中间颜色为基准
    private float stagemMid = 1.0f;
    private float stageOne = 1.21f;
    private float stageTwo = 1.42f;

    private int firstColor = Color.WHITE;
    private int secondColor = Color.BLUE;
    private int threeColor = Color.YELLOW;
    private float strokeWidth = 15f;
    private int startAngle = -90;
    private float rateOfFirstRound = 0.45f; //第一回合所占时间比例


    public MutlColorLoadingView(Context context) {
        this(context, null);
    }

    public MutlColorLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MutlColorLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initAll();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MutlColorLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttr(context, attrs);
        initAll();
    }

    public void initAll() {
        mLayer = new RectF();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);

        mArcPath = new Path();
        mMeasure = new PathMeasure();


        mLoadingAnimator = ValueAnimator.ofFloat(0, 2).setDuration(defaultDuration);
        mLoadingAnimator.setRepeatCount(Animation.INFINITE);
        mLoadingAnimator.setInterpolator(new MutlColorInterpolator(rateOfFirstRound));
        mLoadingAnimator.addUpdateListener(mUpdateListener);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mLoadingAnimator.start();
    }


    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MutlColorLoadingView);
        defaultDuration = ta.getInteger(R.styleable.MutlColorLoadingView_mclv_duration, defaultDuration);
        firstColor = ta.getColor(R.styleable.MutlColorLoadingView_mclv_first_color, firstColor);
        secondColor = ta.getColor(R.styleable.MutlColorLoadingView_mclv_second_color, secondColor);
        threeColor = ta.getColor(R.styleable.MutlColorLoadingView_mclv_three_color, threeColor);
        rateOfFirstRound = ta.getFloat(R.styleable.MutlColorLoadingView_mclv_rate_first_round, rateOfFirstRound);
        strokeWidth = ta.getDimension(R.styleable.MutlColorLoadingView_mclv_stroke_width, strokeWidth);
        startAngle = ta.getInt(R.styleable.MutlColorLoadingView_mclv_start_angle, startAngle);
        ta.recycle();
    }


    ValueAnimator.AnimatorUpdateListener mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mAnimatorValue = (float) animation.getAnimatedValue();
            invalidate();
        }
    };


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mArcPath.reset();
        float len = w > h ? h : w;
        float halfOfStrokeWidth = strokeWidth / 2.0f;
        float paddingLeft = getPaddingLeft();
        float paddingRight = getPaddingRight();
        float paddingTop = getPaddingTop();
        float paddingBottom = getPaddingBottom();
        float offset;
        if (w > h) {
            offset = (w - h) / 2f;
            mLayer.set(halfOfStrokeWidth + paddingLeft + offset, halfOfStrokeWidth + paddingTop,
                    w - halfOfStrokeWidth - paddingRight - offset, h - halfOfStrokeWidth - paddingBottom);
        } else {
            offset = (h - w) / 2f;
            mLayer.set(halfOfStrokeWidth + paddingLeft, halfOfStrokeWidth + paddingTop + offset,
                    w - halfOfStrokeWidth - paddingRight, h - halfOfStrokeWidth - paddingBottom - offset);
        }

        mArcPath.addArc(mLayer, startAngle, 360f);
        mMeasure.setPath(mArcPath, true);
        mMeasureLength = mMeasure.getLength();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSearch(canvas);
    }

    private void drawSearch(Canvas canvas) {
        float progress = mAnimatorValue;
        if (progress >= stagemMid) {
            //第二回合的进度
            float curProgress = progress - stagemMid;
            if (progress < stageOne) { //1-1.3
                //画蓝色
                drawSecondSegment(canvas, curProgress);
            } else if (progress < stageTwo) {//1.3-1.6
                drawThreeSegment(canvas, curProgress);
            } else {
                //1.6-2.0
                drawFourSegment(canvas, curProgress);
            }
            //绘制第三条segment，白色 segment
            drawFirstSegment(canvas, curProgress);
        } else {
            //第一回合的进度
            mPaint.setColor(Color.WHITE);
            Path path = new Path();
            float start = 0;
            //用了抛物线和直线的公式，区别同一时间的路程差
            float stop = (float) (1 / 2f * progress * (3 - progress) * mMeasureLength);
            mMeasure.getSegment(start, stop, path, true);
            canvas.drawPath(path, mPaint);
        }
    }

    //蓝色 黄色 位置1.42-2.0
    private void drawFourSegment(Canvas canvas, float curProgress) {
        //画蓝色
        float whiteProgress = conveterToRealProgress(curProgress, 0.6f);//蓝色 start进度 在总进度 0.6 跑完
        float blueProgress = conveterToRealProgress(curProgress, 0.7f); //蓝色 end 进度 在总进度 0.7 跑完
        mPaint.setColor(secondColor);
        float start = mapRang(0.3f, blueProgress) * mMeasureLength;
        float end = whiteProgress * mMeasureLength; // end 和白色的进度走
        Path path = new Path();
        mMeasure.getSegment(start, end, path, true);
        canvas.drawPath(path, mPaint);

        //画黄色
        float yellowProgress = conveterToRealProgress(curProgress, 0.95f);//黄色 start 进度 在总进度 0.95 跑完
        mPaint.setColor(threeColor);
        float start1 = mapRang(0.42f, curProgress) * mMeasureLength;
        float end1 = mapRang(0.21f / 0.95f, yellowProgress) * mMeasureLength;
        Path path1 = new Path();
        mMeasure.getSegment(start1, end1, path1, true);
        canvas.drawPath(path1, mPaint);
    }

    //蓝色 黄色 位置1.21-1.42
    private void drawThreeSegment(Canvas canvas, float curProgress) {
        //画蓝色
        float whiteProgress = conveterToRealProgress(curProgress, 0.6f);//蓝色 start进度 在总进度 0.6 跑完
        float blueProgress = conveterToRealProgress(curProgress, 0.7f); //蓝色 end 进度 在总进度 0.7 跑完
        mPaint.setColor(secondColor);
        float start = mapRang(0.3f, blueProgress) * mMeasureLength;
        float end = whiteProgress * mMeasureLength; // end 和白色的进度走
        Path path = new Path();
        mMeasure.getSegment(start, end, path, true);
        canvas.drawPath(path, mPaint);

        //画黄色
        float yellowProgress = conveterToRealProgress(curProgress, 0.95f);//黄色 start 进度 在总进度 0.95 跑完
        mPaint.setColor(threeColor);
        float start1 = 0;
        float end1 = mapRang(0.21f / 0.95f, yellowProgress) * mMeasureLength;
        Path path1 = new Path();
        mMeasure.getSegment(start1, end1, path1, true);
        canvas.drawPath(path1, mPaint);
    }


    //蓝色位置 1-1.21
    private void drawSecondSegment(Canvas canvas, float curProgress) {
        //第二回合 蓝色生命周期只有 0.7倍
        curProgress = conveterToRealProgress(curProgress, 0.6f);

        mPaint.setColor(secondColor);
        float start = 0;
        float end = curProgress * mMeasureLength;
        Path path = new Path();
        mMeasure.getSegment(start, end, path, true);
        canvas.drawPath(path, mPaint);
    }


    //白色位置 1-2.0
    private void drawFirstSegment(Canvas canvas, float curProgress) {
        mPaint.setColor(firstColor);
        curProgress = conveterToRealProgress(curProgress, 0.6f); // 白色loading 占第二回合的 0.6 个生命周期
        float start = curProgress * mMeasureLength;
        float end = mMeasureLength;
        Path path = new Path();
        mMeasure.getSegment(start, end, path, true);
        canvas.drawPath(path, mPaint);
    }


    public float conveterToRealProgress(float progress, float rate) {
        float curProgress = progress / rate;
        if (curProgress > 1) curProgress = 1f;
        return curProgress;
    }

    //输入 startX-1 线性映射 0-1
    public float mapRang(float startX, float x) {
        float k = 1.0f / (1.0f - startX);
        return k * x + (1 - k);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mLoadingAnimator != null && mLoadingAnimator.isStarted()) {
            mLoadingAnimator.cancel();
        }
    }

    /**
     * 自定义加速器，控制第一回合和总时间的比率
     */

    public class MutlColorInterpolator implements android.view.animation.Interpolator {

        float rate;

        public MutlColorInterpolator(float rate) {
            this.rate = rate;
        }


        public float getInterpolation(float time) {

            if (time < rate) {
                return time * (0.5f / rate);
            } else {
                float k = 0.5f / (1 - rate);
                return k * time + (1 - k);
            }
        }
    }


}
