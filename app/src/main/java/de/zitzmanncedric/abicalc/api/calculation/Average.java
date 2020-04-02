package de.zitzmanncedric.abicalc.api.calculation;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.api.Grade;
import de.zitzmanncedric.abicalc.api.Seminar;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.database.AppDatabase;
import needle.Needle;
import needle.UiRelatedTask;

/**
 * Klasse mit Funktionen zur Berechnung verschiedener Durchschnittswerte, Punktezahlen und zur Ermittlung streichbarer Fächer
 */
public class Average {
    private static final String TAG = "Average";

    /**
     * Ermittelt den Durchschnittswert für ein bestimmtes Fach in einem bestimmten Halbjahr in einem neuen Thread (asynchron)
     * @param subject Übergabe des betroffenen Fachs
     * @param termID Übergabe der Halbjahres-ID
     * @param callback Interface, zum Abfangen des asynchron berechneten Resultats
     */
    public static void getOfTermAndSubject(Subject subject, int termID, CalcCallback<Integer> callback) {
        Needle.onBackgroundThread().execute(() -> {
            int avg = getOfTermAndSubjectSync(subject, termID);
            callback.onCalcFinished(avg);
        });
    }

    /**
     * Ermittelt den Durchschnittswert für ein bestimmtes Fach in einem bestimmten Halbjahr im gleichen Thread (synchron)
     * @param subject Übergabe des betroffenen Fachs
     * @param termID Übergabe der Halbjahres-ID
     * @return Durchschnitt als Integer
     */
    public static int getOfTermAndSubjectSync(Subject subject, int termID) {
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
            return avg;
        }

        avg = 0;

        // Return average of KAs if these are the only grades
        if(normalFactor.isEmpty()) {
            for(Grade grade : thirdFactor) {
                avg += grade.getValue();
            }
            avg = Math.round((float) avg / (float) thirdFactor.size());
            return avg;
        }

        // Return average of all others if these are the only grades
        if(thirdFactor.isEmpty()) {
            for(Grade grade : normalFactor) {
                avg += grade.getValue();
            }
            avg = Math.round((float) avg / (float) normalFactor.size());
            return avg;
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
        return avg;
    }

    /**
     * Ermittelt den Durchschnittswert aller Noten des Seminarfachs im gleichen Thread (synchron)
     * @return Durchschnittswert als Integer
     */
    public static int getSeminarSync(){
        float avg = 0;

        for(Grade grade : AppDatabase.getInstance().getGradesForSeminar()) {
            if(grade.getType() == Grade.Type.PROCESS) {
                avg += grade.getValue()*0.2f;
            }
            if(grade.getType() == Grade.Type.THESIS) {
                avg += grade.getValue()*0.3f;
            }
            if(grade.getType() == Grade.Type.PRESENTATION) {
                avg += grade.getValue()*0.5f;
            }
        }

        return Math.round(avg);
    }

    /**
     * Gibt den gespeicherten Durchschnitt eines Fachs eines Halbjahres zurück
     * @param subject Angabe des Fachs
     * @param termID Angabe der Halbjahres-ID
     * @return Durchschnitt als Integer
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
     * Berechnet Gesamtnote im Hintergrund aus (asynchron). Es werden die 4 schlechtesten Fächer automatisch gestrichen, aus jedem Halbjahr eins.
     * @param callback Interface, um auf Resultat zurückzugreifen
     */
    public static void getGeneral(CalcCallback<Double> callback){
        Needle.onBackgroundThread().execute(new UiRelatedTask<Double>() {
            @Override
            protected Double doWork() {
                double avg;
                int points = getAllPointsSync();

                // Durchschnitt von 0.9 und weniger vermeiden
                if(points > 822) {
                    avg = 1.0;
                    return avg;
                }

                if(points <= 61) {
                    if(points >= 44){
                        avg = 5.7;
                    } else if(points >= 27) {
                        avg = 5.8;
                    } else if(points >= 10){
                        avg = 5.9;
                    } else {
                        avg = 6.0;
                    }
                    return avg;
                }

                avg = (double) 17 / 3 - ((double) points / (double) 180); // N = 17/3 – (E / 180)

                return avg;
            }

            @Override
            protected void thenDoUiRelatedWork(Double result) {
                callback.onCalcFinished(result);
            }
        });
    }

    /**
     * Berechnet Gesamtpunktezahl im Hintergrund (asynchron). Es werden die 4 schlechtesten Fächer automatisch gestrichen aus jedem Halbjahr eins.
     * @param callback Interface, um auf Resultat zurückzugreifen
     */
    public static void getAllPoints(CalcCallback<Integer> callback) {
        Needle.onBackgroundThread().execute(new UiRelatedTask<Integer>() {
            @Override
            protected Integer doWork() {
                return getAllPointsSync();
            }

            @Override
            protected void thenDoUiRelatedWork(Integer result) {
                callback.onCalcFinished(result);
            }
        });
    }

    /**
     * Berechnet Gesamtpunktezahl im gleichen Thread (synchron). Es werden die 4 schlechtesten Fächer automatisch gestrichen, aus jedem Halbjahr eins.
     * @return Gesamtpunktezahl als Integer
     */
    private static synchronized int getAllPointsSync() {
        int points = 0;
        for(Subject subject : AppDatabase.getInstance().getUserSubjects()) {
            points += subject.getQuickAvgT1()+subject.getQuickAvgT2()+subject.getQuickAvgT3()+subject.getQuickAvgT4();
            if(subject.isExam()) {
                if(!(Seminar.getInstance().isMinded() && Seminar.getInstance().getReplacedSubjectID() == subject.getId())) {
                    points += subject.getQuickAvgTA() * 4;
                }
            }
        }
        if(Seminar.getInstance().isMinded()) {
            points += getSeminarSync()*4;
        }

        HashMap<Integer, Subject> striked = getStrikedSync();

        for(Integer term : striked.keySet()) {
            int avg = getQuickAverageOfTerm(striked.get(term), term);
            points -= avg;
        }

        return points;
    }

    /**
     * Ermittelt im Hintergrund (asynchron) eine Auswahl an streichbaren Fächern. Definitiv 1 pro Halbjahr. Nicht gestrichen wird, wenn Leistungsfach oder letztes Halbjahr eines Prüfungsfachs
     * @param callback Interface. um auf Rückgabe zurückzugreifen
     */
    public static void getStriked(CalcCallback<HashMap<Integer, Subject>> callback){
        Needle.onBackgroundThread().execute(new UiRelatedTask<HashMap<Integer, Subject>>() {
            @Override
            protected HashMap<Integer, Subject> doWork() {
                return getStrikedSync();
            }

            @Override
            protected void thenDoUiRelatedWork(HashMap<Integer, Subject> result) {
                callback.onCalcFinished(result);
            }
        });
    }

    /**
     * Ermittelt im Vordergrund (synchron) eine Auswahl an streichbaren Fächern. Definitiv 1 pro Halbjahr. Nicht gestrichen wird, wenn Leistungsfach oder letztes Halbjahr eines Prüfungsfachs
     * @return Map(Integer, Subject)
     */
    private static HashMap<Integer, Subject> getStrikedSync(){
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
                            if(i == 3 && Seminar.getInstance().getReplacedSubjectID() != subject.getId()) {
                                candidatesIterator.remove();
                            }
                        } else {
                            candidatesIterator.remove();
                        }
                    } else {
                        if(subject.isIntensified()) {
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
