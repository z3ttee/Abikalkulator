package de.zitzmanncedric.abicalc.activities.subject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class SubjectEditorActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "SubjectEditorActivity";

    private AppActionBar actionBar;
    private Spinner spinnerSubjectsView;
    private CheckBox markAsExamView;
    private CheckBox markAsOralExamView;

    private int COUNT_WRITTEN_EXAMS = 0;
    private int COUNT_ORAL_EXAMS = 0;

    private Subject subject;
    private ArrayList<String> spinnerItems = new ArrayList<>();
    private ArrayList<Subject> subjects;

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
        markAsExamView = findViewById(R.id.checkbox_markasexam);
        markAsOralExamView = findViewById(R.id.checkbox_markasoralexam);

        subjects = new ArrayList<>(AppDatabase.getInstance().appSubjects.values());
        for(Subject subject : subjects) {
            spinnerItems.add(subject.getTitle());
            if(subject.isExam()) {
                if(subject.isOralExam()) ++COUNT_ORAL_EXAMS;
                else ++COUNT_WRITTEN_EXAMS;
            }
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

    @Override
    public void onClick(View v) {
        if(v.getId() == actionBar.getCloseView().getId()) {
            setResult(AppCore.ResultCodes.RESULT_CANCELLED);
            finish();
            return;
        }
        if(v.getId() == actionBar.getSaveView().getId()) {
            // TODO: Save settings
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Subject sbj = subjects.get(position);
        if(subject.getId() != sbj.getId()) {
            ConfirmDialog dialog = new ConfirmDialog(this);
            dialog.setCallback(new ConfirmDialog.DialogCallback() {
                @Override
                public void onButtonPositiveClicked(AppButton button) {
                    spinnerSubjectsView.setSelection(spinnerItems.indexOf(sbj.getTitle()));
                    dialog.dismiss();
                }

                @Override
                public void onButtonNegativeClicked(AppButton button) {
                    spinnerSubjectsView.setSelection(spinnerItems.indexOf(subject.getTitle()));
                    dialog.dismiss();
                }
            });
            dialog.setIcon(R.drawable.ic_warning);
            dialog.setTitle(getString(R.string.headline_yousure));
            dialog.setDescription(getString(R.string.notice_replace_subjects));
            dialog.show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}
