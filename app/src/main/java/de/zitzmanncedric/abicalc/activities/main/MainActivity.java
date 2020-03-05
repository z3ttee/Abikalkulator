package de.zitzmanncedric.abicalc.activities.main;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.AppFragments;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.activities.subject.AddGradeActivity;
import de.zitzmanncedric.abicalc.fragments.main.GoalsFragment;
import de.zitzmanncedric.abicalc.fragments.main.OverviewFragment;
import de.zitzmanncedric.abicalc.fragments.main.SettingsFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private FrameLayout fragmentContainer;

    private BottomAppBar bottomAppBar;
    private FloatingActionButton fab;

    private ImageButton homeBtn, goalsBtn, settingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentContainer = findViewById(R.id.app_fragment_container);
        bottomAppBar = findViewById(R.id.app_bottom_bar);
        fab = findViewById(R.id.app_fab);
        homeBtn = findViewById(R.id.menu_home);
        goalsBtn = findViewById(R.id.menu_goals);
        settingsBtn = findViewById(R.id.menu_settings);

        setSupportActionBar(bottomAppBar);

        homeBtn.setColorFilter(getColor(R.color.colorAccent));
        fab.setColorFilter(getColor(android.R.color.white));

        AppFragments.replaceFragment(getSupportFragmentManager(), fragmentContainer, new OverviewFragment(), true, null, 0, R.anim.fragment_scaleout);
    }

    public void goHome(View view) {
        if(getSupportFragmentManager().getFragments().get(0) instanceof OverviewFragment) return;

        homeBtn.setColorFilter(getColor(R.color.colorAccent));
        goalsBtn.setColorFilter(getColor(R.color.colorPrimaryDark));
        settingsBtn.setColorFilter(getColor(R.color.colorPrimaryDark));

        AppFragments.replaceFragment(getSupportFragmentManager(), fragmentContainer, new OverviewFragment(), true, null, R.anim.fragment_scalein, R.anim.fragment_scaleout);
    }
    public void goGoals(View view) {
        if(getSupportFragmentManager().getFragments().get(0) instanceof GoalsFragment) return;

        goalsBtn.setColorFilter(getColor(R.color.colorAccent));
        homeBtn.setColorFilter(getColor(R.color.colorPrimaryDark));
        settingsBtn.setColorFilter(getColor(R.color.colorPrimaryDark));

        AppFragments.replaceFragment(getSupportFragmentManager(), fragmentContainer, new GoalsFragment(), true, null, R.anim.fragment_scalein, R.anim.fragment_scaleout);
    }
    public void goSettings(View view) {
        if(getSupportFragmentManager().getFragments().get(0) instanceof SettingsFragment) return;

        settingsBtn.setColorFilter(getColor(R.color.colorAccent));
        homeBtn.setColorFilter(getColor(R.color.colorPrimaryDark));
        goalsBtn.setColorFilter(getColor(R.color.colorPrimaryDark));

        AppFragments.replaceFragment(getSupportFragmentManager(), fragmentContainer, new SettingsFragment(), true, null, R.anim.fragment_scalein, R.anim.fragment_scaleout);
    }

    public void addGrade(View view) {
        Intent intent = new Intent(this, AddGradeActivity.class);
        startActivityForResult(intent, AppCore.RequestCodes.REQUEST_ADD_GRADE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Note hinzufügen
        if(requestCode == AppCore.RequestCodes.REQUEST_ADD_GRADE) {
            if(resultCode == AppCore.ResultCodes.RESULT_OK){
                // Note wurde hinzugefügt
                // TODO: Show something?
            }

        }
        // Toast.makeText(this, resultCode, Toast.LENGTH_SHORT).show();
    }
}
