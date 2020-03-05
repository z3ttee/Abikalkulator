package de.zitzmanncedric.abicalc.fragments.subject;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.zitzmanncedric.abicalc.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubjectsFragment extends Fragment {


    public SubjectsFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subjects, container, false);



        return view;
    }

}
