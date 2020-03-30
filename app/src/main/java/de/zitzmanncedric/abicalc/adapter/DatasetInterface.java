package de.zitzmanncedric.abicalc.adapter;

import java.util.ArrayList;

/**
 * Interface, welches implementiert werden kann. Implementiert Methoden zur Verwaltung von Listen.
 * @param <T>
 */
public interface DatasetInterface<T> {
    /**
     * Fügt einen Datentyp zur Liste hinzu
     * @param object Datenobjekt
     */
    void add(T object);

    /**
     * Entfernt einen Datentyp von der Liste
     * @param object Datenobjekt
     */
    void remove(T object);

    /**
     * Ersetzt das gesamte Datenset.
     * @param list Neues Datenset
     */
    void set(ArrayList<T> list);

    /**
     * Aktualisiert einen Datentyp in der Liste
     * @param old vorheriges Datenobjekt
     * @param updated aktualisiertes Datenobjekt
     */
    void update(T old, T updated);

    /**
     * Löscht alle Einträge aus dem Datenset
     */
    void clear();
}
