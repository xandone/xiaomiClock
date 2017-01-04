package app.xandone.com.xiaomiclock;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.Calendar;

/**
 * Created by xandone on 2017/1/3.
 */
public class ClockView extends View {
    private Paint mPaint;
    private Path mTrianglePath;

    private int mScaleColor;
    private int mClockColor;
    private int mMinuteColor;
    private int mSecondColor;
    private int mCircleColor;
    private int mTextColor;

    private int mCirclePadding;
    private int mTrianglePadding;

    private int mDefaultSize;
    private int mTextSize;
    private int mScaleSize;
    private int mClockSize;
    private int mMinuteSize;
    private int mCircleRadiu;
    private int mInnerCircleRadiu;
    private int mTriangleHeight;

    private int mSecondStartAngle;//秒针开始的角度
    private int mMinuteStartAngle;//分针开始的角度
    private int mClockStartAngle;//时针开始的角度

    private int mWidth, mHeight;
    private int mInnerX, mInnerY;
    private int mOuterX, mOuterY;

    private Context mContext;
    private Calendar mCalendar;
    private ValueAnimator mValueAnimator;

    private String[] clockNum = {"3", "6", "9", "12"};
    private int[] scaleColors = {0xFFA1F31B, 0xFFAFF33E, 0xFFBEF563};
    private final int mTouchDistance = 50;

    public ClockView(Context context) {
        this(context, null);
    }

