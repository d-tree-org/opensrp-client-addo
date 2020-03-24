package org.smartregister.addo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import org.smartregister.addo.R;
import org.smartregister.addo.contract.EmptyResultFragmentContract;
import org.smartregister.addo.model.SimPrintIdentificationFragmentModel;
import org.smartregister.addo.presenter.EmptyResultFragmentPresenter;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;
import java.util.Set;

public class EmptyResultFragment extends BaseRegisterFragment implements EmptyResultFragmentContract.View {


    @Override
    protected int getLayout() {
        return R.layout.empty_results;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.empty_results,container, false);
        this.rootView = view;

        this.setupViews(view);

        return view;
    }

    @Override
    public void setupViews(View view) {
    }

    @Override
    protected void initializePresenter() {

        this.presenter = new EmptyResultFragmentPresenter(this, new SimPrintIdentificationFragmentModel(), null);

    }


    @Override
    public void setUniqueID(String qrCode) {

    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> advancedSearchFormData) {

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
    protected void onViewClicked(View view) {

    }

    @Override
    public void showNotFoundPopup(String opensrpId) {

    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {

    }

    @Override
    public EmptyResultFragmentContract.Presenter presenter() {
        return (EmptyResultFragmentContract.Presenter) presenter;
    }

    @Override
    public void setFamilyHead(String familyHead) {

    }

    @Override
    public void setPrimaryCaregiver(String primaryCaregiver) {

    }



}
