package de.zitzmanncedric.abicalc.fragments.setup;


import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.activities.main.SetupActivity;
import de.zitzmanncedric.abicalc.adapter.AdvancedSubjectListAdapter;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.dialogs.QuickSubjectEditDialog;
import de.zitzmanncedric.abicalc.listener.OnActivityToFragment;
import de.zitzmanncedric.abicalc.listener.OnListItemCallback;
import de.zitzmanncedric.abicalc.views.AppButton;

public class AddIntensifiedFragment extends Fragment implements OnActivityToFragment, OnListItemCallback {

    private AppButton continueSetupBtn;
    private AppButton addSubjectBtn;

    private RecyclerView recyclerView;
    private AdvancedSubjectListAdapter adapter;

    private SetupActivity setupActivity;

    public AddIntensifiedFragment(AppButton continueSetupBtn, AppButton addSubjectBtn) {
        this.continueSetupBtn = continueSetupBtn;
        this.addSubjectBtn = addSubjectBtn;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_intensified, container, false);
        if(getActivity() != null) {
            recyclerView = view.findViewById(R.id.setup_recycler_intensified);
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

            ArrayList<ListableObject> objects = new ArrayList<>(((SetupActivity) getActivity()).intensified);
            adapter = new AdvancedSubjectListAdapter(objects);

            adapter.setOnCallback(this);
            adapter.setCorrespondingRecyclerView(recyclerView);
            recyclerView.setAdapter(adapter);

            setupActivity = (SetupActivity) getActivity();
        }
        return view;
    }

    @Override
    public void onActivityToFragment(Activity activity, Object object, int actionCode) {
        try {
            adapter.add((ListableObject) object);
            recyclerView.scrollToPosition(adapter.getItemCount());
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        continueSetupBtn.setText(getString(R.string.btn_continue));

        try {
            if (setupActivity.intensified.size() == SetupActivity.AMOUNT_INTENSIFIED) {
                continueSetupBtn.setEnabled(true);
                addSubjectBtn.setEnabled(false);
            } else {
                addSubjectBtn.setEnabled(true);
                continueSetupBtn.setEnabled(false);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onItemDeleted(ListableObject object) {
        if(object instanceof Subject) {
            Subject subject = (Subject) object;

            if(setupActivity.intensified.remove(object)) {
                adapter.remove(object);
                setupActivity.onFragmentToActivity(this, object, AppCore.ActionCodes.ACTION_LIST_REMOVEITEM);
            }
        }
    }

    @Override
    public void onItemEdit(final ListableObject object) {
        try {
            if(object instanceof Subject) {
                Subject old = (Subject) object;
                old.setIntensified(true);

                QuickSubjectEditDialog dialog = new QuickSubjectEditDialog(getContext(), old, setupActivity);
                dialog.setCallback(sbj -> {
                    int index = setupActivity.intensified.indexOf(old);
                    setupActivity.intensified.set(index, sbj);
                    adapter.update(old, sbj);
                });
                dialog.show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onItemClicked(ListableObject object) {
        onItemEdit(object);
    }

    @Override
    public void onItemLongClicked(ListableObject object) { }
}
