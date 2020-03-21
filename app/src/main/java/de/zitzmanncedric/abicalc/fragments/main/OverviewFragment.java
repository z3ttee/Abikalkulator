package de.zitzmanncedric.abicalc.fragments.main;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.activities.subject.ViewSubjectActivity;
import de.zitzmanncedric.abicalc.adapter.RecyclerGridAdapter;
import de.zitzmanncedric.abicalc.api.Seminar;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.fragments.subject.GradesFragment;
import de.zitzmanncedric.abicalc.fragments.subject.SubjectsFragment;
import de.zitzmanncedric.abicalc.listener.OnListItemCallback;
import de.zitzmanncedric.abicalc.utils.AppSerializer;
import de.zitzmanncedric.abicalc.views.AverageView;

/**
 * Teil des Hauptbildschirms. Zeigt die Übersicht über alle Fächer an, sowie voraussichtlicher Abiturschnitt
 * @author Cedric Zitzmann
 */
public class OverviewFragment extends Fragment {
    private static final String TAG = "OverviewFragment";

    private ViewPager fragmentPager;
    private AverageView averageView;

    /**
     * Standard-Konstruktor der Klasse (wird benötigt durch die Erbung von Fragment)
     */
    public OverviewFragment() {}

    /**
     * Fragment wird aufgebaut
     * @param inflater Von Android übergeben
     * @param container Von Android übergeben
     * @param savedInstanceState Von Android übergeben
     * @return Gibt das erstellte Element zurück
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        /*TabLayout tabLayout = view.findViewById(R.id.app_tabbar);
        fragmentPager = view.findViewById(R.id.app_fragment_pager);

        fragmentPager.setAdapter(new Adapter(getChildFragmentManager(), view.getContext()));
        tabLayout.setupWithViewPager(fragmentPager);

        int currentTerm = AppCore.getSharedPreferences().getInt("currentTerm", 0);
        new Handler().post(() -> {
            fragmentPager.setCurrentItem(currentTerm, true);
        });*/
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //setup();
    }

    private void setup() {
        TabLayout tabLayout = getView().findViewById(R.id.app_tabbar);
        fragmentPager = getView().findViewById(R.id.app_fragment_pager);
        averageView = getView().findViewById(R.id.app_averageview);

        fragmentPager.setAdapter(new Adapter(getChildFragmentManager(), getView().getContext()));
        tabLayout.setupWithViewPager(fragmentPager);

        int currentTerm = AppCore.getSharedPreferences().getInt("currentTerm", 0);
        new Handler().post(() -> {
            fragmentPager.setCurrentItem(currentTerm, true);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setup();
        averageView.recalculate();
        /*int currentTerm = AppCore.getSharedPreferences().getInt("currentTerm", 0);
        new Handler().post(() -> {
            fragmentPager.setCurrentItem(currentTerm, true);
        });*/

    }

    private static class Adapter extends FragmentPagerAdapter {
        private static final String TAG = "Adapter";

        private ArrayList<String> titles;

        Adapter(@NonNull FragmentManager fm, Context context) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

            titles = new ArrayList<>(Arrays.asList(
                    context.getString(R.string.exp_term).replace("%", String.valueOf(1)),
                    context.getString(R.string.exp_term).replace("%", String.valueOf(2)),
                    context.getString(R.string.exp_term).replace("%", String.valueOf(3)),
                    context.getString(R.string.exp_term).replace("%", String.valueOf(4)),
                    context.getString(R.string.exp_abi)
            ));
        }

        @Override
        public int getCount() {
            return 5;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            Fragment fragment = new SubjectsFragment(0);
            switch (position) {
                case 1:
                    fragment = new SubjectsFragment(1);
                    break;
                case 2:
                    fragment = new SubjectsFragment(2);
                    break;
                case 3:
                    fragment = new SubjectsFragment(3);
                    break;
                case 4:
                    fragment = new SubjectsFragment(4);
                    break;
            }

            return fragment;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AppCore.RequestCodes.REQUEST_ADD_GRADE){
            if(resultCode == AppCore.ResultCodes.RESULT_OK) {
                setup();
            }
        }
    }
}
