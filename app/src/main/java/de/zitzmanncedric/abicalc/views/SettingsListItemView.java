package de.zitzmanncedric.abicalc.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.listener.OnButtonTouchListener;
import lombok.Getter;

/**
 * Klasse zur Definition eines Listenelements in den Einstellungen
 */
public class SettingsListItemView extends LinearLayout {

    private ImageView itemIconView;
    private TextView itemNameView;
    private TextView itemDescView;

    @Getter private Drawable icon;
    @Getter private @StringRes int name;
    @Getter private @StringRes int description;

    /**
     * Konstruktor der Klasse. Ruft init() auf.
     * @param context Context zur Übergabe an Elternklasse
     */
    public SettingsListItemView(Context context) {
        super(context);
        init(context);
    }

    /**
     * Konstruktor der Klasse. Ruft init() auf.
     * @param context Context zur Übergabe an Elternklasse
     * @param attrs Übergibt Style-Attribute an die Elternklasse
     */
    public SettingsListItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Konstruktor der Klasse. Ruft init() auf.
     * @param context Context zur Übergabe an Elternklasse
     * @param attrs Übergibt Style-Attribute an die Elternklasse
     * @param defStyleAttr Übergibt die Standard-Style Resource als ID an die Elternklasse
     */
    public SettingsListItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * Funktion zum Erstellen des Layouts
     * @param context Context zum Zugriff auf App-Resourcen
     */
    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(inflater != null) {
            View view = inflater.inflate(R.layout.view_settingsitem, this);

            itemIconView = view.findViewById(R.id.item_icon);
            itemNameView = view.findViewById(R.id.item_name);
            itemDescView = view.findViewById(R.id.item_description);

            MarginLayoutParams layoutParams = new MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics());
            layoutParams.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics());
            setLayoutParams(layoutParams);

            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            setForeground(context.getDrawable(outValue.resourceId));
            setBackground(context.getDrawable(R.drawable.background_listitem));

            setClipToOutline(true);
            setOnTouchListener(new OnButtonTouchListener());
        }
    }

    /**
     * Ändert den anzuzeigenden Name des Listenelements
     * @param res ID der String-Resource
     */
    public void setName(@StringRes int res) {
        this.name = res;
        this.itemNameView.setText(this.name);
    }

    /**
     * Ändert die anzuzeigende Beschreibung des Listenelements
     * @param res ID der String-Resource
     */
    public void setDescription(@StringRes int res) {
        this.description = res;
        this.itemDescView.setText(this.description);
    }

    /**
     * Änder das Symbol des Listenelements
     * @param icon Bildasset, das angezeigt werden soll
     */
    public void setIcon(Drawable icon) {
        this.icon = icon;
        this.itemIconView.setImageDrawable(this.icon);
    }
}
