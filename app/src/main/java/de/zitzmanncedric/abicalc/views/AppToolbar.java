package de.zitzmanncedric.abicalc.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import de.zitzmanncedric.abicalc.R;
import lombok.Getter;

/**
 * Benutzerdefinierter Toolbar-View. Ersetzt die von Android standardweise angezeigte Toolbar in einer Aktivität
 */
public class AppToolbar extends Toolbar {
    private static final String TAG = "AppToolbar";

    private TextView titleView;
    @Getter private ImageView closeView;
    @Getter private ImageView saveView;

    private String title = getContext().getString(R.string.app_name);

    /**
     * Konstruktor der Klasse
     * @param context Context zur Übergabe an Elternklasse
     */
    public AppToolbar(Context context) {
        super(context);
        init(context);
    }

    /**
     * Konstruktor der Klasse. Ruft init() auf.
     * @param context Context zur Übergabe an Elternklasse
     * @param attrs Übergibt Style-Attribute an die Elternklasse
     */
    public AppToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Konstruktor der Klasse. Ruft init() auf.
     * @param context Context zur Übergabe an Elternklasse
     * @param attrs Übergibt Style-Attribute an die Elternklasse
     * @param defStyleAttr Übergibt die Standard-Style Resource als ID an die Elternklasse
     */
    public AppToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * Funktion zum Vorbereiten der Toolbar. Hier werden einzelne Elemente je nach Voreinstellung sichtbar oder unsichtbar gemacht
     * @param context Context zum Zugriff auf App-Resourcen
     */
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater != null) {
            View view = inflater.inflate(R.layout.view_toolbar, this);
            titleView = view.findViewById(R.id.toolbar_title);
            closeView = view.findViewById(R.id.toolbar_btn_close);
            saveView = view.findViewById(R.id.toolbar_btn_save);

            titleView.setText(title);
            closeView.setClipToOutline(true);
            saveView.setClipToOutline(true);
            setShowClose(false);
            setShowSave(false);
        }
    }

    /**
     * Ändert den Titel der Toolbar
     * @param resId Resource-ID des zu setzenden Strings
     */
    @Override
    public void setTitle(int resId) {
        this.title = getContext().getString(resId);
        if(titleView != null) titleView.setText(resId);
    }

    /**
     * Ändert den Titel der Toolbar
     * @param title Titel, der gesetzt werden soll
     */
    @Override
    public void setTitle(CharSequence title) {
        this.title = String.valueOf(title);
        if(titleView != null) titleView.setText(title);
    }

    /**
     * Legt fest, ob der "Zurück" oder "Schließen"-Button in der Toolbar versteckt oder angezeigt werden soll
     * @param showClose Wenn true, wird Button versteckt
     */
    public void setShowClose(boolean showClose) {
        closeView.setVisibility((showClose ? VISIBLE : INVISIBLE));
    }

    /**
     * Legt fest, ob der "Zurück" oder "Schließen"-Button in der Toolbar versteckt oder angezeigt werden soll
     * @param showSave Wenn true, wird Button versteckt
     */
    public void setShowSave(boolean showSave) {
        saveView.setVisibility((showSave ? VISIBLE : INVISIBLE));
    }
}
