package de.zitzmanncedric.abicalc.fragments.subject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.activities.subject.SeminarActivity;
import de.zitzmanncedric.abicalc.activities.subject.ViewSubjectActivity;
import de.zitzmanncedric.abicalc.adapter.RecyclerGridAdapter;
import de.zitzmanncedric.abicalc.api.Grade;
import de.zitzmanncedric.abicalc.api.Seminar;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.calculation.Average;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.listener.OnListItemCallback;
import de.zitzmanncedric.abicalc.utils.AppSerializer;
import lombok.Getter;
import needle.Needle;
import needle.UiRelatedProgressTask;

/**
 * Teil des Hauptbildschirms. Zeigt die Übersicht über alle Fächer eines Halbjahres an
 * @author Cedric Zitzmann
 */
public class SubjectsFragment extends Fragment implements OnListItemCallback {
    private static final String TAG = "SubjectsFragment";

    @Getter private int termID;

    private RecyclerGridAdapter intensifiedAdapter;
    private RecyclerGridAdapter basicsAdapter;
    private RecyclerGridAdapter seminarAdapter;

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

        Seminar.getInstance().setAside(String.valueOf(Average.getSeminarSync()));

        RecyclerView intensifiedView = view.findViewById(R.id.app_grid_intensified);
        RecyclerView basicsView = view.findViewById(R.id.app_grid_basics);
        RecyclerView seminarView = view.findViewById(R.id.app_grid_seminar);

        intensifiedAdapter = new RecyclerGridAdapter(getContext(), termID, new ArrayList<>(5));
        basicsAdapter = new RecyclerGridAdapter(getContext(), termID, new ArrayList<>(6));

        intensifiedAdapter.setItemCallback(this);
        intensifiedView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        intensifiedView.setAdapter(intensifiedAdapter);

        basicsAdapter.setItemCallback(this);
        basicsView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        basicsView.setAdapter(basicsAdapter);

        seminarAdapter = new RecyclerGridAdapter(getContext(), termID, new ArrayList<>(new ArrayList<>(Collections.singleton(Seminar.getInstance()))));
        seminarAdapter.setItemCallback(this);
        seminarView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        seminarView.setAdapter(seminarAdapter);

        Needle.onBackgroundThread().withThreadPoolSize(2).execute(new UiRelatedProgressTask<Void, Subject>() {
            @Override
            protected Void doWork() {
                for (Subject subject : AppDatabase.getInstance().getUserSubjects()) {
                    if (termID != 4) {
                        publishProgress(subject);
                    } else {
                        if (subject.isExam() && Seminar.getInstance().getReplacedSubjectID() != subject.getId()) {
                            publishProgress(subject);
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Subject subject) {
                if (subject.isIntensified()) intensifiedAdapter.add(subject);
                else basicsAdapter.add(subject);
            }

            @Override
            protected void thenDoUiRelatedWork(Void v) { }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        seminarAdapter.update(Seminar.getInstance(), Seminar.getInstance());
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

        startActivityForResult(intent, AppCore.RequestCodes.REQUEST_VIEW_SUBJECT);
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

    /**
     * Prüft, ob über das Hauptfenster eine Notehinzugefügt wurde. Bei Bedarf wird das passende ELemente in der Liste aktualisiert
     * @param requestCode Code zur Identifizierung der Anfrage
     * @param resultCode Code zur Identifizierung des Ergebnistyps
     * @param data Datenobjekt, welches die hinzugefügte Note enthält
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null) {

            if (requestCode == AppCore.RequestCodes.REQUEST_ADD_GRADE) {
                if (resultCode == AppCore.ResultCodes.RESULT_OK) {
                    try {
                        Grade grade = (Grade) AppSerializer.deserialize(data.getByteArrayExtra("grade"));
                        Subject subject = AppDatabase.getInstance().getUserSubjectByID(grade.getSubjectID());

                        if(subject.isIntensified()) {
                            intensifiedAdapter.update(subject, subject);
                        } else {
                            basicsAdapter.update(subject, subject);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        if(requestCode == AppCore.RequestCodes.REQUEST_VIEW_SUBJECT) {
            Fragment parent = getParentFragment();
            if(parent != null) {
                parent.onActivityResult(requestCode, resultCode, data);
            }
        }

        if(requestCode == AppCore.RequestCodes.REQUEST_UPDATE_VIEWS) {
            intensifiedAdapter.notifyDataSetChanged();
            basicsAdapter.notifyDataSetChanged();

            Seminar.getInstance().setAside(String.valueOf(Average.getSeminarSync()));
            seminarAdapter.set(new ArrayList<>(Collections.singletonList(Seminar.getInstance())));
        }
    }
}
