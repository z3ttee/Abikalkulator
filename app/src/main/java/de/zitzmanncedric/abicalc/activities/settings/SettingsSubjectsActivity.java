package de.zitzmanncedric.abicalc.activities.settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.activities.subject.SubjectEditorActivity;
import de.zitzmanncedric.abicalc.adapter.AdvancedSubjectListAdapter;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.listener.OnListItemCallback;
import de.zitzmanncedric.abicalc.utils.AppSerializer;
import de.zitzmanncedric.abicalc.views.AppActionBar;

public class SettingsSubjectsActivity extends AppCompatActivity implements OnListItemCallback, View.OnClickListener {

    private AppActionBar actionBarView;
    private AdvancedSubjectListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_subjects);

        actionBarView = findViewById(R.id.app_toolbar);
        RecyclerView subjectsListView = findViewById(R.id.recyclerview_subjects);

        setSupportActionBar(actionBarView);
        actionBarView.setShowClose(true);
        actionBarView.getCloseView().setOnClickListener(this);

        adapter = new AdvancedSubjectListAdapter(new ArrayList<>(AppDatabase.getInstance().userSubjects), this, false);
        subjectsListView.setLayoutManager(new LinearLayoutManager(this));
        subjectsListView.setAdapter(adapter);
    }

    @Override
    public void onItemClicked(ListableObject object) {
        if(object instanceof Subject) {
            Subject subject = (Subject) object;

            Intent intent = new Intent(this, SubjectEditorActivity.class);
            intent.putExtra("subjectID", subject.getId());
            startActivityForResult(intent, AppCore.RequestCodes.REQUEST_UPDATE_SUBJECT);
        }
    }
    @Override
    public void onItemEdit(ListableObject object) {
        onItemClicked(object);
    }

    @Override
    public void onItemDeleted(ListableObject object) { }
    @Override
    public void onItemLongClicked(ListableObject object) { }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == AppCore.ResultCodes.RESULT_OK) {
            if (requestCode == AppCore.RequestCodes.REQUEST_UPDATE_SUBJECT) {
                adapter.set(new ArrayList<>(AppDatabase.getInstance().userSubjects));
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == actionBarView.getCloseView().getId()){
            finish();
        }
    }
}
