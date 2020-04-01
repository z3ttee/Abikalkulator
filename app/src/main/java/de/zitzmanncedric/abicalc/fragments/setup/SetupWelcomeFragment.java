package de.zitzmanncedric.abicalc.fragments.setup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.zitzmanncedric.abicalc.R;

/**
 * Fragment zum Anzeigen des Willkommen-Bildschirms in der Ersteinrichtung
 */
public class SetupWelcomeFragment extends Fragment {

    /**
     * Konstruktor der Klasse. (Durch Veerbung benötigt) (leer)
     */
    public SetupWelcomeFragment() { }

    /**
     * Das Layout wird bestimmt.
     * @param inflater Inflater zum erstellen des Layouts
     * @param container Der View, der das Layout umschließt
     * @param savedInstanceState Von Android übergeben (nicht genutzt)
     * @return Erstelltes View-Element aus dem Layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setup_welcome, container, false);
    }
}
