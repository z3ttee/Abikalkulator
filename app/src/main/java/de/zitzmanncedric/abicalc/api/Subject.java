package de.zitzmanncedric.abicalc.api;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import lombok.Getter;
import lombok.Setter;

public class Subject extends ListableObject implements Serializable, Cloneable {

    @Getter @Setter private int id;
    @Getter @Setter private boolean exam;
    @Getter @Setter private boolean oralExam;
    @Getter @Setter private boolean intensified;
    @Getter @Setter private ArrayList<Term> terms = new ArrayList<>(Arrays.asList(
            new Term(0, new ArrayList<Grade>()),        // 1.
            new Term(1, new ArrayList<Grade>()),        // 2.
            new Term(2, new ArrayList<Grade>()),        // 3.
            new Term(3, new ArrayList<Grade>()),        // 4.
            new Term(4, new ArrayList<Grade>())         // Abi
    ));

    @Getter @Setter private int quickAvgT1 = AppCore.getSharedPreferences().getInt("defaultAVG", 8);
    @Getter @Setter private int quickAvgT2 = AppCore.getSharedPreferences().getInt("defaultAVG", 8);
    @Getter @Setter private int quickAvgT3 = AppCore.getSharedPreferences().getInt("defaultAVG", 8);
    @Getter @Setter private int quickAvgT4 = AppCore.getSharedPreferences().getInt("defaultAVG", 8);
    @Getter @Setter private int quickAvgTA = AppCore.getSharedPreferences().getInt("defaultAVG", 8);

    @Getter @Setter private long cacheKey = System.currentTimeMillis();

    public Subject() { }
    public Subject(int id, String title, boolean exam, boolean oralExam, boolean intensified, ArrayList<Term> terms) {
        super(title, "", "");

        this.id = id;
        this.exam = exam;
        this.oralExam = oralExam;
        this.intensified = intensified;
        this.terms = terms;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
