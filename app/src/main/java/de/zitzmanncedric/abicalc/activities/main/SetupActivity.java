package de.zitzmanncedric.abicalc.activities.main;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.AppFragments;
import de.zitzmanncedric.abicalc.api.Grade;
import de.zitzmanncedric.abicalc.api.Seminar;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.dialogs.ProgressDialog;
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
 * Klasse zur Behandlung des Ersteinrichtungs-Menü.
 * @author Cedric Zitzmann
 */
public class SetupActivity extends AppCompatActivity implements OnSubjectChosenListener, OnFragmentToActivity {

    public static final int AMOUNT_INTENSIFIED = 5;   // 5
    public static final int AMOUNT_NORMALS = 6;       // 6
    public static final int AMOUNT_EXAMS_MAX = 5;     // 5

    private FrameLayout appFragmentContainer;

    /*private AppButton addSubjectBtn;
    private AppButton continueSetupBtn;*/

    public ArrayList<Subject> intensified = new ArrayList<>();
    public ArrayList<Subject> normals = new ArrayList<>();

    /**
     * Von Android implementiert. Methode zum Aufbauen des Fensters
     * @param savedInstanceState Von Android übergeben (nicht genutzt)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setBackgroundDrawable(new ColorDrawable(getColor(R.color.splash_background)));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        appFragmentContainer = findViewById(R.id.app_fragment_container);
        /*addSubjectBtn = findViewById(R.id.btn_add_subject);
        continueSetupBtn = findViewById(R.id.btn_continue);

        continueSetupBtn.setEnabled(false);*/

        /*AppFragments.replaceFragment(getSupportFragmentManager(),
                appFragmentContainer,
                new AddIntensifiedFragment(continueSetupBtn, addSubjectBtn),
                true,
                null,
                0, R.anim.fragment_slideout_left, R.anim.fragment_slidein_left, R.anim.fragment_slideout_right);*/

        setResult(AppCore.ResultCodes.RESULT_CANCELLED);
    }

    /**
     * Wird ausgeführt, wenn "Fach hinzufügen" gedrückt wurde. Ruft damit das BottomSheet (Liste mit Fächern) auf, um ein Fach hinzuzufügen
     * @param view Angeklickter Button
     */
    public void addSubject(View view){
        ArrayList<Subject> disabled = new ArrayList<>();
        disabled.addAll(intensified);
        disabled.addAll(normals);

        ChooseSubjectSheet sheet = new ChooseSubjectSheet(this, disabled);
        sheet.setTitle(getString(R.string.label_chose_subject));
        sheet.setOnSubjectChosenListener(this);

        if(getCountWrittenExams() >= 3) {
            sheet.setOnlyOralExam(true);
        }
        if(getCountOralExams() >= 2) {
            sheet.setOnlyWrittenExam(true);
        }

        sheet.show();
    }

    /**
     * Ermittelt Anzahl von ausgewählten schriftlichen Prüfungsfächer
     * @return Anzahl als Integer
     */
    public int getCountWrittenExams() {
        ArrayList<Subject> subjects = new ArrayList<>(intensified);

        int count_written = 0;
        for(Subject subject : subjects) {
            if(subject.isExam()) {
                if(!subject.isOralExam()){
                    ++count_written;
                }
            }
        }

        return count_written;
    }

    /**
     * Ermittelt Anzahl von ausgewählten mündlichen Prüfungsfächer
     * @return Anzahl als Integer
     */
    public int getCountOralExams() {
        ArrayList<Subject> subjects = new ArrayList<>();
        subjects.addAll(intensified);
        subjects.addAll(normals);

        int count_oral = 0;
        for(Subject subject : subjects) {
            if(subject.isExam()) {
                if(subject.isOralExam()){
                    ++count_oral;
                }
            }
        }

        return count_oral;
    }

    /**
     * Wird ausgeführt, um in der Einrichtung fortzufahren, oder um diese zu beenden
     * @param view Angeklickter Button
     */
    public void continueSetup(View view) {
        Fragment fragment = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getFragments().size()-1);

        if(fragment instanceof AddNormalFragment) {
            finishSetup();
            return;
        }

        /*AppFragments.replaceFragment(getSupportFragmentManager(),
                appFragmentContainer,
                new AddNormalFragment(continueSetupBtn, addSubjectBtn),
                false,
                "fragment2",
                R.anim.fragment_slidein_right, R.anim.fragment_slideout_left, R.anim.fragment_slidein_left, R.anim.fragment_slideout_right);*/
    }

    /**
     * Funktion zum beenden der Einrichtung. Wird aufgerufen durch continueSetup(), wenn sich der Nutzer im letzten Schritt der Einrichtung befindet. Hier werden alle Eingaben überprüft und gespeichert.
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
    }

    /**
     * Von Android implementiert. Setzt das Resultat der Aktivität auf "Abgebrochen", wenn die Einrichtung über den "Zurück"-Button des Smartphones abgebrochen wurde.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(AppCore.ResultCodes.RESULT_CANCELLED);
    }

    /**
     * Sobald ein Fach/Kurs ausgewählt wurde, wird dieses zur Liste der ausgewählten hinzugefügt (passend der Kategorie Leistungs- oder Grundkurs
     * @param subject Ausgewähltes Fach
     */
    @Override
    public void onSubjectChosen(Subject subject) {
        Fragment fragment = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getFragments().size()-1);
        if(fragment instanceof AddIntensifiedFragment) {
            subject.setIntensified(true);
            intensified.add(subject);

            if(intensified.size() == AMOUNT_INTENSIFIED) {
                /*addSubjectBtn.setEnabled(false);
                continueSetupBtn.setEnabled(true);*/
            }

            // Send info to fragment
            ((AddIntensifiedFragment) fragment).onActivityToFragment(this, subject, AppCore.ActionCodes.ACTION_LIST_ADDITEM);
        }
        if(fragment instanceof AddNormalFragment) {
            subject.setIntensified(false);
            normals.add(subject);

            if(normals.size() == AMOUNT_NORMALS) {
                /*addSubjectBtn.setEnabled(false);
                continueSetupBtn.setEnabled(true);*/
            }

            // Send info to fragment
            ((AddNormalFragment) fragment).onActivityToFragment(this, subject, AppCore.ActionCodes.ACTION_LIST_ADDITEM);
        }
    }

    /**
     * Dient der Kommunikation zwischen übergeordneter Aktivität
     * @param fragment Dient der Verifizierung, woher die Daten stammen
     * @param object Übergibt das betreffende Datenobjekt
     * @param actionCode Legt die Aktion fest. Bestimmt wie mit dem Datenobjekt verfahren werden soll
     */
    @Override
    public void onFragmentToActivity(Fragment fragment, Object object, int actionCode) {
        if(fragment instanceof AddIntensifiedFragment) {
            if (intensified.size() < AMOUNT_INTENSIFIED) {
                /*addSubjectBtn.setEnabled(true);
                continueSetupBtn.setEnabled(false);*/
            }
        }
        if(fragment instanceof AddNormalFragment) {
            if (normals.size() < AMOUNT_NORMALS) {
                /*addSubjectBtn.setEnabled(true);
                continueSetupBtn.setEnabled(false);*/
            }
        }
    }
}
