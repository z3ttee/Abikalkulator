package de.zitzmanncedric.abicalc.dialogs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
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
import lombok.Getter;
import lombok.Setter;

/**
 * Klasse zur Erstellung eines Bestätigungs-Dialogs
 */
public class ConfirmDialog extends AppDialog implements View.OnClickListener {
    private static final String TAG = "ConfirmDialog";

    @Getter private AppButton buttonPositive;
    @Getter private AppButton buttonNegative;

    @Setter private DialogCallback callback;

    /**
     * Konstruktor zur Übergabe des Context-Objekts. Gleichzeitig wird das Layout gesetzt
     * @param context Context zum Zugriff auf App-Ressourcen
     */
    public ConfirmDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    /**
     * Funktion zum initialisieren des Dialogs. Hier werden anzuzeigende Buttons erstellt und hinzugefügt.
     * @param context Context zum Zugriff auf App-Resourcen
     */
    private void init(Context context){
        buttonPositive = new AppButton(new ContextThemeWrapper(context, R.style.Button_primary), null, 0);
        buttonPositive.setText(R.string.btn_continue);
        buttonPositive.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        buttonPositive.setOnClickListener(this);

        buttonNegative = new AppButton(new ContextThemeWrapper(context, R.style.Button_primary), null, 0);
        buttonNegative.setText(R.string.btn_cancel);
        buttonNegative.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        buttonNegative.setOnClickListener(this);

        addButton(buttonPositive);
        addButton(buttonNegative);
    }

    /**
     * Fängt alle Klick-Events im Fenster ab. Bei einem Klick wird das gesetzte Callback aufgerufen und die passende Methode behandelt
     * @param v Angeklickter Button
     */
    @Override
    public void onClick(View v) {
        if(v == buttonPositive){
            if(callback != null) callback.onButtonClicked(buttonPositive);
            return;
        }
        if(v == buttonNegative){
            if(callback != null) callback.onButtonClicked(buttonNegative);
        }
    }
}
