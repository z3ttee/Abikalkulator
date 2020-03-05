package de.zitzmanncedric.abicalc.activities.subject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.Grade;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.utils.AppSerializer;
import de.zitzmanncedric.abicalc.views.AppActionBar;

/**
 * Aktivität zum Hinzufügen einer Note zu einem bestimmten Fach in einem Halbjahr
 */
public class AddGradeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "AddGradeActivity";

    private Spinner subjectSpinner;
    private Spinner termSpinner;
    private Spinner typeSpinner;
    private NumberPicker numberPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_grade);

        AppActionBar actionBar = findViewById(R.id.app_toolbar);
        setSupportActionBar(actionBar);

        subjectSpinner = findViewById(R.id.spinner_subjects);
        termSpinner = findViewById(R.id.spinner_terms);
        typeSpinner = findViewById(R.id.spinner_type);
        numberPicker = findViewById(R.id.picker_grade_value);

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(15);

        {
            List<String> items = new ArrayList<>();
            for (Subject subject : AppDatabase.getInstance().userSubjects) {
                items.add(subject.getTitle());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
            subjectSpinner.setAdapter(adapter);
            subjectSpinner.setOnItemSelectedListener(this);
            subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    populateTermSpinner(AppDatabase.getInstance().userSubjects.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });
        }
        {
            List<String> items = new ArrayList<>();
            for(int i = 1; i<=5; ++i) {
                if(i != 5) {
                    items.add(getString(R.string.exp_term).replace("%", String.valueOf(i)));
                } else {
                    items.add(getString(R.string.exp_abi));
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
            termSpinner.setAdapter(adapter);
            termSpinner.setOnItemSelectedListener(this);
        }
        {
            List<String> items = new ArrayList<>();
            for(Grade.Type type : Grade.Type.values()) {
                items.add(type.getTitle());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
            typeSpinner.setAdapter(adapter);
            typeSpinner.setOnItemSelectedListener(this);
        }
    }

    private void populateTermSpinner(Subject subject) {
        List<String> items = new ArrayList<>();
        for(int i = 1; i<=5; ++i) {
            if(i != 5) {
                items.add(getString(R.string.exp_term).replace("%", String.valueOf(i)));
            } else {
                if(subject.isExam()) {
                    items.add(getString(R.string.exp_abi));
                }
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        termSpinner.setAdapter(adapter);
        termSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    /**
     *
     * @param adapterView
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * Funktion für "Hinzufügen"-Button. Übermittelt Daten an übergeordnete Aktivität
     * @param view Übergibt Button-Element
     */
    public void addGrade(View view) {
        int subjectPosition = subjectSpinner.getSelectedItemPosition(); // beginnend bei 0
        int termPosition = termSpinner.getSelectedItemPosition();       // beginnend bei 0
        int typePosition = typeSpinner.getSelectedItemPosition();       // beginnend bei 0
        int value = numberPicker.getValue();                            // Standard: 0

        Subject subject = AppDatabase.getInstance().userSubjects.get(subjectPosition);

        Grade.Type type = Grade.Type.getByID(typePosition);
        Grade grade = new Grade(0, subject.getId(), termPosition, value, type); // SubjectID ist gleich mit position, durch sortierung Geht nur, weil positionen wie in der Liste der AppDatenbank

        AppDatabase.getInstance().createGrade(subject, grade);
        setResult(AppCore.ResultCodes.RESULT_OK);
        finish();
    }

    /**
     * Funktion für "Abbrechen"-Button. Übermittelt der übergeordneten Aktivität, dass Prozess abgebrochen wurde
     * @param view Übergibt Button-Element
     */
    public void cancel(View view) {
        setResult(AppCore.ResultCodes.RESULT_CANCELLED);
        finish();
    }
}
