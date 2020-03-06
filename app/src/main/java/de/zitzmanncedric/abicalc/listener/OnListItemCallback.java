package de.zitzmanncedric.abicalc.listener;

import de.zitzmanncedric.abicalc.api.list.ListableObject;

public interface OnListItemCallback {
    void onItemClicked(ListableObject object);
    void onItemDeleted(ListableObject object);
    void onItemEdit(ListableObject object);
    void onItemLongClicked(ListableObject object);
}
