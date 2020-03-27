package de.zitzmanncedric.abicalc;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import de.zitzmanncedric.abicalc.activities.SplashActivity;
import de.zitzmanncedric.abicalc.broadcast.GradeBroadcaster;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import lombok.Getter;

/**
 * Hauptklasse der App
 * @author Cedric Zitzmann
 */

public class AppCore extends Application {
    private static final String TAG = "AppCore";

    @Getter private static AppCore instance;
    @Getter private static SharedPreferences sharedPreferences;

    /**
     * Bildet instanz der App
     */
    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        AppDatabase.createInstance(this, 1);
        GradeBroadcaster.getInstance();
    }

    /**
     *
     */
    public void restartApp(Activity callerActivity) {
        try {
            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
            int mPendingIntentId = 0;
            PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
            System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(getApplicationContext(), "Restart failed. App must be restarted manually.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Klasse beinhaltet Codes zur Anfragenverwaltung und Identifizierung
     */
    public static class Setup {
        @Getter private static boolean setupPassed = sharedPreferences.getBoolean("setupPassed", false);

        public static void setSetupPassed(boolean setupPassed) {
            Setup.setupPassed = setupPassed;
            sharedPreferences.edit().putBoolean("setupPassed", setupPassed).apply();
        }
    }

    /**
     * Klasse beinhaltet Codes zur Anfragenverwaltung und Identifizierung
     */
    public static class RequestCodes {
        public static int REQUEST_SETUP = 0;
        public static int REQUEST_ADD_GRADE = 1;
        public static int REQUEST_UPDATE_GRADE = 2;
        public static int REQUEST_VIEW_SUBJECT = 3;
        public static int REQUEST_UPDATE_SUBJECT = 4;
        public static int REQUEST_UPDATE_VIEWS = 5;
    }

    /**
     * Klasse beinhaltet Codes zur Resultatverwaltung und Identifizierung
     */
    public static class ResultCodes {
        public static int RESULT_FAILED = 0;
        public static int RESULT_OK = 1;
        public static int RESULT_CANCELLED = 2;
    }

    /**
     * Klasse beinhaltet Codes zur Verwaltung von Aktionen und deren Identifizierung
     */
    public static class ActionCodes {
        public static int ACTION_LIST_REMOVEITEM = 0;
        public static int ACTION_LIST_ADDITEM = 1;
        public static int ACTION_LIST_REPLACEITEM = 2;
    }
}
