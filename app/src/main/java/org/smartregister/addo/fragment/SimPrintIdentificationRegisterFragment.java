package org.smartregister.addo.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.smartregister.addo.R;
import org.smartregister.addo.contract.SimPrintResultFragmentContract;
import org.smartregister.addo.model.SimPrintIdentificationFragmentModel;
import org.smartregister.addo.presenter.SimPrintIdentificationFragmentPresenter;
import org.smartregister.addo.provider.SimPrintIdentificationResultProvider;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.family.util.Utils;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class SimPrintIdentificationRegisterFragment extends BaseRegisterFragment implements SimPrintResultFragmentContract.View {


    @Override
    protected int getLayout() {
        return R.layout.activity_addo_home;
    }

    @Nullable
    @Override
    public android.view.View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        android.view.View view = inflater.inflate(R.layout.fingerprint_identification_profile, container, false);
        rootView = view;//handle to the root



        Toolbar toolbar = view.findViewById(R.id.register_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);

        toolbar.setContentInsetStartWithNavigation(0);
        toolbar.setVisibility(android.view.View.VISIBLE);



        CustomFontTextView titleView = view.findViewById(R.id.txt_title_label);
        if (titleView != null) {
            titleView.setPadding(0, titleView.getTop(), titleView.getPaddingRight(), titleView.getPaddingBottom());
        }

        setupViews(view);
        return view;
    }

    @Override
    public void initializeAdapter(Set<View> visibleColumns) {

        SimPrintIdentificationResultProvider provider = new SimPrintIdentificationResultProvider(getActivity(),
                commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        this.clientAdapter = new RecyclerViewPaginatedAdapter(null, provider, context().commonrepository(this.tablename));
        this.clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);

    }

    @Override
    public SimPrintResultFragmentContract.Presenter presenter() {
        return (SimPrintResultFragmentContract.Presenter) presenter;
    }

    @Override
    public void setFamilyHead(String familyHead) {

    }

    @Override
    public void setPrimaryCaregiver(String primaryCaregiver) {

    }

    @Override
    public void showNotFoundPopup(String s) {

    }

    @Override
    protected void initializePresenter() {
        ArrayList<String> ids = this.getActivity().getIntent().getStringArrayListExtra("clients");
        this.presenter = new SimPrintIdentificationFragmentPresenter(this, new SimPrintIdentificationFragmentModel(),
                null, ids);
    }

    @Override
    public void setUniqueID(String s) {

    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {

    }

    @Override
    protected String getMainCondition() {
        return this.presenter().getMainCondition();
    }

    @Override
    protected String getDefaultSortQuery() {
        return null;
    }

    @Override
    protected void startRegistration() {

    }

    @Override
    protected void onViewClicked(android.view.View view) {
        if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == "click_view_normal") {
            CommonPersonObjectClient pc = (CommonPersonObjectClient) view.getTag();
            String relational_id = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), "relationalid", false);
            CommonPersonObject patient = org.smartregister.family.util.Utils.context().commonrepository(Utils.metadata().familyRegister.tableName)
                    .findByCaseID(relational_id);
            Intent intent = new Intent(this.getActivity(), org.smartregister.family.util.Utils.metadata().profileActivity);
            intent.putExtra("family_base_entity_id", patient.getCaseId());
            intent.putExtra("family_head",
                    org.smartregister.family.util.Utils.getValue(patient.getColumnmaps(), "family_head", false));
            intent.putExtra("primary_caregiver",
                    org.smartregister.family.util.Utils.getValue(patient.getColumnmaps(), "primary_caregiver", false));
            intent.putExtra("village_town",
                    org.smartregister.family.util.Utils.getValue(patient.getColumnmaps(), "village_town", false));
            intent.putExtra("family_name",
                    org.smartregister.family.util.Utils.getValue(patient.getColumnmaps(), "first_name", false));
            intent.putExtra("go_to_due_page", false);
            this.startActivity(intent);
        }

    }

    @Override
    public RecyclerViewPaginatedAdapter getClientsCursorAdapter() {
        return super.getClientsCursorAdapter();
    }


}