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

import de.zitzmanncedric.abicalc.utils.AppUtils;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.Seminar;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.calculation.Average;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import de.zitzmanncedric.abicalc.listener.OnButtonTouchListener;
import de.zitzmanncedric.abicalc.listener.OnListItemCallback;
import lombok.Setter;

/**
 * Zweck dieser Klasse ist zu bestimmen, wie Elemente in einer Liste aufgebaut sind.
 */
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

    /**
     * Konstruktor der Klasse
     * @param context Context dient des späteren Zugriffs auf App-Resourcen
     * @param termID ID des aktuellen Halbjahres
     * @param layoutManager LayoutManager, der die Anordnung der Listenelemente verwaltet
     */
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

    /**
     * ViewHolder wird erstellt. Hierin wird später ein Listenelement bestimmt. Es wird zwischen Text und anderen Elementen unterschieden. Handelt es sich bei dem Element in der Liste um einen Text, wird hier das Aussehen festgelegt.
     * @param parent Von Android übergeben
     * @param viewType Von Android übergeben
     * @return Gibt den erstellten ViewHolder zurück
     */
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

    /**
     * Ermittelt den Typ des Elements in der Liste.
     * @param position Position des Elements in der Liste
     * @return Typ des Elements als Integer (1=Text; 0=Kurs)
     */
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

    /**
     * Ein Element von DATASET wird an den ViewHolder gebunden. Das Aussehen des Elements wird bestimmt und Daten werden geladen und gesetzt.
     * @param holder Gibt den ViewHolder an
     * @param position Gibt die Position des Elements in der Liste an
     */
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

    /**
     * Gibt die Anzahl an Elementen im Dataset an
     * @return Integer
     */
    @Override
    public int getItemCount() {
        return dataset.size();
    }

    /**
     * Fügt ein Element zum Dataset hinzu. An bestimmten Positionen werden "Labels" eingefügt, um die Bereiche zu Kennzeichnen
     * @param object Element, das hinzugefügt werden soll
     */
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

    /**
     * Entfernt ein Element vom Dataset
     * @param object Element, das entfernt werden soll
     */
    @Override
    public void remove(ListableObject object) {
        int index = dataset.indexOf(object);
        dataset.remove(object);
        this.notifyItemRemoved(index);
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
        private View container;
        private TextView titleView;
        private TextView pointsView;

        /**
         * Konstruktor, erstellt den View, der in der Liste angezeigt werden soll
         * @param itemView View, der in der Liste angezeigt wird
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView;
            titleView = itemView.findViewById(R.id.item_name);
            pointsView = itemView.findViewById(R.id.item_points);
        }
    }

    /**
     * Klasse zur Verwaltung der y-Länge eines Elements
     */
    private static class SpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
        private int termID;

        /**
         * Konstruktor, setzt die Halbjahres-ID zur weiteren Verarbeitung
         * @param termID Halbjahres-ID
         */
        public SpanSizeLookup(int termID) {
            this.termID = termID;
        }

        /**
         * Ermittelt die y-Länge eines Elements. Text wird über die gesamte Breite dargestellt, während Kurse als Kacheln angezeigt werden
         * @param position Position des Elements in der Liste
         * @return
         */
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
