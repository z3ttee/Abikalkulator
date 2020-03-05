package de.zitzmanncedric.abicalc.listener;

import de.zitzmanncedric.abicalc.api.list.ListableObject;

public interface OnListItemCallback {
    void onItemClicked(int position);
    void onItemClicked(ListableObject object);
    void onItemDeleted(int position);
    void onItemEdit(int position);
    void onItemLongClicked(ListableObject object);
}
