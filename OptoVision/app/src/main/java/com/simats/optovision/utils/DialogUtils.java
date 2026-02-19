package com.simats.optovision.utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.Button;
import android.widget.TextView;

/**
 * Utility to force white background, black text, blue buttons on ALL dialogs.
 * Call DialogUtils.styleWhite(dialog) right after .show()
 */
public class DialogUtils {

    private static final int BG_COLOR = Color.WHITE;
    private static final int TEXT_COLOR = Color.BLACK;
    private static final int BUTTON_COLOR = Color.parseColor("#3B82F6");

    /** Style an android.app.AlertDialog */
    public static void styleWhite(android.app.AlertDialog dialog) {
        if (dialog == null)
            return;

        // White background
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(BG_COLOR));
        }

        // Black title
        int titleId = dialog.getContext().getResources()
                .getIdentifier("alertTitle", "id", "android");
        if (titleId > 0) {
            TextView title = dialog.findViewById(titleId);
            if (title != null)
                title.setTextColor(TEXT_COLOR);
        }

        // Black message
        TextView message = dialog.findViewById(android.R.id.message);
        if (message != null)
            message.setTextColor(TEXT_COLOR);

        // Blue buttons
        Button pos = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
        if (pos != null)
            pos.setTextColor(BUTTON_COLOR);
        Button neg = dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
        if (neg != null)
            neg.setTextColor(BUTTON_COLOR);
        Button neutral = dialog.getButton(android.app.AlertDialog.BUTTON_NEUTRAL);
        if (neutral != null)
            neutral.setTextColor(BUTTON_COLOR);
    }

    /** Style an androidx.appcompat.app.AlertDialog */
    public static void styleWhite(androidx.appcompat.app.AlertDialog dialog) {
        if (dialog == null)
            return;

        // White background
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(BG_COLOR));
        }

        // Black title
        int titleId = dialog.getContext().getResources()
                .getIdentifier("alertTitle", "id", "android");
        if (titleId > 0) {
            TextView title = dialog.findViewById(titleId);
            if (title != null)
                title.setTextColor(TEXT_COLOR);
        }
        // Also try appcompat title id
        try {
            int appcompatTitleId = dialog.getContext().getResources()
                    .getIdentifier("alertTitle", "id", dialog.getContext().getPackageName());
            if (appcompatTitleId > 0) {
                TextView title = dialog.findViewById(appcompatTitleId);
                if (title != null)
                    title.setTextColor(TEXT_COLOR);
            }
        } catch (Exception ignored) {
        }

        // Black message
        TextView message = dialog.findViewById(android.R.id.message);
        if (message != null)
            message.setTextColor(TEXT_COLOR);

        // Blue buttons
        Button pos = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
        if (pos != null)
            pos.setTextColor(BUTTON_COLOR);
        Button neg = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE);
        if (neg != null)
            neg.setTextColor(BUTTON_COLOR);
        Button neutral = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL);
        if (neutral != null)
            neutral.setTextColor(BUTTON_COLOR);
    }
}
