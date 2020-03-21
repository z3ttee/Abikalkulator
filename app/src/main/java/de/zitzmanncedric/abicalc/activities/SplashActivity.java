package de.zitzmanncedric.abicalc.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.activities.main.MainActivity;
import de.zitzmanncedric.abicalc.activities.main.SetupActivity;
import de.zitzmanncedric.abicalc.utils.AppSerializer;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.dialogs.ProgressDialog;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == AppCore.ResultCodes.RESULT_OK && requestCode == AppCore.RequestCodes.REQUEST_SETUP) {
            AppCore.getInstance().restartApp(this);
        } else if(resultCode == AppCore.ResultCodes.RESULT_CANCELLED) {
            // Close app -> setup cancelled
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle(getString(R.string.error_headline)).setMessage(getString(R.string.error_setup_cancelled)).create();
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.btn_ok), null, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finishAndRemoveTask();
                }
            });
            dialog.show();
        } else if(resultCode == AppCore.ResultCodes.RESULT_FAILED) {
            showError();
        }
    }

    private void showError() {
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(getString(R.string.error_headline)).setMessage(getString(R.string.error_occured)).create();
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.btn_ok), null, (dialog1, which) -> {
            dialog1.dismiss();
            finishAndRemoveTask();
        });
        dialog.show();
    }
}
