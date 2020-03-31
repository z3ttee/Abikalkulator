package de.zitzmanncedric.abicalc.api;

import androidx.annotation.NonNull;

import java.io.Serializable;
import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import lombok.Getter;
import lombok.Setter;

/**
 * Objekt eines Fachs
 */
public class Subject extends ListableObject implements Serializable, Cloneable {

    @Getter @Setter private int id;
    @Getter @Setter private boolean exam;
    @Getter @Setter private boolean oralExam;
    @Getter @Setter private boolean intensified;

    @Getter @Setter private int quickAvgT1 = AppCore.getSharedPreferences().getInt("defaultAVG", 8);
    @Getter @Setter private int quickAvgT2 = AppCore.getSharedPreferences().getInt("defaultAVG", 8);
    @Getter @Setter private int quickAvgT3 = AppCore.getSharedPreferences().getInt("defaultAVG", 8);
    @Getter @Setter private int quickAvgT4 = AppCore.getSharedPreferences().getInt("defaultAVG", 8);
    @Getter @Setter private int quickAvgTA = AppCore.getSharedPreferences().getInt("defaultAVG", 8);

    @Getter @Setter private long cacheKey = System.currentTimeMillis();

    /**
     * Funktion dient dem Klonen des Objekts
     * @return Geklontes Objekt
     * @throws CloneNotSupportedException Wirft einen Fehler, wenn das Klonen nicht unterstützt wird.
     */
    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
