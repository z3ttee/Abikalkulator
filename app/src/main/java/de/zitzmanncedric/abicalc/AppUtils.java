package de.zitzmanncedric.abicalc;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

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

}
