package de.zitzmanncedric.abicalc.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;

import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.views.AppButton;
import lombok.Setter;

public class QuickSubjectEditDialog extends Dialog implements View.OnClickListener {

    private Subject subject;
    @Setter private DialogCallback callback;

    private CheckBox checkBox;

    public QuickSubjectEditDialog(@NonNull Context context, @NonNull Subject subject) {
        super(context);
        this.subject = subject;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_quicksubjectedit);
        checkBox = findViewById(R.id.dialog_checkbox);

        checkBox.setChecked(subject.isExam());

        AppButton btnPositive = findViewById(R.id.dialog_btn_positive);
        btnPositive.setOnClickListener(this);
    }

    @Override
    public void dismiss() {
        subject.setExam(checkBox.isChecked());
        if(callback != null) callback.onCallback(subject);
        super.dismiss();
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    public interface DialogCallback {
        void onCallback(Subject subject);
    }
}
