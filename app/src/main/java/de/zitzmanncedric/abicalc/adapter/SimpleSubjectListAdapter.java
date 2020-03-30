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

public class SimpleSubjectListAdapter extends RecyclerView.Adapter<SimpleSubjectListAdapter.ViewHolder> implements DatasetInterface<Subject> {
    private ArrayList<Subject> dataset;
    private ArrayList<Subject> disabled = new ArrayList<>();
    @Setter private OnListItemCallback onCallback;

    public SimpleSubjectListAdapter(ArrayList<Subject> dataset, @Nullable ArrayList<Subject> disabled) {
        this.dataset = dataset;
        if(disabled != null) this.disabled = disabled;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(new SubjectListItemView(parent.getContext()));
    }

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

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        Subject subject = this.dataset.get(holder.getAdapterPosition());

        holder.itemView.setOnClickListener(view -> {
            if(onCallback != null) onCallback.onItemClicked(subject);
        });
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.setOnClickListener((view)->{});
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override
    public void add(Subject object) {
        dataset.add(object);
        this.notifyItemInserted(dataset.size());
    }

    @Override
    public void remove(Subject object) {
        int index = dataset.indexOf(object);
        dataset.remove(object);
        this.notifyItemRemoved(index);
    }

    @Override
    public void set(ArrayList<Subject> list) {
        this.dataset = list;
        this.notifyDataSetChanged();
    }

    @Override
    public void update(Subject old, Subject updated) {
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

        @Getter private SubjectListItemView itemView;
        ViewHolder(@NonNull SubjectListItemView itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
}
