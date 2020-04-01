package de.zitzmanncedric.abicalc.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import de.zitzmanncedric.abicalc.R;
import lombok.Getter;
import lombok.Setter;

/**
 * Klasse zur Definition eines Listenelements in der Fächer- oder Notenübersicht
 */
public class SubjectListItemView extends LinearLayout implements View.OnClickListener {

    private TextView itemNameView;
    private TextView itemSubView;
    @Getter private TextView itemPoints;
    private ImageView itemEditBtn;
    private ImageView itemDeleteBtn;
    private LinearLayout itemDividerView;

    @Getter private String title,subtitle;
    @Getter private int points;
    @Getter private boolean showEdit, showDelete, showPoints;

    @Setter private OnDeleteCallback onDeleteListener;
    @Setter private OnEditCallback onEditCallback;

    /**
     * Konstruktor der Klasse. Ruft init() auf.
     * @param context Context zur Übergabe an Elternklasse
     */
    public SubjectListItemView(Context context) {
        super(context);
        init(context, null);
    }

    /**
     * Konstruktor der Klasse. Ruft init() auf.
     * @param context Context zur Übergabe an Elternklasse
     * @param attrs Übergibt Style-Attribute an die Elternklasse
     */
    public SubjectListItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * Konstruktor der Klasse. Ruft init() auf.
     * @param context Context zur Übergabe an Elternklasse
     * @param attrs Übergibt Style-Attribute an die Elternklasse
     * @param defStyleAttr Übergibt die Standard-Style Resource als ID an die Elternklasse
     */
    public SubjectListItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * Definiert das Layout und lädt Standardwerte, die über den Style geliefert werden
     * @param context Context zum Zugriff auf App-Resourcen
     * @param attrs Attribute, die im Style festgelegt wurden
     */
    @SuppressLint("SetTextI18n")
    private void init(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater != null) {
            inflater.inflate(R.layout.view_subjectlistitem, this);

            itemNameView = findViewById(R.id.item_name);
            itemSubView = findViewById(R.id.item_subtitle);
            itemPoints = findViewById(R.id.item_points);
            itemEditBtn = findViewById(R.id.item_edit);
            itemDeleteBtn = findViewById(R.id.item_delete);
            itemDividerView = findViewById(R.id.item_divider);

            if(attrs != null) {
                TypedArray attributs = getResources().obtainAttributes(attrs, R.styleable.SubjectListItemView);
                title = attributs.getString(R.styleable.SubjectListItemView_title);
                subtitle = attributs.getString(R.styleable.SubjectListItemView_subtitle);
                points = attributs.getInteger(R.styleable.SubjectListItemView_points, 0);
                showEdit = attributs.getBoolean(R.styleable.SubjectListItemView_showEdit, true);
                showDelete = attributs.getBoolean(R.styleable.SubjectListItemView_showDelete, true);
                showPoints = attributs.getBoolean(R.styleable.SubjectListItemView_showPoints, true);
                attributs.recycle();
            }

            itemNameView.setText(title);
            itemSubView.setText(subtitle);
            itemPoints.setText(points+"P");
            itemEditBtn.setOnClickListener(this);
            itemDeleteBtn.setOnClickListener(this);

            this.itemEditBtn.setVisibility((showEdit ? VISIBLE : GONE));
            this.itemDeleteBtn.setVisibility((showDelete ? VISIBLE : GONE));
            this.itemPoints.setVisibility((showPoints ? VISIBLE : GONE));

            toggleDividerIfNeeded();
            toggleSubtitleIfNeeded();

            this.setOnClickListener(this);

            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            setLayoutParams(layoutParams);

            ConstraintLayout wrapper = findViewById(R.id.item_wrapper);
            wrapper.setClipToOutline(true);
        }
    }

    /**
     * Ändert den Titel des Listenelements
     * @param title Titel, der gesetzt werden soll
     */
    public void setTitle(String title) {
        itemNameView.setText(title);
        this.title = title;
    }

    /**
     * Ändert den Untertitel des Listenelements
     * @param subtitle Untertitel, der gesetzt werden soll
     */
    public void setSubtitle(String subtitle) {
        itemSubView.setText(subtitle);
        this.subtitle = subtitle;
        toggleSubtitleIfNeeded();
    }

    /**
     * Ändert die Punktezahl des Listenelements
     * @param points Punkte als Integer
     */
    @SuppressLint("SetTextI18n")
    public void setPoints(int points) {
        itemPoints.setText(points+"P");
        this.points = points;
    }

    /**
     * Setzt statt einer Punktezahl einen festgelegten Text
     * @param text Text als String
     */
    public void setTextPoints(String text) {
        itemPoints.setText(text);
    }

    /**
     * Legt fest, ob der "Bearbeiten"-Button angezeigt werden soll
     * @param showEdit Wenn true, wird der Button angezeigt
     */
    public void setShowEdit(boolean showEdit) {
        this.showEdit = showEdit;
        this.itemEditBtn.setVisibility((showEdit ? VISIBLE : GONE));
        toggleDividerIfNeeded();
    }

    /**
     * Legt fest, ob der "Löschen"-Button angezeigt werden soll
     * @param showDelete Wenn true, wird der Button angezeigt
     */
    public void setShowDelete(boolean showDelete) {
        this.showDelete = showDelete;
        this.itemDeleteBtn.setVisibility((showDelete ? VISIBLE : GONE));
        toggleDividerIfNeeded();
    }

    /**
     * Legt fest, ob die Punkte angezeigt werden soll
     * @param showPoints Wenn true, werden die Punkte angezeigt
     */
    public void setShowPoints(boolean showPoints) {
        this.showPoints = showPoints;
        this.itemPoints.setVisibility((showPoints ? VISIBLE : GONE));
        toggleDividerIfNeeded();
    }

    /**
     * Zeigt bei Bedarf den Trenner neben den Buttons im Element an
     */
    public void toggleDividerIfNeeded(){
        if(!this.showEdit && !this.showDelete) {
            this.itemDividerView.setVisibility(GONE);
        } else {
            this.itemDividerView.setVisibility(VISIBLE);
        }
    }

    /**
     * Zeigt bei Bedarf den Untertitel des Listenelements an
     */
    public void toggleSubtitleIfNeeded(){
        if(this.itemSubView.getText().length() == 0) {
            this.itemSubView.setVisibility(GONE);
        } else {
            this.itemSubView.setVisibility(VISIBLE);
        }
    }

    /**
     * Fängt alle Klick-Events im Listenelement ab und ruft das passende Callback auf (dient Funktionsfähigkeit von "Löschen" und "Bearbeiten"
     * @param view Angeklickter Button
     */
    @Override
    public void onClick(View view) {
        try {
            if (view.getId() == itemEditBtn.getId()) {
                if(this.onEditCallback != null) onEditCallback.onEditItem();
                return;
            }
            if (view.getId() == itemDeleteBtn.getId()) {
                if(this.onDeleteListener != null) onDeleteListener.onDeleteItem();
            }
        } catch (Exception ignored) {
            // In case a view wasn't defined in init();
        }
    }

    /**
     * Interface zur Behandlung des Löschen-Events
     */
    public interface OnDeleteCallback {
        /**
         * Wird aufgerufen, wenn auf "Löschen" geklickt wurde
         */
        void onDeleteItem();
    }

    /**
     * Interface zur Behandlung des Bearbeiten-Events
     */
    public interface OnEditCallback {
        /**
         * Wird aufgerufen, wenn auf "Bearbeiten" geklickt wurde
         */
        void onEditItem();
    }
}
