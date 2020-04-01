package de.zitzmanncedric.abicalc.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import de.zitzmanncedric.abicalc.R;
import lombok.Getter;

/**
 * Benutzerdefinierter Toolbar-View. Ersetzt die von Android standardweise angezeigte Toolbar in einer Aktivität
 */
public class AppToolbar extends Toolbar {
    private static final String TAG = "AppToolbar";

    private TextView titleView;
    @Getter private ImageView closeView;
    @Getter private ImageView saveView;

    private String title = getContext().getString(R.string.app_name);

    public AppToolbar(Context context) {
        super(context);
        init(context);
    }

    public AppToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AppToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater != null) {
            View view = inflater.inflate(R.layout.view_toolbar, this);
            titleView = view.findViewById(R.id.toolbar_title);
            closeView = view.findViewById(R.id.toolbar_btn_close);
            saveView = view.findViewById(R.id.toolbar_btn_save);

            titleView.setText(title);
            closeView.setClipToOutline(true);
            saveView.setClipToOutline(true);
            setShowClose(false);
            setShowSave(false);

            ImageView iconView = view.findViewById(R.id.toolbar_icon);
            String visibility = (iconView.getVisibility() == View.VISIBLE ? "Visible" : (iconView.getVisibility() == View.INVISIBLE ? "Invisible" : "Gone"));
            Log.i(TAG, "init: "+visibility);
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
        closeView.setVisibility((showClose ? VISIBLE : INVISIBLE));
    }
    public void setShowSave(boolean showSave) {
        saveView.setVisibility((showSave ? VISIBLE : INVISIBLE));
    }
}
