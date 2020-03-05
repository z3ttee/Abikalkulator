package de.zitzmanncedric.abicalc.listener;

import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;

public class OnButtonTouchListener implements View.OnTouchListener {

    private float scaleValue;
    private final int scaleDuration = 40;

    /**
     *
     */
    public OnButtonTouchListener() {
        TypedValue value = new TypedValue();
        AppCore.getInstance().getResources().getValue(R.dimen.default_scaleDown, value, true);

        this.scaleValue = value.getFloat();
    }

    public OnButtonTouchListener(float scaleValue) {
        this.scaleValue = scaleValue;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            view.animate().scaleX(scaleValue).scaleY(scaleValue).setDuration(scaleDuration).setInterpolator(new AccelerateDecelerateInterpolator());
        } else {
            if(motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                view.animate().scaleX(1f).scaleY(1f).setDuration(scaleDuration).setInterpolator(new AccelerateDecelerateInterpolator());
            }
        }
        return false;
    }
}
