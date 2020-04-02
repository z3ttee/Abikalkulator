package de.zitzmanncedric.abicalc.fragments.setup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.listener.OnButtonTouchListener;
import de.zitzmanncedric.abicalc.utils.AppUtils;

/**
 * Fragment zum Anzeigen des vierten Schritts in der Ersteinrichtung. Hier können Nebenfächer hinzugefügt werden
 */
public class SetupSettingsFragment extends Fragment {

    private ConstraintLayout containerDefaultPoints;
    private ConstraintLayout containerGoalAverage;
    private ConstraintLayout containerGoalPoints;

    private static EditText defaultPointsInput;
    private static EditText goalAverageInput;
    private static EditText goalPointsInput;

    private Context context;
    private View view;

    /**
     * Konstruktor der Klasse. Setzt Context zur späteren Verarbeitung
     * @param context Context zur späteren Benutzung
     */
    public SetupSettingsFragment(Context context) {
        this.context = context;
    }

    /**
     * Das Layout wird bestimmt.
     * @param inflater Inflater zum erstellen des Layouts
     * @param container Der View, der das Layout umschließt
     * @param savedInstanceState Von Android übergeben (nicht genutzt)
     * @return Erstelltes View-Element aus dem Layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setup_settings, container, false);
    }

    /**
     * Festlegen der UI-Elemente aus dem Layout
     * @param view View-Element
     * @param savedInstanceState Von Android übergeben (nicht genutzt)
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        defaultPointsInput = view.findViewById(R.id.input_default_points);
        goalAverageInput = view.findViewById(R.id.input_goalavg);
        goalPointsInput = view.findViewById(R.id.input_goalpoints);

        defaultPointsInput.setText(String.valueOf(AppCore.getSharedPreferences().getInt("defaultAVG", 10)));
        goalAverageInput.setText(String.valueOf(AppCore.getSharedPreferences().getFloat("goalAVG", 2.3f)));
        goalPointsInput.setText(String.valueOf(AppCore.getSharedPreferences().getInt("goalPoints", 600)));

        this.containerDefaultPoints = view.findViewById(R.id.container_input_defaultpoints);
        this.containerGoalAverage = view.findViewById(R.id.container_input_goalavg);
        this.containerGoalPoints = view.findViewById(R.id.container_input_goalpoints);

        prepareContainer();

        defaultPointsInput.addTextChangedListener(new TextValidator(defaultPointsInput));
        goalAverageInput.addTextChangedListener(new TextValidator(goalAverageInput));
        goalPointsInput.addTextChangedListener(new TextValidator(goalPointsInput));
    }

    /**
     * Ist das Fragment gerade nicht aktiv, wird die Tastatur geschlossen
     */
    @Override
    public void onPause() {
        super.onPause();
        AppUtils.hideKeyboardFrom(context, view);
    }

    /**
     * Wird die App geschlossen, wird auch die Tastatur geschlossen
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        AppUtils.hideKeyboardFrom(context, view);
    }

    /**
     * Private Funktion, zum öffnen der Tastatur und zum fokusieren eines Edittexts
     * @param focused Element, das fokusiert werden soll
     */
    private void showKeyboard(View focused){
        AppUtils.hideKeyboardFrom(context, view);
        AppUtils.showKeyboard(context, view, focused);
    }

    /**
     * Private Funktion zur Vorbereitung aller Rahmenelemente um das eigentliche Edittext
     */
    private void prepareContainer(){
        containerDefaultPoints.setClipToOutline(true);
        containerDefaultPoints.setOnTouchListener(new OnButtonTouchListener());
        containerDefaultPoints.setOnClickListener(v -> {
            showKeyboard(defaultPointsInput);
            defaultPointsInput.setSelection(defaultPointsInput.getText().length());
        });
        containerGoalAverage.setClipToOutline(true);
        containerGoalAverage.setOnTouchListener(new OnButtonTouchListener());
        containerGoalAverage.setOnClickListener(v -> {
            showKeyboard(goalAverageInput);
            goalAverageInput.setSelection(goalAverageInput.getText().length());
        });
        containerGoalPoints.setClipToOutline(true);
        containerGoalPoints.setOnTouchListener(new OnButtonTouchListener());
        containerGoalPoints.setOnClickListener(v -> {
            showKeyboard(goalPointsInput);
            goalPointsInput.setSelection(goalPointsInput.getText().length());
        });
    }

    /**
     * Private innere Klasse. Dient der validation von Eingaben
     */
    private static class TextValidator implements TextWatcher {

        private EditText text;

        /**
         * Konstruktor der Klasse. Setzt das Edittext-Feld, welches behandelt wird
         * @param text
         */
        TextValidator(EditText text) {
            this.text = text;
        }

        /**
         * Implementiert, aber nicht genutzt
         */
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        /**
         * Funktion zum Prüfen von Texteingaben. Sollten Fehler auffallen, werde diese behoben.
         * @param s Eingabe durch den Nutzer
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            text.removeTextChangedListener(this);
            String string = s.toString();
            boolean changed = false;

            if(text == defaultPointsInput){
                // Darf nicht mit 0 starten
                if (s.length() > 1) {
                    if (s.charAt(0) == '0') {
                        s = s.subSequence(1, s.length());
                        string = s.toString();
                        changed = true;
                    }
                }

                try {
                    int input = Integer.parseInt(s.toString());
                    if (input > 15) {
                        string = "15";
                        changed = true;
                    }
                } catch (Exception ignored){ }

            } else if(text == goalAverageInput) {
                // Darf nicht mit 0 starten
                if (s.length() == 1) {
                    if (s.charAt(0) == '0') {
                        string = "1.0";
                        changed = true;
                    }
                }

                // Ziel darf nicht größer als 6.0 und nicht kleiner als 1.0 sein
                if(s.length() > 2) {
                    float input = Float.parseFloat(s.toString());
                    if(input > 6.0f){
                        string = "6.0";
                        changed = true;
                    }
                    if(input < 1.0f){
                        string = "1.0";
                        changed = true;
                    }
                }

                // Nicht länger als 3 Zeichen
                if(s.length() > 3) {
                    s = s.subSequence(0, 3);
                    string = s.toString();
                    changed = true;
                }
            } else if(text == goalPointsInput){
                // Darf nicht mit 0 starten
                if (s.length() > 1) {
                    if (s.charAt(0) == '0') {
                        s = s.subSequence(1, s.length());
                        string = s.toString();
                        changed = true;
                    }
                }

                try {
                    int input = Integer.parseInt(s.toString());
                    if(input > 900){
                        string = "900";
                        changed = true;
                    }
                } catch (Exception ignored){ }
            }


            if(changed) {
                text.setText(string);
                text.setSelection(text.getText().length());
            }

            try {
                if (text == defaultPointsInput){
                    int defaultAVG = Integer.parseInt(string);
                    AppCore.getSharedPreferences().edit().putInt("defaultAVG", defaultAVG).apply();
                } else if(text == goalAverageInput){
                    float goalAVG = Float.parseFloat(string);
                    AppCore.getSharedPreferences().edit().putFloat("goalAVG", goalAVG).apply();
                } else if(text == goalPointsInput){
                    int goalPoints = Integer.parseInt(string);
                    AppCore.getSharedPreferences().edit().putInt("goalPoints", goalPoints).apply();
                }
            } catch (Exception ignored){ }

            text.addTextChangedListener(this);
        }

        /**
         * Implementiert, aber nicht genutzt
         */
        @Override
        public void afterTextChanged(Editable s) { }
    }
}
