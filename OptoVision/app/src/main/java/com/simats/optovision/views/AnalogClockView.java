package com.simats.optovision.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/**
 * Custom view that draws an analog clock face with hour and minute hands
 */
public class AnalogClockView extends View {

    private Paint circlePaint;
    private Paint tickPaint;
    private Paint numberPaint;
    private Paint hourHandPaint;
    private Paint minuteHandPaint;
    private Paint centerPaint;

    private int hour = 3;
    private int minute = 0;

    public AnalogClockView(Context context) {
        super(context);
        init();
    }

    public AnalogClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnalogClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Clock circle
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.WHITE);
        circlePaint.setStyle(Paint.Style.FILL);

        // Tick marks
        tickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tickPaint.setColor(Color.DKGRAY);
        tickPaint.setStrokeWidth(3);
        tickPaint.setStyle(Paint.Style.STROKE);

        // Numbers
        numberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        numberPaint.setColor(Color.BLACK);
        numberPaint.setTextSize(36);
        numberPaint.setTextAlign(Paint.Align.CENTER);
        numberPaint.setTypeface(Typeface.DEFAULT_BOLD);

        // Hour hand
        hourHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hourHandPaint.setColor(Color.BLACK);
        hourHandPaint.setStrokeWidth(10);
        hourHandPaint.setStrokeCap(Paint.Cap.ROUND);

        // Minute hand
        minuteHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        minuteHandPaint.setColor(0xFF4CAF50); // Green
        minuteHandPaint.setStrokeWidth(6);
        minuteHandPaint.setStrokeCap(Paint.Cap.ROUND);

        // Center circle
        centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPaint.setColor(Color.BLACK);
        centerPaint.setStyle(Paint.Style.FILL);
    }

    public void setTime(int hour, int minute) {
        this.hour = hour % 12;
        this.minute = minute;
        invalidate();
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(width, height) / 2 - 20;

        // Draw outer circle (border)
        Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(Color.LTGRAY);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(4);
        canvas.drawCircle(centerX, centerY, radius, borderPaint);

        // Draw clock face
        canvas.drawCircle(centerX, centerY, radius - 2, circlePaint);

        // Draw hour markers and numbers
        for (int i = 1; i <= 12; i++) {
            double angle = Math.toRadians(i * 30 - 90);

            // Draw tick marks
            float innerRadius = radius * 0.85f;
            float outerRadius = radius * 0.95f;
            float startX = centerX + (float) (Math.cos(angle) * innerRadius);
            float startY = centerY + (float) (Math.sin(angle) * innerRadius);
            float endX = centerX + (float) (Math.cos(angle) * outerRadius);
            float endY = centerY + (float) (Math.sin(angle) * outerRadius);

            tickPaint.setStrokeWidth(i % 3 == 0 ? 4 : 2);
            canvas.drawLine(startX, startY, endX, endY, tickPaint);

            // Draw numbers
            float numberRadius = radius * 0.72f;
            float numX = centerX + (float) (Math.cos(angle) * numberRadius);
            float numY = centerY + (float) (Math.sin(angle) * numberRadius) + 12;
            canvas.drawText(String.valueOf(i), numX, numY, numberPaint);
        }

        // Draw minute hand
        double minuteAngle = Math.toRadians(minute * 6 - 90);
        float minuteHandLength = radius * 0.7f;
        float minuteEndX = centerX + (float) (Math.cos(minuteAngle) * minuteHandLength);
        float minuteEndY = centerY + (float) (Math.sin(minuteAngle) * minuteHandLength);
        canvas.drawLine(centerX, centerY, minuteEndX, minuteEndY, minuteHandPaint);

        // Draw hour hand (accounts for minutes as well)
        double hourAngle = Math.toRadians((hour * 30) + (minute * 0.5) - 90);
        float hourHandLength = radius * 0.5f;
        float hourEndX = centerX + (float) (Math.cos(hourAngle) * hourHandLength);
        float hourEndY = centerY + (float) (Math.sin(hourAngle) * hourHandLength);
        canvas.drawLine(centerX, centerY, hourEndX, hourEndY, hourHandPaint);

        // Draw center circle
        canvas.drawCircle(centerX, centerY, 10, centerPaint);
    }
}
