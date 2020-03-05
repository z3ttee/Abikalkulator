package de.zitzmanncedric.abicalc.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class AppButtonList extends LinearLayout {
    public AppButtonList(Context context) {
        super(context);
        init(context);
    }

    public AppButtonList(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AppButtonList(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

    }
}
