package de.zitzmanncedric.abicalc.fragments.setup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import de.zitzmanncedric.abicalc.activities.subject.AddSubjectActivity;
import de.zitzmanncedric.abicalc.adapter.AdvancedSubjectListAdapter;
import de.zitzmanncedric.abicalc.api.Subject;
import de.zitzmanncedric.abicalc.api.list.ListableObject;
import de.zitzmanncedric.abicalc.dialogs.QuickSubjectEditDialog;
import de.zitzmanncedric.abicalc.listener.OnListItemCallback;
import de.zitzmanncedric.abicalc.utils.AppSerializer;
import de.zitzmanncedric.abicalc.views.AppButton;

public class AddNormalFragment extends Fragment implements OnListItemCallback, View.OnClickListener {

    private AppButton addSubjectBtn;
    private AdvancedSubjectListAdapter adapter;
    private SetupActivity setupActivity;

    private Context context;
    public AddNormalFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_basic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.setupActivity = (SetupActivity) getActivity();

        this.addSubjectBtn = view.findViewById(R.id.btn_setup_addsubject);
        this.addSubjectBtn.setOnClickListener(this);

        this.adapter = new AdvancedSubjectListAdapter(new ArrayList<>(this.setupActivity.getBasics()));
        this.adapter.setOnCallback(this);

        RecyclerView recyclerView = view.findViewById(R.id.setup_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == this.addSubjectBtn.getId()) {

            ArrayList<Subject> disabled = new ArrayList<>();
            disabled.addAll(this.setupActivity.getIntensified());
            disabled.addAll(this.setupActivity.getBasics());

            Intent intent = new Intent(context, AddSubjectActivity.class);
            intent.putExtra("disabled", AppSerializer.serialize(disabled));
            intent.putExtra("onlyOralExam", true);
            intent.putExtra("countOral", this.setupActivity.getCountOralExams());
            intent.putExtra("countWritten", this.setupActivity.getCountWrittenExams());

            startActivityForResult(intent, AppCore.RequestCodes.REQUEST_ADD_SUBJECT);
        }
    }

    @Override
    public void onItemDeleted(ListableObject object) {
        if(object instanceof Subject) {
            if(setupActivity.getBasics().remove(object)) {
                adapter.remove(object);

                if(this.setupActivity.getBasics().size() >= SetupActivity.AMOUNT_BASICS) {
                    addSubjectBtn.setEnabled(false);
                } else {
                    addSubjectBtn.setEnabled(true);
                }
            }
        }
    }

    @Override
    public void onItemEdit(final ListableObject object) {
        try {
            if(object instanceof Subject) {
                Subject old = (Subject) object;
                old.setIntensified(false);

                QuickSubjectEditDialog dialog = new QuickSubjectEditDialog(context, old, setupActivity);
                dialog.setCallback(sbj -> {
                    int index = 0;
                    for(Subject subject : setupActivity.getBasics()){
                        if(subject.getTitle().equals(sbj.getTitle())) index = setupActivity.getBasics().indexOf(subject);
                    }

                    setupActivity.getBasics().set(index, sbj);
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

    /**
     * Unwichtig (nicht genutzt)
     */
    @Override
    public void onItemLongClicked(ListableObject object) { }

    /**
     * Von Android implementiert. Fängt das Resultat durch eine geschlossene Aktivität ab. Bei Erfolg wird das zuvor ausgewählte Element in der Liste angezeigt
     * @param requestCode Code, zur Identifizierung der Anfrage
     * @param resultCode Code, zur Identifizierung des Resultats
     * @param data Zurückgegebene Daten
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AppCore.RequestCodes.REQUEST_ADD_SUBJECT && resultCode == AppCore.ResultCodes.RESULT_OK && data != null){
            Subject subject = (Subject) AppSerializer.deserialize(data.getByteArrayExtra("subjectData"));
            this.setupActivity.getBasics().add(subject);
            adapter.add(subject);

            if(this.setupActivity.getBasics().size() >= SetupActivity.AMOUNT_BASICS) {
                addSubjectBtn.setEnabled(false);
            }
        }
    }
}
