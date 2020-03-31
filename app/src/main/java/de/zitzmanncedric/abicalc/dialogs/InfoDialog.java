package de.zitzmanncedric.abicalc.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.views.AppButton;
import lombok.Setter;

/**
 * Klasse zur Erstellung eines Info-Dialogs
 */
public class InfoDialog extends AppDialog implements View.OnClickListener {

    private String title;
    private String description;
    private AppButton buttonPositive;

    @Setter private DialogCallback callback;

    /**
     * Konstruktor zur Übergabe des Context-Objekts. Gleichzeitig wird das Layout gesetzt
     * @param context Context zum Zugriff auf App-Ressourcen
     */
    public InfoDialog(@NonNull Context context) {
        super(context, R.layout.dialog_info);
    }

    /**
     * Das Dialogfenster wird aufgebaut und die Informationen werden angezeigt.
     * @param savedInstanceState Von Android übergeben (Nicht genutzt)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView title = findViewById(R.id.dialog_title);
        TextView description = findViewById(R.id.dialog_description);

        buttonPositive = findViewById(R.id.dialog_btn_positive);
        buttonPositive.setOnClickListener(this);

        title.setText(this.title);
        description.setText(this.description);
    }

    /**
     * Fängt alle Klick-Events im Fenster ab. Das Dialogfenster wird dabei geschlossen. Bei einem Klick wird das gesetzte Callback aufgerufen und die passende Methode behandelt
     * @param v Angeklickter Button
     */
    @Override
    public void onClick(View v) {
        if(v.getId() == buttonPositive.getId()){
            dismiss();
            if(callback != null) {
                callback.onButtonPositiveClicked(buttonPositive);
            }
        }
    }

    /**
     * Ändert den Titel des Dialogfensters
     * @param title Titel, der gesetzt werden soll
     */
    @Override
    public void setTitle(@Nullable CharSequence title) {
        this.title = String.valueOf(title);
    }

    /**
     * Ändert den Titel des Dialogfensters
     * @param titleId ID der Resource, die als Titel gesetzt werden soll
     */
    @Override
    public void setTitle(@StringRes int titleId) {
        String s = getContext().getString(titleId);
        setTitle(s);
    }

    /**
     * Ändert die Beschreibung des Dialogfensters
     * @param description ID der Resource, die als Beschreibung gesetzt werden soll
     */
    public void setDescription(@StringRes int description) {
        String s = getContext().getString(description);
        setDescription(s);
    }

    /**
     * Ändert die Beschreibung des Dialogfensters
     * @param description Beschreibung, die gesetzt werden soll
     */
    public void setDescription(CharSequence description) {
        this.description = String.valueOf(description);
    }

    /**
     * Interface, um auf Interaktionen im Fenster zurückzugreifen
     */
    public interface DialogCallback {
        /**
         * Funktion zum behandeln des Klick-Events auf den "Bestätigen"-Button
         * @param button Angeklickter Button
         */
        void onButtonPositiveClicked(AppButton button);
    }
}
