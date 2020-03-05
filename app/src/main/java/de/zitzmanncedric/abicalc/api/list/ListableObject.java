package de.zitzmanncedric.abicalc.api.list;

import android.content.Context;

import de.zitzmanncedric.abicalc.views.SubjectListItemView;
import lombok.Getter;
import lombok.Setter;

public abstract class ListableObject {

    @Getter @Setter private String title;
    @Getter @Setter private String subtitle;
    @Getter @Setter private String aside;

    public ListableObject() { }
    public ListableObject(String title, String subtitle, String aside) {
        this.title = title;
        this.subtitle = subtitle;
        this.aside = aside;
    }

    public abstract SubjectListItemView getListItemView(Context context);
}
