package de.zitzmanncedric.abicalc.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import lombok.Getter;
import lombok.Setter;

public class SubjectListItemView extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "SubjectListItemView";

    private TextView itemNameView;
    private TextView itemSubView;
    @Getter private TextView itemPoints;
    private ImageView itemEditBtn;
    private ImageView itemDeleteBtn;
    private LinearLayout itemDividerView;

    @Getter private String title,subtitle;
    @Getter private int points, positionInList;
    @Getter private boolean showEdit, showDelete, showPoints;

    @Getter @Setter private RecyclerView correspondingRecycler;
    @Getter @Setter private ArrayList<? extends ListableObject> correspondingDataset = new ArrayList<>();

    @Setter private OnDeleteCallback onDeleteListener;
    @Setter private OnEditCallback onEditCallback;

    public SubjectListItemView(Context context) {
        super(context);
        init(context, null);
    }

    public SubjectListItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SubjectListItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @SuppressLint("SetTextI18n")
    private void init(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater != null) {
            inflater.inflate(R.layout.view_subjectlistitem, this);

            itemNameView = findViewById(R.id.item_name);
            itemSubView = findViewById(R.id.item_subtitle);
            itemPoints = findViewById(R.id.item_points);
            itemEditBtn = findViewById(R.id.item_edit);
            itemDeleteBtn = findViewById(R.id.item_delete);
            itemDividerView = findViewById(R.id.item_divider);

            if(attrs != null) {
                TypedArray attributs = getResources().obtainAttributes(attrs, R.styleable.SubjectListItemView);
                title = attributs.getString(R.styleable.SubjectListItemView_title);
                subtitle = attributs.getString(R.styleable.SubjectListItemView_subtitle);
                points = attributs.getInteger(R.styleable.SubjectListItemView_points, 0);
                showEdit = attributs.getBoolean(R.styleable.SubjectListItemView_showEdit, true);
                showDelete = attributs.getBoolean(R.styleable.SubjectListItemView_showDelete, true);
                showPoints = attributs.getBoolean(R.styleable.SubjectListItemView_showPoints, true);
                attributs.recycle();
            }

            itemNameView.setText(title);
            itemSubView.setText(subtitle);
            itemPoints.setText(points+"P");
            itemEditBtn.setOnClickListener(this);
            itemDeleteBtn.setOnClickListener(this);

            this.itemEditBtn.setVisibility((showEdit ? VISIBLE : GONE));
            this.itemDeleteBtn.setVisibility((showDelete ? VISIBLE : GONE));
            this.itemPoints.setVisibility((showPoints ? VISIBLE : GONE));
            positionInList = 0;

            toggleDividerIfNeeded();
            toggleSubtitleIfNeeded();

            this.setOnClickListener(this);

            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            setLayoutParams(layoutParams);

            ConstraintLayout wrapper = findViewById(R.id.item_wrapper);
            wrapper.setClipToOutline(true);
        }
    }

    public void setTitle(String title) {
        itemNameView.setText(title);
        this.title = title;
    }
    public void setSubtitle(String subtitle) {
        itemSubView.setText(subtitle);
        this.subtitle = subtitle;
        toggleSubtitleIfNeeded();
    }

    public void setPositionInList(int positionInList) {
        this.positionInList = positionInList;
    }

    @SuppressLint("SetTextI18n")
    public void setPoints(int points) {
        itemPoints.setText(points+"P");
        this.points = points;
    }

    public void setTextPoints(String points) {
        itemPoints.setText(points);
    }

    public void setShowEdit(boolean showEdit) {
        this.showEdit = showEdit;
        this.itemEditBtn.setVisibility((showEdit ? VISIBLE : GONE));
        toggleDividerIfNeeded();
    }
    public void setShowDelete(boolean showDelete) {
        this.showDelete = showDelete;
        this.itemDeleteBtn.setVisibility((showDelete ? VISIBLE : GONE));
        toggleDividerIfNeeded();
    }
    public void setShowPoints(boolean showPoints) {
        this.showPoints = showPoints;
        this.itemPoints.setVisibility((showPoints ? VISIBLE : GONE));
        toggleDividerIfNeeded();
    }
    public void toggleDividerIfNeeded(){
        if(!this.showEdit && !this.showDelete) {
            this.itemDividerView.setVisibility(GONE);
        } else {
            this.itemDividerView.setVisibility(VISIBLE);
        }
    }
    public void toggleSubtitleIfNeeded(){
        if(this.itemSubView.getText().length() == 0) {
            this.itemSubView.setVisibility(GONE);
        } else {
            this.itemSubView.setVisibility(VISIBLE);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(enabled) setAlpha(1.0f);
        else setAlpha(0.4f);
    }

    @Override
    public void onClick(View view) {
        try {
            if (view.getId() == itemEditBtn.getId()) {
                if(this.onEditCallback != null) onEditCallback.onEditItem();
                return;
            }
            if (view.getId() == itemDeleteBtn.getId()) {
                if(this.onDeleteListener != null) onDeleteListener.onDeleteItem();
                return;
            }
            if (view.getId() == this.getId()) {
                // Click on whole view
            }
        } catch (NullPointerException ex) {
            // In case a view wasn't defined in init();
            ex.printStackTrace();
        }
    }

    public interface OnDeleteCallback {
        void onDeleteItem();
    }
    public interface OnEditCallback {
        void onEditItem();
    }
}
