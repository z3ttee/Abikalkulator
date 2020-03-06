package de.zitzmanncedric.abicalc;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Verwaltet Funktionen für spezielle Vorgänge, die öfter wiederholt werden
 */
public class AppUtils {

    /**
     * Methode wird aufgerufen, um ein haptisches Feedback zu geben, in Form einer Vibration
     * @param milliseconds Beeinflusst die Länge einer Vibration
     * @param amplitude Beeinflusst die Intensität der Vibration
     */
    public static void sendHapticFeedback(long milliseconds, int amplitude) {
        Vibrator vibrator = (Vibrator) AppCore.getInstance().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        try {
            // Method deprecated on android oreo and later
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // vibrator.vibrate(VibrationEffect.EFFECT_CLICK);
                vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, amplitude));
            } else {
                vibrator.vibrate(milliseconds);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Methode zum erstellen der Standardanimation von Elementen in einer Liste
     * @return Objekt der erstellten Animation
     */
    public static Animation getListItemEnterAnim(){
        Animation animation = AnimationUtils.loadAnimation(AppCore.getInstance().getApplicationContext(), R.anim.list_item_falldown);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setDuration(AppCore.getInstance().getResources().getInteger(R.integer.anim_speed_quickly));
        return animation;
    }

}
