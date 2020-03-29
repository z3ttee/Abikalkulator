package de.zitzmanncedric.abicalc.activities.settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.activities.subject.SubjectEditorActivity;
import de.zitzmanncedric.abicalc.adapter.AdvancedSubjectListAdapter;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.listener.OnListItemCallback;
import de.zitzmanncedric.abicalc.views.AppActionBar;

/**
 * Klasse zur Behandlung des Einstellungs-Menü für die Kursbelegung
 * @author Cedric Zitzmann
 */
public class SettingsSubjectsActivity extends AppCompatActivity implements OnListItemCallback, View.OnClickListener {

    private AppActionBar actionBarView;
    private AdvancedSubjectListAdapter adapter;

    /**
     * Von Android implementiert. Methode zum Aufbauen des Fensters. Es werden alle Einstellungen geladen und angezeigt
     * @param savedInstanceState Von Android übergeben (nicht genutzt)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_subjects);

        actionBarView = findViewById(R.id.app_toolbar);
        RecyclerView subjectsListView = findViewById(R.id.recyclerview_subjects);

        setSupportActionBar(actionBarView);
        actionBarView.setShowClose(true);
        actionBarView.getCloseView().setOnClickListener(this);

        adapter = new AdvancedSubjectListAdapter(new ArrayList<>(AppDatabase.getInstance().userSubjects), this, false);
        subjectsListView.setLayoutManager(new LinearLayoutManager(this));
        subjectsListView.setAdapter(adapter);
    }

    /**
     * Wenn das Element angeklickt wurde, wird der Kurseditor gestartet und das Element an die neue Aktivität übergeben.
     * @param object Betroffenes Listenelement
     */
    @Override
    public void onItemClicked(ListableObject object) {
        if(object instanceof Subject) {
            Subject subject = (Subject) object;

            Intent intent = new Intent(this, SubjectEditorActivity.class);
            intent.putExtra("subjectID", subject.getId());
            startActivityForResult(intent, AppCore.RequestCodes.REQUEST_UPDATE_SUBJECT);
        }
    }

    /**
     * Wenn auf den "Bearbeiten"-Button gedrückt wurde, erfolgt das gleiche Ereignis wie beim anklicken des gesamten Elements
     * @param object Betroffenes Listenelement
     */
    @Override
    public void onItemEdit(ListableObject object) {
        onItemClicked(object);
    }

    /**
     * Nicht benötigt
     * @param object Betroffenes Listenelement
     */
    @Override
    public void onItemDeleted(ListableObject object) { }

    /**
     * Nicht benötigt
     * @param object Betroffenes Listenelement
     */
    @Override
    public void onItemLongClicked(ListableObject object) { }

    /**
     * Von Android implementiert. Fängt das Resultat durch eine geschlossene Aktivität ab. Bei Erfolg, wird die Liste der Fächer aktualisiert
     * @param requestCode Code, zur Identifizierung der Anfrage
     * @param resultCode Code, zur Identifizierung des Resultats
     * @param data Zurückgegebene Daten
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == AppCore.ResultCodes.RESULT_OK) {
            if (requestCode == AppCore.RequestCodes.REQUEST_UPDATE_SUBJECT) {
                adapter.set(new ArrayList<>(AppDatabase.getInstance().userSubjects));
            }
        }
    }

    /**
     * Fängt das Klick-Event des "Zurück"-Buttons in der Toolbar ab. Dabei wird die Aktivität geschlossen.
     * @param v Angeklickter Button
     */
    @Override
    public void onClick(View v) {
        if(v.getId() == actionBarView.getCloseView().getId()){
            finish();
        }
    }
}
