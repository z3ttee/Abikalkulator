package de.zitzmanncedric.abicalc.api;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import lombok.Getter;

/**
 * Seminarfach Objekt zur Verwaltung des Seminarfachs. Eine Instanz muss vor Nutzen gebildet werden über getInstance(); (es kann nur ein Seminarfach geben)
 */
public class Seminar extends ListableObject {

    private static Seminar instance;
    @Getter private final int subjectID = -1;

    /**
     * Konstruktor der Klasse. Standardwerte werden vom System gesetzt (betrifft Titel)
     */
    public Seminar() {
        super(AppCore.getInstance().getString(R.string.subject_seminar), "", "");
        instance = this;
    }

    /**
     * Bildet eine Instanz der Klasse, wenn keine vorhanden ist. Andernfalls wird die zuvor erstellte Instanz zurückgegeben
     * @return Instanz der Klasse
     */
    public static Seminar getInstance() {
        if (instance == null) instance = new Seminar();
        return instance;
    }

    /**
     * Stellt ein, ob das Seminarfach eingebracht werden soll
     * @param b Wird true übergeben, wird das Seminarfach eingebracht, andernfalls nicht.
     */
    public void setMinded(boolean b) {
        AppCore.getSharedPreferences().edit().putBoolean("seminarMinded", b).apply();
    }

    /**
     * Prüft, ob das Seminarfach eingebracht wird
     * @return true, wenn das Seminarfach eingebracht wird
     */
    public boolean isMinded(){
        return AppCore.getSharedPreferences().getBoolean("seminarMinded", false);
    }

    /**
     * Ermittelt die ID des Fachs, welches durch das Seminarfach in den Prüfungen ersetzt werden soll
     * @return ID des Fachs als Integer
     */
    public int getReplacedSubjectID(){
        return AppCore.getSharedPreferences().getInt("seminarReplacedSubject", -2);
    }

    /**
     * Stellt die ID des Fachs ein, welches durch das Seminarfach ersetzt werden soll
     * @param subjectID ID des Fachs
     */
    public void setReplacedSubjectID(int subjectID) {
        AppCore.getSharedPreferences().edit().putInt("seminarReplacedSubject", subjectID).apply();
    }
}
