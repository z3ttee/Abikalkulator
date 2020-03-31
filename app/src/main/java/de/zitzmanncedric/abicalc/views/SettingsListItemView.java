package de.zitzmanncedric.abicalc.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.listener.OnButtonTouchListener;
import lombok.Getter;

public class SettingsListItemView extends LinearLayout {

    private ImageView itemIconView;
    private TextView itemNameView;
    private TextView itemDescView;

    @Getter private Drawable icon;
    @Getter private @StringRes int name;
    @Getter private @StringRes int description;

    public SettingsListItemView(Context context) {
        super(context);
        init(context);
    }

    public SettingsListItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SettingsListItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(inflater != null) {
            View view = inflater.inflate(R.layout.view_settingsitem, this);

            LinearLayout itemContainerView = view.findViewById(R.id.item_container);
            itemIconView = view.findViewById(R.id.item_icon);
            itemNameView = view.findViewById(R.id.item_name);
            itemDescView = view.findViewById(R.id.item_description);

            MarginLayoutParams layoutParams = new MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics());
            layoutParams.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics());
            setLayoutParams(layoutParams);

            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            setForeground(context.getDrawable(outValue.resourceId));
            setBackground(context.getDrawable(R.drawable.background_listitem));

            setClipToOutline(true);
            setOnTouchListener(new OnButtonTouchListener());
        }
    }

    public void setName(@StringRes int res) {
        this.name = res;
        this.itemNameView.setText(this.name);
    }
    public void setDescription(@StringRes int res) {
        this.description = res;
        this.itemDescView.setText(this.description);
    }
    public void setIcon(Drawable icon) {
        this.icon = icon;
        this.itemIconView.setImageDrawable(this.icon);
    }
}
