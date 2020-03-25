package de.zitzmanncedric.abicalc.dialogs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;

import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.activities.main.SetupActivity;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.views.AppButton;
import lombok.Getter;
import lombok.Setter;

public class QuickSubjectEditDialog extends AppDialog implements View.OnClickListener {
    private static final String TAG = "QuickSubjectEditDialog";

    private Subject subject;
    @Setter private DialogCallback callback;

    private CheckBox checkBoxExam;
    private CheckBox checkBoxOralExam;

    @Getter @Setter private Activity owner;

    public QuickSubjectEditDialog(@NonNull Context context, @NonNull Subject subject, Activity owner) {
        super(context, R.layout.dialog_quicksubjectedit);
        this.subject = subject;
        this.owner = owner;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            checkBoxExam = findViewById(R.id.dialog_checkbox_exam);
            checkBoxOralExam = findViewById(R.id.dialog_checkbox_oralexam);

            checkBoxExam.setChecked(subject.isExam());
            checkBoxOralExam.setChecked(subject.isOralExam());

            AppButton btnPositive = findViewById(R.id.dialog_btn_positive);
            btnPositive.setOnClickListener(this);

            checkBoxOralExam.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    checkBoxExam.setChecked(true);
                }
                if(!subject.isIntensified() && !isChecked) {
                    checkBoxExam.setChecked(false);
                }

            });
            checkBoxExam.setOnCheckedChangeListener(((buttonView, isChecked) -> {
                if(!isChecked) {
                    checkBoxOralExam.setChecked(false);
                }

            }));

            if(!subject.isIntensified()) {
                checkBoxExam.setEnabled(false);
                checkBoxExam.setAlpha(0.5f);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        SetupActivity activity = (SetupActivity) owner;

        // mündlich gewählt
        if(checkBoxExam.isChecked() || checkBoxOralExam.isChecked()) {
            if(subject.isOralExam() && !checkBoxOralExam.isChecked() && activity.getCountWrittenExams() >= 3) {
                checkBoxExam.setChecked(false);
                editSubject();
                super.dismiss();
                return;
            }

            if(checkBoxExam.isChecked() && checkBoxOralExam.isChecked() && activity.getCountOralExams() < 2 ||
                    checkBoxExam.isChecked() && !checkBoxOralExam.isChecked() && activity.getCountWrittenExams() < 3 ||
                    subject.isOralExam() && !checkBoxExam.isChecked() && !checkBoxOralExam.isChecked()) {
                // Fach ändern

                editSubject();
                super.dismiss();
                return;
            }
        } else {
            if(subject.isExam() && !checkBoxExam.isChecked()) {
                editSubject();
                super.dismiss();
                return;
            }
        }

        if(activity.getCountOralExams() >= 2) {
            Toast.makeText(activity, "Du hast das Limit an mündl. Prüfungen erreicht.", Toast.LENGTH_LONG).show();
        } else if(activity.getCountWrittenExams() >= 3) {
            Toast.makeText(activity, "Du hast das Limit an schriftl. Prüfungen erreicht.", Toast.LENGTH_LONG).show();
        }
        super.dismiss();
    }

    private void editSubject() {
        subject.setExam(checkBoxExam.isChecked());
        if (checkBoxOralExam.isChecked()) subject.setExam(true);
        subject.setOralExam(checkBoxOralExam.isChecked());
        if (callback != null) callback.onCallback(subject);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    public interface DialogCallback {
        void onCallback(Subject subject);
    }
}
