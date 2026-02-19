package com.simats.optovision.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Custom view that displays a red center dot and a blue peripheral dot
 * for the visual field test. The blue dot position changes for each trial.
 * User can tap the blue dot to indicate they saw it.
 */
public class VisualFieldView extends View {

    private Paint redDotPaint;
    private Paint blueDotPaint;
    private Paint tappedDotPaint;

    private float redDotRadius = 12f;
    private float blueDotRadius = 22f;

    // Position of blue dot (0-3: top-left, top-right, bottom-left, bottom-right)
    private int blueDotPosition = 0;
    private boolean showBlueDot = true;
    private boolean blueDotTapped = false;

    // Current blue dot coordinates
    private float blueX, blueY;

    // Listener for blue dot tap events
    private OnBlueDotTappedListener listener;

    public interface OnBlueDotTappedListener {
        void onBlueDotTapped();
    }

    public VisualFieldView(Context context) {
        super(context);
        init();
    }

    public VisualFieldView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VisualFieldView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        redDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redDotPaint.setColor(Color.parseColor("#EF5350")); // Red color
        redDotPaint.setStyle(Paint.Style.FILL);

        blueDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        blueDotPaint.setColor(Color.parseColor("#64B5F6")); // Light blue color
        blueDotPaint.setStyle(Paint.Style.FILL);

        tappedDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tappedDotPaint.setColor(Color.parseColor("#4CAF50")); // Green color when tapped
        tappedDotPaint.setStyle(Paint.Style.FILL);
    }

    public void setOnBlueDotTappedListener(OnBlueDotTappedListener listener) {
        this.listener = listener;
    }

    /**
     * Set the position of the blue peripheral dot
     * 
     * @param position 0=top-left, 1=top-right, 2=bottom-left, 3=bottom-right
     */
    public void setBlueDotPosition(int position) {
        this.blueDotPosition = position % 4;
        this.blueDotTapped = false; // Reset tapped state
        invalidate();
    }

    /**
     * Show or hide the blue dot
     */
    public void setShowBlueDot(boolean show) {
        this.showBlueDot = show;
        invalidate();
    }

    /**
     * Check if the blue dot has been tapped
     */
    public boolean isBlueDotTapped() {
        return blueDotTapped;
    }

    /**
     * Reset the tapped state
     */
    public void resetTappedState() {
        blueDotTapped = false;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && showBlueDot && !blueDotTapped) {
            float touchX = event.getX();
            float touchY = event.getY();

            // Check if touch is within the blue dot area (with some tolerance)
            float distance = (float) Math.sqrt(Math.pow(touchX - blueX, 2) + Math.pow(touchY - blueY, 2));
            float tapTolerance = blueDotRadius * 2.5f; // Allow some tolerance for easier tapping

            if (distance <= tapTolerance) {
                blueDotTapped = true;
                invalidate();

                if (listener != null) {
                    listener.onBlueDotTapped();
                }
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        if (width == 0 || height == 0)
            return;

        // Draw red center dot
        float centerX = width / 2f;
        float centerY = height / 2f;
        canvas.drawCircle(centerX, centerY, redDotRadius, redDotPaint);

        // Calculate blue dot position
        if (showBlueDot) {
            float offsetX = width * 0.3f; // 30% from edge
            float offsetY = height * 0.25f; // 25% from edge

            switch (blueDotPosition) {
                case 0: // Top-left
                    blueX = offsetX;
                    blueY = offsetY;
                    break;
                case 1: // Top-right
                    blueX = width - offsetX;
                    blueY = offsetY;
                    break;
                case 2: // Bottom-left
                    blueX = offsetX;
                    blueY = height - offsetY;
                    break;
                case 3: // Bottom-right
                default:
                    blueX = width - offsetX;
                    blueY = height - offsetY;
                    break;
            }

            // Draw blue dot with appropriate color (blue or green if tapped)
            Paint dotPaint = blueDotTapped ? tappedDotPaint : blueDotPaint;
            canvas.drawCircle(blueX, blueY, blueDotRadius, dotPaint);
        }
    }
}
