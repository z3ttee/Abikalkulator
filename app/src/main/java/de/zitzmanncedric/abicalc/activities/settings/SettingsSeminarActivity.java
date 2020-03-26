package de.zitzmanncedric.abicalc.activities.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.Seminar;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.views.AppActionBar;

public class SettingsSeminarActivity extends AppCompatActivity implements View.OnClickListener {

    private AppActionBar actionBar;
    private Spinner replaceSubjectView;
    private Switch mindSeminarView;
    private TextView labelSpinnerReplace;

    private ArrayList<Subject> oralExams = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_seminar);

        actionBar = findViewById(R.id.app_toolbar);
        replaceSubjectView = findViewById(R.id.spinner_replace);
        mindSeminarView = findViewById(R.id.switch_mind_seminar);
        labelSpinnerReplace = findViewById(R.id.label_spinner_replace);

        setSupportActionBar(actionBar);
        actionBar.setShowClose(true);
        actionBar.getCloseView().setOnClickListener(this);

        if(Seminar.getInstance().isMinded()) {
            mindSeminarView.setChecked(true);
            toggleReplacement(false);
        } else {
            toggleReplacement(true);
        }

        Subject selected = null;
        ArrayList<String> items = new ArrayList<>();
        for(Subject subject : AppDatabase.getInstance().userSubjects){
            if(subject.isOralExam()) {
                oralExams.add(subject);
                items.add(subject.getTitle());

                if(Seminar.getInstance().getReplacedSubjectID() == subject.getId()) {
                    selected = subject;
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        replaceSubjectView.setAdapter(adapter);

        mindSeminarView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            toggleReplacement(!isChecked);
            Seminar.getInstance().setMinded(isChecked);

            if(!isChecked) {
                Seminar.getInstance().setReplacedSubjectID(-2);
            } else {
                Seminar.getInstance().setReplacedSubjectID(oralExams.get(replaceSubjectView.getSelectedItemPosition()).getId());
            }
        });
        replaceSubjectView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Seminar.getInstance().setReplacedSubjectID(oralExams.get(position).getId());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        if(selected != null) {
            replaceSubjectView.setSelection(items.indexOf(selected.getTitle()));
        }
    }

    private void toggleReplacement(boolean hide) {
        if(hide) {
            labelSpinnerReplace.animate().alpha(0.5f).setDuration(getResources().getInteger(R.integer.anim_speed_quickly));
            replaceSubjectView.setEnabled(false);
            replaceSubjectView.animate().alpha(0.5f).setDuration(getResources().getInteger(R.integer.anim_speed_quickly));
        } else {
            labelSpinnerReplace.animate().alpha(1f).setDuration(getResources().getInteger(R.integer.anim_speed_quickly));
            replaceSubjectView.setEnabled(true);
            replaceSubjectView.animate().alpha(1f).setDuration(getResources().getInteger(R.integer.anim_speed_quickly));
        }
    }

    @Override
    public void finish() {
        Toast.makeText(this, getString(R.string.notice_settings_saved), Toast.LENGTH_SHORT).show();
        super.finish();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == actionBar.getCloseView().getId()) {
            finish();
        }
    }
}
