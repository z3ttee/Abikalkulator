package de.zitzmanncedric.abicalc.api;

import android.content.Context;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.views.SubjectListItemView;
import lombok.Getter;
import lombok.Setter;

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

    public static Seminar getInstance() {
        if (instance == null) instance = new Seminar();
        return instance;
    }

    public void setMinded(boolean b) {
        AppCore.getSharedPreferences().edit().putBoolean("seminarMinded", b).apply();
    }
    public boolean isMinded(){
        return AppCore.getSharedPreferences().getBoolean("seminarMinded", false);
    }
    public int getReplacedSubjectID(){
        return AppCore.getSharedPreferences().getInt("seminarReplacedSubject", -2);
    }
    public void setReplacedSubjectID(int subjectID) {
        AppCore.getSharedPreferences().edit().putInt("seminarReplacedSubject", subjectID).apply();
    }
}
