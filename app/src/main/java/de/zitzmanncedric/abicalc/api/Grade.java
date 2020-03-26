package de.zitzmanncedric.abicalc.api;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.Serializable;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.views.SubjectListItemView;
import lombok.Getter;
import lombok.Setter;

/**
 * Datenobjekt für eine Note
 */
public class Grade extends ListableObject implements Serializable, Cloneable {

    @Getter @Setter private long id;
    @Getter private int subjectID;
    @Getter private int termID;
    @Getter @Setter private int value;
    @Getter @Setter private Type type;
    @Getter private long dateCreated;

    public Grade(long id, int subjectID, int termID, int value, Type type) {
        super("", "", "");
        this.id = id;
        this.subjectID = subjectID;
        this.termID = termID;
        this.value = value;
        this.type = type;
        this.dateCreated = System.currentTimeMillis();
    }
    public Grade(long id, int subjectID, int termID, int value, Type type, long date) {
        super("", "", "");
        this.id = id;
        this.subjectID = subjectID;
        this.termID = termID;
        this.value = value;
        this.type = type;
        this.dateCreated = date;
    }

    /**
     * Enum zum Festlegen eines Notentyps
     */
    public enum Type implements Cloneable {
        LK(0, AppCore.getInstance().getString(R.string.type_lk)),
        KA(1, AppCore.getInstance().getString(R.string.type_ka)),
        ORAL(2, AppCore.getInstance().getString(R.string.type_oral)),
        EPOCH(3, AppCore.getInstance().getString(R.string.type_epoch)),
        PROCESS(4, AppCore.getInstance().getString(R.string.type_process)),
        THESIS(5, AppCore.getInstance().getString(R.string.type_thesis)),
        PRESENTATION(6, AppCore.getInstance().getString(R.string.type_presentation));

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

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
