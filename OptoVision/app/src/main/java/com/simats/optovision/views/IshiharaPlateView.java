package com.simats.optovision.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Custom view that draws Ishihara-style color blindness test plates
 * with a number hidden among colored circles - improved for clarity
 */
public class IshiharaPlateView extends View {

    private Paint circlePaint;
    private Random random = new Random();

    private int displayNumber = 12;
    private int plateType = 0;

    // Pre-computed circle positions
    private List<Circle> circles = new ArrayList<>();
    private boolean circlesGenerated = false;

    // Color schemes (background colors, number colors)
    private int[][] bgColors = {
            { 0xFFFFCDD2, 0xFFFFAB91, 0xFFFFE0B2, 0xFFF8BBD0, 0xFFFFCC80 }, // Pinks/Oranges
            { 0xFFB2EBF2, 0xFF80DEEA, 0xFFA5D6A7, 0xFF81C784, 0xFFB2DFDB }, // Cyans/Greens
            { 0xFFE1BEE7, 0xFFCE93D8, 0xFFF8BBD0, 0xFFF48FB1, 0xFFE1BEE7 }, // Purples/Pinks
            { 0xFFDCEDC8, 0xFFC5E1A5, 0xFFB2DFDB, 0xFF80CBC4, 0xFFA5D6A7 }, // Greens
            { 0xFFFFE0B2, 0xFFFFCC80, 0xFFFFCDD2, 0xFFEF9A9A, 0xFFFFAB91 } // Oranges/Pinks
    };

    private int[][] numColors = {
            { 0xFF2E7D32, 0xFF388E3C, 0xFF43A047 }, // Greens (on pink)
            { 0xFFC62828, 0xFFD32F2F, 0xFFE53935 }, // Reds (on cyan)
            { 0xFF1565C0, 0xFF1976D2, 0xFF1E88E5 }, // Blues (on purple)
            { 0xFFAD1457, 0xFFC2185B, 0xFFD81B60 }, // Magentas (on green)
            { 0xFF4527A0, 0xFF512DA8, 0xFF5E35B1 } // Purples (on orange)
    };

    private static class Circle {
        float x, y, radius;
        boolean isNumber;
        int colorIndex;

