package de.zitzmanncedric.abicalc.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.zitzmanncedric.abicalc.R;

public class ProgressDialog extends AppDialog {

    private ProgressBar progressBar;
    private TextView textView;

    private String title = "";

    public ProgressDialog(@NonNull Context context) {
        super(context, R.layout.dialog_loading);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressBar = findViewById(R.id.dialog_progressbar);
        textView = findViewById(R.id.dialog_title);

        textView.setText(this.title);
        setCancelable(false);
    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
        this.title = String.valueOf(title);
        if(textView != null) textView.setText(title);
    }
    @Override
    public void setTitle(int titleId) {
        this.title = getContext().getString(titleId);
        if(textView != null) textView.setText(titleId);
    }
}
