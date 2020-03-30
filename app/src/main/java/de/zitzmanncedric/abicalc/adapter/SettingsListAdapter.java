package de.zitzmanncedric.abicalc.adapter;

import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.AppUtils;
import de.zitzmanncedric.abicalc.api.settings.SettingsItem;
import de.zitzmanncedric.abicalc.views.SettingsListItemView;

/**
 * Zweck dieser Klasse ist zu bestimmen, wie Elemente in einer Liste aufgebaut sind.
 */
public class SettingsListAdapter extends RecyclerView.Adapter<SettingsListAdapter.ViewHolder> implements DatasetInterface<SettingsItem> {
    private static final String TAG = "SettingsListAdapter";

    private ArrayList<SettingsItem> dataset;
    private Callback callback;

    /**
     * Konstruktor der Klasse. Setzt gleichzeitig ein Callback-Interface
     * @param dataset Bestimmt die Elemente, die in einer Liste angezeigt werden
     * @param callback Setzt ein Callback-Interface, um Interaktion mit Elementen abzufangen
     */
    public SettingsListAdapter(ArrayList<SettingsItem> dataset, Callback callback) {
        this.dataset = dataset;
        this.callback = callback;
    }

    /**
     * ViewHolder wird erstellt. Hierin wird später ein Listenelement bestimmt
     * @param parent Von Android übergeben
     * @param viewType Von Android übergeben
     * @return Gibt den erstellten ViewHolder zurück
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(new SettingsListItemView(parent.getContext()));
    }

    /**
     * Ein Element von DATASET wird an den ViewHolder gebunden.
     * @param holder Gibt den ViewHolder an
     * @param position Gibt die Position des Elements in der Liste an
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SettingsItem item = this.dataset.get(position);

        Animation animation = AppUtils.getListItemEnterAnim();
        animation.setStartOffset(100*position);

        holder.itemView.setAnimation(animation);

        holder.itemView.setName(item.getName());
        holder.itemView.setDescription(item.getDescription());
        holder.itemView.setIcon(item.getIcon());
        holder.itemView.setOnClickListener((view) -> {
            if(this.callback != null) callback.onItemClicked(item);
        });

        Log.i(TAG, "binding: "+item.getName());
    }

    /**
     * Gibt die Anzahl an Elementen im Dataset an
     * @return Integer
     */
    @Override
    public int getItemCount() {
        return this.dataset.size();
    }

    /**
     * Fügt ein Element zum Dataset hinzu
     * @param object Element, das hinzugefügt werden soll
     */
    @Override
    public void add(SettingsItem object) {
        dataset.add(object);
        this.notifyItemInserted(dataset.size());
    }

    /**
     * Entfernt ein Element vom Dataset
     * @param object Element, das entfernt werden soll
     */
    @Override
    public void remove(SettingsItem object) {
        int index = dataset.indexOf(object);
        if(dataset.remove(object)) {
            this.notifyItemRemoved(index);
        }
    }

    /**
     * Setzt das gesamte Dataset neu
     * @param list Dataset, das das alte Dataset ersetzen soll
     */
    @Override
    public void set(ArrayList<SettingsItem> list) {
        this.dataset = list;
        this.notifyDataSetChanged();
    }

    /**
     * Aktualisiert ein Element in der Liste
     * @param old Vorherige Element
     * @param updated Aktualisiertes Element
     */
    @Override
    public void update(SettingsItem old, SettingsItem updated) {
        int index = 0;
        for(SettingsItem object : this.dataset) {
            if(object.getName() == updated.getName()) {
                index = this.dataset.indexOf(object);
            }
        }

        this.dataset.set(index, updated);
        this.notifyItemChanged(index);
    }

    /**
     * Entfernt alle Einträge des Datasets
     */
    @Override
    public void clear() {
        this.dataset.clear();
        this.notifyDataSetChanged();
    }

    /**
     * ViewHolder-Klasse
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private SettingsListItemView itemView;

        /**
         * Konstruktor, erstellt den View, der in der Liste angezeigt werden soll
         * @param itemView View, der in der Liste angezeigt wird
         */
        ViewHolder(@NonNull SettingsListItemView itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }

    /**
     * Interface, um Interaktionen in der Liste abzufangen und zu bearbeiten
     */
    public interface Callback {
        void onItemClicked(SettingsItem item);
    }
}
