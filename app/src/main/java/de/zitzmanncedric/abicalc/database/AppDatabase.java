package de.zitzmanncedric.abicalc.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.api.Grade;
import de.zitzmanncedric.abicalc.api.Seminar;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.calculation.Average;
import lombok.Getter;

/**
 * Verwaltet die Datenbank der App
 * @author Cedric Zitzmann
 */
public class AppDatabase extends SQLiteOpenHelper {
    private static final String TAG = "AppDatabase";

    @Getter private static AppDatabase instance;
    private static final String TABLE_SUBJECTS = "ac_subjects";
    private static final String TABLE_GRADES = "ac_grades";

    @Getter public HashMap<Integer, Subject> appSubjects = new HashMap<>();
    @Getter public List<Subject> userSubjects = new ArrayList<>();
    @Getter public HashMap<Integer, String> subjectShorts = new HashMap<>();

    public AppDatabase(Context context, int version) {
        super(context, context.getPackageName(), null, version);

        // Load default subjects of the app
        try {
            XmlResourceParser parser = context.getResources().getXml(R.xml.subjects);
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("subject")) {

                    Subject subject = new Subject();

                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        if (parser.getAttributeName(i).equals("title")) {
                            subject.setTitle(context.getResources().getString(parser.getAttributeResourceValue(i, -1)));
                        }
                        if(parser.getAttributeName(i).equals("id")) {
                            subject.setId(Integer.valueOf(parser.getAttributeValue(i)));
                        }
                        if(parser.getAttributeName(i).equals("short")) {
                            subjectShorts.put(subject.getId(), parser.getAttributeValue(i));
                        }
                    }

                    appSubjects.put(subject.getId(), subject);
                }
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }

        // Load users subjects
        Cursor cursor = getReadableDatabase().query(TABLE_SUBJECTS, new String[]{"*"}, "", new String[0], null, null, null);
        if(cursor != null) {

            Log.i(TAG, "AppDatabase: "+cursor.getCount());

            if(cursor.moveToFirst()) {
                do {
                    int subjectIDIndex = cursor.getColumnIndex("subjectID");
                    int examIndex = cursor.getColumnIndex("exam");
                    int oralExamIndex = cursor.getColumnIndex("oralExam");
                    int intensifiedIndex = cursor.getColumnIndex("intensified");

                    int quickAvgT1Index = cursor.getColumnIndex("quickAvgT1");
                    int quickAvgT2Index = cursor.getColumnIndex("quickAvgT2");
                    int quickAvgT3Index = cursor.getColumnIndex("quickAvgT3");
                    int quickAvgT4Index = cursor.getColumnIndex("quickAvgT4");
                    int quickAvgTAIndex = cursor.getColumnIndex("quickAvgTA");

                    int cacheKeyIndex = cursor.getColumnIndex("cacheKey");

                    int subjectID = cursor.getInt(subjectIDIndex);
                    boolean exam = cursor.getInt(examIndex) == 1;
                    boolean oralExam = cursor.getInt(oralExamIndex) == 1;
                    boolean intensified = cursor.getInt(intensifiedIndex) == 1;

                    int quickAvgT1 = cursor.getInt(quickAvgT1Index);
                    int quickAvgT2 = cursor.getInt(quickAvgT2Index);
                    int quickAvgT3 = cursor.getInt(quickAvgT3Index);
                    int quickAvgT4 = cursor.getInt(quickAvgT4Index);
                    int quickAvgTA = cursor.getInt(quickAvgTAIndex);

                    long cacheKey = cursor.getLong(cacheKeyIndex);

                    try {
                        Subject subject = appSubjects.get(subjectID);
                        subject.setExam(exam);
                        subject.setOralExam(oralExam);
                        subject.setIntensified(intensified);

                        subject.setQuickAvgT1(quickAvgT1);
                        subject.setQuickAvgT2(quickAvgT2);
                        subject.setQuickAvgT3(quickAvgT3);
                        subject.setQuickAvgT4(quickAvgT4);
                        subject.setQuickAvgTA(quickAvgTA);

                        subject.setCacheKey(cacheKey);

                        userSubjects.add(subject);
                    } catch (Exception ex) {
                        // TODO: Print error to user
                        ex.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS `"+TABLE_SUBJECTS+"` (" +
                "id INTEGER NOT NULL, " +
                "subjectID INTEGER NOT NULL, " +
                "exam BOOLEAN NOT NULL," +
                "oralExam BOOLEAN NOT NULL," +
                "intensified BOOLEAN NOT NULL," +

                "quickAvgT1 INTEGER NOT NULL," +
                "quickAvgT2 INTEGER NOT NULL," +
                "quickAvgT3 INTEGER NOT NULL," +
                "quickAvgT4 INTEGER NOT NULL," +
                "quickAvgTA INTEGER NOT NULL," +

                "cacheKey LONG NOT NULL," +
                "PRIMARY KEY(id), UNIQUE(subjectID));");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS `"+TABLE_GRADES+"` (" +
                "id INTEGER NOT NULL, " +
                "termID INTEGER NOT NULL, " +
                "subjectID INTEGER NOT NULL, " +
                "value INTEGER NOT NULL," +
                "typeID INTEGER NOT NULL," +
                "date LONG NOT NULL," +
                "PRIMARY KEY(id));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Upgrade entries
    }

    /**
     *
     * @param context
     * @param version
     */
    public static void createInstance(Context context, int version){
        instance = new AppDatabase(context, version);
    }

    /**
     * Creates a database entry of a subject
     * @param subject Subject Object that should be created
     * @return Returns id of newly created entry
     */
    public long createSubjectEntry(Subject subject){
        if(subjectExists(subject.getId())) {
            return updateSubject(subject);
        }

        ContentValues values = new ContentValues();
        values.put("subjectID", subject.getId());
        values.put("exam", subject.isExam());
        values.put("oralExam", subject.isOralExam());
        values.put("intensified", subject.isIntensified());

        values.put("quickAvgT1", subject.getQuickAvgT1());
        values.put("quickAvgT2", subject.getQuickAvgT2());
        values.put("quickAvgT3", subject.getQuickAvgT3());
        values.put("quickAvgT4", subject.getQuickAvgT4());
        values.put("quickAvgTA", subject.getQuickAvgTA());

        values.put("cacheKey", System.currentTimeMillis());

        Log.i(TAG, "createSubjectEntry: created entry for "+subject.getTitle());

        // Return ID of entry
        return getWritableDatabase().insert(TABLE_SUBJECTS, null, values);
    }

    /**
     *
     * @param subject
     * @return
     */
    public int updateSubject(Subject subject) {
        ContentValues values = new ContentValues();
        values.put("subjectID", subject.getId());
        values.put("exam", subject.isExam());
        values.put("oralExam", subject.isOralExam());
        values.put("intensified", subject.isIntensified());

        values.put("quickAvgT1", subject.getQuickAvgT1());
        values.put("quickAvgT2", subject.getQuickAvgT2());
        values.put("quickAvgT3", subject.getQuickAvgT3());
        values.put("quickAvgT4", subject.getQuickAvgT4());
        values.put("quickAvgTA", subject.getQuickAvgTA());

        values.put("cacheKey", System.currentTimeMillis());

        return getWritableDatabase().updateWithOnConflict(TABLE_SUBJECTS, values, "subjectID=?", new String[]{""+subject.getId()}, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public int updateQuickAverage(Subject subject, int termID, int quickAvg) {
        ContentValues values = new ContentValues();
        int index = getUserSubjects().indexOf(subject);

        switch (termID) {
            case 0:
                subject.setQuickAvgT1(quickAvg);
                values.put("quickAvgT1", quickAvg);
            case 1:
                subject.setQuickAvgT2(quickAvg);
                values.put("quickAvgT2", quickAvg);
            case 2:
                subject.setQuickAvgT3(quickAvg);
                values.put("quickAvgT3", quickAvg);
            case 3:
                subject.setQuickAvgT4(quickAvg);
                values.put("quickAvgT4", quickAvg);
            case 4:
                subject.setQuickAvgTA(quickAvg);
                values.put("quickAvgTA", quickAvg);
        }

        getUserSubjects().set(index, subject);
        return getWritableDatabase().updateWithOnConflict(TABLE_SUBJECTS, values, "subjectID=?", new String[]{""+subject.getId()}, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public Subject getUserSubjectByID(int id) {
        for(Subject subject : userSubjects) {
            if(subject.getId() == id) {
                return  subject;
            }
        }
        return userSubjects.get(0);
    }

    /**
     *
     * @param subjectID
     * @return
     */
    public boolean subjectExists(int subjectID) {
        Cursor cursor = getReadableDatabase().query(TABLE_SUBJECTS, new String[]{"id"}, "subjectID=?", new String[]{String.valueOf(subjectID)}, null, null, null);
        boolean b = cursor.getCount() != 0;
        cursor.close();
        return b;
    }

    /**
     *
     * @param subjectID
     * @param grade
     * @return
     */
    // TODO
    public long createGrade(int subjectID, Grade grade) {
        ContentValues values = new ContentValues();
        values.put("subjectID", subjectID);
        values.put("typeID", grade.getType().getId());
        values.put("value", grade.getValue());
        values.put("date", grade.getDateCreated());
        values.put("termID", grade.getTermID());

        long id = getWritableDatabase().insert(TABLE_GRADES, null, values);
        AppDatabase.getInstance().notifyGradesChanged(subjectID, grade.getTermID());

        if(subjectID != Seminar.getInstance().getSubjectID()) {
            AppCore.getSharedPreferences().edit().putInt("currentTerm", grade.getTermID()).apply();
        }
        return id;
    }

    /**
     *
     * @param subjectID
     * @param grade
     * @return
     */
    public long updateGrade(int subjectID, Grade grade) {
        ContentValues values = new ContentValues();
        values.put("subjectID", subjectID);
        values.put("typeID", grade.getType().getId());
        values.put("value", grade.getValue());
        values.put("date", grade.getDateCreated());
        values.put("termID", grade.getTermID());

        long l = getWritableDatabase().updateWithOnConflict(TABLE_GRADES, values, "id=?", new String[]{String.valueOf(grade.getId())}, SQLiteDatabase.CONFLICT_REPLACE);
        AppDatabase.getInstance().notifyGradesChanged(subjectID, grade.getTermID());
        return l;
    }

    public int removeGrade(Grade grade) {
        int i = getWritableDatabase().delete(TABLE_GRADES, "id=?", new String[]{String.valueOf(grade.getId())});
        Subject subject = AppDatabase.getInstance().getUserSubjectByID(grade.getSubjectID());
        AppDatabase.getInstance().notifyGradesChanged(subject.getId(), grade.getTermID());
        return i;
    }

    /**
     * Funktion, um alle Noten eines Halbjahres in einem Fach zu erhalten
     * @param subject Übergibt das Fach
     * @param termID Übergibt das Halbjahr
     * @return Liste aller Noten des angegebenen Fachs in einem Halbjahr
     */
    public ArrayList<Grade> getGradesForTerm(Subject subject, int termID) {
        ArrayList<Grade> grades = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(TABLE_GRADES, new String[]{"*"}, "termID=? AND subjectID=?", new String[]{String.valueOf(termID), String.valueOf(subject.getId())}, null, null, "date");

        if(cursor != null && cursor.moveToFirst()){
            do {
                int idIndex = cursor.getColumnIndex("id");
                int subjectIDIndex = cursor.getColumnIndex("subjectID");
                int typeIDIndex = cursor.getColumnIndex("typeID");
                int valueIndex = cursor.getColumnIndex("value");
                int dateIndex = cursor.getColumnIndex("date");
                int termIDIndex = cursor.getColumnIndex("termID");

                Grade.Type type = Grade.Type.getByID(cursor.getInt(typeIDIndex));

                Grade grade = new Grade(
                        cursor.getInt(idIndex),
                        cursor.getInt(subjectIDIndex),
                        cursor.getInt(termIDIndex),
                        cursor.getInt(valueIndex),
                        type,
                        cursor.getLong(dateIndex));
                grades.add(grade);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return grades;
    }

    /**
     * Funktion, um alle Noten des Seminarfachs zu erhalten
     */
    public ArrayList<Grade> getGradesForSeminar() {
        ArrayList<Grade> grades = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(TABLE_GRADES, new String[]{"*"}, "subjectID=?", new String[]{String.valueOf(Seminar.getInstance().getSubjectID())}, null, null, "date");

        if(cursor != null && cursor.moveToFirst()){
            do {
                int idIndex = cursor.getColumnIndex("id");
                int subjectIDIndex = cursor.getColumnIndex("subjectID");
                int typeIDIndex = cursor.getColumnIndex("typeID");
                int valueIndex = cursor.getColumnIndex("value");
                int dateIndex = cursor.getColumnIndex("date");
                int termIDIndex = cursor.getColumnIndex("termID");

                Grade.Type type = Grade.Type.getByID(cursor.getInt(typeIDIndex));

                Grade grade = new Grade(
                        cursor.getInt(idIndex),
                        cursor.getInt(subjectIDIndex),
                        cursor.getInt(termIDIndex),
                        cursor.getInt(valueIndex),
                        type,
                        cursor.getLong(dateIndex));
                grades.add(grade);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return grades;
    }

    public void notifyGradesChanged(int subjectID, int termID){
        try {
            if (subjectID != Seminar.getInstance().getSubjectID()) {
                Subject subject = getUserSubjectByID(subjectID);

                Average.getOfTermAndSubject(subject, termID, (result -> {
                    updateQuickAverage(subject, termID, result);
                }));
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
