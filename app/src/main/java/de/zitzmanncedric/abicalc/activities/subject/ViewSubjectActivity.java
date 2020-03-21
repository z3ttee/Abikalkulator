package de.zitzmanncedric.abicalc.activities.subject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.AppFragments;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.Grade;
import de.zitzmanncedric.abicalc.api.Seminar;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.Term;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.fragments.subject.GradesFragment;
import de.zitzmanncedric.abicalc.utils.AppSerializer;
import de.zitzmanncedric.abicalc.views.AppActionBar;

public class ViewSubjectActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ViewSubjectActivity";

    private ViewPager fragmentPager;
    private TabLayout tabLayout;
    private Subject subject;
    private int termID;

    private FloatingActionButton fab;

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

    private void reSetup(){
        fragmentPager.setAdapter(new Adapter(getSupportFragmentManager(), this, subject));
        tabLayout.setupWithViewPager(fragmentPager, true);
        new Handler().post(() -> fragmentPager.setCurrentItem(termID, false));
    }

    /**
     * Lädt Noten, wenn Aktivität fortgesetzt wird
     */
    @Override
    protected void onResume() {
        super.onResume();
        termID = AppCore.getSharedPreferences().getInt("currentTerm", 0);
    }

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

    private static class Adapter extends FragmentPagerAdapter {
        private static final String TAG = "Adapter";

        private ArrayList<String> titles;
        private Subject subject;

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

        @Override
        public int getCount() {
            return (subject.isExam() ? 5 : 4);
        }

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

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

}
