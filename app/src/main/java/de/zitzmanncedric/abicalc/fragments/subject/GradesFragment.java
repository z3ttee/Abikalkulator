package de.zitzmanncedric.abicalc.fragments.subject;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.activities.subject.GradeEditorActivity;
import de.zitzmanncedric.abicalc.adapter.AdvancedSubjectListAdapter;
import de.zitzmanncedric.abicalc.api.Grade;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.broadcast.GradeBroadcaster;
import de.zitzmanncedric.abicalc.broadcast.OnBroadcastActionListener;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.listener.OnListItemCallback;
import de.zitzmanncedric.abicalc.utils.AppSerializer;
import needle.Needle;
import needle.UiRelatedProgressTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class GradesFragment extends Fragment implements OnListItemCallback, OnBroadcastActionListener {
    private static final String TAG = "GradesFragment";

    private Subject subject;
    private int termID;

    private AdvancedSubjectListAdapter adapter;

    public GradesFragment() { }
    public GradesFragment(Subject subject, int termID) {
        this.subject = subject;
        this.termID = termID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grades, container, false);

        GradeBroadcaster.getInstance().register(this);

        RecyclerView recyclerView = view.findViewById(R.id.grades_list);
        adapter = new AdvancedSubjectListAdapter(view.getContext(), new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        LinearLayout wrapper = view.findViewById(R.id.fragment_wrapper);
        ArrayList<ListableObject> grades = new ArrayList<>(AppDatabase.getInstance().getGradesForTerm(subject, termID));

        if(grades.isEmpty()) {
            TextView textView = new TextView(new ContextThemeWrapper(view.getContext(), R.style.TextAppearance));

            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(params);

            textView.setAlpha(0.5f);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setText(view.getContext().getString(R.string.label_grades_missing));
            wrapper.addView(textView);
        } else {
            populate();
        }
        return view;
    }

    private void populate(){
        adapter.clear();

        Needle.onBackgroundThread().execute(new UiRelatedProgressTask<Void, ListableObject>() {
            @Override
            protected Void doWork() {
                ArrayList<ListableObject> grades = new ArrayList<>(AppDatabase.getInstance().getGradesForTerm(subject, termID));

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
    public void onDestroy() {
        super.onDestroy();
        GradeBroadcaster.getInstance().unregister(this);
    }

    @Override
    public void onItemClicked(ListableObject object) {
        Intent intent = new Intent(getContext(), GradeEditorActivity.class);

        if(object instanceof Grade) {
            Grade grade = (Grade) object;
            intent.putExtra("action", "edit");
            intent.putExtra("grade", AppSerializer.serialize(grade));
            startActivity(intent);
        }
    }

    @Override
    public void onItemDeleted(ListableObject object) {
        if(object instanceof Grade) {
            int id = AppDatabase.getInstance().removeGrade((Grade) object);
            adapter.remove(object);
        }
    }

    @Override
    public void onItemEdit(ListableObject object) { }

    @Override
    public void onItemLongClicked(ListableObject object) { }

    @Override
    public void onBroadcast(int code, String payload) {
        Log.i(TAG, "onBroadcast: received code ["+code+"] with payload ["+payload+"].");
        populate();
    }
}
