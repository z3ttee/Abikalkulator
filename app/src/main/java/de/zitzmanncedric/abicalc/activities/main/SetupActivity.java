package de.zitzmanncedric.abicalc.activities.main;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.AppFragments;
import de.zitzmanncedric.abicalc.activities.SplashActivity;
import de.zitzmanncedric.abicalc.api.Grade;
import de.zitzmanncedric.abicalc.api.Seminar;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.dialogs.ProgressDialog;
import de.zitzmanncedric.abicalc.utils.AppSerializer;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.fragments.setup.AddIntensifiedFragment;
import de.zitzmanncedric.abicalc.fragments.setup.AddNormalFragment;
import de.zitzmanncedric.abicalc.listener.OnFragmentToActivity;
import de.zitzmanncedric.abicalc.listener.OnSubjectChosenListener;
import de.zitzmanncedric.abicalc.sheets.ChooseSubjectSheet;
import de.zitzmanncedric.abicalc.views.AppButton;
import needle.Needle;
import needle.UiRelatedTask;

/**
 * Verwaltung der Ersteinrichtung der App
 * @author Cedric Zitzmann
 */
public class SetupActivity extends AppCompatActivity implements OnSubjectChosenListener, OnFragmentToActivity {
    private static final String TAG = "SetupActivity";

    public static final int AMOUNT_INTENSIFIED = 5;   // 5
    public static final int AMOUNT_NORMALS = 6;       // 6
    public static final int AMOUNT_EXAMS_MAX = 5;     // 5

    // TODO
    private final int AMOUNT_INTENSIFIED_EXAMS_MAX = 5;
    private final int AMOUNT_NORMALS_EXAMS_MAX = 2;

    private FrameLayout appFragmentContainer;

    private AppButton addSubjectBtn;
    private AppButton continueSetupBtn;

    public ArrayList<Subject> intensified = new ArrayList<>();
    public ArrayList<Subject> normals = new ArrayList<>();

    /**
     * Baut die Aktivität auf
     * @param savedInstanceState Von Android übergeben
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setBackgroundDrawable(new ColorDrawable(getColor(R.color.splash_background)));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        appFragmentContainer = findViewById(R.id.app_fragment_container);
        addSubjectBtn = findViewById(R.id.btn_add_subject);
        continueSetupBtn = findViewById(R.id.btn_continue);

        continueSetupBtn.setEnabled(false);

        AppFragments.replaceFragment(getSupportFragmentManager(),
                appFragmentContainer,
                new AddIntensifiedFragment(continueSetupBtn, addSubjectBtn),
                true,
                null,
                0, R.anim.fragment_slideout_left, R.anim.fragment_slidein_left, R.anim.fragment_slideout_right);

        setResult(AppCore.ResultCodes.RESULT_CANCELLED);
    }

    /**
     * Wird ausgeführt, wenn "Fach hinzufügen" gedrückt wurde. Ruft damit das BottomSheet auf, um ein Fach hinzuzufügen
     * @param view Übergibt das Button-Element
     */
    public void addSubject(View view){
        ArrayList<Subject> disabled = new ArrayList<>();
        disabled.addAll(intensified);
        disabled.addAll(normals);

        ChooseSubjectSheet sheet = new ChooseSubjectSheet(this, disabled);
        sheet.setTitle(getString(R.string.label_chose_subject));
        sheet.setOnSubjectChosenListener(this);
        sheet.show();
    }

    /**
     * Wird ausgeführt, um in der Einrichtung fortzufahren, oder um dieses zu beenden
     * @param view Übergibt das Button-Element
     */
    public void continueSetup(View view) {
        Fragment fragment = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getFragments().size()-1);

        if(fragment instanceof AddNormalFragment) {
            finishSetup();
            return;
        }

