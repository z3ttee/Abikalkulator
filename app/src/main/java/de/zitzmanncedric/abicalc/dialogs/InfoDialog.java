package de.zitzmanncedric.abicalc.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.views.AppButton;
import lombok.Getter;
import lombok.Setter;

/**
 * Klasse zur Erstellung eines Info-Dialogs
 */
public class InfoDialog extends AppDialog implements View.OnClickListener {

    @Getter private AppButton buttonPositive;

    /**
     * Konstruktor zur Übergabe des Context-Objekts. Gleichzeitig wird das Layout gesetzt
     * @param context Context zum Zugriff auf App-Ressourcen
     */
    public InfoDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    /**
     * Funktion zum initialisieren des Dialogs. Hier werden anzuzeigende Buttons erstellt und hinzugefügt.
     * @param context Context zum Zugriff auf App-Resourcen
     */
    private void init(Context context){
        buttonPositive = new AppButton(new ContextThemeWrapper(context, R.style.Button_primary), null, 0);
        buttonPositive.setText(R.string.btn_ok);
        buttonPositive.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        buttonPositive.setOnClickListener(this);

        addButton(buttonPositive);
    }

    /**
     * Fängt alle Klick-Events im Fenster ab. Das Dialogfenster wird dabei geschlossen. Bei einem Klick wird das gesetzte Callback aufgerufen und die passende Methode behandelt
     * @param v Angeklickter Button
     */
    @Override
    public void onClick(View v) {
        if(v == buttonPositive){
            dismiss();
            if(getCallback() != null) {
                getCallback().onButtonClicked(buttonPositive);
            }
        }
    }
}
