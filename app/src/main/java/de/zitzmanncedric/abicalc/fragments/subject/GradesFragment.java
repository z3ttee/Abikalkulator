package de.zitzmanncedric.abicalc.fragments.subject;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.activities.subject.EditGradeActivity;
import de.zitzmanncedric.abicalc.adapter.AdvancedSubjectListAdapter;
import de.zitzmanncedric.abicalc.api.Grade;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.listener.OnListItemCallback;
import de.zitzmanncedric.abicalc.utils.AppSerializer;

/**
 * A simple {@link Fragment} subclass.
 */
public class GradesFragment extends Fragment implements OnListItemCallback {
    private static final String TAG = "GradesFragment";

    private RecyclerView recyclerView;
    private ArrayList<? extends ListableObject> dataset = new ArrayList<>();

    private Subject subject;
    private int termID;

    public GradesFragment() { }
    public GradesFragment(Subject subject, int termID) {
        this.subject = subject;
        this.termID = termID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grades, container, false);

        recyclerView = view.findViewById(R.id.grades_list);
        AdvancedSubjectListAdapter adapter = new AdvancedSubjectListAdapter(view.getContext(), new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);


        new Handler().post(() -> {
            ArrayList<ListableObject> grades = new ArrayList<>(AppDatabase.getInstance().getGradesForTerm(subject, termID));
            adapter.set(grades);
        });

        return view;
    }



    @Override
    public void onItemClicked(ListableObject object) {
        Intent intent = new Intent(getContext(), EditGradeActivity.class);

        if(object instanceof Grade) {
            Grade grade = (Grade) object;
            intent.putExtra("subjectID", grade.getSubjectID());
            intent.putExtra("termID", termID);
            intent.putExtra("value", grade.getValue());
            intent.putExtra("typeID", grade.getType().getId());
            intent.putExtra("grade", AppSerializer.serialize(grade));
            startActivity(intent);
        }
    }

    @Override
    public void onItemDeleted(ListableObject object) { }

    @Override
    public void onItemEdit(ListableObject object) { }

    @Override
    public void onItemLongClicked(ListableObject object) { }
}
