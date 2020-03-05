package de.zitzmanncedric.abicalc.api;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class Term implements Serializable {

    @Getter private int id;
    @Getter @Setter
    private ArrayList<Grade> grades;

    public Term(int id, ArrayList<Grade> grades) {
        this.id = id;
        this.grades = grades;
    }
}
