package de.zitzmanncedric.abicalc.fragments.main;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

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
 * Teil des Hauptbildschirms. Zeigt die Übersicht über alle Fächer an, sowie voraussichtlicher Abiturschnitt
 * @author Cedric Zitzmann
 */
public class OverviewFragment extends Fragment implements OnListItemCallback, TabLayout.OnTabSelectedListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private RecyclerView intensifiedView;
    private RecyclerView normalsView;
    private RecyclerView seminarView;

    private List<Subject> intensified = new ArrayList<>();
    private List<Subject> normals = new ArrayList<>();
    private List<? extends ListableObject> seminar = new ArrayList<>(Collections.singletonList(Seminar.getInstance()));

    private int termID = 0;

    /**
     * Standard-Konstruktor der Klasse (wird benötigt durch die Erbung von Fragment)
     */
    public OverviewFragment() {}

    /**
     * Fragment wird aufgebaut und Informationen werden geladen
     * @param inflater Von Android übergeben
     * @param container
     * @param savedInstanceState
     * @return Gibt das erstellte Element zurück
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        intensifiedView = view.findViewById(R.id.app_grid_intensified);
        normalsView = view.findViewById(R.id.app_grid_normals);
        seminarView = view.findViewById(R.id.app_grid_seminar);

        tabLayout = view.findViewById(R.id.app_tabbar);
        viewPager = view.findViewById(R.id.app_viewpager);

        {
            RecyclerGridAdapter adapter = new RecyclerGridAdapter(view.getContext(), intensified);
            adapter.setItemCallback(this);
            intensifiedView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
            intensifiedView.setAdapter(adapter);
        }
        {
            RecyclerGridAdapter adapter = new RecyclerGridAdapter(view.getContext(), seminar);
            adapter.setItemCallback(this);
            seminarView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
            seminarView.setAdapter(adapter);
        }
        {
            RecyclerGridAdapter adapter = new RecyclerGridAdapter(view.getContext(), normals);
            adapter.setItemCallback(this);
            normalsView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
            normalsView.setAdapter(adapter);
        }

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        populate();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void populate() {
        // Populate lists async
        new Handler().post(() -> {
            for(Subject subject : AppDatabase.getInstance().getUserSubjects()) {
                if(subject.isIntensified()) {
                    intensified.add(subject);
                } else {
                    normals.add(subject);
                }
            }
        });
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
    public void onItemDeleted(int position) { }

    /**
     * Nicht benötigt (implementiert durch OnListItemCallback
     */
    @Override
    public void onItemEdit(int position) { }

    /**
     * Nicht benötigt (implementiert durch OnListItemCallback
     */
    @Override
    public void onItemClicked(int position) { }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        // TODO
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) { }

    @Override
    public void onTabReselected(TabLayout.Tab tab) { }
}
