package de.zitzmanncedric.abicalc.fragments.main;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.activities.settings.SettingsGoalsActivity;
import de.zitzmanncedric.abicalc.activities.settings.SettingsSeminarActivity;
import de.zitzmanncedric.abicalc.activities.settings.SettingsSubjectsActivity;
import de.zitzmanncedric.abicalc.adapter.SettingsListAdapter;
import de.zitzmanncedric.abicalc.api.settings.SettingsItem;
import needle.Needle;
import needle.UiRelatedProgressTask;

public class SettingsFragment extends Fragment implements SettingsListAdapter.Callback {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView list = view.findViewById(R.id.recyclerview_settings);

        ArrayList<SettingsItem> items = new ArrayList<>();
        items.add(new SettingsItem(R.string.settings_seminar, R.string.settings_seminar_desc, view.getContext().getDrawable(R.drawable.ic_student)));
        items.add(new SettingsItem(R.string.settings_subjects, R.string.settings_subjects_desc, view.getContext().getDrawable(R.drawable.ic_schedule)));
        items.add(new SettingsItem(R.string.settings_goals, R.string.settings_goals_desc, view.getContext().getDrawable(R.drawable.ic_award)));
        // TODO: Share option
        // items.add(new SettingsItem(R.string.settings_share, R.string.settings_share_desc, view.getContext().getDrawable(R.drawable.ic_share)));
        items.add(new SettingsItem(R.string.settings_privacy, R.string.settings_privacy_desc, view.getContext().getDrawable(R.drawable.ic_shield)));
        items.add(new SettingsItem(R.string.settings_reset, R.string.settings_reset_desc, view.getContext().getDrawable(R.drawable.ic_refresh)));

        SettingsListAdapter adapter = new SettingsListAdapter(items, this);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setAdapter(adapter);
    }

    @Override
    public void onItemClicked(SettingsItem item) {
        switch (item.getName()) {
            case R.string.settings_seminar:
                Intent semiSettings = new Intent(getContext(), SettingsSeminarActivity.class);
                startActivity(semiSettings);
                break;
            case R.string.settings_subjects:
                Intent subjects = new Intent(getContext(), SettingsSubjectsActivity.class);
                startActivity(subjects);
                break;
            case R.string.settings_goals:
                Intent goals = new Intent(getContext(), SettingsGoalsActivity.class);
                startActivity(goals);
                break;
            case R.string.settings_privacy:
                String url = "https://zitzmann-cedric.de/projects/abikalkulator/privacy/";
                Intent openUrl = new Intent(Intent.ACTION_VIEW);
                openUrl.setData(Uri.parse(url));
                startActivity(openUrl);
                break;
            case R.string.settings_reset:
                Intent info = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                info.setData(Uri.parse("package:" + AppCore.getInstance().getPackageName()));
                startActivity(info);
                break;
        }

    }
}
