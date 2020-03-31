package de.zitzmanncedric.abicalc.activities.subject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.dialogs.ConfirmDialog;
import de.zitzmanncedric.abicalc.dialogs.ProgressDialog;
import de.zitzmanncedric.abicalc.utils.AppSerializer;
import de.zitzmanncedric.abicalc.views.AppActionBar;
import de.zitzmanncedric.abicalc.views.AppButton;
import needle.Needle;
import needle.UiRelatedTask;

/**
 * Klasse zur Behandlung des Kurseditors
 * @author Cedric Zitzmann
 */
public class SubjectEditorActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private AppActionBar actionBar;
    private Spinner spinnerSubjectsView;

    private Subject subject;
    private ArrayList<String> spinnerItems = new ArrayList<>();
    private ArrayList<Subject> subjects;

    /**
     * Von Android implementiert. Methode zum Aufbauen des Fensters. Lädt und zeigt alle Einstellungen an.
     * @param savedInstanceState Von Android übergeben (nicht genutzt)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_subject);

        actionBar = findViewById(R.id.app_toolbar);
        actionBar.setShowClose(true);
        actionBar.setShowSave(true);
        actionBar.getCloseView().setOnClickListener(this);
        actionBar.getSaveView().setOnClickListener(this);
        setSupportActionBar(actionBar);

        spinnerSubjectsView = findViewById(R.id.spinner_subjects);
        CheckBox markAsExamView = findViewById(R.id.checkbox_markasexam);
        CheckBox markAsOralExamView = findViewById(R.id.checkbox_markasoralexam);

        subjects = new ArrayList<>(AppDatabase.getInstance().appSubjects.values());
        for(Subject subject : subjects) {
            spinnerItems.add(subject.getTitle());
        }

        Intent intent = getIntent();
        int subjectID = intent.getIntExtra("subjectID", 0);
        subject = AppDatabase.getInstance().getUserSubjectByID(subjectID);

        if(subject.isExam()) {
            markAsExamView.setChecked(true);
            if(subject.isOralExam()) {
                markAsOralExamView.setChecked(true);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerItems);
        spinnerSubjectsView.setAdapter(adapter);
        spinnerSubjectsView.setSelection(spinnerItems.indexOf(subject.getTitle()));
        spinnerSubjectsView.setOnItemSelectedListener(this);

        markAsExamView.setEnabled(false);
        markAsExamView.animate().alpha(0.5f).setDuration(getResources().getInteger(R.integer.anim_speed_quickly));
        markAsOralExamView.setEnabled(false);
        markAsOralExamView.animate().alpha(0.5f).setDuration(getResources().getInteger(R.integer.anim_speed_quickly));
    }

    /**
     * Fängt alle Klick-Events im Fenster ab. Es wird entweder das Fenster geschlossen, oder alle Einstellungen übernommen.
     * @param v Angeklickter Button
     */
    @Override
    public void onClick(View v) {
        if(v.getId() == actionBar.getCloseView().getId()) {
            setResult(AppCore.ResultCodes.RESULT_CANCELLED);
            finish();
            return;
        }
        if(v.getId() == actionBar.getSaveView().getId()) {
            Subject newSubject = subjects.get(spinnerSubjectsView.getSelectedItemPosition());

            if(subject.getId() != newSubject.getId()) {
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle(R.string.notice_settings_beingsaved);
                dialog.show();

                Needle.onBackgroundThread().execute(new UiRelatedTask<Void>() {
                    @Override
                    protected Void doWork() {
                        AppDatabase.getInstance().replaceSubject(subject, newSubject);
                        AppDatabase.getInstance().reloadSubjects();
                        return null;
                    }

                    @Override
                    protected void thenDoUiRelatedWork(Void aVoid) {
                        dialog.dismiss();
                        setResult(AppCore.ResultCodes.RESULT_OK);
                        Toast.makeText(SubjectEditorActivity.this, getString(R.string.notice_settings_saved), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } else {
                setResult(AppCore.ResultCodes.RESULT_OK);
                finish();
            }
        }
    }

    /**
     * Wenn ein Element in der Dropdown-Liste ausgewählt wurde, wird geprüft, ob sich dieses vom vorherigen unterscheidet. Da es zur Löschung von Noten kommen kann, wird dem Nutzer eine Warnung angezeigt.
     * @param parent Elternelement der Liste
     * @param view Angeklicktes Element
     * @param position Position des Elements in der Liste
     * @param id ID des Elements
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Subject sbj = subjects.get(position);
        if(subject.getId() != sbj.getId()) {
            ConfirmDialog dialog = new ConfirmDialog(this);
            dialog.setCallback((button) -> {
                if(button == dialog.getButtonPositive()){
                    spinnerSubjectsView.setSelection(spinnerItems.indexOf(sbj.getTitle()));
                    dialog.dismiss();
                    return;
                }
                if(button == dialog.getButtonNegative()) {
                    spinnerSubjectsView.setSelection(spinnerItems.indexOf(subject.getTitle()));
                    dialog.dismiss();
                }
            });
            dialog.setBanner(R.drawable.ic_warning, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics()));
            dialog.setTitle(getString(R.string.headline_yousure));
            dialog.setMessage(R.string.notice_replace_subjects);
            dialog.setOnCancelListener(dialog1 -> spinnerSubjectsView.setSelection(spinnerItems.indexOf(subject.getTitle())));
            dialog.show();
        }
    }

    /**
     * Unwichtig. Wird nicht benutzt
     * @param parent Elternelement der Liste
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}
