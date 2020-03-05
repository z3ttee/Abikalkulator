package de.zitzmanncedric.abicalc.api;

import android.content.Context;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.views.SubjectListItemView;
import lombok.Getter;

public class Seminar extends ListableObject {

    private static Seminar instance;
    @Getter private final int subjectID = -1;

    @Getter private Grade processGrade;
    @Getter private Grade thesisGrade;
    @Getter private Grade presentationGrade;

    public Seminar() {
        super(AppCore.getInstance().getString(R.string.subject_seminar), "", "");
        instance = this;
    }

    @Override
    public SubjectListItemView getListItemView(Context context) {
        return null;
    }

    public static Seminar getInstance() {
        if(instance == null) instance = new Seminar();
        return instance;
    }
}
