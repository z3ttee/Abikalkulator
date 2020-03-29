package de.zitzmanncedric.abicalc.activities.subject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.adapter.AdvancedSubjectListAdapter;
import de.zitzmanncedric.abicalc.adapter.RecyclerGridAdapter;
import de.zitzmanncedric.abicalc.api.Grade;
import de.zitzmanncedric.abicalc.api.Seminar;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.fragments.subject.GradesFragment;
import de.zitzmanncedric.abicalc.listener.OnListItemCallback;
import de.zitzmanncedric.abicalc.utils.AppSerializer;
import de.zitzmanncedric.abicalc.views.AppActionBar;
import needle.Needle;
import needle.UiRelatedProgressTask;

/**
 * Klasse zur Darstellung aller Seminarfachnoten
 * @author Cedric Zitzmann
 */
public class SeminarActivity extends AppCompatActivity implements View.OnClickListener, OnListItemCallback {

    private AdvancedSubjectListAdapter adapter;

    private TextView noticeView;
    private LinearLayout wrapper;

    /**
     * Von Android implementiert. Methode zum Aufbauen des Fensters. Sind keine Noten vorhanden, wird eine Meldung angezeigt. Andernfalls werden alle Noten geladen und angezeigt
     * @param savedInstanceState Von Android übergeben (nicht genutzt)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seminar);

        AppActionBar actionBar = findViewById(R.id.app_toolbar);
        setSupportActionBar(actionBar);

        actionBar.setShowClose(true);
        actionBar.getCloseView().setOnClickListener(this);

        RecyclerView recyclerView = findViewById(R.id.app_grid_seminars);
        adapter = new AdvancedSubjectListAdapter(new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        wrapper = findViewById(R.id.wrapper);
        ArrayList<ListableObject> grades = new ArrayList<>(AppDatabase.getInstance().getGradesForSeminar());

        noticeView = new TextView(new ContextThemeWrapper(this, R.style.TextAppearance));
        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        marginLayoutParams.topMargin = (int) getResources().getDimension(R.dimen.default_padding_large);
        noticeView.setLayoutParams(marginLayoutParams);

        noticeView.setAlpha(0.5f);
        noticeView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        noticeView.setText(getString(R.string.label_grades_missing));

        if(grades.isEmpty()) {
            wrapper.addView(noticeView);
        } else {
            populate();
        }
    }

    /**
     * Funktion zum Laden der Noten und das anschließende Anzeigen in der Liste
     */
    private void populate(){
        adapter.clear();

        Needle.onBackgroundThread().execute(new UiRelatedProgressTask<Void, ListableObject>() {
            @Override
            protected Void doWork() {
                ArrayList<ListableObject> grades = new ArrayList<>(AppDatabase.getInstance().getGradesForSeminar());

                for (ListableObject grade : grades) {
                    publishProgress(grade);
                }
                try {
                    Thread.sleep(100);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(ListableObject grade) {
                adapter.add(grade);
            }

            @Override
            protected void thenDoUiRelatedWork(Void aVoid) { }
        });
    }

    /**
     * Fängt alle Klick-Events im Fenster ab. Das Fenster wird beim Klick auf "Zurück" in der Toolbar geschlossen
     * @param v Angeklickter Button
     */
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_close) {
            finish();
        }
    }

    /**
     * Von Android implementiert. Fängt das Resultat durch eine geschlossene Aktivität ab. Bei Erfolg wird das betroffene Element in der Liste aktualisiert.
     * @param requestCode Code, zur Identifizierung der Anfrage
     * @param resultCode Code, zur Identifizierung des Resultats
     * @param data Zurückgegebene Daten
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null) {
            if (requestCode == AppCore.RequestCodes.REQUEST_UPDATE_GRADE) {
                new Handler().postDelayed(() -> {
                    try {
                        Grade oldGrade = (Grade) AppSerializer.deserialize(data.getByteArrayExtra("oldGrade"));
                        Grade newGrade = (Grade) AppSerializer.deserialize(data.getByteArrayExtra("newGrade"));

                        adapter.update(oldGrade, newGrade);

                        if (adapter.getItemCount() > 0) {
                            if (noticeView != null) wrapper.removeView(noticeView);
                        }
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }
                }, 100);
            }
        }
    }

    /**
     * Registriert, ob auf ein Listenelement gedrückt wurde. Es wird der Noteneditor geöffnet.
     * @param object Betroffenes Element
     */
    @Override
    public void onItemClicked(ListableObject object) {
        Intent intent = new Intent(this, GradeEditorActivity.class);

        if(object instanceof Grade) {
            Grade grade = (Grade) object;
            intent.putExtra("action", "edit");
            intent.putExtra("grade", AppSerializer.serialize(grade));
            startActivityForResult(intent, AppCore.RequestCodes.REQUEST_UPDATE_GRADE);
        }
    }

    /**
     * Registriert, ob bei einem Listenelement auf bearbeiten gedrückt wurde. Leitet das Ereignis an onItemClicked(object) weiter.
     * @param object Betroffenes Element
     */
    @Override
    public void onItemEdit(ListableObject object) {
        onItemClicked(object);
    }

    /**
     * Unwichtig (nicht genutzt)
     * @param object Betroffenes Element
     */
    @Override
    public void onItemDeleted(ListableObject object) { }
    /**
     * Unwichtig (nicht genutzt)
     * @param object Betroffenes Element
     */
    @Override
    public void onItemLongClicked(ListableObject object) { }
}
