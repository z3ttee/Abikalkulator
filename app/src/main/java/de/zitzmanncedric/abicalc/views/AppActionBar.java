package de.zitzmanncedric.abicalc.views;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.listener.OnButtonTouchListener;
import lombok.Getter;

public class AppActionBar extends Toolbar {

    private TextView titleView;
    @Getter private ImageView closeView;

    private String title = getContext().getString(R.string.app_name);

    public AppActionBar(Context context) {
        super(context);
        init(context);
    }

    public AppActionBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AppActionBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater != null) {
            View view = inflater.inflate(R.layout.view_actionbar, this);
            titleView = view.findViewById(R.id.toolbar_title);
            closeView = view.findViewById(R.id.btn_close);

            titleView.setText(title);
            closeView.setClipToOutline(true);
            setShowClose(false);
        }
    }

    @Override
    public void setTitle(int resId) {
        this.title = getContext().getString(resId);
        if(titleView != null) titleView.setText(resId);
    }
    @Override
    public void setTitle(CharSequence title) {
        this.title = String.valueOf(title);
        if(titleView != null) titleView.setText(title);
    }

    public void setShowClose(boolean showClose) {
        closeView.setVisibility((showClose ? VISIBLE : GONE));
    }
}
