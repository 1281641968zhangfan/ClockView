package com.don.clockviewlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;

import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * <p>
 * Description����̬ʱ��
 * </p>
 *
 * @author tangzhijie
 */
public class ClockView extends View {

    //ʹ��wrap_contentʱĬ�ϵĳߴ�
    private final static int DEFAULT_SIZE = 400;

    //�̶��߿��
    private final static int MARK_WIDTH = 8;

    //�̶��߳���
    private final static int MARK_LENGTH = 20;

    //�̶�����Բ�ľ���
    private final static int MARK_GAP = 12;

    //ʱ����
    private final static int HOUR_LINE_WIDTH = 10;

    //ʱ����
    private final static int MINUTE_LINE_WIDTH = 6;

    //ʱ����
    private final static int SECOND_LINE_WIDTH = 4;

    //Բ������
    private int centerX;
    private int centerY;

    //Բ�뾶
    private int radius;

    //Բ�Ļ���
    private Paint circlePaint;

    //�̶��߻���
    private Paint markPaint;

    //ʱ�뻭��
    private Paint hourPaint;

    //���뻭��
    private Paint minutePaint;

    //���뻭��
    private Paint secondPaint;

    //ʱ�볤��
    private int hourLineLength;

    //���볤��
    private int minuteLineLength;

    //���볤��
    private int secondLineLength;

    private Bitmap hourBitmap;
    private Bitmap minuteBitmap;
    private Bitmap secondBitmap;

    private Canvas hourCanvas;
    private Canvas minuteCanvas;
    private Canvas secondCanvas;

    //Բ����ɫ
    private int mCircleColor = Color.WHITE;
    //ʱ�����ɫ
    private int mHourColor = Color.BLACK;
    //�������ɫ
    private int mMinuteColor = Color.BLACK;
    //�������ɫ
    private int mSecondColor = Color.RED;
    //һ���ӿ̶��ߵ���ɫ
    private int mQuarterMarkColor = Color.parseColor("#B5B5B5");
    //���ӿ̶��ߵ���ɫ
    private int mMinuteMarkColor = Color.parseColor("#EBEBEB");
    //�Ƿ����3��ָ���Բ��
    private boolean isDrawCenterCircle = false;

    //��ȡʱ�����
    private OnCurrentTimeListener onCurrentTimeListener;

    public void setOnCurrentTimeListener(OnCurrentTimeListener onCurrentTimeListener) {
        this.onCurrentTimeListener = onCurrentTimeListener;
    }

    public ClockView(Context context) {
        super(context);
        init();
    }

