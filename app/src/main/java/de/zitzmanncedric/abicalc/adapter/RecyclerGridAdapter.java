package de.zitzmanncedric.abicalc.adapter;

import android.content.Context;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.AppUtils;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.listener.OnButtonTouchListener;
import de.zitzmanncedric.abicalc.listener.OnListItemCallback;
import lombok.Setter;

public class RecyclerGridAdapter extends RecyclerView.Adapter<RecyclerGridAdapter.ViewHolder> implements DatasetInterface<ListableObject> {

    private Context context;
    private ArrayList<ListableObject> dataset;
    @Setter private OnListItemCallback itemCallback;

    public RecyclerGridAdapter(Context context, ArrayList<ListableObject> dataset) {
        this.context = context;
        this.dataset = dataset;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.view_subjectgriditem, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        ListableObject obj = dataset.get(position);

        holder.container.setAnimation(AppUtils.getListItemEnterAnim());

        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, value, true);
        holder.container.setForeground(context.getDrawable(value.resourceId));
        holder.container.setClipToOutline(true);
        holder.container.setOnTouchListener(new OnButtonTouchListener(0.96f));
        holder.container.setOnClickListener(v -> {
            if(itemCallback != null) itemCallback.onItemClicked(obj);
        });
        holder.container.setOnLongClickListener(v -> {
            AppUtils.sendHapticFeedback(60, 50);
            if(itemCallback != null) itemCallback.onItemLongClicked(obj);
            return true;
        });

        if(obj instanceof Subject) {
            Subject subject = (Subject) obj;
            holder.titleView.setText(AppDatabase.getInstance().getSubjectShorts().containsKey(subject.getId()) ? AppDatabase.getInstance().getSubjectShorts().get(subject.getId()) : subject.getTitle());
            holder.pointsView.setText(String.valueOf(subject.getQuickAverage()));
            if (((Subject) obj).isExam()) {
                holder.container.setBackground(context.getDrawable(R.drawable.background_listitem_selected));
            }
        } else {
            holder.titleView.setText(String.valueOf(obj.getTitle()));
            holder.pointsView.setText(String.valueOf(obj.getAside()));
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

        private View container;
        private TextView titleView;
        private TextView pointsView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView;
            titleView = itemView.findViewById(R.id.item_name);
            pointsView = itemView.findViewById(R.id.item_points);
        }
    }

    public void update(ArrayList<ListableObject> dataset){
        this.dataset.clear();
        this.dataset = dataset;
        notifyDataSetChanged();
    }
    public void add(Subject subject) {
        this.dataset.add(subject);
        notifyItemInserted(this.dataset.indexOf(subject));
    }
}
