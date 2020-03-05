package de.zitzmanncedric.abicalc.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.activities.subject.EditGradeActivity;
import de.zitzmanncedric.abicalc.api.Grade;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.listener.OnListItemCallback;
import de.zitzmanncedric.abicalc.views.SubjectListItemView;
import lombok.Setter;

public class AdvancedSubjectListAdapter extends RecyclerView.Adapter<AdvancedSubjectListAdapter.ViewHolder> {
    private static final String TAG = "AdvancedSubjectListAdap";

    private Context context;
    private ArrayList<? extends ListableObject> dataset;
    @Setter private RecyclerView correspondingRecyclerView;
    @Setter private OnListItemCallback onCallback;

    public AdvancedSubjectListAdapter(Context context, ArrayList<? extends ListableObject> dataset) {
        this.dataset = dataset;
        this.context = context;
    }
    public AdvancedSubjectListAdapter(Context context, ArrayList<? extends ListableObject> dataset, OnListItemCallback onCallback) {
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

        if(obj instanceof Grade) {
            Grade grade = (Grade) obj;

            holder.itemView.setTitle(grade.getType().getTitle());
            holder.itemView.setSubtitle(new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date(grade.getDateCreated())));
            holder.itemView.setPoints(grade.getValue());
            holder.itemView.setShowPoints(true);
            holder.itemView.setOnClickListener(v -> {
                if (onCallback != null) onCallback.onItemClicked(obj);
            });

            return;
        }

        if(obj instanceof Subject) {
            Subject subject = (Subject) obj;

            holder.itemView.setTranslationY(30);
            holder.itemView.setAlpha(0);
            holder.itemView.animate().alpha(1).translationY(0).setStartDelay(context.getResources().getInteger(R.integer.anim_delay)).setDuration(context.getResources().getInteger(R.integer.anim_speed));

            holder.itemView.setShowEdit(true);
            holder.itemView.setShowDelete(true);
            holder.itemView.setShowPoints(false);
            holder.itemView.setTitle(subject.getTitle());
            holder.itemView.setSubtitle((subject.isExam() ? context.getString(R.string.exp_examsubject) : ""));
            holder.itemView.setPositionInList(position);
            holder.itemView.setCorrespondingDataset(dataset);
            holder.itemView.setOnDeleteListener(() -> {
                if (onCallback != null) onCallback.onItemDeleted(position);
            });
            holder.itemView.setOnEditCallback(() -> {
                if (onCallback != null) onCallback.onItemEdit(position);
            });

            if (correspondingRecyclerView != null)
                holder.itemView.setCorrespondingRecycler(correspondingRecyclerView);
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private SubjectListItemView itemView;
        public ViewHolder(@NonNull SubjectListItemView itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }

    public void update(ArrayList<? extends ListableObject> dataset){
        this.dataset.clear();
        this.dataset = dataset;
        notifyDataSetChanged();
    }

}
