package com.simats.optovision.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Custom view that draws striped patterns for contrast sensitivity test
 * Can display vertical or horizontal stripes with varying contrast levels
 */
public class StripedPatternView extends View {

    private Paint stripePaint;
    private Paint backgroundPaint;

    private boolean isVertical = true;
    private float contrastLevel = 1.0f; // 0.0 to 1.0, where 1.0 is highest contrast
    private int stripeCount = 15;

    public StripedPatternView(Context context) {
        super(context);
        init();
    }

    public StripedPatternView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StripedPatternView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        stripePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        stripePaint.setStyle(Paint.Style.FILL);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.LTGRAY);
        backgroundPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * Set the pattern orientation
     * 
     * @param isVertical true for vertical stripes, false for horizontal
     */
    public void setVertical(boolean isVertical) {
        this.isVertical = isVertical;
        invalidate();
    }

    public boolean isVertical() {
        return isVertical;
    }

    /**
     * Set the contrast level of the stripes
     * 
     * @param level 0.0 (no contrast, invisible) to 1.0 (full contrast, black on
     *              white)
     */
    public void setContrastLevel(float level) {
        this.contrastLevel = Math.max(0.0f, Math.min(1.0f, level));
        invalidate();
    }

    /**
     * Set the number of stripes
     */
    public void setStripeCount(int count) {
        this.stripeCount = count;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        if (width == 0 || height == 0)
            return;

        // Calculate stripe color based on contrast level
        // At contrast 1.0: stripes are black (0) and background is light gray (200)
        // At contrast 0.0: both are the same gray
        int baseGray = 180;
        int stripeGray = (int) (baseGray - (baseGray * contrastLevel * 0.8f));

        stripePaint.setColor(Color.rgb(stripeGray, stripeGray, stripeGray));
        backgroundPaint.setColor(Color.rgb(baseGray, baseGray, baseGray));

        // Draw background
        RectF rect = new RectF(0, 0, width, height);
        canvas.drawRoundRect(rect, 16, 16, backgroundPaint);

        // Draw stripes
        if (isVertical) {
            float stripeWidth = (float) width / (stripeCount * 2);
            for (int i = 0; i < stripeCount; i++) {
                float left = i * stripeWidth * 2;
                float right = left + stripeWidth;
                canvas.drawRect(left, 0, right, height, stripePaint);
            }
        } else {
            float stripeHeight = (float) height / (stripeCount * 2);
            for (int i = 0; i < stripeCount; i++) {
                float top = i * stripeHeight * 2;
                float bottom = top + stripeHeight;
                canvas.drawRect(0, top, width, bottom, stripePaint);
            }
        }
    }
}
