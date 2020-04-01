package de.zitzmanncedric.abicalc.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.calculation.Average;

/**
 * Benutzerdefiniertes View. Wird benutzt um den Gesamtdurchschnitt und Gesamtpunktezahl anzuzeigen
 */
public class AverageView extends LinearLayout {

    private TextView subtitle;
    private TextView amount;
    private ImageView goal;

    /**
     * Konstruktor der Klasse. Ruft init() auf.
     * @param context Context zur Übergabe an Elternklasse
     */
    public AverageView(Context context) {
        super(context);
        init(context);
    }

    /**
     * Konstruktor der Klasse. Ruft init() auf.
     * @param context Context zur Übergabe an Elternklasse
     * @param attrs Übergibt Style-Attribute an die Elternklasse
     */
    public AverageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Konstruktor der Klasse. Ruft init() auf.
     * @param context Context zur Übergabe an Elternklasse
     * @param attrs Übergibt Style-Attribute an die Elternklasse
     * @param defStyleAttr Übergibt die Standard-Style Resource als ID an die Elternklasse
     */
    public AverageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * Funktion zum erstellen des Layouts
     * @param context Context zum Zugriff auf App-Resourcen
     */
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater != null) {
            inflater.inflate(R.layout.view_averageview, this);

            subtitle = findViewById(R.id.averageview_subtitle);
            amount = findViewById(R.id.averageview_amount);
            goal = findViewById(R.id.averageview_goal);
        }
    }

    /**
     * Funktion zum Berechnen des Gesamtdurchschnitts, um diesen anzuzeigen
     * @param callback Interface, um auf das Resultat zurückzugreifen
     */
    public void recalculate(ProgressCallback callback){
        Average.getAllPoints((result) -> {
            subtitle.setText(getContext().getString(R.string.exp_points).replace("%points%", String.valueOf(result)));
            Average.getGeneral((r -> {
                amount.setText(String.valueOf(r).substring(0, 3));

                String goalAvg = String.valueOf(AppCore.getSharedPreferences().getFloat("goalAVG", 2.0f)).substring(0,3);
                String avg = String.valueOf((float) r.doubleValue()).substring(0,3);

                if(avg.equals(goalAvg)){
                    goal.animate().alpha(1f).setDuration(getContext().getResources().getInteger(R.integer.anim_speed_quickly));
                } else {
                    goal.animate().alpha(0f).setDuration(getContext().getResources().getInteger(R.integer.anim_speed_quickly));
                }

                callback.onFinish();
            }));
        });

    }

    /**
     * Callback-Interface
     */
    public interface ProgressCallback {

        /**
         * Funktion, die ausgeführt wird, wenn der Vorgang beendet wurde
         */
        void onFinish();
    }
}
