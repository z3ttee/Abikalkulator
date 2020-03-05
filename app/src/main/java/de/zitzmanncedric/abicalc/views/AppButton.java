package de.zitzmanncedric.abicalc.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

import de.zitzmanncedric.abicalc.listener.OnButtonTouchListener;

public class AppButton extends AppCompatButton {
    public AppButton(Context context) {
        super(context);
        init();
    }

    public AppButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setClipToOutline(true);
        setOnTouchListener(new OnButtonTouchListener());
    }
}
