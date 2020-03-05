package de.zitzmanncedric.abicalc;

import android.app.Application;
import android.content.SharedPreferences;

import de.zitzmanncedric.abicalc.database.AppDatabase;
import lombok.Getter;

/**
 * Hauptklasse der App
 * @author Cedric Zitzmann
 */

public class AppCore extends Application {

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
        public static int REQUEST_ADD_SUBJECT = 1;
        public static int REQUEST_ADD_GRADE = 2;
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