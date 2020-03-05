package de.zitzmanncedric.abicalc.fragments.setup;


import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.zitzmanncedric.abicalc.AppCore;
import de.zitzmanncedric.abicalc.R;
import de.zitzmanncedric.abicalc.activities.setup.SetupActivity;
import de.zitzmanncedric.abicalc.adapter.AdvancedSubjectListAdapter;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.dialogs.QuickSubjectEditDialog;
import de.zitzmanncedric.abicalc.listener.OnActivityToFragment;
import de.zitzmanncedric.abicalc.listener.OnListItemCallback;
import de.zitzmanncedric.abicalc.views.AppButton;

public class AddNormalFragment extends Fragment implements OnActivityToFragment, OnListItemCallback {
    private static final String TAG = "AddNormalFragment";

    private AppButton continueSetupBtn;
    private AppButton addSubjectBtn;
    private RecyclerView recyclerView;

    public AddNormalFragment(AppButton continueSetupBtn, AppButton addSubjectBtn) {
        this.continueSetupBtn = continueSetupBtn;
        this.addSubjectBtn = addSubjectBtn;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_normal, container, false);

        if(getActivity() != null) {
            recyclerView = view.findViewById(R.id.setup_recycler_normal);
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            AdvancedSubjectListAdapter adapter = new AdvancedSubjectListAdapter(getContext(),((SetupActivity) getActivity()).normals);
            adapter.setOnCallback(this);

            adapter.setCorrespondingRecyclerView(recyclerView);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }

    @Override
    public void onActivityToFragment(Activity activity, Object object, int actionCode) {
        try {
            recyclerView.getAdapter().notifyDataSetChanged();
            recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount());
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onItemClicked(int position) {
        // TODO
    }

    @Override
    public void onItemClicked(ListableObject object) {

    }

    @Override
    public void onItemDeleted(int position) {
        ((SetupActivity) getActivity()).onFragmentToActivity(this, position, AppCore.ActionCodes.ACTION_LIST_REMOVEITEM);
    }

    @Override
    public void onItemEdit(final int position) {
        try {
            QuickSubjectEditDialog dialog = new QuickSubjectEditDialog(getContext(), ((SetupActivity) getActivity()).normals.get(position));
            dialog.setCallback(new QuickSubjectEditDialog.DialogCallback() {
                @Override
                public void onCallback(Subject subject) {
                    ((SetupActivity) getActivity()).normals.set(position, subject);
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            });
            dialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onItemLongClicked(ListableObject object) {

    }

    @Override
    public void onResume() {
        super.onResume();
        continueSetupBtn.setText(getString(R.string.btn_finish));

        try {
            if (((SetupActivity) getActivity()).normals.size() == SetupActivity.AMOUNT_NORMALS) {
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
}
