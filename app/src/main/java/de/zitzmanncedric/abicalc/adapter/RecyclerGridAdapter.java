package de.zitzmanncedric.abicalc.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.AppUtils;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.Seminar;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.calculation.Average;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.listener.OnButtonTouchListener;
import de.zitzmanncedric.abicalc.listener.OnListItemCallback;
import lombok.Setter;

public class RecyclerGridAdapter extends RecyclerView.Adapter<RecyclerGridAdapter.ViewHolder> implements DatasetInterface<ListableObject> {

    private static final int VIEWTYPE_LISTITEM = 0;
    private static final int VIEWTYPE_TEXTVIEW = 1;

    private static final int POSITION_TEXT_1 = 0;
    private static final int POSITION_TEXT_2 = 6;
    private static final int POSITION_TEXT_3 = 8;

    private Context context;
    private ArrayList<ListableObject> dataset = new ArrayList<>();
    @Setter private OnListItemCallback itemCallback;
    private int termID;

    public RecyclerGridAdapter(Context context, int termID, GridLayoutManager layoutManager) {
        this.context = context;

        if(termID == 4) {
            this.dataset.add(new ListableObject(context.getString(R.string.label_yourexams), "", "") {});
            if(Seminar.getInstance().isMinded()) {
                this.dataset.add(Seminar.getInstance());
            }
        } else {
            this.dataset.add(new ListableObject(context.getString(R.string.label_yourintensified), "", "") {});
        }
        this.termID = termID;
        layoutManager.setSpanSizeLookup(new SpanSizeLookup(termID));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.view_subjectgriditem, parent, false));

        if(viewType == VIEWTYPE_TEXTVIEW) {
            TextView view = new TextView(new ContextThemeWrapper(parent.getContext(), R.style.TextAppearance_Label));

            ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = (int) parent.getContext().getResources().getDimension(R.dimen.default_padding);
            view.setLayoutParams(layoutParams);

            viewHolder = new ViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if(termID == 4) {
            if(position == POSITION_TEXT_1) {
                return VIEWTYPE_TEXTVIEW;
            }
        } else {
            switch (position) {
                case POSITION_TEXT_1:
                case POSITION_TEXT_2:
                case POSITION_TEXT_3:
                    return VIEWTYPE_TEXTVIEW;
            }
        }

        return VIEWTYPE_LISTITEM;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        ListableObject obj = dataset.get(position);

        if(getItemViewType(position) == VIEWTYPE_TEXTVIEW) {
            TextView textView = (TextView) holder.itemView;
            textView.setText(obj.getTitle());
            Animation animation = AppUtils.getListItemEnterAnim();
            animation.setStartOffset(10 * position);
            textView.setAnimation(animation);
            return;
        }
        if(getItemViewType(position) == VIEWTYPE_LISTITEM) {
            Animation animation = AppUtils.getListItemEnterAnim();
            animation.setStartOffset(10 * position);
            holder.container.setAnimation(animation);

            TypedValue value = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, value, true);
            holder.container.setForeground(context.getDrawable(value.resourceId));
            holder.container.setClipToOutline(true);
            holder.container.setOnTouchListener(new OnButtonTouchListener(0.94f));
            holder.container.setOnClickListener(v -> {
                if (itemCallback != null) itemCallback.onItemClicked(obj);
            });
            holder.container.setOnLongClickListener(v -> {
                AppUtils.sendHapticFeedback(60, 50);
                if (itemCallback != null) itemCallback.onItemLongClicked(obj);
                return true;
            });

            if (obj instanceof Subject) {
                Subject subject = (Subject) obj;
                holder.titleView.setText(AppDatabase.getInstance().getSubjectShorts().containsKey(subject.getId()) ? AppDatabase.getInstance().getSubjectShorts().get(subject.getId()) : subject.getTitle());
                holder.pointsView.setText(String.valueOf(Average.getQuickAverageOfTerm(subject, termID)));
                if (((Subject) obj).isExam()) {
                    if (!(Seminar.getInstance().isMinded() && Seminar.getInstance().getReplacedSubjectID() == ((Subject) obj).getId())) {
                        holder.container.setBackground(context.getDrawable(R.drawable.background_listitem_selected));
                    }
                }
            } else {
                holder.titleView.setText(String.valueOf(obj.getTitle()));
                holder.pointsView.setText(String.valueOf(obj.getAside()));

                if (obj instanceof Seminar) {
                    if (Seminar.getInstance().isMinded()) {
                        holder.container.setBackground(context.getDrawable(R.drawable.background_listitem_selected));
                    }
                }
            }
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

        if(termID != 4) {
            if (dataset.size() == POSITION_TEXT_2) {
                dataset.add(new ListableObject(context.getString(R.string.label_yourseminar), "", "") {});
                this.notifyItemInserted(dataset.size());
                this.notifyItemRangeChanged(dataset.size() - 1, dataset.size());

                dataset.add(Seminar.getInstance());
                this.notifyItemInserted(dataset.size());
                this.notifyItemRangeChanged(dataset.size() - 1, dataset.size());

                dataset.add(new ListableObject(context.getString(R.string.label_yourbasics), "", "") {
                });
                this.notifyItemInserted(dataset.size());
                this.notifyItemRangeChanged(dataset.size() - 1, dataset.size());
            }
        }
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

    private static class SpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
        private int termID;

        public SpanSizeLookup(int termID) {
            this.termID = termID;
        }

        @Override
        public int getSpanSize(int position) {
            if(termID == 4) {
                if(position == POSITION_TEXT_1) {
                    return 3;
                }
                return 1;
            } else {
                switch (position) {
                    case POSITION_TEXT_1:
                    case POSITION_TEXT_2:
                    case POSITION_TEXT_3:
                        return 3;
                    default:
                        return 1;
                }
            }
        }
    }
}
