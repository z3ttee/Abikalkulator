package de.zitzmanncedric.abicalc.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.zitzmanncedric.abicalc.R;

/**
 * Klasse zur Erstellung eines Fortschritt-Dialogs zum Anzeigen eines Ladebalkens mit der Beschreibung des Vorgangs
 */
public class ProgressDialog extends AppDialog {

    private TextView textView;
    private String title = "";

    /**
     * Konstruktor zur Übergabe des Context-Objekts. Gleichzeitig wird das Layout gesetzt
     * @param context Context zum Zugriff auf App-Ressourcen
     */
    public ProgressDialog(@NonNull Context context) {
        super(context, R.layout.dialog_loading);
    }

    /**
     * Das Dialogfenster wird aufgebaut und die Informationen werden angezeigt
     * @param savedInstanceState Von Android übergeben (Nicht genutzt)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textView = findViewById(R.id.dialog_title);
        textView.setText(this.title);
        setCancelable(false);
    }

    /**
     * Ändert den Titel des Dialogfensters
     * @param title Titel, der gesetzt werden soll
     */
    @Override
    public void setTitle(@Nullable CharSequence title) {
        this.title = String.valueOf(title);
        if(textView != null) textView.setText(title);
    }

    /**
     * Ändert den Titel des Dialogfensters
     * @param titleId ID der Resource, die als Titel gesetzt werden soll
     */
    @Override
    public void setTitle(int titleId) {
        this.title = getContext().getString(titleId);
        if(textView != null) textView.setText(titleId);
    }
}
