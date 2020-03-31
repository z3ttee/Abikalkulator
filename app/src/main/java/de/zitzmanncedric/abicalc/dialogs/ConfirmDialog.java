package de.zitzmanncedric.abicalc.dialogs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.views.AppButton;
import lombok.Setter;

/**
 * Klasse zur Erstellung eines Bestätigungs-Dialogs
 */
public class ConfirmDialog extends AppDialog implements View.OnClickListener {

    private Drawable icon;
    private String title;
    private String description;

    private AppButton buttonPositive;
    private AppButton buttonNegative;

    @Setter private DialogCallback callback;

    /**
     * Konstruktor zur Übergabe des Context-Objekts. Gleichzeitig wird das Layout gesetzt
     * @param context Context zum Zugriff auf App-Ressourcen
     */
    public ConfirmDialog(@NonNull Context context) {
        super(context, R.layout.dialog_confirm);
    }

    /**
     * Das Dialogfenster wird aufgebaut und die Informationen werden angezeigt
     * @param savedInstanceState Von Android übergeben (Nicht genutzt)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageView icon = findViewById(R.id.dialog_icon);
        TextView title = findViewById(R.id.dialog_title);
        TextView description = findViewById(R.id.dialog_description);

        buttonPositive = findViewById(R.id.dialog_btn_positive);
        buttonNegative = findViewById(R.id.dialog_btn_negative);
        buttonNegative.setOnClickListener(this);
        buttonPositive.setOnClickListener(this);

        icon.setImageDrawable(this.icon);
        title.setText(this.title);
        description.setText(this.description);

        if(getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
     * Ändert das Icon des Dialogfensters
     * @param icon ID der Resource, die als Icon gesetzt werden soll
     */
    public void setIcon(@DrawableRes int icon) {
        Drawable s = getContext().getDrawable(icon);
        setIcon(s);
    }

    /**
     * Ändert das Icon des Dialogfensters
     * @param icon ID der Resource, die als Icon gesetzt werden soll
     */
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    /**
     * Fängt alle Klick-Events im Fenster ab. Bei einem Klick wird das gesetzte Callback aufgerufen und die passende Methode behandelt
     * @param v Angeklickter Button
     */
    @Override
    public void onClick(View v) {
        if(v.getId() == buttonPositive.getId()){
            if(callback != null) callback.onButtonPositiveClicked(buttonPositive);
            return;
        }
        if(v.getId() == buttonNegative.getId()){
            if(callback != null) callback.onButtonNegativeClicked(buttonNegative);
        }
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

        /**
         * Funktion zum behandeln des Klick-Events auf den "Abbrechen"-Button
         * @param button Angeklickter Button
         */
        void onButtonNegativeClicked(AppButton button);
    }
}
