package de.zitzmanncedric.abicalc.sheets;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import de.zitzmanncedric.abicalc.R;

public class SubjectQuickOptionsSheet extends BottomSheetDialog {

    public SubjectQuickOptionsSheet(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sheet_subjectquickoptions);


    }
}
