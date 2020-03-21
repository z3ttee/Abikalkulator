package de.zitzmanncedric.abicalc.api.calculation;

import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.api.Grade;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import needle.Needle;

public class Average {
    private static final String TAG = "Average";

    public static void getOfTermAndSubject(Subject subject, int termID, CalcCallback<Integer> callback) {
        Needle.onBackgroundThread().execute(() -> {
            int avg = AppCore.getSharedPreferences().getInt("defaultAVG", 8);

            ArrayList<Grade> normalFactor = new ArrayList<>();
            ArrayList<Grade> thirdFactor = new ArrayList<>();

            for(Grade grade : AppDatabase.getInstance().getGradesForTerm(subject, termID)){
                if(grade.getType() == Grade.Type.KA) {
                    thirdFactor.add(grade);
                } else {
                    normalFactor.add(grade);
                }
            }

            if(normalFactor.isEmpty() && thirdFactor.isEmpty()) {
                callback.onCalcFinished(avg);
                return;
            }

            avg = 0;

            // Return average of KAs if these are the only grades
            if(normalFactor.isEmpty()) {
                Log.i(TAG, "getOfTermAndSubject: normals empty.");
                for(Grade grade : thirdFactor) {
                    avg += grade.getValue();
                }
                avg = Math.round((float) avg / (float) thirdFactor.size());
                callback.onCalcFinished(avg);
                return;
            }

            // Return average of all others if these are the only grades
            if(thirdFactor.isEmpty()) {
                Log.i(TAG, "getOfTermAndSubject: thirds empty.");
                Log.i(TAG, "getOfTermAndSubject: "+normalFactor.size());
                for(Grade grade : normalFactor) {
                    avg += grade.getValue();
                }
                Log.i(TAG, "getOfTermAndSubject: "+avg);
                avg = Math.round((float) avg / (float) normalFactor.size());
                callback.onCalcFinished(avg);
                return;
            }

            int partA = 0;
            int partB = 0;

            for(Grade grade : thirdFactor) {
                partA += grade.getValue();
            }
            partA = partA / thirdFactor.size();

            for(Grade grade : normalFactor) {
                partB += grade.getValue();
            }
            partB = partB / normalFactor.size();

            avg = Math.round((0.3333f*partA)+0.6666f*partB);
            callback.onCalcFinished(avg);
        });


    }

    /**
     * Gibt den gespeicherten Durchschnitts eines Fachs eines Halbjahres zurück
     * @param subject Angabe des Fachs
     * @param termID Angabe des Halbjahres
     * @return Durchschnitt des Fachs
     */
    public static int getQuickAverageOfTerm(Subject subject, int termID) {
        switch (termID) {
            case 0:
                return subject.getQuickAvgT1();
            case 1:
                return subject.getQuickAvgT2();
            case 2:
                return subject.getQuickAvgT3();
            case 3:
                return subject.getQuickAvgT4();
            case 4:
                return subject.getQuickAvgTA();
            default:
                return AppCore.getSharedPreferences().getInt("defaultAVG", 8);
        }
    }

    /**
     * Berechnet Gesamtnote im Hintergrund aus. Es werden die 4 schlechtesten Fächer automatisch gestrichen aus jedem Halbjahr eins.
     * @param callback Um auf Rückgabe zurückzugreifen
     */
    public static void getGeneral(CalcCallback<Float> callback){
        Needle.onBackgroundThread().execute(() -> {
            float avg;
            int points = getAllPointsSync();
            avg = (5.6667f) - (points / 180f); // N = 17/3 – (E / 180)
            callback.onCalcFinished(avg);
        });
    }

    /**
     * Berechnet Gesamtpunktezahl im Hintergrund. Es werden die 4 schlechtesten Fächer automatisch gestrichen aus jedem Halbjahr eins.
     * @param callback Um auf Rückgabe zurückzugreifen
     */
    public static void getAllPoints(CalcCallback<Integer> callback) {
        Needle.onBackgroundThread().execute(() -> {
            int points = getAllPointsSync();
            callback.onCalcFinished(points);
        });
    }

    /**
     * Berechnet Gesamtpunktezahl im gleichen Thread. Es werden die 4 schlechtesten Fächer automatisch gestrichen aus jedem Halbjahr eins.
     * @return Gibt direkten Wert zurück
     */
    public static int getAllPointsSync() {
        int points = 0;
        for(Subject subject : AppDatabase.getInstance().getUserSubjects()) {
            points += subject.getQuickAvgT1()+subject.getQuickAvgT2()+subject.getQuickAvgT3()+subject.getQuickAvgT4();
            if(subject.isExam()) {
                // TODO: Seminarfach einbringen beachten
                points += subject.getQuickAvgTA()*4;
            }
        }

        HashMap<Integer, Subject> striked = getStrikedSync();

        for(Integer term : striked.keySet()) {
            int avg = getQuickAverageOfTerm(striked.get(term), term);
            points -= avg;
        }

        return points;
    }

    /**
     * Ermittelt im Hintergrund eine Auswahl an streichbaren Fächern. Definitiv 1 pro Halbjahr. Nicht gestrichen wird, wenn Leistungsfach oder letztes Halbjahr eines Prüfungsfachs
     * @param callback Um auf Rückgabe zurückzugreifen
     */
    public static void getStriked(CalcCallback<HashMap<Integer, Subject>> callback){
        Needle.onBackgroundThread().execute(() -> {
            HashMap<Integer, Subject> striked = getStrikedSync();
            callback.onCalcFinished(striked);
        });
    }

    /**
     * Ermittelt im Vordergrund oder gleichen Thread eine Auswahl an streichbaren Fächern. Definitiv 1 pro Halbjahr. Nicht gestrichen wird, wenn Leistungsfach oder letztes Halbjahr eines Prüfungsfachs
     * @return Direkter Wert
     */
    public static HashMap<Integer, Subject> getStrikedSync(){
            HashMap<Integer, Subject> striked = new HashMap<>();

            for(int i = 0; i < 4; i++) {
                int lowestAvg = 15;
                Subject lowest = null;

                ArrayList<Subject> candidates = new ArrayList<>(AppDatabase.getInstance().getUserSubjects());
                Iterator<Subject> candidatesIterator = candidates.iterator();

                while (candidatesIterator.hasNext()) {
                    Subject subject = candidatesIterator.next();

                    if(subject.isExam()){
                        if(subject.isOralExam()) {
                            if(i == 3) {
                                candidatesIterator.remove();
                            }
                        } else {
                            candidatesIterator.remove();
                        }
                    }
                }

                for(Subject subject : candidates) {
                    int strikeCount = 0;

                    for(Subject strike : striked.values()) {
                        if(subject == strike) ++strikeCount;
                    }

                    if(strikeCount < 2) {
                        int avg = getQuickAverageOfTerm(subject, i);
                        if (avg <= lowestAvg) {
                            lowestAvg = avg;
                            lowest = subject;
                        }
                    }
                }

                striked.put(i, lowest);
            }
            return striked;
    }

    /**
     * Interface für Rückgriff auf Rückgabewerte
     * @param <T> Bestimmt erwarteten rückgegebenen Datentyp
     */
    public interface CalcCallback<T> {
        void onCalcFinished(T result);
    }
}
