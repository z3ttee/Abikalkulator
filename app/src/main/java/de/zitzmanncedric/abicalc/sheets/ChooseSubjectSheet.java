package de.zitzmanncedric.abicalc.sheets;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Klasse für BottomSheet, um ein neues Fach hinzuzufügen
 * @author Cedric Zitzmann
 */
public class ChooseSubjectSheet extends BottomSheetDialog implements OnListItemCallback {
    private static final String TAG = "ChooseSubjectSheet";

    private ArrayList<Subject> dataset = new ArrayList<>();
    private ArrayList<Subject> disabled;

    private RecyclerView recyclerView;
    private TextView titleView;
    private CheckBox checkBox;

    private SimpleSubjectListAdapter adapter;

    private String title;
    @Setter private OnSubjectChosenListener onSubjectChosenListener;

    int lastViewHeight = 0;

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
        setContentView(R.layout.layout_bottomsheet);
        setWhiteNavigationBar(this);

        LinearLayout wrapperView = findViewById(R.id.bottomsheet_wrapper);
        titleView = findViewById(R.id.bottomsheet_title);

        // TODO: Add subjects async (no loading required to open sheet) and sort items after enabled states (visible ones to top)
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        recyclerView = new RecyclerView(context);
        recyclerView.setLayoutParams(layoutParams);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        dataset = new ArrayList<>(11);
        adapter = new SimpleSubjectListAdapter(dataset, disabled);
        adapter.setOnCallback(this);
        recyclerView.setAdapter(adapter);

        checkBox = new CheckBox(new ContextThemeWrapper(context, R.style.CheckBox));
        checkBox.setText(context.getString(R.string.label_markAsExam));

        wrapperView.addView(checkBox);  // Adding view to sheet
        wrapperView.addView(recyclerView);  // Adding view to sheet
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Populate Recyclerview asynchronously
        new Handler().post(() -> {
            dataset.addAll(AppDatabase.getInstance().appSubjects.values());
            recyclerView.getAdapter().notifyDataSetChanged();
            recyclerView.invalidate();
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
     * @param position Gibt die Position des Objekts in der Liste an
     */
    @Override
    public void onItemClicked(int position) {
        this.dismiss();
        try {
            Subject subject = AppDatabase.getInstance().getAppSubjects().get(position);
            subject.setExam(checkBox.isChecked());

            if (this.onSubjectChosenListener != null)
                this.onSubjectChosenListener.onSubjectChosen(AppDatabase.getInstance().getAppSubjects().get(position));
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(AppCore.getInstance().getApplicationContext(), "Error occured. Procedure failed.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Unwichtig
     */
    @Override
    public void onItemClicked(ListableObject object) { }

    /**
     * Unwichtig
     */
    @Override
    public void onItemDeleted(int position) { }

    /**
     * Unwichtig
     */
    @Override
    public void onItemEdit(int position) {

    }

    /**
     * Unwichtig
     */
    @Override
    public void onItemLongClicked(ListableObject object) { }
}
