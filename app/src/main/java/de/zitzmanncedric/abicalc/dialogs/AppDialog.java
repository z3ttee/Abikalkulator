package de.zitzmanncedric.abicalc.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import java.util.Objects;

import de.zitzmanncedric.abicalc.R;

public abstract class AppDialog extends Dialog {

    private @LayoutRes int contentview;

    public AppDialog(@NonNull Context context, @LayoutRes int contentview) {
        super(context);
        this.contentview = contentview;
        setCancelable(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(contentview);

    }

    @Override
    public void show() {
        Objects.requireNonNull(getWindow()).getAttributes().windowAnimations = R.style.dialog_scale;
        getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.background_dialog));
        super.show();
    }
}
