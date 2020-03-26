package de.zitzmanncedric.abicalc.api.settings;

import android.graphics.drawable.Drawable;

import androidx.annotation.StringRes;

import lombok.Getter;
import lombok.Setter;

public class SettingsItem {

    @Getter @Setter private @StringRes int name;
    @Getter @Setter private @StringRes int description;
    @Getter @Setter private Drawable icon;

    public SettingsItem(int name, int description, Drawable icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
    }
}
