package de.zitzmanncedric.abicalc.activities.subject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.adapter.AdvancedSubjectListAdapter;
import de.zitzmanncedric.abicalc.adapter.RecyclerGridAdapter;
import de.zitzmanncedric.abicalc.api.Grade;
import de.zitzmanncedric.abicalc.api.Seminar;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.listener.OnListItemCallback;
import de.zitzmanncedric.abicalc.utils.AppSerializer;
import de.zitzmanncedric.abicalc.views.AppActionBar;
import needle.Needle;
import needle.UiRelatedProgressTask;

public class SeminarActivity extends AppCompatActivity implements View.OnClickListener, OnListItemCallback {

    private RecyclerView recyclerView;
    private AdvancedSubjectListAdapter adapter;

    private boolean limitReached = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seminar);

        AppActionBar actionBar = findViewById(R.id.app_toolbar);
        setSupportActionBar(actionBar);

        actionBar.setShowClose(true);
        actionBar.getCloseView().setOnClickListener(this);

        recyclerView = findViewById(R.id.app_grid_seminars);
        adapter = new AdvancedSubjectListAdapter(this, new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        LinearLayout wrapper = findViewById(R.id.wrapper);
        ArrayList<ListableObject> grades = new ArrayList<>(AppDatabase.getInstance().getGradesForSeminar());
        if(grades.size() >= 3) this.limitReached = true;

        if(grades.isEmpty()) {
            TextView textView = new TextView(new ContextThemeWrapper(this, R.style.TextAppearance));

            ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            marginLayoutParams.topMargin = (int) getResources().getDimension(R.dimen.default_padding_large);
            textView.setLayoutParams(marginLayoutParams);

            textView.setAlpha(0.5f);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setText(getString(R.string.label_grades_missing));
            wrapper.addView(textView);
        } else {
            populate();
        }
    }

    private void populate(){
        adapter.clear();

        Needle.onBackgroundThread().execute(new UiRelatedProgressTask<Void, ListableObject>() {
            @Override
            protected Void doWork() {
                ArrayList<ListableObject> grades = new ArrayList<>(AppDatabase.getInstance().getGradesForSeminar());

                for (ListableObject grade : grades) {
                    publishProgress(grade);
                }
                try {
                    Thread.sleep(100);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(ListableObject grade) {
                adapter.add(grade);
            }

            @Override
            protected void thenDoUiRelatedWork(Void aVoid) { }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_close) {
            finish();
            return;
        }

        if(v.getId() == R.id.app_fab) {
            if(limitReached) {
                Toast.makeText(this, getString(R.string.error_limitforsubject_reached), Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, GradeEditorActivity.class);
            intent.putExtra("subjectID", Seminar.getInstance().getSubjectID());
            startActivityForResult(intent, AppCore.RequestCodes.REQUEST_ADD_GRADE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // TODO: Update views when grade updated.

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AppCore.RequestCodes.REQUEST_ADD_GRADE) {
            if(resultCode == AppCore.ResultCodes.RESULT_OK) {
                ArrayList<ListableObject> grades = new ArrayList<>(AppDatabase.getInstance().getGradesForSeminar());
                if(grades.size() >= 3) this.limitReached = true;

                populate();
            }
        }
    }

    @Override
    public void onItemClicked(ListableObject object) {
        Intent intent = new Intent(this, GradeEditorActivity.class);

        if(object instanceof Grade) {
            Grade grade = (Grade) object;
            intent.putExtra("action", "edit");
            intent.putExtra("grade", AppSerializer.serialize(grade));
            startActivity(intent);
        }
    }

    @Override
    public void onItemEdit(ListableObject object) {
        Intent intent = new Intent(this, GradeEditorActivity.class);

        if(object instanceof Grade) {
            Grade grade = (Grade) object;
            intent.putExtra("action", "edit");
            intent.putExtra("grade", AppSerializer.serialize(grade));
            startActivity(intent);
        }
    }
    @Override
    public void onItemDeleted(ListableObject object) { }
    @Override
    public void onItemLongClicked(ListableObject object) { }
}
