package de.zitzmanncedric.abicalc.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import de.zitzmanncedric.abicalc.R;

public class AverageView extends LinearLayout {
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


        }
    }
}
