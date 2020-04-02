package de.zitzmanncedric.abicalc.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.AnimRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.Grade;
import de.zitzmanncedric.abicalc.api.Seminar;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.database.AppDatabase;

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

    /**
     * Startet einen Fragment-Übergang
     * @param manager Benötigt, um ein Fragment durch ein anderes zu ersetzen
     * @param container Benötigt, legt den Container fest, wo das neue Fragment eingesetzt werden soll
     * @param newFragment Benötigt, übergibt das neue Fragment, welches eingefügt werden soll
     * @param popBackstack Legt fest, ob Fragments im Cache gelöscht werden sollen
     * @param name Legt den Name eines Fragments im Cache fest, um es zu identifzieren
     * @param animations Legt Eingangs und Ausgangsanimationen fest
     */
    public static void replaceFragment(FragmentManager manager, View container, Fragment newFragment, boolean popBackstack, @Nullable String name, @AnimRes int... animations) {
        if(popBackstack) manager.popBackStack();
        FragmentTransaction transaction = manager.beginTransaction();

        if(animations.length == 2) {
            transaction.setCustomAnimations(animations[0], animations[1]);
        } else if(animations.length == 4) {
            transaction.setCustomAnimations(animations[0], animations[1],animations[2],animations[3]);
        }

        if(name != null) {
            transaction.addToBackStack(name);
        }

        transaction.replace(container.getId(), newFragment).commit();
    }

    /**
     * Funktion zum Zurücksetzen der gesamten App-Daten
     */
    public static void resetAppSettings(){
        AppCore.getSharedPreferences().edit().putInt("defaultAVG", 10).putFloat("goalAVG", 2.3f).putInt("goalPoints", 600).putInt("currentTerm", 0).apply();

        AppCore.Setup.setSetupPassed(false);
        Seminar.getInstance().setMinded(false);
        Seminar.getInstance().setReplacedSubjectID(-1);

        AppCore.getInstance().getApplicationContext().deleteDatabase(AppDatabase.getInstance().getDatabaseName());
        AppDatabase.createInstance(AppCore.getInstance().getApplicationContext(), AppCore.DATABASE_VERSION);
    }

    /**
     * Funktion zum Öffnen der Tastatur
     * @param context Context zum Zugriff auf App-Resourcen
     * @param view Das UI-ELement zum Zugriff auf den Fenster-Token
     * @param focused UI-Element welches fokusiert werden soll, um eine Eingabe vorzunehmen
     */
    public static void showKeyboard(Context context, View view, @Nullable View focused){
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null) {
            inputMethodManager.toggleSoftInputFromWindow(view.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            if(focused != null) focused.requestFocus();
        }
    }

    /**
     * Funktion zum Schließen der Tastatur
     * @param context Context zum Zugriff auf App-Resourcen
     * @param view Das UI-ELement zum Zugriff auf den Fenster-Token
     */
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