        Circle(float x, float y, float radius, boolean isNumber, int colorIndex) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.isNumber = isNumber;
            this.colorIndex = colorIndex;
        }
    }

    public IshiharaPlateView(Context context) {
        super(context);
        init();
    }

    public IshiharaPlateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IshiharaPlateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setNumber(int number, int type) {
        this.displayNumber = number;
        this.plateType = type % bgColors.length;
        circlesGenerated = false;
        invalidate();
    }

    private void generateCircles() {
        circles.clear();

        int width = getWidth();
        int height = getHeight();
        if (width == 0 || height == 0)
            return;

        int centerX = width / 2;
        int centerY = height / 2;
        int plateRadius = Math.min(width, height) / 2 - 10;

        // Generate grid-based circles for more uniform coverage
        float spacing = 18f;

        for (float y = centerY - plateRadius; y <= centerY + plateRadius; y += spacing) {
            for (float x = centerX - plateRadius; x <= centerX + plateRadius; x += spacing) {
                // Check if within circular plate area
                float dx = x - centerX;
                float dy = y - centerY;
                if (dx * dx + dy * dy > plateRadius * plateRadius)
                    continue;

                // Add some randomness to position
                float px = x + (random.nextFloat() - 0.5f) * 8;
                float py = y + (random.nextFloat() - 0.5f) * 8;

                // Random radius between 6 and 12
                float radius = 6 + random.nextFloat() * 6;

                // Check if this point is part of the number
                boolean isNumber = isPointInNumber(px - centerX, py - centerY, plateRadius * 0.5f);

                int colorIndex = random.nextInt(isNumber ? numColors[plateType].length : bgColors[plateType].length);

                circles.add(new Circle(px, py, radius, isNumber, colorIndex));
            }
        }

        circlesGenerated = true;
    }

    private boolean isPointInNumber(float x, float y, float scale) {
        String numStr = String.valueOf(displayNumber);
        float totalWidth = numStr.length() * scale * 0.6f;
        float startX = -totalWidth / 2;

        for (int i = 0; i < numStr.length(); i++) {
            char digit = numStr.charAt(i);
            float digitCenterX = startX + i * scale * 0.6f + scale * 0.3f;

            if (isPointInDigit(x - digitCenterX, y, scale * 0.45f, digit)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPointInDigit(float x, float y, float size, char digit) {
        float strokeWidth = size * 0.35f; // Thicker strokes for visibility

        switch (digit) {
            case '0':
                return isInZero(x, y, size, strokeWidth);
            case '1':
                return isInOne(x, y, size, strokeWidth);
            case '2':
                return isInTwo(x, y, size, strokeWidth);
            case '3':
                return isInThree(x, y, size, strokeWidth);
            case '4':
                return isInFour(x, y, size, strokeWidth);
            case '5':
                return isInFive(x, y, size, strokeWidth);
            case '6':
                return isInSix(x, y, size, strokeWidth);
            case '7':
                return isInSeven(x, y, size, strokeWidth);
            case '8':
                return isInEight(x, y, size, strokeWidth);
            case '9':
                return isInNine(x, y, size, strokeWidth);
            default:
                return false;
        }
    }

    private boolean isInZero(float x, float y, float size, float stroke) {
        // Oval shape
        float rx = size * 0.5f;
        float ry = size * 0.9f;
        float outer = (x * x) / (rx * rx) + (y * y) / (ry * ry);
        float innerRx = rx - stroke;
        float innerRy = ry - stroke;
        if (innerRx <= 0 || innerRy <= 0)
            return outer <= 1;
        float inner = (x * x) / (innerRx * innerRx) + (y * y) / (innerRy * innerRy);
        return outer <= 1 && inner >= 1;
    }

    private boolean isInOne(float x, float y, float size, float stroke) {
        // Vertical line
        return Math.abs(x) < stroke * 0.6f && Math.abs(y) < size * 0.85f;
    }

    private boolean isInTwo(float x, float y, float size, float stroke) {
        float hw = size * 0.5f;
        float hh = size * 0.9f;

        // Top arc
        if (y < -hh * 0.2f) {
            float cy = -hh * 0.5f;
            float r = hh * 0.4f;
            float dist = (float) Math.sqrt(x * x + (y - cy) * (y - cy));
            if (dist >= r - stroke && dist <= r + stroke && y < cy + r * 0.5f)
                return true;
        }
        // Diagonal
        if (y >= -hh * 0.3f && y <= hh * 0.6f) {
            float targetX = -hw + (y + hh * 0.3f) / (hh * 0.9f) * (hw * 2);
            if (Math.abs(x - targetX) < stroke)
                return true;
        }
        // Bottom line
        if (y > hh * 0.5f && Math.abs(y - hh * 0.7f) < stroke && Math.abs(x) < hw)
            return true;

        return false;
    }

    private boolean isInThree(float x, float y, float size, float stroke) {
        float hw = size * 0.4f;
        float hh = size * 0.9f;

        // Top arc
        float topR = hh * 0.4f;
        float topCy = -hh * 0.45f;
        float distTop = (float) Math.sqrt(x * x + (y - topCy) * (y - topCy));
        if (distTop >= topR - stroke && distTop <= topR + stroke && x > -hw * 0.3f && y < 0)
            return true;

        // Bottom arc
        float botR = hh * 0.45f;
        float botCy = hh * 0.4f;
        float distBot = (float) Math.sqrt(x * x + (y - botCy) * (y - botCy));
        if (distBot >= botR - stroke && distBot <= botR + stroke && x > -hw * 0.3f && y > -stroke)
            return true;

        return false;
    }

    private boolean isInFour(float x, float y, float size, float stroke) {
        float hw = size * 0.5f;
        float hh = size * 0.9f;

        // Right vertical
        if (Math.abs(x - hw * 0.4f) < stroke * 0.7f && Math.abs(y) < hh)
            return true;
        // Left diagonal
        if (y < hh * 0.1f && y > -hh) {
            float targetX = -hw * 0.3f - (y + hh) / (hh * 1.1f) * (hw * 0.3f);
            if (Math.abs(x - targetX) < stroke && x < hw * 0.3f)
                return true;
        }
        // Horizontal
        if (Math.abs(y - hh * 0.1f) < stroke * 0.7f && x >= -hw * 0.5f && x <= hw * 0.6f)
            return true;

        return false;
    }

    private boolean isInFive(float x, float y, float size, float stroke) {
        float hw = size * 0.45f;
        float hh = size * 0.9f;

        // Top horizontal
        if (Math.abs(y + hh * 0.75f) < stroke && x >= -hw * 0.6f && x <= hw * 0.6f)
            return true;
        // Left vertical
        if (Math.abs(x + hw * 0.4f) < stroke * 0.7f && y >= -hh * 0.8f && y <= -hh * 0.1f)
            return true;
        // Middle horizontal
        if (Math.abs(y + hh * 0.15f) < stroke && x >= -hw * 0.5f && x <= hw * 0.2f)
            return true;
        // Bottom arc
        float r = hh * 0.45f;
        float cy = hh * 0.35f;
        float dist = (float) Math.sqrt(x * x + (y - cy) * (y - cy));
        if (dist >= r - stroke && dist <= r + stroke && (x > -hw * 0.3f || y > cy))
            return true;

        return false;
    }

    private boolean isInSix(float x, float y, float size, float stroke) {
        float hw = size * 0.45f;
        float hh = size * 0.9f;

        // Bottom circle
        float r = hh * 0.45f;
        float cy = hh * 0.35f;
        float dist = (float) Math.sqrt(x * x + (y - cy) * (y - cy));
        float innerR = r - stroke;
        if (innerR > 0) {
            float innerDist = (float) Math.sqrt(x * x + (y - cy) * (y - cy));
            if (dist <= r && innerDist >= innerR)
                return true;
        } else {
            if (dist <= r)
                return true;
        }

        // Top curve
        if (y < cy && x < 0) {
            float topDist = (float) Math.sqrt((x + hw * 0.1f) * (x + hw * 0.1f) + (y + hh * 0.2f) * (y + hh * 0.2f));
            if (topDist >= hh * 0.5f - stroke && topDist <= hh * 0.5f + stroke && x < hw * 0.2f)
                return true;
        }

        return false;
    }

    private boolean isInSeven(float x, float y, float size, float stroke) {
        float hw = size * 0.45f;
        float hh = size * 0.9f;

        // Top horizontal
        if (Math.abs(y + hh * 0.75f) < stroke && Math.abs(x) < hw * 0.7f)
            return true;
        // Diagonal
        float slope = (hh * 1.5f) / (hw * 0.8f);
        float targetX = hw * 0.5f - (y + hh * 0.7f) / slope;
        if (Math.abs(x - targetX) < stroke && y > -hh * 0.7f)
            return true;

        return false;
    }

    private boolean isInEight(float x, float y, float size, float stroke) {
        float hh = size * 0.9f;

        // Top circle
        float topR = hh * 0.35f;
        float topCy = -hh * 0.45f;
        float distTop = (float) Math.sqrt(x * x + (y - topCy) * (y - topCy));
        if (distTop >= topR - stroke && distTop <= topR + stroke)
            return true;

        // Bottom circle (slightly larger)
        float botR = hh * 0.42f;
        float botCy = hh * 0.38f;
        float distBot = (float) Math.sqrt(x * x + (y - botCy) * (y - botCy));
        if (distBot >= botR - stroke && distBot <= botR + stroke)
            return true;

        return false;
    }

    private boolean isInNine(float x, float y, float size, float stroke) {
        float hw = size * 0.45f;
        float hh = size * 0.9f;

        // Top circle
        float r = hh * 0.45f;
        float cy = -hh * 0.35f;
        float dist = (float) Math.sqrt(x * x + (y - cy) * (y - cy));
        float innerR = r - stroke;
        if (innerR > 0) {
            float innerDist = (float) Math.sqrt(x * x + (y - cy) * (y - cy));
            if (dist <= r && innerDist >= innerR)
                return true;
        } else {
            if (dist <= r)
                return true;
        }

        // Bottom tail
        if (y > cy && x > -hw * 0.2f) {
            float tailDist = (float) Math.sqrt((x - hw * 0.1f) * (x - hw * 0.1f) + (y - hh * 0.2f) * (y - hh * 0.2f));
            if (tailDist >= hh * 0.5f - stroke && tailDist <= hh * 0.5f + stroke && x > -hw * 0.3f)
                return true;
        }

        return false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        circlesGenerated = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!circlesGenerated || circles.isEmpty()) {
            generateCircles();
        }

        int[] bgColorArray = bgColors[plateType];
        int[] numColorArray = numColors[plateType];

        for (Circle circle : circles) {
            if (circle.isNumber) {
                circlePaint.setColor(numColorArray[circle.colorIndex % numColorArray.length]);
            } else {
                circlePaint.setColor(bgColorArray[circle.colorIndex % bgColorArray.length]);
            }
            canvas.drawCircle(circle.x, circle.y, circle.radius, circlePaint);
        }
    }
}
