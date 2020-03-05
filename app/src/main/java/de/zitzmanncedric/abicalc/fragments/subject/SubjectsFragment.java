package de.zitzmanncedric.abicalc.fragments.subject;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.activities.subject.ViewSubjectActivity;
import de.zitzmanncedric.abicalc.adapter.RecyclerGridAdapter;
import de.zitzmanncedric.abicalc.api.Seminar;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.listener.OnListItemCallback;
import de.zitzmanncedric.abicalc.utils.AppSerializer;

/**
 * Teil des Hauptbildschirms. Zeigt die Übersicht über alle Fächer eines Halbjahres an
 * @author Cedric Zitzmann
 */
public class SubjectsFragment extends Fragment implements OnListItemCallback {
    private static final String TAG = "SubjectsFragment";

    private int termID;

    private RecyclerView intensifiedView;
    private RecyclerView basicsView;
    private RecyclerView seminarView;

    public SubjectsFragment() {}
    public SubjectsFragment(int termID) {
        this.termID = termID;
    }

    /**
     * Fragment wird aufgebaut und Informationen werden geladen
     * @param inflater Von Android übergeben
     * @param container Von Android übergeben
     * @param savedInstanceState Von Android übergeben
     * @return Gibt das erstellte Element zurück
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subjects, container, false);

        Seminar seminar = Seminar.getInstance();
        if(termID != 4) {
            seminar.setAside("--");
        }

        ArrayList<? extends ListableObject> intensifiedDummy = new ArrayList<>();
        ArrayList<? extends ListableObject> basicsDummy = new ArrayList<>();

        intensifiedView = view.findViewById(R.id.app_grid_intensified);
        basicsView = view.findViewById(R.id.app_grid_basics);
        seminarView = view.findViewById(R.id.app_grid_seminar);

        RecyclerGridAdapter intensifiedAdapter = new RecyclerGridAdapter(view.getContext(), intensifiedDummy);
        RecyclerGridAdapter basicsAdapter = new RecyclerGridAdapter(view.getContext(), basicsDummy);

        intensifiedAdapter.setItemCallback(this);
        intensifiedView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
        intensifiedView.setAdapter(intensifiedAdapter);

        basicsAdapter.setItemCallback(this);
        basicsView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
        basicsView.setAdapter(basicsAdapter);

        {
            RecyclerGridAdapter adapter = new RecyclerGridAdapter(view.getContext(), Collections.singletonList(seminar));
            adapter.setItemCallback(this);
            seminarView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
            seminarView.setAdapter(adapter);
        }

        new Handler().post(() -> {
            ArrayList<Subject> intensified = new ArrayList<>();
            ArrayList<Subject> basics = new ArrayList<>();

            for(Subject subject : AppDatabase.getInstance().getUserSubjects()) {
                if(termID == 4) {
                    if(subject.isIntensified() && subject.isExam()) {
                        intensified.add(subject);
                    } else if(subject.isExam()) {
                        basics.add(subject);
                    }
                } else {
                    if(subject.isIntensified()) {
                        intensified.add(subject);
                    } else {
                        basics.add(subject);
                    }
                }

            }

            intensifiedAdapter.update(intensified);
            basicsAdapter.update(basics);
        });
        return view;
    }

    /**
     * Öffnet eine Aktivität (Übersicht über Noten dieses Kurses) wenn auf einen Kurs geklickt wurde
     * @param object Übergibt das Fach, auf welches geklickt wurde
     */
    @Override
    public void onItemClicked(ListableObject object) {
        Intent intent = new Intent(getContext(), ViewSubjectActivity.class);

        if(object instanceof Subject) {
            Subject subject = (Subject) object;
            intent.putExtra("subject", AppSerializer.serialize(subject));
            intent.putExtra("termID", termID);
        }

        startActivity(intent);
    }

    /**
     * Öffnet eine Aktivität (Übersicht über Noten dieses Kurses) wenn auf einen Kurs geklickt wurde
     * @param object Übergibt das Objekt des Fachs in der Liste
     */
    @Override
    public void onItemLongClicked(ListableObject object) {
        Toast.makeText(getContext(), "TODO: Show Sheet with options", Toast.LENGTH_SHORT).show();
        // TODO: Show menu with options
    }

    /**
     * Nicht benötigt (implementiert durch OnListItemCallback
     */
    @Override
    public void onItemClicked(int position) { }

    /**
     * Nicht benötigt (implementiert durch OnListItemCallback
     */
    @Override
    public void onItemDeleted(int position) { }

    /**
     * Nicht benötigt (implementiert durch OnListItemCallback
     */
    @Override
    public void onItemEdit(int position) { }
}
