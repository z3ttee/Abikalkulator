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

/**
 * Klasse zur Erstellung des Schnellbearbeitungs-Dialogs
 */
public class QuickSubjectEditDialog extends AppDialog implements View.OnClickListener {

    private Subject subject;

    private CheckBox checkBoxExam;
    private CheckBox checkBoxOralExam;

    @Setter private DialogCallback callback;
    @Getter @Setter private Activity owner;

    /**
     * Konstruktor zur Übergabe des Context-Objekts. Gleichzeitig wird das Layout gesetzt
     * @param context Context zum Zugriff auf App-Ressourcen
     * @param subject Definiert das Fach, das bearbeitet werden soll
     * @param owner Definiert die Aktivität, in welcher das Dialogfenster angezeigt wird
     */
    public QuickSubjectEditDialog(@NonNull Context context, @NonNull Subject subject, Activity owner) {
        super(context, R.layout.dialog_quicksubjectedit);
        this.subject = subject;
        this.owner = owner;
    }

    /**
     * Das Dialogfenster wird aufgebaut und die Informationen werden angezeigt
     * @param savedInstanceState Von Android übergeben (Nicht genutzt)
     */
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

    /**
     * Funktion zum Schließen des Fensters. Dabei werden die Eingaben überprüft und gespeichert.
     */
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

    /**
     * Funktion zum Bearbeiten des Fachs. Alle Informationen werden gespeichert und das gesetzt Callback wird aufgerufen
     */
    private void editSubject() {
        subject.setExam(checkBoxExam.isChecked());
        if (checkBoxOralExam.isChecked()) subject.setExam(true);
        subject.setOralExam(checkBoxOralExam.isChecked());
        if (callback != null) callback.onCallback(subject);
    }

    /**
     * Bei einem Klick-Event wird das Fenster geschlossen
     * @param v Angeklickter Button
     */
    @Override
    public void onClick(View v) {
        dismiss();
    }

    /**
     * Interface, um auf Interaktionen im Fenster zurückzugreifen
     */
    public interface DialogCallback {
        /**
         * Funktion zum Behandeln des Callbacks
         */
        void onCallback(Subject subject);
    }
}