    public ClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    public void init() {
        mDefaultSize = Utils.dp2px(mContext, 300);
        mTextSize = Utils.dp2px(mContext, 14);
        mScaleSize = Utils.dp2px(mContext, 14);
        mClockSize = Utils.dp2px(mContext, 80);
        mMinuteSize = Utils.dp2px(mContext, 100);
        mInnerCircleRadiu = Utils.dp2px(mContext, 6);
        mTriangleHeight = Utils.dp2px(mContext, 16);

        mCirclePadding = Utils.dp2px(mContext, 16);
        mTrianglePadding = Utils.dp2px(mContext, 4);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTrianglePath = new Path();
        mCalendar = Calendar.getInstance();

        mScaleColor = 0xFFD5FA97;
        mClockColor = 0xFFDAEEF7;
        mMinuteColor = 0xFFF7FAF3;
        mSecondColor = 0xFFFFFFFF;
        mCircleColor = 0xFF70A5F9;
        mTextColor = 0xFFFF0000;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas);
        drawScale(canvas);
        drawInnerCircle(canvas);
        drawClockNum(canvas);
        drawSecondTriangle(canvas);
        drawColorScale(canvas);
        drawMinuteLine(canvas);
        drawClockLine(canvas);
    }

    /**
     * 绘制外围圆形
     *
     * @param canvas
     */
    public void drawCircle(Canvas canvas) {
        mPaint.setColor(mCircleColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        //圆中心坐标是相对于自己，w，h是相对了当前view的父布局坐标
        canvas.drawCircle(mWidth / 2, mHeight / 2, mCircleRadiu, mPaint);
    }

    /**
     * 绘制刻度
     *
     * @param canvas
     */
    public void drawScale(Canvas canvas) {
        mPaint.setColor(mScaleColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(2);
        canvas.save();
        for (int i = 0; i < 360; i++) {
            canvas.rotate(1, mOuterX, mOuterY);
            canvas.drawLine(mOuterX * 2 - mCirclePadding - mScaleSize, mOuterY, mOuterX * 2 - mCirclePadding, mOuterY, mPaint);
        }
        canvas.restore();
    }

    /**
     * 绘制渐变的刻度
     *
     * @param canvas
     */
    public void drawColorScale(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(4);
        canvas.save();
        canvas.rotate(mSecondStartAngle, mOuterX, mOuterY);

        for (int j = 0; j < scaleColors.length; j++) {
            mPaint.setColor(scaleColors[j]);
            for (int i = 0; i < 30; i++) {
                canvas.drawLine(mOuterX * 2 - mCirclePadding - mScaleSize, mOuterY, mOuterX * 2 - mCirclePadding, mOuterY, mPaint);
                canvas.rotate(-1, mOuterX, mOuterY);
            }
        }
        canvas.restore();
    }

    /**
     * 绘制内圆
     *
     * @param canvas
     */
    public void drawInnerCircle(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mInnerCircleRadiu - 4);
        canvas.drawCircle(mInnerX, mInnerY, mInnerCircleRadiu, mPaint);
    }

    /**
     * 绘制时钟数字
     *
     * @param canvas
     */
    public void drawClockNum(Canvas canvas) {
        mPaint.setColor(mTextColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setStrokeWidth(4);
        mPaint.setTextSize(mTextSize);
        canvas.drawText(clockNum[0], mWidth - 20, mHeight / 2, mPaint);
        canvas.drawText(clockNum[1], mWidth / 2, mHeight - 10, mPaint);
        canvas.drawText(clockNum[2], 20, mHeight / 2, mPaint);
        canvas.drawText(clockNum[3], mWidth / 2, 30, mPaint);
    }

    /**
     * 绘制秒针三角形
     *
     * @param canvas
     */
    public void drawSecondTriangle(Canvas canvas) {
        mPaint.setColor(mSecondColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.save();
        canvas.rotate(mSecondStartAngle, mWidth / 2, mHeight / 2);
        mTrianglePath.moveTo(mWidth - mCirclePadding - mScaleSize - mTrianglePadding, mCircleRadiu);
        mTrianglePath.lineTo(mWidth - mCirclePadding - mScaleSize - mTrianglePadding - mTriangleHeight, mCircleRadiu + mTriangleHeight / 2);
        mTrianglePath.lineTo(mWidth - mCirclePadding - mScaleSize - mTrianglePadding - mTriangleHeight, mCircleRadiu - mTriangleHeight / 2);
        mTrianglePath.close();
        canvas.drawPath(mTrianglePath, mPaint);
        canvas.restore();
    }

    /**
     * 绘制分针
     *
     * @param canvas
     */
    public void drawMinuteLine(Canvas canvas) {
        mMinuteStartAngle = (Utils.getMinute(mCalendar) * 60 + Utils.getSecond(mCalendar)) * 360 / (60 * 60) - 90;
        mPaint.setColor(mMinuteColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(10);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.save();
        canvas.rotate(mMinuteStartAngle, mInnerX, mInnerY);
        canvas.drawLine(mInnerX, mInnerY, mInnerX + mMinuteSize, mInnerY, mPaint);
        canvas.restore();
    }

    /**
     * 绘制时针
     *
     * @param canvas
     */
    public void drawClockLine(Canvas canvas) {
        mClockStartAngle = (Utils.getClock(mCalendar) * 60 + Utils.getMinute(mCalendar)) * 360 / (12 * 60) - 90;
        mPaint.setColor(mClockColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(12);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.save();
        canvas.rotate(mClockStartAngle, mInnerX, mInnerY);
        canvas.drawLine(mInnerX, mInnerY, mInnerX + mClockSize, mInnerY, mPaint);
        canvas.restore();
    }

    /**
     * 分针动画
     */
    public void triangleAnim() {
        mSecondStartAngle = (Utils.getSecond(mCalendar) * 1000 + Utils.getMillisecond(mCalendar)) * 360 / (60 * 1000) - 90;
        final ValueAnimator valueAnimator = ValueAnimator.ofInt(mSecondStartAngle, 360 + mSecondStartAngle);
        valueAnimator.setDuration(60000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSecondStartAngle = (int) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        valueAnimator.start();
    }

    /**
     * 触碰时触发的动画
     */
    public void touchAnim() {
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
        }
        mValueAnimator.setDuration(3000);
        mValueAnimator.setInterpolator(new BounceInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mInnerX = (int) mValueAnimator.getAnimatedValue();
                mInnerY = (int) mValueAnimator.getAnimatedValue();
                mOuterX = (int) mValueAnimator.getAnimatedValue();
                mOuterY = (int) mValueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        mValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mInnerX = mWidth / 2;
                mInnerY = mHeight / 2;
                mOuterX = mWidth / 2;
                mOuterY = mHeight / 2;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mValueAnimator.start();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = measureDimension(widthMeasureSpec);
        int h = measureDimension(heightMeasureSpec);
        setMeasuredDimension(w, h);
    }

    public int measureDimension(int measureSpec) {
        int size = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                size = Math.min(mDefaultSize, specSize);
                break;
            case MeasureSpec.EXACTLY:
                size = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                size = mDefaultSize;
                break;
            default:
                size = mDefaultSize;
                break;
        }
        return size;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mInnerX = mWidth / 2;
        mInnerY = mHeight / 2;
        mOuterX = mWidth / 2;
        mOuterY = mHeight / 2;
        mCircleRadiu = mWidth / 2;
        mValueAnimator = ValueAnimator.ofInt(mWidth / 2 + mTouchDistance, mWidth / 2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInnerX = mInnerX + mTouchDistance;
                mInnerY = mInnerY + mTouchDistance;
                mOuterX = mOuterX - mTouchDistance;
                mOuterY = mOuterY - mTouchDistance;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                touchAnim();
                break;
        }
        return true;
    }
}
