package de.zitzmanncedric.abicalc.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import java.util.ArrayList;
import java.util.Objects;

import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.views.AppButton;
import lombok.Getter;
import lombok.Setter;

/**
 * Abstrakte Klasse, um alle Dialogfenster zusammenzufassen und sich wiederholende Funktionen zu zentralisieren.
 */
public abstract class AppDialog extends Dialog {
    private static final String TAG = "AppDialog";

    private @LayoutRes int contentview;

    private String title = "", message = "";
    private Drawable banner;
    private ArrayList<AppButton> buttons = new ArrayList<>();
    private int bannerHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

    @Setter @Getter DialogCallback callback;

    /**
     * Konstruktor der Klasse. Hier werden Standardwerte vom System gesetzt
     * @param context Context zum Zugriff auf App-Resourcen
     */
    AppDialog(@NonNull Context context) {
        super(context);
        this.contentview = R.layout.dialog_general;
        setCancelable(true);
    }
    /**
     * Optionaler Konstruktor der Klasse. Hier kann eine Layout-Resource selbst definiert werden
     * @param context Context zum Zugriff auf App-Resourcen
     * @param contentview ID der Layout-Resource
     */
    AppDialog(@NonNull Context context, @LayoutRes int contentview) {
        super(context);
        this.contentview = contentview;
        setCancelable(true);
    }

    /**
     * Das Dialogfenster wird aufgebaut und das Layout wird gesetzt.
     * @param savedInstanceState Von Android übergeben (Nicht genutzt)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(this.contentview);

        LinearLayout buttonContainerView = findViewById(R.id.dialog_button_container);
        TextView titleView = findViewById(R.id.dialog_title);
        TextView messageView = findViewById(R.id.dialog_message);
        ImageView bannerView = findViewById(R.id.dialog_banner);

        for(AppButton button : this.buttons){
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) button.getLayoutParams();
            if(layoutParams == null) {
                layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            if(this.buttons.indexOf(button) != this.buttons.size()-1) {
                layoutParams.bottomMargin = (int) getContext().getResources().getDimension(R.dimen.default_padding);
            }

            button.setLayoutParams(layoutParams);
            buttonContainerView.addView(button);

            Log.i(TAG, "onCreate: "+button.getId());
        }

        if(titleView != null) titleView.setText(this.title);
        if(messageView != null) messageView.setText(this.message);
        if(bannerView != null) {
            if(this.banner == null){
                bannerView.setVisibility(View.GONE);

                if(titleView != null){
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) titleView.getLayoutParams();
                    layoutParams.topMargin = 0;
                }
            } else {
                bannerView.getLayoutParams().height = this.bannerHeight;
                bannerView.setImageDrawable(this.banner);
                bannerView.requestLayout();
            }
        }

        if(getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    /**
     * Behandelt das Öffnen eines Dialogfensters. Gleichzeitig wird der Hintergrund des Fensters gesetzt und eine Eingangs, sowie Ausgangsanimation festgelegt
     */
    @Override
    public void show() {
        Objects.requireNonNull(getWindow()).getAttributes().windowAnimations = R.style.anim_dialog_scale;
        getWindow().setBackgroundDrawable(new ColorDrawable(getContext().getColor(android.R.color.transparent)));
        super.show();
    }

    /**
     * Fügt einen Button zum Dialog hinzu.
     * @param button Hinzuzufügender Button
     */
    void addButton(AppButton button){
        if(!this.buttons.contains(button)) {
            this.buttons.add(button);
        }
    }

    /**
     * Ändert den Titel des Dialogfensters
     * @param title Titel, der gesetzt werden soll
     */
    @Override
    public void setTitle(@Nullable CharSequence title) {
        this.title = String.valueOf(title);
    }

    /**
     * Ändert den Titel des Dialogfensters
     * @param titleId ID der Resource, die als Titel gesetzt werden soll
     */
    @Override
    public void setTitle(@StringRes int titleId) {
        String s = getContext().getString(titleId);
        setTitle(s);
    }

    /**
     * Ändert die Beschreibung des Dialogfensters
     * @param messageID ID der Resource, die als Beschreibung gesetzt werden soll
     */
    public void setMessage(@StringRes int messageID) {
        String s = getContext().getString(messageID);
        setMessage(s);
    }

    /**
     * Ändert die Beschreibung des Dialogfensters
     * @param description Beschreibung, die gesetzt werden soll
     */
    private void setMessage(CharSequence description) {
        this.message = String.valueOf(description);
    }

    /**
     * Ändert das Icon des Dialogfensters
     * @param bannerID ID der Resource, die als Icon gesetzt werden soll
     */
    public void setBanner(@DrawableRes int bannerID, int height) {
        Drawable s = getContext().getDrawable(bannerID);
        setBanner(s, height);
    }

    /**
     * Ändert das Icon des Dialogfensters
     * @param icon ID der Resource, die als Icon gesetzt werden soll
     */
    private void setBanner(Drawable icon, int height) {
        this.banner = icon;
        this.bannerHeight = height;
    }

    /**
     * Interface, um auf Interaktionen im Fenster zurückzugreifen
     */
    public interface DialogCallback {
        /**
         * Funktion zum behandeln des Klick-Events auf einen Button im Dialog
         * @param button Angeklickter Button
         */
        void onButtonClicked(@Nullable AppButton button);
    }
}
