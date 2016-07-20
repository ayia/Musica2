package com.tyolar.inc.musica.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;


public class StretchyImageView extends ImageView {
    public StretchyImageView(Context context) {
        super(context);
    }

    public StretchyImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public StretchyImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    protected void onMeasure(int i, int i2) {
        Object obj = 1;
        super.onMeasure(i, i2);
        if (getDrawable() != null) {
            int mode = MeasureSpec.getMode(i);
            int mode2 = MeasureSpec.getMode(i2);
            int intrinsicWidth = getDrawable().getIntrinsicWidth();
            int intrinsicHeight = getDrawable().getIntrinsicHeight();
            if (intrinsicWidth <= 0) {
                intrinsicWidth = 1;
            }
            if (intrinsicHeight <= 0) {
                intrinsicHeight = 1;
            }
            float f = ((float) intrinsicWidth) / ((float) intrinsicHeight);
            Object obj2 = mode != BASS.BASS_SYNC_MIXTIME ? 1 : null;
            if (mode2 == BASS.BASS_SYNC_MIXTIME) {
                obj = null;
            }
            int paddingLeft = getPaddingLeft();
            mode = getPaddingRight();
            mode2 = getPaddingTop();
            int paddingBottom = getPaddingBottom();
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();
            if (obj2 != null && obj == null) {
                setMeasuredDimension((((int) (((float) ((measuredHeight - mode2) - paddingBottom)) * f)) + paddingLeft) + mode, measuredHeight);
            } else if (obj != null && obj2 == null) {
                setMeasuredDimension(measuredWidth, (((int) (((float) ((measuredWidth - paddingLeft) - mode)) / f)) + mode2) + paddingBottom);
            }
        }
    }
}

