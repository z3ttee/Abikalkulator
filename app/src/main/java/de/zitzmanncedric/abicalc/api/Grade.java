package de.zitzmanncedric.abicalc.api;

import android.content.Context;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.views.SubjectListItemView;
import lombok.Getter;
import lombok.Setter;

/**
 * Datenobjekt für eine Note
 */
public class Grade extends ListableObject {

    @Getter private int id;
    @Getter private int subjectID;
    @Getter private int termID;
    @Getter @Setter private int value;
    @Getter @Setter private Type type;
    @Getter private long dateCreated;

    public Grade(int id, int subjectID, int termID, int value, Type type) {
        super("", "", "");
        this.id = id;
        this.subjectID = subjectID;
        this.termID = termID;
        this.value = value;
        this.type = type;
        this.dateCreated = System.currentTimeMillis();
    }
    public Grade(int id, int subjectID, int termID, int value, Type type, long date) {
        super("", "", "");
        this.id = id;
        this.subjectID = subjectID;
        this.termID = termID;
        this.value = value;
        this.type = type;
        this.dateCreated = date;
    }

    @Override
    public SubjectListItemView getListItemView(Context context) {
        return null;
    }

    /**
     * Enum zum Festlegen eines Notentyps
     */
    public enum Type {
        LK(0, AppCore.getInstance().getString(R.string.type_lk)),
        KA(1, AppCore.getInstance().getString(R.string.type_ka));

        @Getter private int id;
        @Getter private String title;

        /**
         * Konstrukter für Enumeintrag
         * @param id Nummer zur Identifikation des Typs
         * @param title Name des Typs zum Anzeige in Listen
         */
        Type(int id, String title) {
            this.id = id;
            this.title = title;
        }

        /**
         * Funktion zum finden eines Notentyps mit einer ID
         * @param id Nummer des gesuchten Typs
         * @return Notentyp
         */
        public static Type getByID(int id){
            for(Type type : values()){
                if(type.getId() == id) return type;
            }
            return Type.LK; // Return default if not found
        }
    }
}