    public ClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ClockView);
        mCircleColor = a.getColor(R.styleable.ClockView_circle_color, Color.WHITE);
        mHourColor = a.getColor(R.styleable.ClockView_hour_color, Color.BLACK);
        mMinuteColor = a.getColor(R.styleable.ClockView_minute_color, Color.BLACK);
        mSecondColor = a.getColor(R.styleable.ClockView_second_color, Color.RED);
        mQuarterMarkColor = a.getColor(R.styleable.ClockView_quarter_mark_color, Color.parseColor("#B5B5B5"));
        mMinuteMarkColor = a.getColor(R.styleable.ClockView_minute_mark_color, Color.parseColor("#EBEBEB"));
        isDrawCenterCircle = a.getBoolean(R.styleable.ClockView_draw_center_circle, false);
        a.recycle();
        init();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        reMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        centerX = width / 2 + getPaddingLeft();
        centerY = height / 2 + getPaddingTop();
        radius = Math.min(width, height) / 2;

        hourLineLength = radius / 2;
        minuteLineLength = radius * 3 / 4;
        secondLineLength = radius * 3 / 4;

        //ʱ��
        hourBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        hourCanvas = new Canvas(hourBitmap);

        //����
        minuteBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        minuteCanvas = new Canvas(minuteBitmap);

        //����
        secondBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        secondCanvas = new Canvas(secondBitmap);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //����Բ
        canvas.drawCircle(centerX, centerY, radius, circlePaint);
        //���ƿ̶���
        for (int i = 0; i < 12; i++) {
            if (i % 3 == 0) {//һ����
                markPaint.setColor(mQuarterMarkColor);
            } else {
                markPaint.setColor(mMinuteMarkColor);
            }
            canvas.drawLine(
                    centerX,
                    centerY - radius + MARK_GAP,
                    centerX,
                    centerY - radius + MARK_GAP + MARK_LENGTH,
                    markPaint);
            canvas.rotate(30, centerX, centerY);
        }
        canvas.save();

        Calendar calendar = Calendar.getInstance();
        int hour24 = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        //(����һ)ÿ��һСʱ(3600��)ʱ�����30�ȣ�����ÿ��ʱ����ӣ�1/120����
        //(������)ÿ��һСʱ(60����)ʱ�����30�ȣ�����ÿ����ʱ����ӣ�1/2����
        hourCanvas.save();
        //��ջ���
        hourCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        hourCanvas.rotate(hour24 * 30 + (minute * 0.5f), getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        hourCanvas.drawLine(getMeasuredWidth() / 2, getMeasuredHeight() / 2,
                getMeasuredWidth() / 2, getMeasuredHeight() / 2 - hourLineLength, hourPaint);
        if (isDrawCenterCircle)//����ָ�����ɫ����Բ��
            hourCanvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, 2 * HOUR_LINE_WIDTH, hourPaint);
        hourCanvas.restore();

        //ÿ��һ���ӣ�60�룩�������6�ȣ�����ÿ�������ӣ�1/10���ȣ���minute��1ʱ������second��0
        minuteCanvas.save();
        //��ջ���
        minuteCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        minuteCanvas.rotate(minute * 6 + (second * 0.1f), getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        minuteCanvas.drawLine(getMeasuredWidth() / 2, getMeasuredHeight() / 2,
                getMeasuredWidth() / 2, getMeasuredHeight() / 2 - minuteLineLength, minutePaint);
        if (isDrawCenterCircle)//����ָ�����ɫ����Բ��
            minuteCanvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, 2 * MINUTE_LINE_WIDTH, minutePaint);
        minuteCanvas.restore();

        //ÿ��һ����ת6��
        secondCanvas.save();
        //��ջ���
        secondCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        secondCanvas.rotate(second * 6, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        secondCanvas.drawLine(getMeasuredWidth() / 2, getMeasuredHeight() / 2,
                getMeasuredWidth() / 2, getMeasuredHeight() / 2 - secondLineLength, secondPaint);
        if (isDrawCenterCircle)//����ָ�����ɫ����Բ��
            secondCanvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, 2 * SECOND_LINE_WIDTH, secondPaint);
        secondCanvas.restore();

        canvas.drawBitmap(hourBitmap, 0, 0, null);
        canvas.drawBitmap(minuteBitmap, 0, 0, null);
        canvas.drawBitmap(secondBitmap, 0, 0, null);

        //ÿ��1s���»���
        postInvalidateDelayed(1000);

        if (onCurrentTimeListener != null) {
            //Сʱ����24Сʱ�Ʒ���
            int h = calendar.get(Calendar.HOUR_OF_DAY);
            String currentTime = intAdd0(h) + ":" + intAdd0(minute) + ":" + intAdd0(second);
            onCurrentTimeListener.currentTime(currentTime);
        }
    }

    /**
     * ��ʼ��
     */
    private void init() {
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(mCircleColor);

        markPaint = new Paint();
        circlePaint.setAntiAlias(true);
        markPaint.setStyle(Paint.Style.FILL);
        markPaint.setStrokeCap(Paint.Cap.ROUND);
        markPaint.setStrokeWidth(MARK_WIDTH);

        hourPaint = new Paint();
        hourPaint.setAntiAlias(true);
        hourPaint.setColor(mHourColor);
        hourPaint.setStyle(Paint.Style.FILL);
        hourPaint.setStrokeCap(Paint.Cap.ROUND);
        hourPaint.setStrokeWidth(HOUR_LINE_WIDTH);

        minutePaint = new Paint();
        minutePaint.setAntiAlias(true);
        minutePaint.setColor(mMinuteColor);
        minutePaint.setStyle(Paint.Style.FILL);
        minutePaint.setStrokeCap(Paint.Cap.ROUND);
        minutePaint.setStrokeWidth(MINUTE_LINE_WIDTH);

        secondPaint = new Paint();
        secondPaint.setAntiAlias(true);
        secondPaint.setColor(mSecondColor);
        secondPaint.setStyle(Paint.Style.FILL);
        secondPaint.setStrokeCap(Paint.Cap.ROUND);
        secondPaint.setStrokeWidth(SECOND_LINE_WIDTH);

    }

    /**
     * ��������view�ߴ�
     */
    private void reMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (measureWidthMode == MeasureSpec.AT_MOST
                && measureHeightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(DEFAULT_SIZE, DEFAULT_SIZE);
        } else if (measureWidthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(DEFAULT_SIZE, measureHeight);
        } else if (measureHeightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(measureWidth, DEFAULT_SIZE);
        }
    }

    public interface OnCurrentTimeListener {
        void currentTime(String time);
    }

    /**
     * intС��10�����0
     *
     * @param i
     * @return
     */
    private String intAdd0(int i) {
        DecimalFormat df = new DecimalFormat("00");
        if (i < 10) {
            return df.format(i);
        } else {
            return i + "";
        }
    }
}
