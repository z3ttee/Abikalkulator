package de.zitzmanncedric.abicalc.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.Locale;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.calculation.Average;
import needle.Needle;

public class AverageView extends LinearLayout {

    private TextView subtitle;
    private TextView amount;

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
            amount = findViewById(R.id.average_amount);
        }
    }

    public void recalculate(ProgressCallback callback){
        Average.getAllPoints((result) -> {
            subtitle.setText(getContext().getString(R.string.exp_points).replace("%points%", String.valueOf(result)));
            Average.getGeneral((r -> {
                amount.setText(String.valueOf(r).substring(0, 3));
                callback.onFinish();
            }));
        });

    }

    public interface ProgressCallback {
        void onFinish();
    }
}
