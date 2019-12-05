package org.smartregister.addo.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.smartregister.addo.contract.SimPrintResultFragmentContract;
import org.smartregister.addo.presenter.SimPrintIdentificationFrgamentModel;
import org.smartregister.addo.presenter.SimPrintIdentificationFrgamentPresenter;
import org.smartregister.addo.provider.SimPrintIdentificationResultProvider;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;
import java.util.Set;

public class SimPrintIdentificationResultFragment extends BaseRegisterFragment implements SimPrintResultFragmentContract.View {


    @Nullable
    @Override
    public android.view.View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        android.view.View view = inflater.inflate(org.smartregister.family.R.layout.fragment_profile_member, container, false);
        rootView = view;//handle to the root

        setupViews(view);
        return view;
    }

    @Override
    public void initializeAdapter(Set<View> visibleColumns, String familyHead, String primaryCaregiver) {

        SimPrintIdentificationResultProvider provider = new SimPrintIdentificationResultProvider(getActivity(),
                commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler, familyHead, primaryCaregiver);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, provider, context().commonrepository("ec_family_member"));
        clientAdapter.setCurrentlimit(20);
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
        presenter = new SimPrintIdentificationFrgamentPresenter(this, new SimPrintIdentificationFrgamentModel(),
                null, null, null, null);
    }

    @Override
    public void setUniqueID(String s) {

    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {

    }

    @Override
    protected String getMainCondition() {
        return null;
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

    }
}