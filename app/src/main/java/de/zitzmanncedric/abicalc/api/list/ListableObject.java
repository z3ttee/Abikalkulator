package de.zitzmanncedric.abicalc.api.list;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Abstrakte Klasse, um Objekte zu einer Familie zusammenzufassen. Dient der erleichterten Verwaltung von Objekten in Listen
 */
public abstract class ListableObject implements Serializable {

    @Getter @Setter private String title;
    @Getter @Setter private String subtitle;
    @Getter @Setter private String aside;

    /**
     * Leerer Konstruktor
     */
    public ListableObject() { }

    /**
     * Konstruktor, welcher übergebene Standardwerte setzt.
     * @param title Titel des Elements, der in einer Liste angezeigt werden soll
     * @param subtitle Untertitel des Elements
     * @param aside Information, die rechts im Element angezeigt werden soll (z.B. Punktzahl bei einer Note)
     */
    public ListableObject(String title, String subtitle, String aside) {
        this.title = title;
        this.subtitle = subtitle;
        this.aside = aside;
    }
}
