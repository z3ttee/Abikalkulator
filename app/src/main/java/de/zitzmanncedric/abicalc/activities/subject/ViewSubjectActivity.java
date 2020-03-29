package de.zitzmanncedric.abicalc.activities.subject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.Grade;
import de.zitzmanncedric.abicalc.api.Seminar;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.fragments.subject.GradesFragment;
import de.zitzmanncedric.abicalc.utils.AppSerializer;
import de.zitzmanncedric.abicalc.views.AppActionBar;

/**
 * Klasse zur Behandlung der Notenübersicht eines Fachs
 * @author Cedric Zitzmann
 */
public class ViewSubjectActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager fragmentPager;
    private TabLayout tabLayout;
    private Subject subject;
    private int termID;

    private FloatingActionButton fab;

    /**
     * Von Android implementiert. Methode zum Aufbauen des Fensters
     * @param savedInstanceState Von Android übergeben (nicht genutzt)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_subject);

        fragmentPager = findViewById(R.id.app_fragment_pager);
        tabLayout = findViewById(R.id.app_tabbar);

        AppActionBar actionBar = findViewById(R.id.app_toolbar);
        actionBar.setShowClose(true);
        actionBar.getCloseView().setOnClickListener(this);

        Intent intent = getIntent();
        termID = intent.getIntExtra("termID", 0);
        byte[] bytes = intent.getByteArrayExtra("subject");

        if(bytes != null) {
            subject = (Subject) AppSerializer.deserialize(bytes);
        }

        fab = findViewById(R.id.app_fab);
        fab.setColorFilter(getColor(android.R.color.white));
        fab.setOnClickListener(this);

        reSetup();
    }

    /**
     * Fängt alle Klick-Events im Fenster ab. Das Fenster wird beim Klick auf "Zurück" in der Toolbar geschlossen. Wird auf "Hinzufügen" geklickt, so öffnet sich der Noteneditor und eine neue Note kann erstellt werden.
     * @param v Angeklickter Button
     */
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_close) {
            finish();
            return;
        }
        if(v.getId() == fab.getId()) {
            Intent intent = new Intent(this, GradeEditorActivity.class);
            intent.putExtra("action", "add");
            intent.putExtra("subjectID", subject.getId());
            intent.putExtra("termID", tabLayout.getSelectedTabPosition());
            startActivityForResult(intent, AppCore.RequestCodes.REQUEST_ADD_GRADE);
        }
    }

    /**
     * Sorgt für das erneute Einrichten der Notenansicht
     */
    private void reSetup(){
        fragmentPager.setAdapter(new Adapter(getSupportFragmentManager(), this, subject));
        tabLayout.setupWithViewPager(fragmentPager, true);
        new Handler().post(() -> {
            fragmentPager.setCurrentItem(termID, true);
        });
    }

    /**
     * Von Android implementiert. Fängt das Resultat durch eine geschlossene Aktivität ab. Bei Erfolg werden betreffende Bereiche aktualisiert.
     * @param requestCode Code, zur Identifizierung der Anfrage
     * @param resultCode Code, zur Identifizierung des Resultats
     * @param data Zurückgegebene Daten
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null) {
            if(requestCode == AppCore.RequestCodes.REQUEST_ADD_GRADE) {
                byte[] bytes = data.getByteArrayExtra("grade");
                Grade grade = (Grade) AppSerializer.deserialize(bytes);

                if (grade != null) {
                    for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                        if (fragment instanceof GradesFragment) {
                            GradesFragment f = (GradesFragment) fragment;
                            if (f.getTermID() == grade.getTermID()) {
                                f.onActivityResult(requestCode, resultCode, data);
                            }
                        }
                    }
                }
            }
            if(requestCode == AppCore.RequestCodes.REQUEST_UPDATE_GRADE) {
                byte[] bytes = data.getByteArrayExtra("oldGrade");
                Grade grade = (Grade) AppSerializer.deserialize(bytes);

                if (grade != null) {
                    for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                        if (fragment instanceof GradesFragment) {
                            GradesFragment f = (GradesFragment) fragment;
                            if(grade.getSubjectID() == Seminar.getInstance().getSubjectID()) {
                                f.onActivityResult(requestCode, resultCode, data);
                                return;
                            }
                            if (f.getTermID() == grade.getTermID()) {
                                f.onActivityResult(requestCode, resultCode, data);
                            }

                        }
                    }
                }
            }
        }

    }

    /**
     * Adapter der Liste. Sorgt für die Darstellung der Übersicht im ViewPager in Verbindung mit der Tabbar
     */
    private static class Adapter extends FragmentPagerAdapter {

        private ArrayList<String> titles;
        private Subject subject;

        /**
         * Konstruktor der Klasse. Lädt die Titel aller Elemente der Tabbar
         * @param fm FragmentManager zum Verwalten der Fragmente
         * @param context Context, zum Laden der Strings aus den App-Resourcen
         * @param subject Betreffendes Fach
         */
        Adapter(@NonNull FragmentManager fm, Context context, Subject subject) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.subject = subject;

            titles = new ArrayList<>(Arrays.asList(
                    context.getString(R.string.exp_term).replace("%", String.valueOf(1)),
                    context.getString(R.string.exp_term).replace("%", String.valueOf(2)),
                    context.getString(R.string.exp_term).replace("%", String.valueOf(3)),
                    context.getString(R.string.exp_term).replace("%", String.valueOf(4)),
                    context.getString(R.string.exp_abi)
            ));
        }

        /**
         * Gibt die Anzahl der Halbjahre an, um diese in der Tabbar anwählbar zu machen
         * @return Anzahl als Integer
         */
        @Override
        public int getCount() {
            return (subject.isExam() && Seminar.getInstance().getReplacedSubjectID() != subject.getId() ? 5 : 4);
        }

        /**
         * Ermittelt die Notenübersicht eines angewählten Halbjahres
         * @param position Angeklickte Position in der Tabbar oder ausgewählte Position durch den ViewPager
         * @return Fragment
         */
        @NonNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new GradesFragment(subject, 0);
            switch (position) {
                case 1:
                    fragment = new GradesFragment(subject, 1);
                    break;
                case 2:
                    fragment = new GradesFragment(subject, 2);
                    break;
                case 3:
                    fragment = new GradesFragment(subject, 3);
                    break;
                case 4:
                    fragment = new GradesFragment(subject, 4);
                    break;
            }

            return fragment;
        }

        /**
         * Ermittelt den Seitentitel eines Halbjahres zum Anzeigen in der Tabbar
         * @param position Position in der Reihenfolge der Fragmente
         * @return Titel als CharSequence
         */
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

}
