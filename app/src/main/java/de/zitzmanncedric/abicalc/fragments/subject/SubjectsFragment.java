package de.zitzmanncedric.abicalc.fragments.subject;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.activities.subject.SeminarActivity;
import de.zitzmanncedric.abicalc.activities.subject.ViewSubjectActivity;
import de.zitzmanncedric.abicalc.adapter.RecyclerGridAdapter;
import de.zitzmanncedric.abicalc.api.Seminar;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.listener.OnListItemCallback;
import de.zitzmanncedric.abicalc.utils.AppSerializer;
import needle.Needle;
import needle.UiRelatedProgressTask;
import needle.UiRelatedTask;

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

    private RecyclerGridAdapter intensifiedAdapter;
    private RecyclerGridAdapter basicsAdapter;

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

        ArrayList<ListableObject> intensifiedDummy = new ArrayList<>();
        ArrayList<ListableObject> basicsDummy = new ArrayList<>();

        intensifiedView = view.findViewById(R.id.app_grid_intensified);
        basicsView = view.findViewById(R.id.app_grid_basics);
        seminarView = view.findViewById(R.id.app_grid_seminar);

        intensifiedAdapter = new RecyclerGridAdapter(view.getContext(), intensifiedDummy);
        basicsAdapter = new RecyclerGridAdapter(view.getContext(), basicsDummy);

        intensifiedAdapter.setItemCallback(this);
        intensifiedView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
        intensifiedView.setAdapter(intensifiedAdapter);

        basicsAdapter.setItemCallback(this);
        basicsView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
        basicsView.setAdapter(basicsAdapter);

        {
            RecyclerGridAdapter adapter = new RecyclerGridAdapter(view.getContext(), new ArrayList<>(Collections.singletonList(seminar)));
            adapter.setItemCallback(this);
            seminarView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
            seminarView.setAdapter(adapter);
        }

        Needle.onBackgroundThread().withThreadPoolSize(1).execute(new UiRelatedProgressTask<ArrayList<Subject>, Subject>() {
            @Override
            protected ArrayList<Subject> doWork() {
                ArrayList<Subject> elements = new ArrayList<>();

                for(Subject subject : AppDatabase.getInstance().getUserSubjects()) {
                    if(termID != 4) {
                        elements.add(subject);
                        publishProgress(subject);
                    } else {
                        if(subject.isExam()) {
                            elements.add(subject);
                            publishProgress(subject);
                        }
                    }

                    try {
                        Thread.sleep(100);
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
                return elements;
            }

            @Override
            protected void onProgressUpdate(Subject subject) {
                if(subject.isIntensified()) intensifiedAdapter.add(subject);
                else basicsAdapter.add(subject);
            }

            @Override
            protected void thenDoUiRelatedWork(ArrayList<Subject> arrayList) { }
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

        if(object instanceof Seminar) {
            intent = new Intent(getContext(), SeminarActivity.class);
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
    public void onItemDeleted(ListableObject object) { }

    /**
     * Nicht benötigt (implementiert durch OnListItemCallback
     */
    @Override
    public void onItemEdit(ListableObject object) { }
}
