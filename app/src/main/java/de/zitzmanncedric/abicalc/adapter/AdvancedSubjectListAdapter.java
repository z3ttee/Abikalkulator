package de.zitzmanncedric.abicalc.adapter;

import android.content.Context;
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
import de.zitzmanncedric.abicalc.listener.OnListItemCallback;
import de.zitzmanncedric.abicalc.views.SubjectListItemView;
import lombok.Setter;

public class AdvancedSubjectListAdapter extends RecyclerView.Adapter<AdvancedSubjectListAdapter.ViewHolder> implements DatasetInterface<ListableObject> {
    private static final String TAG = "AdvancedSubjectListAdap";

    private Context context;
    private ArrayList<ListableObject> dataset;
    @Setter private RecyclerView correspondingRecyclerView;
    @Setter private OnListItemCallback onCallback;

    public AdvancedSubjectListAdapter(Context context, ArrayList<ListableObject> dataset) {
        this.dataset = dataset;
        this.context = context;
    }
    public AdvancedSubjectListAdapter(Context context, ArrayList<ListableObject> dataset, OnListItemCallback onCallback) {
        this.dataset = dataset;
        this.context = context;
        this.onCallback = onCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(new SubjectListItemView(parent.getContext()));
    }

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

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override
    public void add(ListableObject object) {
        dataset.add(object);
        this.notifyItemInserted(dataset.size());
        this.notifyItemRangeChanged(dataset.size()-1, dataset.size());
    }

    @Override
    public void remove(ListableObject object) {
        int index = dataset.indexOf(object);
        dataset.remove(object);
        this.notifyItemRemoved(index);
    }

    @Override
    public void set(ArrayList<ListableObject> list) {
        this.dataset = list;
        this.notifyDataSetChanged();
    }

    @Override
    public void update(ListableObject old, ListableObject updated) {
        int index = this.dataset.indexOf(old);
        this.dataset.set(index, updated);
        this.notifyItemChanged(index);
    }

    @Override
    public void clear() {
        this.dataset.clear();
        this.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private SubjectListItemView itemView;
        public ViewHolder(@NonNull SubjectListItemView itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }

}
