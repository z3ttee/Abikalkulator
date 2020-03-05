package de.zitzmanncedric.abicalc.api;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.views.SubjectListItemView;
import lombok.Getter;
import lombok.Setter;

public class Subject extends ListableObject implements Serializable {

    @Getter @Setter private int id;
    @Getter @Setter private boolean exam;
    @Getter @Setter private boolean intensified;
    @Getter @Setter private ArrayList<Term> terms = new ArrayList<>(Arrays.asList(
            new Term(0, new ArrayList<Grade>()),        // 1.
            new Term(1, new ArrayList<Grade>()),        // 2.
            new Term(2, new ArrayList<Grade>()),        // 3.
            new Term(3, new ArrayList<Grade>()),        // 4.
            new Term(4, new ArrayList<Grade>())         // Abi
    ));
    @Getter @Setter private int quickAverage = AppCore.getSharedPreferences().getInt("defaultAVG", 8);
    @Getter @Setter private long cacheKey = System.currentTimeMillis();

    public Subject() { }
    public Subject(int id, String title, boolean exam, boolean intensified, ArrayList<Term> terms, int quickAverage) {
        super(title, "", String.valueOf(quickAverage));

        this.id = id;
        this.exam = exam;
        this.intensified = intensified;
        this.terms = terms;
        this.quickAverage = quickAverage;
    }

    public void syncWithDatabase(){
        // TODO: Database sync
    }

    @Override
    public SubjectListItemView getListItemView(Context context) {
        SubjectListItemView itemView = new SubjectListItemView(context);

        itemView.setTitle(getTitle());
        itemView.setSubtitle((isExam() ? context.getString(R.string.exp_examsubject) : ""));
        itemView.setPoints(quickAverage);
        itemView.setShowPoints(true);
        itemView.setShowEdit(false);
        itemView.setShowDelete(false);
        return itemView;
    }
}
