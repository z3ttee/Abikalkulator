package de.zitzmanncedric.abicalc.fragments.main;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.activities.main.MainActivity;
import de.zitzmanncedric.abicalc.api.Grade;
import de.zitzmanncedric.abicalc.fragments.subject.SubjectsFragment;
import de.zitzmanncedric.abicalc.utils.AppSerializer;
import de.zitzmanncedric.abicalc.views.AverageView;

/**
 * Teil des Hauptbildschirms. Zeigt die Übersicht über alle Fächer an, sowie voraussichtlicher Abiturschnitt
 * @author Cedric Zitzmann
 */
public class OverviewFragment extends Fragment {

    private ViewPager fragmentPager;
    private AverageView averageView;
    private ProgressBar progressBar;
    private TabLayout tabLayout;

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

        fragmentPager = view.findViewById(R.id.app_fragment_pager);
        tabLayout = view.findViewById(R.id.app_tabbar);
        averageView = view.findViewById(R.id.app_averageview);
        progressBar = view.findViewById(R.id.app_progressbar);

        progressBar.animate().alpha(1f).setDuration(AppCore.getInstance().getResources().getInteger(R.integer.anim_speed_quickly));

        new Handler().postDelayed(() -> {
            fragmentPager.setAdapter(new Adapter(getChildFragmentManager(), view.getContext()));
            tabLayout.setupWithViewPager(fragmentPager);
            fragmentPager.setOffscreenPageLimit(5);
            int currentTerm = AppCore.getSharedPreferences().getInt("currentTerm", 0);
            fragmentPager.setCurrentItem(currentTerm, false);

            averageView.recalculate(() -> progressBar.animate().alpha(0f).setDuration(AppCore.getInstance().getResources().getInteger(R.integer.anim_speed_quickly)).setStartDelay(50));
        }, getResources().getInteger(R.integer.anim_speed)-10);
        return view;
    }

    /**
     * Beim Fortsetzen der Aktivität wird der Notendurchschnitt neu berechnet
     */
    @Override
    public void onResume() {
        super.onResume();
        averageView.recalculate(() -> { });
    }

    /**
     * Adapterklasse, um zwischen einzelnen Halbjahren zu wechseln
     */
    private static class Adapter extends FragmentPagerAdapter {
        private ArrayList<String> titles;

        /**
         * Konstruktor der Klasse
         * @param fm Von Android benötigt
         * @param context Von Android benötigt
         */
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

        /**
         * Gibt die Zahl der Halbjahre zurück
         * @return Integer (5)
         */
        @Override
        public int getCount() {
            return 5;
        }

        /**
         * Gibt das Fragment zurück, welches ausgewählt wurde
         * @param position Position in Reihenfolge
         * @return Ausgewähltes Fragment
         */
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

        /**
         * Gibt den Titel des Fragments zurück, um es in der Tabbar anzuzeigen
         * @param position Position in Reihenfolge
         * @return Titel des Fragments
         */
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    /**
     * Registriert, ob einen neue Note über das Hauptfenster hinzugefügt wurde. Leitet das Resultat an das zugehörige Fragment weiter, um das Element in der Liste zu aktualisieren
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

                        for (Fragment fragment : getChildFragmentManager().getFragments()) {
                            if (fragment instanceof SubjectsFragment) {
                                SubjectsFragment f = (SubjectsFragment) fragment;
                                f.onActivityResult(requestCode, resultCode, data);
                            }
                        }

                        fragmentPager.setCurrentItem(grade.getTermID(), false);
                        averageView.recalculate(() -> progressBar.animate().alpha(0f).setDuration(AppCore.getInstance().getResources().getInteger(R.integer.anim_speed_quickly)).setStartDelay(50));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        if (requestCode == AppCore.RequestCodes.REQUEST_VIEW_SUBJECT) {
            for (Fragment fragment : getChildFragmentManager().getFragments()) {
                if (fragment instanceof SubjectsFragment) {
                    SubjectsFragment f = (SubjectsFragment) fragment;
                    f.onActivityResult(AppCore.RequestCodes.REQUEST_UPDATE_VIEWS, resultCode, null);
                }
            }
        }

        if(requestCode == AppCore.RequestCodes.REQUEST_UPDATE_SCHEDULE) {
            try {
                ((MainActivity) getActivity()).onActivityResult(requestCode, resultCode, data);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
