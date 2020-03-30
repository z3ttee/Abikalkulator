package de.zitzmanncedric.abicalc.activities.subject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.activities.main.SetupActivity;
import de.zitzmanncedric.abicalc.adapter.SimpleSubjectListAdapter;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.listener.OnListItemCallback;
import de.zitzmanncedric.abicalc.listener.OnSubjectChosenListener;
import de.zitzmanncedric.abicalc.utils.AppSerializer;
import lombok.Setter;
import needle.Needle;
import needle.UiRelatedProgressTask;

public class AddSubjectActivity extends AppCompatActivity implements OnListItemCallback {
    private static final String TAG = "AddSubjectActivity";

    private ArrayList<Subject> disabled = new ArrayList<>();

    private CheckBox checkBoxExam;
    private CheckBox checkBoxOralExam;

    private SimpleSubjectListAdapter adapter;

    @Setter private boolean onlyOralExam = false;
    @Setter private boolean onlyWrittenExam = false;
    private int count_oral = 0;
    private int count_written = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);
        setResult(AppCore.ResultCodes.RESULT_CANCELLED);

        RecyclerView recyclerView = findViewById(R.id.recyclerview_subjects);
        checkBoxExam = findViewById(R.id.checkbox_exam);
        checkBoxOralExam = findViewById(R.id.checkbox_oralexam);

        Intent intent = getIntent();
        this.onlyOralExam = intent.getBooleanExtra("onlyOralExam", false);
        this.onlyWrittenExam = intent.getBooleanExtra("onlyWrittenExam", false);
        this.count_written = intent.getIntExtra("countWritten", 0);
        this.count_oral = intent.getIntExtra("countOral", 0);

        try {
            this.disabled = (ArrayList<Subject>) AppSerializer.deserialize(intent.getByteArrayExtra("disabled"));
        } catch (Exception ignored){ }

        adapter = new SimpleSubjectListAdapter(new ArrayList<>(), disabled);
        adapter.setOnCallback(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        checkBoxOralExam.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(onlyOralExam) {
                checkBoxExam.setChecked(isChecked);
            }
            if(isChecked) {
                checkBoxExam.setChecked(true);
            }
        });
        checkBoxExam.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if(!isChecked || onlyWrittenExam) {
                checkBoxOralExam.setChecked(false);
            }
        }));

        if(onlyOralExam || count_written >= SetupActivity.AMOUNT_WRITTEN_EXAMS) {
            checkBoxExam.setEnabled(false);
            checkBoxExam.setAlpha(0.5f);
        }
        if(onlyWrittenExam || count_oral >= SetupActivity.AMOUNT_ORAL_EXAMS) {
            checkBoxOralExam.setEnabled(false);
            checkBoxOralExam.setAlpha(0.5f);
        }

        Needle.onBackgroundThread().withThreadPoolSize(1).execute(new UiRelatedProgressTask<Void, Subject>() {
            @Override
            protected Void doWork() {
                for(Subject subject : AppDatabase.getInstance().appSubjects.values()) {
                    boolean exists = false;
                    for(Subject s : disabled){
                        if (s.getTitle().equals(subject.getTitle())) {
                            exists = true;
                            break;
                        }
                    }

                    if(!exists) {
                        publishProgress(subject);
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                return null;
            }

            @Override
            protected void onProgressUpdate(Subject subject) {
                adapter.add(subject);
            }

            @Override
            protected void thenDoUiRelatedWork(Void v) { }
        });
    }

    /**
     * Behandelt das Auswählen eines Objekts aus der Liste
     * @param object Gibt das Objekts in der Liste an
     */
    @Override
    public void onItemClicked(ListableObject object) {
        try {
            if(object instanceof Subject) {
                Subject subject = (Subject) object;
                subject.setExam(checkBoxExam.isChecked());
                subject.setOralExam(checkBoxOralExam.isChecked());

                Intent data = new Intent();
                byte[] bytes = AppSerializer.serialize(subject);
                data.putExtra("subjectData", bytes);
                setResult(AppCore.ResultCodes.RESULT_OK, data);
                finish();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(AppCore.getInstance().getApplicationContext(), "Error occured. Procedure failed.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Unwichtig (nicht genutzt)
     */
    @Override
    public void onItemDeleted(ListableObject object) { }

    /**
     * Unwichtig (nicht genutzt)
     */
    @Override
    public void onItemEdit(ListableObject object) { }

    /**
     * Unwichtig (nicht genutzt)
     */
    @Override
    public void onItemLongClicked(ListableObject object) { }
}
