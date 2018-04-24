package com.raffynato.horizontalprogressbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class Bar extends View {

    private int maxValue = 100;
    private float currentValue = 0;
    private ValueAnimator animation = null;
    private long animationDuration = 3000l;  //Default time to complete the entire bar

    private float   mBarHeight,
                    mBarWidth,
                    mTextMargin;

    private int mContainerBarColor,
                mProgressBarColor,
                mTextColor;

    private boolean mShowText = true;

    private Paint   mProgressBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
                    mContainerBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
                    mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public Bar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Bar, 0, 0);
        try {
            mProgressBarColor = ta.getColor(R.styleable.Bar_progressBarColor, getResources().getColor(R.color.materialBlue));
            mBarHeight = ta.getDimension(R.styleable.Bar_BarHeight, 0.0F);
            mBarWidth = ta.getDimension(R.styleable.Bar_BarWidth, 0.0F);
            mContainerBarColor = ta.getColor(R.styleable.Bar_containerBarColor, getResources().getColor(R.color.boringGrey));
            mTextColor = ta.getColor(R.styleable.Bar_textColor, Color.BLACK);
            mShowText = ta.getBoolean(R.styleable.Bar_showText, mShowText);
            mTextMargin = ta.getDimension(R.styleable.Bar_textToBarMargin, 25.0F);
        } finally {
            ta.recycle();
        }

        mProgressBarPaint.setColor(mProgressBarColor);
        mContainerBarPaint.setColor(mContainerBarColor);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw (Canvas canvas) {
        float containerBarHeight = mBarHeight == 0 ? getHeight() : mBarHeight;
        float progressBarHeight = containerBarHeight - getPaddingBottom();

        Rect txtBounds = new Rect();
        String maxValueString = String.valueOf(Math.round(maxValue));
        mTextPaint.setTextSize(progressBarHeight);
        mTextPaint.getTextBounds(maxValueString, 0, maxValueString.length(), txtBounds);

        float containerBarWidth = mBarWidth == 0 ? getWidth() - (txtBounds.width() + mTextMargin) : mBarWidth;
        float progressBarWidth = containerBarWidth * (currentValue/100f);

        /* Draw container bar */
        canvas.drawRoundRect(0f, 0f, containerBarWidth, containerBarHeight, containerBarHeight/2, containerBarHeight/2, mContainerBarPaint);

        /* Draw progress bar */
        canvas.drawRoundRect(getPaddingStart(), getPaddingTop(), progressBarWidth,progressBarHeight, progressBarHeight/2, progressBarHeight/2, mProgressBarPaint);

        /* Draw percentage text */
        canvas.drawText(String.valueOf(Math.round(currentValue)), getWidth() - txtBounds.width() +  mTextMargin, (containerBarHeight / 2) + (txtBounds.height() / 2), mTextPaint);
    }

    public void setValue(int newValue) {
        float previousValue = currentValue;
        if (newValue < 0) {
            currentValue = 0;
        } else if (newValue > maxValue) {
            currentValue = maxValue;
        } else {
            currentValue = newValue;
        }

        if (animation != null) {
            animation.cancel();
        }


        animation = ValueAnimator.ofFloat(previousValue, currentValue);
        //animationDuration specifies how long it should take to animate the entire graph, so the
        //actual value to use depends on how much the value needs to change
        int changeInValue = (int) Math.abs(currentValue - previousValue);
        long durationToUse = (long) (animationDuration * ((float) changeInValue / (float) maxValue)* .9);
        animation.setDuration(durationToUse);

        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentValue  = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        animation.start();
        invalidate();
    }

}