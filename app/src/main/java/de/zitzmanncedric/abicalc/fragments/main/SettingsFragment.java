package de.zitzmanncedric.abicalc.fragments.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.dialogs.ConfirmDialog;
import de.zitzmanncedric.abicalc.utils.AppUtils;
import de.zitzmanncedric.abicalc.BuildConfig;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.activities.SplashActivity;
import de.zitzmanncedric.abicalc.activities.settings.SettingsGoalsActivity;
import de.zitzmanncedric.abicalc.activities.settings.SettingsSeminarActivity;
import de.zitzmanncedric.abicalc.activities.settings.SettingsSubjectsActivity;
import de.zitzmanncedric.abicalc.adapter.SettingsListAdapter;
import de.zitzmanncedric.abicalc.api.settings.SettingsItem;
import de.zitzmanncedric.abicalc.dialogs.ProgressDialog;
import needle.Needle;
import needle.UiRelatedTask;

public class SettingsFragment extends Fragment implements SettingsListAdapter.Callback {

    private Context context;
    public SettingsFragment(Context context) {
        this.context = context;
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
        items.add(new SettingsItem(R.string.settings_share, R.string.settings_share_desc, view.getContext().getDrawable(R.drawable.ic_share)));
        items.add(new SettingsItem(R.string.settings_privacy, R.string.settings_privacy_desc, view.getContext().getDrawable(R.drawable.ic_shield)));
        items.add(new SettingsItem(R.string.settings_reset, R.string.settings_reset_desc, view.getContext().getDrawable(R.drawable.ic_refresh)));

        SettingsListAdapter adapter = new SettingsListAdapter(items, this);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setAdapter(adapter);

        TextView appInfoView = view.findViewById(R.id.app_info);
        try {
            appInfoView.setText( getString(R.string.app_info).replace("%version%", AppCore.getInstance().getPackageManager().getPackageInfo(AppCore.getInstance().getPackageName(), 0).versionName));
        } catch (Exception ignored) { }
    }

    @Override
    public void onItemClicked(SettingsItem item) {
        switch (item.getName()) {
            case R.string.settings_seminar:
                Intent semiSettings = new Intent(context, SettingsSeminarActivity.class);
                startActivity(semiSettings);
                break;
            case R.string.settings_subjects:
                Intent subjects = new Intent(context, SettingsSubjectsActivity.class);
                startActivity(subjects);
                break;
            case R.string.settings_goals:
                Intent goals = new Intent(context, SettingsGoalsActivity.class);
                startActivity(goals);
                break;
            case R.string.settings_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.paragraph_sharewith).replace("%url%", "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.string.settings_privacy:
                String url = "https://zitzmann-cedric.de/projects/abikalkulator/privacy/";
                Intent openUrl = new Intent(Intent.ACTION_VIEW);
                openUrl.setData(Uri.parse(url));
                startActivity(openUrl);
                break;
            case R.string.settings_reset:
                ConfirmDialog dialog = new ConfirmDialog(context);
                dialog.setTitle(R.string.headline_yousure);
                dialog.setMessage(R.string.notice_app_data_will_resetted);
                dialog.setBanner(R.drawable.ic_undraw_questions, (int) getResources().getDimension(R.dimen.default_banner_height));
                dialog.setCallback((button) -> {
                    if(button == dialog.getButtonPositive()){
                        dialog.dismiss();
                        reset();
                    } else {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
        }

    }

    /**
     * Funktion zum Behandeln des Zurücksetzens der App
     */
    private void reset(){
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(getString(R.string.notice_app_beingresetted));
        dialog.show();

        Needle.onBackgroundThread().execute(new UiRelatedTask<Void>() {
            @Override
            protected Void doWork() {
                AppUtils.resetAppSettings();
                return null;
            }

            @Override
            protected void thenDoUiRelatedWork(Void aVoid) {
                new Handler().postDelayed(() -> {
                    dialog.dismiss();
                    Intent intent = new Intent(context, SplashActivity.class);
                    startActivity(intent);
                    if(getActivity() != null){
                        getActivity().finish();
                    }
                }, 500);
            }
        });
    }
}
