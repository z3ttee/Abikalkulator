package de.zitzmanncedric.abicalc.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.utils.AppUtils;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.views.SubjectListItemView;

/**
 * Zweck dieser Klasse ist zu bestimmen, wie Elemente in einer Liste aufgebaut sind.
 */
public class SubjectStrikeListAdapter extends RecyclerView.Adapter<SubjectStrikeListAdapter.ViewHolder> implements DatasetInterface<ListableObject> {

    private Context context;
    private ArrayList<ListableObject> dataset;

    /**
     * Konstruktor der Klasse
     * @param context Context dient für späteren Zugriff auf App-Resourcen
     * @param dataset Bestimmt die Elemente, die in einer Liste angezeigt werden
     */
    public SubjectStrikeListAdapter(Context context, ArrayList<ListableObject> dataset) {
        this.context = context;
        this.dataset = dataset;
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
        return new ViewHolder(new SubjectListItemView(parent.getContext()));
    }

    /**
     * Ein Element von DATASET wird an den ViewHolder gebunden.
     * @param holder Gibt den ViewHolder an
     * @param position Gibt die Position des Elements in der Liste an
     */
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        ListableObject object = dataset.get(position);

        Animation animation = AppUtils.getListItemEnterAnim();
        animation.setStartOffset(50*position);
        holder.itemView.setAnimation(animation);

        holder.itemView.setShowDelete(false);
        holder.itemView.setShowEdit(false);
        holder.itemView.setTitle(object.getTitle());
        holder.itemView.setShowPoints(true);
        holder.itemView.setTextPoints(context.getString(R.string.exp_term).replace("%", String.valueOf(position+1)));
    }

    /**
     * Gibt die Anzahl an Elementen im Dataset an
     * @return Integer
     */
    @Override
    public int getItemCount() {
        return dataset.size();
    }

    /**
     * Fügt ein Element zum Dataset hinzu
     * @param object Element, das hinzugefügt werden soll
     */
    @Override
    public void add(ListableObject object) {
        dataset.add(object);
        this.notifyItemInserted(dataset.size());
    }

    /**
     * Entfernt ein Element vom Dataset
     * @param object Element, das entfernt werden soll
     */
    @Override
    public void remove(ListableObject object) {
        int index = dataset.indexOf(object);
        dataset.remove(object);
        this.notifyItemRemoved(index);
    }

    /**
     * Setzt das gesamte Dataset neu
     * @param list Dataset, das das alte Dataset ersetzen soll
     */
    @Override
    public void set(ArrayList<ListableObject> list) {
        this.dataset = list;
        this.notifyDataSetChanged();
    }

    /**
     * Aktualisiert ein Element in der Liste
     * @param old Vorherige Element
     * @param updated Aktualisiertes Element
     */
    @Override
    public void update(ListableObject old, ListableObject updated) {
        int index = this.dataset.indexOf(old);
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

        private SubjectListItemView itemView;

        /**
         * Konstruktor, erstellt den View, der in der Liste angezeigt werden soll
         * @param itemView View, der in der Liste angezeigt wird
         */
        ViewHolder(@NonNull SubjectListItemView itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
}
