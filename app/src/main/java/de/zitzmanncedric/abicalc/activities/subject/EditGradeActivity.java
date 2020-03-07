package de.zitzmanncedric.abicalc.activities.subject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.Grade;
import de.zitzmanncedric.abicalc.api.Seminar;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.dialogs.ProgressDialog;
import de.zitzmanncedric.abicalc.utils.AppSerializer;
import de.zitzmanncedric.abicalc.views.AppActionBar;

public class EditGradeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "EditGradeActivity";

    private AppActionBar actionBar;

    private Spinner subjectSpinner;
    private Spinner termSpinner;
    private Spinner typeSpinner;
    private NumberPicker numberPicker;

    private Subject subject;
    private Grade grade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_grade);

        actionBar = findViewById(R.id.app_toolbar);
        setSupportActionBar(actionBar);

        actionBar.setShowSave(true);
        actionBar.setShowClose(true);
        actionBar.getSaveView().setOnClickListener(this);
        actionBar.getCloseView().setOnClickListener(this);

        subjectSpinner = findViewById(R.id.spinner_subjects);
        termSpinner = findViewById(R.id.spinner_terms);
        typeSpinner = findViewById(R.id.spinner_type);
        numberPicker = findViewById(R.id.picker_grade_value);

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(15);

        Intent intent = getIntent();

        grade = (Grade) AppSerializer.deserialize(intent.getByteArrayExtra("grade"));

        if(grade != null) {
            int termID = grade.getTermID();
            int typeID = grade.getType().getId();
            int value = grade.getValue();

            subject = AppDatabase.getInstance().getUserSubjectByID(grade.getSubjectID());

            {
                List<String> items = new ArrayList<>();
                items.add(subject.getTitle());

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
                subjectSpinner.setAdapter(adapter);
            }
            {
                List<String> items = new ArrayList<>();
                if(termID != 4) {
                    items.add(getString(R.string.exp_term).replace("%", String.valueOf(termID+1)));
                } else {
                    items.add(getString(R.string.exp_abi));
                }

                termSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));
            }
            {
                List<String> items = new ArrayList<>();
                for(Grade.Type type : Grade.Type.values()) {
                    items.add(type.getTitle());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
                typeSpinner.setAdapter(adapter);
                typeSpinner.setSelection(typeID);
            }

            numberPicker.setValue(value);
        } else {
            // TODO: Seminar
        }

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == actionBar.getCloseView().getId()) {
            finish();
            return;
        }
        if(v.getId() == actionBar.getSaveView().getId()) {
            // TODO: Save new grade
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle(getString(R.string.label_being_saved));
            dialog.show();

            new Handler().post(()-> {
                if(grade != null && subject != null) {
                    grade.setType(Grade.Type.getByID(typeSpinner.getSelectedItemPosition()));
                    grade.setValue(numberPicker.getValue());
                    AppDatabase.getInstance().updateGrade(subject, grade);
                } else {
                    Toast.makeText(AppCore.getInstance().getApplicationContext(), "Not saved.", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                finish();
            });
        }
    }
}
