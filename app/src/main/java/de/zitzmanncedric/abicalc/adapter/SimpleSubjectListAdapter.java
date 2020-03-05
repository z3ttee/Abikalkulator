package de.zitzmanncedric.abicalc.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.listener.OnButtonTouchListener;
import de.zitzmanncedric.abicalc.listener.OnListItemCallback;
import de.zitzmanncedric.abicalc.views.SubjectListItemView;
import lombok.Getter;
import lombok.Setter;

public class SimpleSubjectListAdapter extends RecyclerView.Adapter<SimpleSubjectListAdapter.ViewHolder> {
    private static final String TAG = "SimpleSubjectListAdapte";

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
                    if(onCallback != null) onCallback.onItemClicked(position);
                });
                holder.itemView.setOnDeleteListener(() -> {
                    if(onCallback != null) onCallback.onItemDeleted(position);
                });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Getter private SubjectListItemView itemView;
        public ViewHolder(@NonNull SubjectListItemView itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
}
