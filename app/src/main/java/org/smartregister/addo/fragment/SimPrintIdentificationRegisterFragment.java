package org.smartregister.addo.fragment;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.smartregister.addo.application.AddoApplication;
import org.smartregister.addo.contract.SimPrintResultFragmentContract;
import org.smartregister.addo.model.SimPrintIdentificationFragmentModel;
import org.smartregister.addo.presenter.SimPrintIdentificationFragmentPresenter;
import org.smartregister.addo.provider.SimPrintIdentificationResultProvider;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;
import java.util.Set;

public class SimPrintIdentificationRegisterFragment extends BaseRegisterFragment implements SimPrintResultFragmentContract.View {


    @Nullable
    @Override
    public android.view.View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        android.view.View view = inflater.inflate(org.smartregister.family.R.layout.fragment_profile_member, container, false);
        rootView = view;//handle to the root


        setupViews(view);
        return view;
    }

    @Override
    public void initializeAdapter(Set<View> visibleColumns) {

       /** AddoRegisterProvider chwRegisterProvider = new AddoRegisterProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, chwRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
        **/
        String query_client = "select last_interacted_with," +
                " relationalid, " +
                " id as _id, " +
                " base_entity_id," +
                " first_name," +
                " middle_name," +
                " last_name," +
                " gender," +
                " unique_id," +
                " entity_type," +
                " dob" +
                " from ec_family_member" +
                " where gender = 'Female' ";

        //String query_client = "select * from ec_family_member where gender='Female'; ";
        Cursor cursor = AddoApplication.getInstance().getRepository().getReadableDatabase().rawQuery(query_client, null);
        SimPrintIdentificationResultProvider provider = new SimPrintIdentificationResultProvider(getActivity(),
                commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        this.clientAdapter = new RecyclerViewPaginatedAdapter(cursor, provider, context().commonrepository(this.tablename));
        this.clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);

        //filter("Mesuo", "", getMainCondition(), false);
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
        this.presenter = new SimPrintIdentificationFragmentPresenter(this, new SimPrintIdentificationFragmentModel(),
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

    }

    @Override
    public RecyclerViewPaginatedAdapter getClientsCursorAdapter() {
        return super.getClientsCursorAdapter();
    }

    @Override
    public void countExecute() {
        //super.countExecute();
        this.clientAdapter.setTotalcount(2);
        this.clientAdapter.setCurrentoffset(0);
    }

}