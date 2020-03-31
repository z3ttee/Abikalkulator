package de.zitzmanncedric.abicalc.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.activities.main.MainActivity;
import de.zitzmanncedric.abicalc.activities.main.SetupActivity;
import de.zitzmanncedric.abicalc.dialogs.InfoDialog;
import de.zitzmanncedric.abicalc.utils.AppSerializer;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.dialogs.ProgressDialog;

/**
 * Klasse zur Behandlung des Starten der App
 * @author Cedric Zitzmann
 */
public class SplashActivity extends AppCompatActivity {

    /**
     * Von Android implementiert. Methode zum Aufbauen des Fensters. Der Benutzer wird ins Hauptmenü weitergeleitet, wenn er die Ersteinrichtung bereits abgeschlossen hat. Andernfalls erfolgt die Weiterleitung zur Ersteinrichtung.
     * @param savedInstanceState Von Android übergeben (nicht genutzt)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(AppCore.Setup.isSetupPassed()) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        } else {
            Intent intent = new Intent(this, SetupActivity.class);
            startActivityForResult(intent, AppCore.RequestCodes.REQUEST_SETUP);
        }
    }

    /**
     * Von Android implementiert. Fängt das Resultat durch eine geschlossene Aktivität ab. Bei Erfolg wird der Benutzer ins Hauptmenü geleitet. Tritt ein Fehler auf, wird der Nutzer benachrichtigt und die App geschlossen, weil ohne Ersteinrichtung die App nicht nutzbar ist.
     * @param requestCode Code, zur Identifizierung der Anfrage
     * @param resultCode Code, zur Identifizierung des Resultats
     * @param data Zurückgegebene Daten
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == AppCore.ResultCodes.RESULT_OK && requestCode == AppCore.RequestCodes.REQUEST_SETUP) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        } else if(resultCode == AppCore.ResultCodes.RESULT_CANCELLED) {
            showCancelled();
        } else if(resultCode == AppCore.ResultCodes.RESULT_FAILED) {
            showError();
        }
    }

    /**
     * Private Funktion der Klasse zum Anzeigen des Fehlers.
     */
    private void showError() {
        InfoDialog dialog = new InfoDialog(this);
        dialog.setTitle(getString(R.string.error_headline));
        dialog.setMessage(R.string.error_occured);
        dialog.setCallback((button) -> {
            dialog.dismiss();
            finishAndRemoveTask();
        });
        dialog.setOnCancelListener(dialog1 -> {
            if(dialog.getCallback() != null) {
                dialog.getCallback().onButtonClicked(null);
            }
        });
        dialog.show();
    }

    /**
     * Private Funktion der Klasse zum Anzeigen der Nachricht, dass die Ersteinrichtung abgebrochen wurde.
     */
    private void showCancelled(){
        InfoDialog dialog = new InfoDialog(this);
        dialog.setTitle(getString(R.string.error_headline));
        dialog.setMessage(R.string.error_setup_cancelled);
        dialog.setBanner(R.drawable.ic_undraw_cancel, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 128, getResources().getDisplayMetrics()));
        dialog.setCallback((button) -> {
            dialog.dismiss();
            finishAndRemoveTask();
        });
        dialog.setOnCancelListener(dialog1 -> {
            if(dialog.getCallback() != null) {
                dialog.getCallback().onButtonClicked(null);
            }
        });
        dialog.show();
    }
}
