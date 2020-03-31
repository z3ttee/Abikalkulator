package de.zitzmanncedric.abicalc.fragments.main;


import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.adapter.SubjectStrikeListAdapter;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.calculation.Average;
import de.zitzmanncedric.abicalc.dialogs.InfoDialog;

public class GoalsFragment extends Fragment implements View.OnClickListener {

    private RecyclerView listView;
    private TextView noticeView;

    private ImageView goalAvgIcon;
    private TextView goalAvgText;
    private ImageView goalPointsIcon;
    private TextView goalPointsText;

    private SubjectStrikeListAdapter adapter;
    private Context context;

    public GoalsFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.recyclerview_striked_subjects);
        noticeView = view.findViewById(R.id.goal_algorythm_notice);
        goalAvgIcon = view.findViewById(R.id.goal_goalavg_icon);
        goalAvgText = view.findViewById(R.id.goal_goalavg_text);
        goalPointsIcon = view.findViewById(R.id.goal_goalpoints_icon);
        goalPointsText = view.findViewById(R.id.goal_goalpoints_text);

        noticeView.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new SubjectStrikeListAdapter(getContext(), new ArrayList<>(4));
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(adapter);

        Average.getAllPoints((result -> {
            int goal = AppCore.getSharedPreferences().getInt("goalPoints", 660);
            if(result >= goal) {
                goalPointsIcon.setImageDrawable(context.getDrawable(R.drawable.ic_check));
                goalPointsIcon.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.colorSuccess)));
                goalPointsText.setText(getString(R.string.notice_goal_reached).replace("%goal%", goal +"Pkt."));
            } else {
                goalPointsIcon.setImageDrawable(context.getDrawable(R.drawable.ic_cross));
                goalPointsIcon.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.colorFailure)));
                goalPointsText.setText(getString(R.string.notice_goal_notreached).replace("%goal%", goal +"Pkt."));
            }
        }));
        Average.getGeneral((r -> {
            float goalValue = AppCore.getSharedPreferences().getFloat("goalAVG", 2.0f);
            String goal = String.valueOf(goalValue).substring(0, 3);
            String result = String.valueOf((float) r.doubleValue()).substring(0, 3);

            if(goal.equalsIgnoreCase(result) || r <= goalValue) {
                goalAvgIcon.setImageDrawable(context.getDrawable(R.drawable.ic_check));
                goalAvgIcon.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.colorSuccess)));
                goalAvgText.setText(getString(R.string.notice_goal_reached).replace("%goal%", goal));
            } else {
                goalAvgIcon.setImageDrawable(context.getDrawable(R.drawable.ic_cross));
                goalAvgIcon.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.colorFailure)));
                goalAvgText.setText(getString(R.string.notice_goal_notreached).replace("%goal%", goal));
            }
        }));

        Average.getStriked(result -> {
            for(Subject subject : result.values()){
                adapter.add(subject);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == noticeView.getId()) {
            InfoDialog dialog = new InfoDialog(context);
            dialog.setTitle(getString(R.string.headline_info));
            dialog.setMessage(R.string.paragraph_algorythm_dialog);
            dialog.show();
        }
    }
}
