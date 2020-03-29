package de.zitzmanncedric.abicalc.sheets;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.views.AppButton;
import lombok.Setter;

public class SubjectQuickOptionsSheet extends BottomSheetDialog implements View.OnClickListener {

    private AppButton buttonViewSubject;
    private AppButton buttonEditSubject;

    @Setter private SheetInterface callback;
    public SubjectQuickOptionsSheet(@NonNull Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setContentView(R.layout.sheet_subjectquickoptions);
        setWhiteNavigationBar(this);

        buttonViewSubject = findViewById(R.id.sheet_btn_view);
        buttonEditSubject = findViewById(R.id.sheet_btn_edit);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buttonViewSubject.setOnClickListener(this);
        buttonEditSubject.setOnClickListener(this);

        getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == buttonViewSubject.getId()) {
            if(this.callback != null) callback.onOptionClicked(buttonViewSubject);
            return;
        }
        if(v.getId() == buttonEditSubject.getId()) {
            if(this.callback != null) callback.onOptionClicked(buttonEditSubject);
        }
    }

    /**
     * Setzt eine weiße Navigationsleiste für schöneres Aussehen
     * @param dialog Übergabe, um welches BottomSheet Fenster es sich handelt
     */
    public static void setWhiteNavigationBar(@NonNull Dialog dialog) {
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

    public interface SheetInterface {
        void onOptionClicked(AppButton button);
    }
}
