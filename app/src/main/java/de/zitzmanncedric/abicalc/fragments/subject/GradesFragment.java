package de.zitzmanncedric.abicalc.fragments.subject;

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
import de.zitzmanncedric.abicalc.adapter.AdvancedSubjectListAdapter;
import de.zitzmanncedric.abicalc.api.Grade;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.database.AppDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class GradesFragment extends Fragment {
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
        AdvancedSubjectListAdapter adapter = new AdvancedSubjectListAdapter(view.getContext(), this.dataset);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        new Handler().post(() -> {
            ArrayList<? extends ListableObject> grades = AppDatabase.getInstance().getGradesForTerm(subject, termID);
            adapter.update(grades);
        });

        return view;
    }
}