        AppFragments.replaceFragment(getSupportFragmentManager(),
                appFragmentContainer,
                new AddNormalFragment(continueSetupBtn, addSubjectBtn),
                false,
                "fragment2",
                R.anim.fragment_slidein_right, R.anim.fragment_slideout_left, R.anim.fragment_slidein_left, R.anim.fragment_slideout_right);
    }

    /**
     * Funktion zum beenden der Einrichtung, wird aufgerufen durch continueSetup() wenn im letzten Schritt der Einrichtung. Hier werden alle Eingaben überprüft.
     */
    private void finishSetup() {
        // Validate input, check for exams
        ArrayList<Subject> subjects = new ArrayList<>(intensified);
        subjects.addAll(normals);

        int exams = 0;
        for(Subject subject : subjects) {
            if(subject.isExam()) ++exams;
        }
        if(exams < AMOUNT_EXAMS_MAX || exams > AMOUNT_EXAMS_MAX) {
            // Show dialog if something is missing.
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle(getString(R.string.error_headline)).setMessage(getString(R.string.error_missing_exams)).create();
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.btn_ok), null, (dialog1, which) -> dialog1.dismiss());
            dialog.show();
            return;
        }

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle(getString(R.string.label_settingup_app));
        dialog.show();

        Needle.onBackgroundThread().execute(new UiRelatedTask<Void>() {
            @Override
            protected Void doWork() {
                for (Subject subject : subjects) {
                    AppDatabase.getInstance().createSubjectEntry(subject);

                }

                // Create default grades for seminar
                AppDatabase.getInstance().createGrade(Seminar.getInstance().getSubjectID(), new Grade(0, Seminar.getInstance().getSubjectID(), 4, 8, Grade.Type.PROCESS));
                AppDatabase.getInstance().createGrade(Seminar.getInstance().getSubjectID(), new Grade(0, Seminar.getInstance().getSubjectID(), 4, 8, Grade.Type.THESIS));
                AppDatabase.getInstance().createGrade(Seminar.getInstance().getSubjectID(), new Grade(0, Seminar.getInstance().getSubjectID(), 4, 8, Grade.Type.PRESENTATION));

                AppCore.Setup.setSetupPassed(true);
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void thenDoUiRelatedWork(Void aVoid) {
                dialog.dismiss();
                setResult(AppCore.ResultCodes.RESULT_OK);
                finish();
            }
        });

        new Handler().post(() -> {

        });

        /*Intent data = new Intent();
        data.putExtra("subjects", bytes);
        setResult(AppCore.ResultCodes.RESULT_OK, data);
        finish();*/
    }

    /**
     * Schließt die Aktivität und zeigt einen Fehler an, wenn beim Einrichten einer aufgetreten ist
     */
    private void showError() {
        setResult(AppCore.ResultCodes.RESULT_FAILED);
        finish();
    }

    /**
     * Prüft, ob die Einrichtung über "Zurück" des Smartphones abgebrochen wurde
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(AppCore.ResultCodes.RESULT_CANCELLED);
    }

    /**
     * Sobald ein Fach/Kurs ausgewählt wurde, wird dieses zur Liste der ausgewählten hinzugefügt (passend der Kategorie Leistungs- oder Grundkurs
     * @param subject Übergibt das Fach, welches ausgewählt wurde
     */
    @Override
    public void onSubjectChosen(Subject subject) {
        Fragment fragment = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getFragments().size()-1);
        if(fragment instanceof AddIntensifiedFragment) {
            subject.setIntensified(true);
            intensified.add(subject);
            if(intensified.size() == AMOUNT_INTENSIFIED) {
                addSubjectBtn.setEnabled(false);
                continueSetupBtn.setEnabled(true);
            }

            // Send info to fragment
            ((AddIntensifiedFragment) fragment).onActivityToFragment(this, subject, AppCore.ActionCodes.ACTION_LIST_ADDITEM);
        }
        if(fragment instanceof AddNormalFragment) {
            subject.setIntensified(false);
            normals.add(subject);
            if(normals.size() == AMOUNT_NORMALS) {
                addSubjectBtn.setEnabled(false);
                continueSetupBtn.setEnabled(true);
            }

            // Send info to fragment
            ((AddNormalFragment) fragment).onActivityToFragment(this, subject, AppCore.ActionCodes.ACTION_LIST_ADDITEM);
        }
    }

    /**
     * Dient der Kommunikation zwischen übergeordneter Aktivität
     * @param fragment Dient der Verifzierung, woher die Daten stammen
     * @param object Übergibt das betreffende Datenobjekt
     * @param actionCode Legt die Aktion fest. Bestimmt wie mit dem Datenobjekt verfahren werden soll
     */
    @Override
    public void onFragmentToActivity(Fragment fragment, Object object, int actionCode) {
        if(fragment instanceof AddIntensifiedFragment) {
            if (intensified.size() < AMOUNT_INTENSIFIED) {
                addSubjectBtn.setEnabled(true);
                continueSetupBtn.setEnabled(false);
            }
        }
        if(fragment instanceof AddNormalFragment) {
            if (normals.size() < AMOUNT_NORMALS) {
                addSubjectBtn.setEnabled(true);
                continueSetupBtn.setEnabled(false);
            }
        }
    }
}
