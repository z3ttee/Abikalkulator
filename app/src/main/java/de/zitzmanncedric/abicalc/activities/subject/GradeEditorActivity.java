package de.zitzmanncedric.abicalc.activities.subject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.Grade;
import de.zitzmanncedric.abicalc.api.Seminar;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.utils.AppSerializer;
import de.zitzmanncedric.abicalc.views.AppToolbar;

public class GradeEditorActivity extends AppCompatActivity implements View.OnClickListener {

    private AppToolbar toolbar;

    private Spinner subjectSpinner;
    private Spinner termSpinner;
    private Spinner typeSpinner;
    private NumberPicker amountPicker;

    private int subjectID = 0;
    private int termID = 0;
    private int typeID = Grade.Type.LK.getId();
    private int value = 8;

    private boolean edit = false;
    private boolean seminar = false;

    private Grade grade;

    /**
     * Von Android implementiert. Methode zum Aufbauen des Fensters. Bei Bedarf werden alle bisherigen Daten geladen und angezeigt, wenn eine Note zum bearbeiten übergeben wurde. Andernfalls kann eine neue Note erstellt werden. Nicht veränderbare Einstellungen werden ausgeblendet
     * @param savedInstanceState Von Android übergeben (nicht genutzt)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_editor_grade);
        super.onCreate(savedInstanceState);

        subjectSpinner = findViewById(R.id.spinner_subjects);
        termSpinner = findViewById(R.id.spinner_terms);
        typeSpinner = findViewById(R.id.spinner_type);
        amountPicker = findViewById(R.id.picker_grade_value);

        TextView labelSubjectsSpinner = findViewById(R.id.label_subjectspinner);
        TextView labelTermSpinner = findViewById(R.id.label_termspinner);
        TextView labelTypeSpinner = findViewById(R.id.label_typespinner);

        toolbar = findViewById(R.id.app_toolbar);
        toolbar.setShowSave(true);
        toolbar.setShowClose(true);
        toolbar.getSaveView().setOnClickListener(this);
        toolbar.getCloseView().setOnClickListener(this);
        setSupportActionBar(toolbar);

        amountPicker.setMinValue(0);
        amountPicker.setMaxValue(15);

        Intent intent = getIntent();

        String action = intent.getStringExtra("action");
        if(action == null) action = "add";

        if(action.equalsIgnoreCase("add")) {
            subjectID = intent.getIntExtra("subjectID", 0);
            termID = intent.getIntExtra("termID", 0);
            typeID = intent.getIntExtra("typeID", Grade.Type.LK.getId());

            this.seminar = (subjectID == Seminar.getInstance().getSubjectID());
            this.edit = false;
        } else if(action.equalsIgnoreCase("edit")) {
            try {
                grade = (Grade) AppSerializer.deserialize(intent.getByteArrayExtra("grade"));
                subjectID = grade.getSubjectID();
                termID = grade.getTermID();
                typeID = grade.getType().getId();
                value = grade.getValue();

                this.seminar = (subjectID == Seminar.getInstance().getSubjectID());
                this.edit = true;
            } catch (Exception ignored) {
                this.edit = false;
            }
        }



            {
                Subject select = AppDatabase.getInstance().getUserSubjectByID(subjectID);
                List<String> items = new ArrayList<>();

                if(seminar) {
                    items.add(Seminar.getInstance().getTitle());
                } else {
                    for (Subject subject : AppDatabase.getInstance().userSubjects) {
                        items.add(subject.getTitle());
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
                subjectSpinner.setAdapter(adapter);
                subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Subject subject = AppDatabase.getInstance().userSubjects.get(position);
                        populateTermSpinner(subject);
                        populateTypeSpinner(subject);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                if(!seminar) subjectSpinner.setSelection(items.indexOf(select.getTitle()));
            }
            amountPicker.setValue(value);
            populateTermSpinner(null);
            populateTypeSpinner(null);

            if (edit || seminar) {
                labelSubjectsSpinner.setAlpha(0.5f);
                subjectSpinner.setEnabled(false);
                subjectSpinner.setClickable(false);
                labelTermSpinner.setAlpha(0.5f);
                termSpinner.setEnabled(false);
                termSpinner.setClickable(false);

                if(seminar) {
                    labelTypeSpinner.setAlpha(0.5f);
                    typeSpinner.setClickable(false);
                    typeSpinner.setEnabled(false);
                }
            }
    }

    /**
     * Private Funktion der Klasse. Sorgt dafür, dass passend zur Fachauswahl die möglichen Halbjahre zur Dropdown-Liste hinzugefügt werden und nun zur Auswahl stehen
     * @param subject Betroffenes Fach
     */
    private void populateTermSpinner(@Nullable Subject subject) {
        List<String> items = new ArrayList<>();
        if(seminar) {
            items.add(getString(R.string.exp_abi));
        } else {
            for (int i = 1; i <= 5; ++i) {
                if (i != 5) {
                    items.add(getString(R.string.exp_term).replace("%", String.valueOf(i)));
                } else {
                    if (subject == null) {
                        items.add(getString(R.string.exp_abi));
                        return;
                    }
                    if (subject.isExam() && Seminar.getInstance().getReplacedSubjectID() != subject.getId()) {
                        items.add(getString(R.string.exp_abi));
                    }
                }
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        termSpinner.setAdapter(adapter);
        if(!seminar) termSpinner.setSelection(termID);
    }

    /**
     * Private Funktion der Klasse. Sorgt dafür, dass passend zur Fachauswahl die Notentypen zur Dropdownliste hinzugefügt werden
     * @param subject Betroffenes Fach
     */
    private void populateTypeSpinner(@Nullable Subject subject) {
        List<String> items = new ArrayList<>();

        for(Grade.Type type : Grade.Type.values()) {
            if(!seminar) {
                if(type.getId() < 4) {
                    if (type == Grade.Type.KA) {
                        if (subject == null) {
                            items.add(type.getTitle());
                            return;
                        }
                        if (subject.isIntensified()) {
                            items.add(type.getTitle());
                        }
                    } else {
                        items.add(type.getTitle());
                    }
                }
            } else {
                if(type.getId() >= 4) {
                    items.add(type.getTitle());
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        typeSpinner.setAdapter(adapter);
        if(!seminar) typeSpinner.setSelection(typeID);
        else typeSpinner.setSelection(typeID-4);
    }

    /**
     * Fängt Klick-Events im Fenster ab. Beim anklicken des "Zurück"-Buttons in der Toolbar wird das Fenster geschlossen. Wird auf "Speichern" in der Toolbar geklickt, so werden die Einstellungen übernommen.
     * @param v Angeklickter Button
     */
    @Override
    public void onClick(View v) {
        if(v.getId() == toolbar.getCloseView().getId()) {
            setResult(AppCore.ResultCodes.RESULT_CANCELLED);
            finish();
            return;
        }
        if(v.getId() == toolbar.getSaveView().getId()) {
            if(!edit) {
                addGrade();
            } else {
                editGrade();
            }
        }
    }

    /**
     * Setzt das Resultat des Fensters auf "Abgebrochen". Dient der weiteren Verarbeitung in anderen Fenstern
     */
    @Override
    public void onBackPressed() {
        setResult(AppCore.ResultCodes.RESULT_CANCELLED);
        super.onBackPressed();
    }

    /**
     * Funktion für "Hinzufügen"-Button. Übermittelt Daten an übergeordnete Aktivität und erstellt eine neue Note
     */
    private void addGrade() {
        Intent intent = new Intent();

        int subjectPosition = (seminar ? Seminar.getInstance().getSubjectID() : subjectSpinner.getSelectedItemPosition());  // beginnend bei 0
        int termPosition = termSpinner.getSelectedItemPosition();                                                           // beginnend bei 0
        int typePosition = typeSpinner.getSelectedItemPosition()+(seminar ? 4 : 0);                                         // beginnend bei 0
        int value = amountPicker.getValue();                                                                                // Standard: 0

        if(!seminar) {
            Subject subject = AppDatabase.getInstance().userSubjects.get(subjectPosition);

            Grade.Type type = Grade.Type.getByID(typePosition);
            Grade grade = new Grade(0, subject.getId(), termPosition, value, type, false); // SubjectID ist gleich mit position, durch sortierung Geht nur, weil positionen wie in der Liste der AppDatenbank

            long id = AppDatabase.getInstance().createGrade(subject.getId(), grade);

            grade.setId(id);
            byte[] bytes = AppSerializer.serialize(grade);
            intent.putExtra("grade", bytes);
        } else {
            Grade.Type type = Grade.Type.getByID(typePosition);
            Grade grade = new Grade(0, Seminar.getInstance().getSubjectID(), 4, value, type, false); // SubjectID ist gleich mit position, durch sortierung Geht nur, weil positionen wie in der Liste der AppDatenbank

            AppDatabase.getInstance().createGrade(Seminar.getInstance().getSubjectID(), grade);
        }

        setResult(AppCore.ResultCodes.RESULT_OK, intent);
        finish();
    }

    /**
     * Funktion für "Hinzufügen"-Button. Speichert alle Änderungen an einer Note
     */
    private void editGrade() {
        Intent intent = new Intent();

        if(grade == null) {
            setResult(AppCore.ResultCodes.RESULT_FAILED);
            finish();
            return;
        }

        try {
            Grade newGrade = new Grade(grade.getId(), grade.getSubjectID(), grade.getTermID(), grade.getValue(), grade.getType(), grade.isEdited());

            int typeID = typeSpinner.getSelectedItemPosition();
            int value = amountPicker.getValue();

            if(seminar) {
                if(newGrade.getValue() != value) {
                    newGrade.setValue(value);
                    newGrade.setEdited(true);
                    AppDatabase.getInstance().updateGrade(newGrade.getSubjectID(), newGrade);
                    intent.putExtra("newGrade", AppSerializer.serialize(newGrade));
                    intent.putExtra("oldGrade", AppSerializer.serialize(grade));
                }
            } else {
                if (newGrade.getType().getId() != typeID || newGrade.getValue() != value) {
                    newGrade.setType(Grade.Type.getByID(typeID));
                    newGrade.setValue(value);
                    AppDatabase.getInstance().updateGrade(newGrade.getSubjectID(), newGrade);
                    intent.putExtra("newGrade", AppSerializer.serialize(newGrade));
                    intent.putExtra("oldGrade", AppSerializer.serialize(grade));
                }
            }

            setResult(AppCore.ResultCodes.RESULT_OK, intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
