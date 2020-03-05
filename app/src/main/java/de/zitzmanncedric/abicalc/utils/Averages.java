package de.zitzmanncedric.abicalc.utils;

import android.os.Handler;


import androidx.core.util.Consumer;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.Term;

public class Averages {

    public static void calculateAverageForTerm(final Subject subject, final int termID, final Consumer<Integer> result){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Term term = subject.getTerms().get(termID);
                if(term != null) {
                    int avg = AppCore.getSharedPreferences().getInt("defaultAVG", 8);

                    result.accept(avg);
                }

            }
        });
    }

}
