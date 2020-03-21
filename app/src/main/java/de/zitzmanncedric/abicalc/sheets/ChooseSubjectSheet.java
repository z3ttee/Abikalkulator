package de.zitzmanncedric.abicalc.sheets;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.adapter.SimpleSubjectListAdapter;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.listener.OnListItemCallback;
import de.zitzmanncedric.abicalc.listener.OnSubjectChosenListener;
import lombok.Setter;
import needle.Needle;
import needle.UiRelatedProgressTask;

/**
 * Klasse für BottomSheet, um ein neues Fach hinzuzufügen
 * @author Cedric Zitzmann
 */
public class ChooseSubjectSheet extends BottomSheetDialog implements OnListItemCallback {
    private static final String TAG = "ChooseSubjectSheet";

    private ArrayList<Subject> dataset = new ArrayList<>();
    private ArrayList<Subject> disabled = new ArrayList<>();

    private RecyclerView recyclerView;
    private TextView titleView;
    private CheckBox checkBoxExam;
    private CheckBox checkBoxOralExam;

    private SimpleSubjectListAdapter adapter;

    @Setter private boolean onlyOralExam = false;
    @Setter private boolean onlyWrittenExam = false;

    private String title;
    @Setter private OnSubjectChosenListener onSubjectChosenListener;

    /**
     * Standartkonstruktor der Klasse
     * @param context Benötigt, wird an init() übergeben
     */
    public ChooseSubjectSheet(@NonNull Context context) {
        super(context);
        init(context);
    }

    /**
     * Standartkonstruktor der Klasse
     * @param context Benötigt, wird an init() übergeben
     * @param disabled Übergabe der Fächer möglich, die bereits ausgewählt wurden
     */
    public ChooseSubjectSheet(@NonNull Context context, ArrayList<Subject> disabled) {
        super(context);
        this.disabled = disabled;
        init(context);
    }

    /**
     * Das BottomSheet wird hier aufgebaut
     * @param context Benötigt, um einzelne Views hinzuzufügen
     */
    private void init(final Context context) {
        setContentView(R.layout.sheet_choosesubjectsheet);
        setWhiteNavigationBar(this);

        titleView = findViewById(R.id.sheet_title);
        recyclerView = findViewById(R.id.sheet_list);
        checkBoxExam = findViewById(R.id.sheet_checkbox_exam);
        checkBoxOralExam = findViewById(R.id.sheet_checkbox_oralexam);

        adapter = new SimpleSubjectListAdapter(new ArrayList<>(11), disabled);
        adapter.setOnCallback(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        checkBoxOralExam.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(onlyOralExam) {
                checkBoxExam.setChecked(isChecked);
            }
            if(isChecked) {
                checkBoxExam.setChecked(true);
            }
        });
        checkBoxExam.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if(!isChecked || onlyWrittenExam) {
                checkBoxOralExam.setChecked(false);
            }
        }));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getBehavior().setState(BottomSheetBehavior.STATE_HALF_EXPANDED);

        if(onlyOralExam) {
            checkBoxExam.setEnabled(false);
            checkBoxExam.setAlpha(0.5f);
        }
        if(onlyWrittenExam) {
            checkBoxOralExam.setEnabled(false);
            checkBoxOralExam.setAlpha(0.5f);
        }

        Needle.onBackgroundThread().withThreadPoolSize(1).execute(new UiRelatedProgressTask<Void, Subject>() {
            @Override
            protected Void doWork() {
                for(Subject subject : AppDatabase.getInstance().appSubjects.values()) {
                    if(!disabled.contains(subject)) {
                        dataset.add(subject);
                        publishProgress(subject);

                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                return null;
            }

            @Override
            protected void onProgressUpdate(Subject subject) {
                adapter.add(subject);
            }

            @Override
            protected void thenDoUiRelatedWork(Void v) { }
        });
    }

    /**
     * Macht den Titel des BottomSheets veränderbar
     * @param title Übergabe eines Strings für den Titel
     */
    @Override
    public void setTitle(CharSequence title) {
        this.title = title.toString();
        titleView.setText(this.title);
    }

    /**
     * Macht den Titel des BottomSheets veränderbar
     * @param titleId Übergabe einer ResourceID um einen String aus strings.xml zu übergeben
     */
    @Override
    public void setTitle(int titleId) {
        this.title = getContext().getString(titleId);
        titleView.setText(this.title);
    }

    /**
     * Setzt eine weiße Navigationsleiste für schöneres Aussehen
     * @param dialog Übergabe, um welches BottomSheet Fenster es sich handelt
     */
    private void setWhiteNavigationBar(@NonNull Dialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            GradientDrawable dimDrawable = new GradientDrawable();

            GradientDrawable navigationBarDrawable = new GradientDrawable();
            navigationBarDrawable.setShape(GradientDrawable.RECTANGLE);
            navigationBarDrawable.setColor(Color.WHITE);

            Drawable[] layers = {dimDrawable, navigationBarDrawable};

            LayerDrawable windowBackground = new LayerDrawable(layers);
            windowBackground.setLayerInsetTop(1, metrics.heightPixels);

            window.setBackgroundDrawable(windowBackground);
        }
    }

    /**
     * Behandelt das Auswählen eines Objekts aus der Liste
     * @param object Gibt das Objekts in der Liste an
     */
    @Override
    public void onItemClicked(ListableObject object) {
        this.dismiss();
        try {
            if(object instanceof Subject) {
                Subject subject = (Subject) object;
                subject.setExam(checkBoxExam.isChecked());
                subject.setOralExam(checkBoxOralExam.isChecked());

                if (this.onSubjectChosenListener != null)
                    this.onSubjectChosenListener.onSubjectChosen(subject);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(AppCore.getInstance().getApplicationContext(), "Error occured. Procedure failed.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Unwichtig
     */
    @Override
    public void onItemDeleted(ListableObject object) { }

    /**
     * Unwichtig
     */
    @Override
    public void onItemEdit(ListableObject object) { }

    /**
     * Unwichtig
     */
    @Override
    public void onItemLongClicked(ListableObject object) { }
}
