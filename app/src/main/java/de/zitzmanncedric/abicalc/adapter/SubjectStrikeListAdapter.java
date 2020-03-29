package de.zitzmanncedric.abicalc.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.AppUtils;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.listener.OnButtonTouchListener;
import de.zitzmanncedric.abicalc.listener.OnListItemCallback;
import de.zitzmanncedric.abicalc.views.SubjectListItemView;
import lombok.Getter;
import lombok.Setter;

public class SubjectStrikeListAdapter extends RecyclerView.Adapter<SubjectStrikeListAdapter.ViewHolder> implements DatasetInterface<ListableObject> {
    private static final String TAG = "SimpleSubjectListAdapte";

    private Context context;
    private ArrayList<ListableObject> dataset;

    public SubjectStrikeListAdapter(Context context, ArrayList<ListableObject> dataset) {
        this.context = context;
        this.dataset = dataset;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(new SubjectListItemView(parent.getContext()));
    }

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

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override
    public void add(ListableObject object) {
        dataset.add(object);
        this.notifyItemInserted(dataset.size());
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

        @Getter
        private SubjectListItemView itemView;
        ViewHolder(@NonNull SubjectListItemView itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
}
