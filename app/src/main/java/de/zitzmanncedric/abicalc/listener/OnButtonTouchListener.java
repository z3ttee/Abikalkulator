package de.zitzmanncedric.abicalc.listener;

import android.annotation.SuppressLint;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;

/**
 * Klasse zum Behandeln von Touch-Events
 */
public class OnButtonTouchListener implements View.OnTouchListener {

    private float scaleValue;

    /**
     * Konstruktor der Klasse. Das System setzt automatisch die Standardwerte
     */
    public OnButtonTouchListener() {
        TypedValue value = new TypedValue();
        AppCore.getInstance().getResources().getValue(R.dimen.default_scaleDown, value, true);

        this.scaleValue = value.getFloat();
    }

    /**
     * Optionaler Konstruktor. Standardwerte können definiert werden
     * @param scaleValue Dezimalwert. Das UI-Element nimmt die neue Größe beim Touch-Event an.
     */
    public OnButtonTouchListener(float scaleValue) {
        this.scaleValue = scaleValue;
    }

    /**
     * Behandlung des Touch-Events. Ein UI-Element wird beim berühren verkleinert. Das dient der Darstellung eines Button-Klicks.
     * @param view UI-Element, das berührt wurde
     * @param motionEvent MotionEvent, welches ausgelöst wurde (z.B. BUTTON_PRESS oder SCROLL)
     * @return false, weil das Event nicht dafür sorgen soll, dass Events wir Klick-Events nicht mehr stattfinden sollen
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int scaleDuration = 40;
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
