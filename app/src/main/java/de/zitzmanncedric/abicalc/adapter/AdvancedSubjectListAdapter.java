package de.zitzmanncedric.abicalc.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.zitzmanncedric.abicalc.AppUtils;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.Grade;
import de.zitzmanncedric.abicalc.api.Seminar;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.listener.OnButtonTouchListener;
import de.zitzmanncedric.abicalc.listener.OnListItemCallback;
import de.zitzmanncedric.abicalc.views.SubjectListItemView;
import lombok.Getter;
import lombok.Setter;

/**
 * Zweck dieser Klasse ist zu bestimmen, wie Elemente in einer Liste aufgebaut sind.
 */
public class AdvancedSubjectListAdapter extends RecyclerView.Adapter<AdvancedSubjectListAdapter.ViewHolder> implements DatasetInterface<ListableObject> {

    @Getter private ArrayList<ListableObject> dataset;
    @Setter private RecyclerView correspondingRecyclerView;
    @Setter private OnListItemCallback onCallback;

    /**
     * Konstruktor der Klasse
     * @param dataset Bestimmt die Elemente, die in einer Liste angezeigt werden
     */
    public AdvancedSubjectListAdapter(ArrayList<ListableObject> dataset) {
        this.dataset = dataset;
    }

    /**
     * Optionaler Konstruktor der Klasse. Setzt gleichzeitig ein Callback-Interface
     * @param dataset Bestimmt die Elemente, die in einer Liste angezeigt werden
     * @param onCallback Setzt ein Callback-Interface, um Interaktion mit Elementen abzufangen
     */
    public AdvancedSubjectListAdapter(ArrayList<ListableObject> dataset, OnListItemCallback onCallback) {
        this.dataset = dataset;
        this.onCallback = onCallback;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        ListableObject obj = dataset.get(position);

        holder.itemView.setAnimation(AppUtils.getListItemEnterAnim());

        if(obj instanceof Grade) {
            Grade grade = (Grade) obj;

            int subjectID = grade.getSubjectID();

            holder.itemView.setTitle(grade.getType().getTitle());
            holder.itemView.setSubtitle(new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date(grade.getDateCreated())));
            holder.itemView.setPoints(grade.getValue());
            holder.itemView.setShowPoints(true);
            holder.itemView.setOnTouchListener(new OnButtonTouchListener());
            holder.itemView.setOnClickListener(v -> {
                if (onCallback != null) onCallback.onItemClicked(obj);
            });
            if(subjectID != Seminar.getInstance().getSubjectID()) {
                holder.itemView.setShowDelete(true);
                holder.itemView.setOnDeleteListener(() -> {
                    if (onCallback != null) onCallback.onItemDeleted(grade);
                });
            } else {
                holder.itemView.setShowEdit(true);
                holder.itemView.setOnEditCallback( () -> {
                    if(onCallback != null) onCallback.onItemEdit(grade);
                });
            }

            return;
        }

        if(obj instanceof Subject) {
            Subject subject = (Subject) obj;

            holder.itemView.setShowEdit(true);
            holder.itemView.setShowDelete(true);
            holder.itemView.setShowPoints(false);
            holder.itemView.setTitle(subject.getTitle());

            if(subject.isExam()) {
                if(subject.isOralExam()) {
                    holder.itemView.setSubtitle(holder.itemView.getContext().getString(R.string.exp_oralexamsubject));
                } else {
                    holder.itemView.setSubtitle(holder.itemView.getContext().getString(R.string.exp_examsubject));
                }
            } else {
                holder.itemView.setSubtitle(null);
            }

            holder.itemView.setPositionInList(position);
            holder.itemView.setCorrespondingDataset(dataset);
            holder.itemView.setOnClickListener((view) -> {
                if(onCallback != null) onCallback.onItemClicked(subject);
            });
            holder.itemView.setOnDeleteListener(() -> {
                if (onCallback != null) onCallback.onItemDeleted(subject);
            });
            holder.itemView.setOnEditCallback(() -> {
                if (onCallback != null) onCallback.onItemEdit(subject);
            });

            if (correspondingRecyclerView != null)
                holder.itemView.setCorrespondingRecycler(correspondingRecyclerView);
        }
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
        if(dataset.remove(object)) {
            this.notifyItemRemoved(index);
        }
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
        if(updated instanceof Grade) {
            Grade grade = (Grade) updated;

            int index = 0;
            for(ListableObject object : this.dataset) {
                if(object instanceof Grade) {
                    if(((Grade) object).getId() == grade.getId()) {
                        index = this.dataset.indexOf(object);
                    }
                }
            }

            this.dataset.set(index, updated);
            this.notifyItemChanged(index);
        }

        if(updated instanceof Subject) {
            Subject subject = (Subject) updated;

            int index = 0;
            for(ListableObject object : this.dataset) {
                if(object instanceof Subject) {
                    if(((Subject) object).getId() == subject.getId()) {
                        index = this.dataset.indexOf(object);
                    }
                }
            }

            this.dataset.set(index, updated);
            this.notifyItemChanged(index);
        }
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
        public ViewHolder(@NonNull SubjectListItemView itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }

}
