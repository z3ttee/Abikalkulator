package de.zitzmanncedric.abicalc.activities.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.animation.ObjectAnimator;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.api.Grade;
import de.zitzmanncedric.abicalc.api.Seminar;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.dialogs.InfoDialog;
import de.zitzmanncedric.abicalc.dialogs.ProgressDialog;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.fragments.setup.AddIntensifiedFragment;
import de.zitzmanncedric.abicalc.fragments.setup.AddNormalFragment;
import de.zitzmanncedric.abicalc.fragments.setup.SetupWelcomeFragment;
import lombok.Getter;
import needle.Needle;
import needle.UiRelatedTask;

/**
 * Klasse zur Behandlung des Ersteinrichtungs-Menü.
 * @author Cedric Zitzmann
 */
public class SetupActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SetupActivity";

    public static final int AMOUNT_INTENSIFIED = 5;
    public static final int AMOUNT_BASICS = 6;
    public static final int AMOUNT_WRITTEN_EXAMS = 3;
    public static final int AMOUNT_ORAL_EXAMS = 2;

    private final int COUNT_STEPS = 3;
    private int CURRENT_STEP = 1;

    private ViewPager fragmentPager;
    private TextView stepsView;
    private ProgressBar progressBar;

    private ImageButton btnPrevious;
    private ImageButton btnNext;

    @Getter private ArrayList<Subject> intensified = new ArrayList<>();
    @Getter private ArrayList<Subject> basics = new ArrayList<>();

    /**
     * Von Android implementiert. Methode zum Aufbauen des Fensters
     * @param savedInstanceState Von Android übergeben (nicht genutzt)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setBackgroundDrawable(new ColorDrawable(getColor(R.color.splash_background)));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        progressBar = findViewById(R.id.setup_progress);
        fragmentPager = findViewById(R.id.app_fragment_pager);
        stepsView = findViewById(R.id.setup_steps);
        btnPrevious = findViewById(R.id.btn_setup_prev);
        btnNext = findViewById(R.id.btn_setup_next);

        setResult(AppCore.ResultCodes.RESULT_CANCELLED);
        updateSteps();

        btnPrevious.setClipToOutline(true);
        btnPrevious.setAlpha(0f);
        btnPrevious.setEnabled(false);
        btnPrevious.setOnClickListener(this);
        btnNext.setClipToOutline(true);
        btnNext.setOnClickListener(this);

        fragmentPager.setAdapter(new Adapter(getSupportFragmentManager()));
        fragmentPager.setOffscreenPageLimit(COUNT_STEPS);
        fragmentPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                int oldStepPosition = CURRENT_STEP-1;

                if(oldStepPosition > position){
                    CURRENT_STEP = position+1;
                    updateSteps();
                    updateButtons();
                    return;
                }

                if(CURRENT_STEP != position+1 && isReadyForNext(true)){
                    CURRENT_STEP = position+1;
                    updateSteps();
                    updateButtons();
                } else {
                    fragmentPager.setCurrentItem(CURRENT_STEP-1, true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
        });
    }

    /**
     * Private Funktion. Aktualisiert die Buttons zum Navigieren in der Ersteinrichtung je nachdem in welchem Schritt der Benutzer ist.
     */
    private void updateButtons(){
        int animSpeed = getResources().getInteger(R.integer.anim_speed_quickly);

        if(CURRENT_STEP == 1){
            btnPrevious.setEnabled(false);
            btnPrevious.animate().alpha(0f).setDuration(animSpeed);
            btnNext.setEnabled(true);
            btnNext.animate().alpha(1f).setDuration(animSpeed);
            btnNext.setImageDrawable(getDrawable(R.drawable.ic_back));
            btnNext.setRotation(180f);
            ObjectAnimator.ofInt(progressBar, "progress", 333).setDuration(animSpeed).start();
            return;
        }
        if(CURRENT_STEP == 2){
            btnPrevious.setEnabled(true);
            btnPrevious.animate().alpha(1f).setDuration(animSpeed);
            btnNext.setEnabled(true);
            btnNext.animate().alpha(1f).setDuration(animSpeed);
            btnNext.setImageDrawable(getDrawable(R.drawable.ic_back));
            btnNext.setRotation(180f);
            ObjectAnimator.ofInt(progressBar, "progress", 666).setDuration(animSpeed).start();
            return;
        }
        if(CURRENT_STEP == 3){
            btnPrevious.setEnabled(true);
            btnPrevious.animate().alpha(1f).setDuration(animSpeed);
            btnNext.setEnabled(true);
            btnNext.animate().alpha(1f).setDuration(animSpeed);
            btnNext.setImageDrawable(getDrawable(R.drawable.ic_check));
            btnNext.setRotation(0f);
            ObjectAnimator.ofInt(progressBar, "progress", 1000).setDuration(animSpeed).start();
        }
    }

    /**
     * Private Funktion. Aktualisiert die Meldung über den derzeitigen Fortschritt als Text
     */
    private void updateSteps(){
        stepsView.setText(getString(R.string.exp_steps).replace("%step%", String.valueOf(CURRENT_STEP)).replace("%steps%", String.valueOf(COUNT_STEPS)));
    }

    /**
     * Fängt alle Klick-Events im Fenster ab. Vorallem wird die Funktion der "Zurück" und "Nächstes" Buttons erfüllt, die im Setup für das navigieren sorgen
     * @param v Angeklickter Button
     */
    @Override
    public void onClick(View v) {
        if(v.getId() == btnPrevious.getId()){
            if(CURRENT_STEP == 3){
                fragmentPager.setCurrentItem(1, true);
                return;
            }
            if(CURRENT_STEP == 2){
                fragmentPager.setCurrentItem(0, true);
            }
            return;
        }
        if(v.getId() == btnNext.getId()){
            if(CURRENT_STEP == 1){
                if(isReadyForNext(true)) {
                    fragmentPager.setCurrentItem(1, true);
                }
                return;
            }
            if(CURRENT_STEP == 2){
                if(isReadyForNext(true)) {
                    fragmentPager.setCurrentItem(2, true);
                }
                return;
            }
            if(CURRENT_STEP == 3){
                if(isReadyForNext(true)) {
                    finishSetup();
                }
            }
        }
    }

    /**
     * Private Funktion. Prüft, ob der nächste Schritt der Einrichtung in Angriff genommen werden kann.
     * @param showDialogOnError Wenn true, wird dem Nutzer der Fehler angezeigt, weshalb nicht fortgefahren werden kann
     * @return true, wenn Einrichtung fortgesetzt werden kann, andernfalls wird false zurückgesetzt
     */
    private boolean isReadyForNext(boolean showDialogOnError){
        boolean ready = true;
        if(CURRENT_STEP == 2){
            InfoDialog dialog = new InfoDialog(this);
            dialog.setTitle(getString(R.string.error_headline));

            if(this.intensified.size() < AMOUNT_INTENSIFIED){
                dialog.setMessage(R.string.error_missing_intensified);
                ready = false;
            } else if(this.getCountWrittenExams() < AMOUNT_WRITTEN_EXAMS){
                dialog.setMessage(R.string.error_missing_writtenexams);
                ready = false;
            }

            if(!ready && showDialogOnError) {
                dialog.show();
            }
        } else if(CURRENT_STEP == 3){
            InfoDialog dialog = new InfoDialog(this);
            dialog.setTitle(getString(R.string.error_headline));

            if(this.basics.size() < AMOUNT_BASICS){
                dialog.setMessage(R.string.error_missing_basics);
                ready = false;
            } else if(this.getCountOralExams() < AMOUNT_ORAL_EXAMS){
                dialog.setMessage(R.string.error_missing_oralexams);
                ready = false;
            }

            if(!ready && showDialogOnError) {
                dialog.show();
            }
        }

        return ready;
    }

    /**
     * Adapterklasse zum Aufbau und Verwaltung des ViewPagers
     */
    private class Adapter extends FragmentPagerAdapter {

        /**
         * Konstruktor der Klasse
         * @param fm Fragmentmanager zur Verwaltung der Fragmente im ViewPager
         */
        Adapter(@NonNull FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        /**
         * Ermittelt das Fragment an einer bestimmten Position im ViewPager
         * @param position Position im ViewPager
         * @return Fragment
         */
        @NonNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position){
                case 1:
                    fragment = new AddIntensifiedFragment(SetupActivity.this);
                    break;
                case 2:
                    fragment = new AddNormalFragment(SetupActivity.this);
                    break;

                default:
                    fragment = new SetupWelcomeFragment();
                    break;
            }
            return fragment;
        }

        /**
         * Ermittelt die Anzahl von Fragmenten im ViewPager
         * @return Anzahl als Integer
         */
        @Override
        public int getCount() {
            return COUNT_STEPS;
        }
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
        subjects.addAll(basics);

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
     * Funktion zum beenden der Einrichtung. Wird aufgerufen durch continueSetup(), wenn sich der Nutzer im letzten Schritt der Einrichtung befindet. Hier werden alle Eingaben überprüft und gespeichert.
     */
    private void finishSetup() {
        ArrayList<Subject> subjects = new ArrayList<>();

        for(Subject subject : this.intensified) {
            subject.setIntensified(true);
            subjects.add(subject);
            Log.i(TAG, "finishSetup: "+subject.isIntensified());
        }
        subjects.addAll(basics);

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
                AppDatabase.getInstance().reloadSubjects();
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
}
