package de.zitzmanncedric.abicalc.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.views.AppButton;
import lombok.Setter;

public class InfoDialog extends AppDialog implements View.OnClickListener {

    private String title;
    private String description;

    private AppButton buttonPositive;

    @Setter private DialogCallback callback;


    public InfoDialog(@NonNull Context context) {
        super(context, R.layout.dialog_info);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView title = findViewById(R.id.dialog_title);
        TextView description = findViewById(R.id.dialog_description);

        buttonPositive = findViewById(R.id.dialog_btn_positive);
        buttonPositive.setOnClickListener(this);

        title.setText(this.title);
        description.setText(this.description);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == buttonPositive.getId()){
            dismiss();
            if(callback != null) {
                callback.onButtonPositiveClicked(buttonPositive);
            }
        }
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

    public interface DialogCallback {
        void onButtonPositiveClicked(AppButton button);
    }
}
