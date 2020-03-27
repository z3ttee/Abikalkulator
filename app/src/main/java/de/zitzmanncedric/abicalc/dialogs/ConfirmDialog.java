package de.zitzmanncedric.abicalc.dialogs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.views.AppButton;
import lombok.Setter;

public class ConfirmDialog extends AppDialog implements View.OnClickListener {

    private Drawable icon;
    private String title;
    private String description;

    private AppButton buttonPositive;
    private AppButton buttonNegative;

    @Setter private DialogCallback callback;

    public ConfirmDialog(@NonNull Context context) {
        super(context, R.layout.dialog_confirm);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageView icon = findViewById(R.id.dialog_icon);
        TextView title = findViewById(R.id.dialog_title);
        TextView description = findViewById(R.id.dialog_description);

        buttonPositive = findViewById(R.id.dialog_btn_positive);
        buttonNegative = findViewById(R.id.dialog_btn_negative);
        buttonNegative.setOnClickListener(this);
        buttonPositive.setOnClickListener(this);

        icon.setImageDrawable(this.icon);
        title.setText(this.title);
        description.setText(this.description);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
        this.title = String.valueOf(title);
    }
    @Override
    public void setTitle(@StringRes int titleId) {
        String s = getContext().getString(titleId);
        setTitle(s);
    }

    public void setDescription(@StringRes int description) {
        String s = getContext().getString(description);
        setDescription(s);
    }
    public void setDescription(CharSequence description) {
        this.description = String.valueOf(description);
    }
    public void setIcon(@DrawableRes int icon) {
        Drawable s = getContext().getDrawable(icon);
        setDescription(s);
    }
    public void setDescription(Drawable icon) {
        this.icon = icon;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == buttonPositive.getId()){
            if(callback != null) callback.onButtonPositiveClicked(buttonPositive);
            return;
        }
        if(v.getId() == buttonNegative.getId()){
            if(callback != null) callback.onButtonNegativeClicked(buttonNegative);
        }
    }

    public interface DialogCallback {
        void onButtonPositiveClicked(AppButton button);
        void onButtonNegativeClicked(AppButton button);
    }
}
