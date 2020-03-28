package de.zitzmanncedric.abicalc.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.calculation.Average;

public class AverageView extends LinearLayout {
    private static final String TAG = "AverageView";

    private TextView subtitle;
    private TextView amount;
    private ImageView goal;

    public AverageView(Context context) {
        super(context);
        init(context, null);
    }

    public AverageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AverageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater != null) {
            View view = inflater.inflate(R.layout.view_averageview, this);

            subtitle = findViewById(R.id.averageview_subtitle);
            amount = findViewById(R.id.averageview_amount);
            goal = findViewById(R.id.averageview_goal);
        }
    }

    public void recalculate(ProgressCallback callback){
        Average.getAllPoints((result) -> {
            subtitle.setText(getContext().getString(R.string.exp_points).replace("%points%", String.valueOf(result)));
            Average.getGeneral((r -> {
                amount.setText(String.valueOf(r).substring(0, 3));

                String goalAvg = String.valueOf(AppCore.getSharedPreferences().getFloat("goalAVG", 2.0f)).substring(0,3);
                String avg = String.valueOf((float) r.doubleValue()).substring(0,3);

                if(avg.equals(goalAvg)){
                    goal.animate().alpha(1f).setDuration(getContext().getResources().getInteger(R.integer.anim_speed_quickly));
                } else {
                    goal.animate().alpha(0f).setDuration(getContext().getResources().getInteger(R.integer.anim_speed_quickly));
                }

                callback.onFinish();
            }));
        });

    }

    public interface ProgressCallback {
        void onFinish();
    }
}
