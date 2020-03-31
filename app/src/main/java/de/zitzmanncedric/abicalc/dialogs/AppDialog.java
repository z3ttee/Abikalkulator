package de.zitzmanncedric.abicalc.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import java.util.Objects;

import de.zitzmanncedric.abicalc.R;

/**
 * Abstrakte Klasse, um alle Dialogfenster zusammenzufassen und sich wiederholende Funktionen zu zentralisieren.
 */
public abstract class AppDialog extends Dialog {

    private @LayoutRes int contentview;

    /**
     * Konstruktor der Klasse. Hier werden Standardwerte gesetzt. Unter anderem kann hier das Layout angegeben werden
     * @param context Context zum Zugriff auf App-Resourcen
     * @param contentview ID der Layout-Resource
     */
    AppDialog(@NonNull Context context, @LayoutRes int contentview) {
        super(context);
        this.contentview = contentview;
        setCancelable(true);
    }

    /**
     * Das Dialogfenster wird aufgebaut und das Layout wird gesetzt.
     * @param savedInstanceState Von Android übergeben (Nicht genutzt)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(contentview);
    }

    /**
     * Behandelt das Öffnen eines Dialogfensters. Gleichzeitig wird der Hintergrund des Fensters gesetzt und eine Eingangs, sowie Ausgangsanimation festgelegt
     */
    @Override
    public void show() {
        Objects.requireNonNull(getWindow()).getAttributes().windowAnimations = R.style.anim_dialog_scale;
        getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.background_dialog));
        super.show();
    }
}
