package de.zitzmanncedric.abicalc.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

import de.zitzmanncedric.abicalc.listener.OnButtonTouchListener;

/**
 * Benutzerdefinierte Button-Klasse zum Zusammenfassen von sich wiederholenden Funktionen
 */
public class AppButton extends AppCompatButton {

    /**
     * Konstruktor der Klasse. Ruft init() auf.
     * @param context Context zur Übergabe an Elternklasse
     */
    public AppButton(Context context) {
        super(context);
        init();
    }

    /**
     * Konstruktor der Klasse. Ruft init() auf.
     * @param context Context zur Übergabe an Elternklasse
     * @param attrs Übergibt Style-Attribute an die Elternklasse
     */
    public AppButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Konstruktor der Klasse. Ruft init() auf.
     * @param context Context zur Übergabe an Elternklasse
     * @param attrs Übergibt Style-Attribute an die Elternklasse
     * @param defStyleAttr Übergibt die Standard-Style Resource als ID an die Elternklasse
     */
    public AppButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Funktion zum hinzufügen der Klick-Animation und zum Anpassen der Button-Ränder an den Hintergrund (Auschließung von Übertretungen)
     */
    private void init() {
        setClipToOutline(true);
        setOnTouchListener(new OnButtonTouchListener());
    }
}
