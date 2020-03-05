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
import java.util.List;

import de.zitzmanncedric.abicalc.AppUtils;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.listener.OnButtonTouchListener;
import de.zitzmanncedric.abicalc.listener.OnListItemCallback;
import lombok.Setter;

public class RecyclerGridAdapter extends RecyclerView.Adapter<RecyclerGridAdapter.ViewHolder> {

    private Context context;
    private List<? extends ListableObject> dataset;
    @Setter private OnListItemCallback itemCallback;

    public RecyclerGridAdapter(Context context, List<? extends ListableObject> dataset) {
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
        // BindViewHolder asynchronous
        new Handler().post(() -> {
            ListableObject obj = dataset.get(position);

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
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataset.size();
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
}
