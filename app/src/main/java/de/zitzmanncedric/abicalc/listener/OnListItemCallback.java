package de.zitzmanncedric.abicalc.listener;

import de.zitzmanncedric.abicalc.api.list.ListableObject;

/**
 * Interface, welches implementiert werden kann, um Interaktionen mit Listenelementen abzufangen
 */
public interface OnListItemCallback {
    /**
     * Funktion wird aufgerufen, wenn auf ein Listenelement geklickt wurde
     * @param object Angeklicktes Element
     */
    void onItemClicked(ListableObject object);

    /**
     * Funktion wird aufgerufen, wenn ein Listenelement gelöscht wurde
     * @param object Angeklicktes Element
     */
    void onItemDeleted(ListableObject object);

    /**
     * Funktion wird aufgerufen, wenn bei einem Listenelement auf "Bearbeiten" geklickt wurde
     * @param object Angeklicktes Element
     */
    void onItemEdit(ListableObject object);

    /**
     * Funktion wird aufgerufen, wenn auf ein Listenelement lange geklickt wurde
     * @param object Angeklicktes Element
     */
    void onItemLongClicked(ListableObject object);
}
