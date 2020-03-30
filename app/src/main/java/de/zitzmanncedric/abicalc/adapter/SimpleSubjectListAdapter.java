package de.zitzmanncedric.abicalc.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.AppUtils;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.listener.OnButtonTouchListener;
import de.zitzmanncedric.abicalc.listener.OnListItemCallback;
import de.zitzmanncedric.abicalc.views.SubjectListItemView;
import lombok.Getter;
import lombok.Setter;

/**
 * Zweck dieser Klasse ist zu bestimmen, wie Elemente in einer Liste aufgebaut sind.
 */
public class SimpleSubjectListAdapter extends RecyclerView.Adapter<SimpleSubjectListAdapter.ViewHolder> implements DatasetInterface<Subject> {

    private ArrayList<Subject> dataset;
    private ArrayList<Subject> disabled = new ArrayList<>();
    @Setter private OnListItemCallback onCallback;

    /**
     * Konstruktor der Klasse
     * @param dataset Bestimmt die Elemente, die in einer Liste angezeigt werden
     * @param disabled Bestimmt die Elemente, die ausgeblendet werden sollen und demnach nicht zur Auswahl stehen
     */
    public SimpleSubjectListAdapter(ArrayList<Subject> dataset, @Nullable ArrayList<Subject> disabled) {
        this.dataset = dataset;
        if(disabled != null) this.disabled = disabled;
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
        Subject subject = dataset.get(position);

        holder.itemView.setAnimation(AppUtils.getListItemEnterAnim());

        holder.itemView.setShowDelete(false);
        holder.itemView.setShowEdit(false);
        holder.itemView.setTitle(subject.getTitle());
        holder.itemView.setShowPoints(false);
        holder.itemView.setPoints(0);

        if(disabled.contains(subject)) {
            holder.itemView.setEnabled(false);
        }

        holder.itemView.performClick();
        holder.itemView.setOnTouchListener(new OnButtonTouchListener());
        holder.itemView.setOnClickListener(view -> {
            if(onCallback != null) onCallback.onItemClicked(subject);
        });
        holder.itemView.setOnDeleteListener(() -> {
            if(onCallback != null) onCallback.onItemDeleted(subject);
        });
    }

    /**
     * Wenn das UI-Element im Fenster sichtbar wird, so wird ein Klick-Event hinzugefügt.
     * @param holder ViewHolder des Elements
     */
    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        Subject subject = this.dataset.get(holder.getAdapterPosition());

        holder.itemView.setOnClickListener(view -> {
            if(onCallback != null) onCallback.onItemClicked(subject);
        });
    }

    /**
     * Wenn das UI-Element im Fenster unsichtbar wird, so wird das Klick-Event entfernt
     * @param holder ViewHolder des Elements
     */
    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.setOnClickListener((view)->{});
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
    public void add(Subject object) {
        dataset.add(object);
        this.notifyItemInserted(dataset.size());
    }

    /**
     * Entfernt ein Element vom Dataset
     * @param object Element, das entfernt werden soll
     */
    @Override
    public void remove(Subject object) {
        int index = dataset.indexOf(object);
        dataset.remove(object);
        this.notifyItemRemoved(index);
    }

    /**
     * Setzt das gesamte Dataset neu
     * @param list Dataset, das das alte Dataset ersetzen soll
     */
    @Override
    public void set(ArrayList<Subject> list) {
        this.dataset = list;
        this.notifyDataSetChanged();
    }

    /**
     * Aktualisiert ein Element in der Liste
     * @param old Vorherige Element
     * @param updated Aktualisiertes Element
     */
    @Override
    public void update(Subject old, Subject updated) {
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
        @Getter private SubjectListItemView itemView;

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
