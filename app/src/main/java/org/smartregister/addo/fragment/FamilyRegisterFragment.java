package org.smartregister.addo.fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.addo.BuildConfig;
import org.smartregister.addo.R;
import org.smartregister.addo.activity.SimPrintIdentificationRegisterActivity;
import org.smartregister.addo.contract.RegisterFragmentContract;
import org.smartregister.addo.custom_views.NavigationMenu;
import org.smartregister.addo.model.AddoRegisterProvider;
import org.smartregister.addo.model.FamilyRegisterFragmentModel;
import org.smartregister.addo.presenter.FamilyRegisterFragmentPresenter;
import org.smartregister.addo.util.Constants;
import org.smartregister.addo.util.JsonFormUtils;
import org.smartregister.addo.util.QueryBuilder;
import org.smartregister.addo.util.Utils;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.fragment.BaseFamilyRegisterFragment;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.simprint.OnDialogButtonClick;
import org.smartregister.simprint.SimPrintsIdentification;
import org.smartregister.simprint.SimPrintsIdentifyActivity;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

public class FamilyRegisterFragment extends BaseFamilyRegisterFragment {

    private View view;
    private View dueOnlyLayout;
    private TextView tvScanFPMessage, tvScanFPMessageInstruction;
    private ImageView ivFScan, fingerPrintPointer;
    private ImageButton buttonSearchCancel;

    private boolean dueFilterActive = false;
    private static final String DUE_FILTER_TAG = "PRESSED";

    private static final int IDENTIFY_RESULT_CODE = 4061;

    @Override
    protected int getLayout() {
        return R.layout.activity_addo_home;
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        this.view = view;

        Toolbar toolbar = view.findViewById(R.id.register_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);

        toolbar.setContentInsetStartWithNavigation(0);

        NavigationMenu.getInstance(this.getActivity(), null, toolbar);

        View navBarContainer = view.findViewById(R.id.register_nav_bar_container);
        navBarContainer.setFocusable(false);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        View searchBarLayout = view.findViewById(R.id.search_bar_layout);
        searchBarLayout.setLayoutParams(params);
        searchBarLayout.setBackgroundResource(R.color.addo_primary);
        searchBarLayout.setPadding(
                searchBarLayout.getPaddingLeft(),
                searchBarLayout.getPaddingTop(),
                searchBarLayout.getPaddingRight(),
                (int) Utils.convertDpToPixel(10.0F, this.getActivity())
        );

        CustomFontTextView titleView = view.findViewById(R.id.txt_title_label);
        if (titleView != null) {
            titleView.setText(R.string.addo_app_home);
            titleView.setPadding(0, titleView.getTop(), titleView.getPaddingRight(),
                    titleView.getPaddingBottom());
        }

        View topLeftLayout = view.findViewById(R.id.top_left_layout);
        topLeftLayout.setVisibility(View.GONE);

        View topRightLayout = view.findViewById(R.id.top_right_layout);
        topRightLayout.setVisibility(View.GONE);

        View sortFilterBarLayout = view.findViewById(R.id.register_sort_filter_bar_layout);
        sortFilterBarLayout.setVisibility(View.GONE);

        View filterSortLayout = view.findViewById(R.id.filter_sort_layout);
        filterSortLayout.setVisibility(View.GONE);

        dueOnlyLayout = view.findViewById(R.id.due_only_layout);
        dueOnlyLayout.setVisibility(View.GONE);

        dueOnlyLayout.setOnClickListener(registerActionHandler);
        clientsView.setVisibility(View.GONE);

        ivFScan = view.findViewById(R.id.ivFScan);
        tvScanFPMessage = view.findViewById(R.id.tvScanFPMessage);
        tvScanFPMessageInstruction = view.findViewById(R.id.tvScanFPMessageInstruction);
        fingerPrintPointer = view.findViewById(R.id.fingerPrintPointer);

        buttonSearchCancel = view.findViewById(R.id.btn_search_cancel);
        buttonSearchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                buttonSearchCancel.setVisibility(View.GONE);

                ivFScan.setVisibility(View.VISIBLE);
                tvScanFPMessage.setVisibility(View.VISIBLE);
                tvScanFPMessageInstruction.setVisibility(View.VISIBLE);
                fingerPrintPointer.setVisibility(View.VISIBLE);
                clientsView.setVisibility(View.GONE);
            }
        });

        if (getSearchView() != null) {
            getSearchView().setBackgroundResource(org.smartregister.family.R.color.white);
            getSearchView().setCompoundDrawablesWithIntrinsicBounds(org.smartregister.family.R.drawable.ic_icon_search, 0, 0, 0);
            getSearchView().setTextColor(this.getResources().getColor(R.color.text_black));
        }

        TextView skipToSearch = view.findViewById(R.id.skip_to_search);
        skipToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseRegisterActivity) getContext()).switchToFragment(2);
            }
        });

    }

    @Override
    protected void initializePresenter() {

        if (getActivity() == null) {
            return;
        }

        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new FamilyRegisterFragmentPresenter(this, new FamilyRegisterFragmentModel(), viewConfigurationIdentifier);

    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {

    }

    public void fingerprintScannedSuccessfully(String uniqueId) {
        /**
         * Search from the clients list to fing the client with the Identifier(simprintsID)
         * get the client's uniqueID
         * pass the unique id to filter so as to filter the list
         */

        if (StringUtils.isNotEmpty(uniqueId)) {
            filter(uniqueId, "", getMainCondition(), false);
            ivFScan.setVisibility(View.GONE);
            tvScanFPMessage.setVisibility(View.GONE);
            tvScanFPMessageInstruction.setVisibility(View.GONE);
            fingerPrintPointer.setVisibility(View.GONE);
            clientsView.setVisibility(View.VISIBLE);
        } else {
            tvScanFPMessage.setText("Fingerprint not found");
        }
    }

    public void onIdentificationFromSimPrints(ArrayList<SimPrintsIdentification> simPrintsIdentifications,
                                              String sessionId) {



        if (simPrintsIdentifications.isEmpty()) {

            // Need to implement a notification or something when there is no FP from SIMPRINT
            showFingerPrintFail(this.getActivity(), new OnDialogButtonClick() {
                @Override
                public void onOkButtonClick() {
                    SimPrintsIdentifyActivity.startSimprintsIdentifyActivity(getActivity(),
                            BuildConfig.SIMPRINT_MODULE_ID, IDENTIFY_RESULT_CODE);
                }

                @Override
                public void onCancelButtonClick() {
                    Intent returnIntent = new Intent();

                }
            });
        } else {
            // This will just find the basentity ids and pass them to the SimPrint Identification Activity but we also need guids for confirmation

            HashMap<String, String> baseEntityGuid = new HashMap<String, String>();
            for (SimPrintsIdentification simPrintsIdentification : simPrintsIdentifications) {
                baseEntityGuid.put(JsonFormUtils.lookForClientsBaseEntityId(simPrintsIdentification.getGuid()), simPrintsIdentification.getGuid());
            }
            Intent intent = new Intent(this.getActivity(), SimPrintIdentificationRegisterActivity.class);
            intent.putExtra("baseids_guids", baseEntityGuid);
            intent.putExtra("session_id", sessionId);
            startActivity(intent);
        }

    }

    private void showFingerPrintFail(Context context, final OnDialogButtonClick onDialogButtonClick){
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setMessage(getString(R.string.no_client_found));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(org.smartregister.simprint.R.string.scan_again), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDialogButtonClick.onOkButtonClick();
                alertDialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(org.smartregister.simprint.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDialogButtonClick.onCancelButtonClick();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    protected String getMainCondition() {
        return presenter().getMainCondition();
    }

    @Override
    protected String getDefaultSortQuery() {
        return presenter().getDefaultSortQuery();
    }

    @Override
    protected void onResumption() {
        if (dueFilterActive && dueOnlyLayout != null) {
            dueFilter(dueOnlyLayout);
        }
        super.onResumption();
    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        AddoRegisterProvider addoRegisterProvider = new AddoRegisterProvider(getActivity(),
                commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, addoRegisterProvider,
                context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    public RegisterFragmentContract.Presenter presenter() {
        return (RegisterFragmentContract.Presenter) presenter;
    }

    public void dueFilter(String mainConditionString) {
        this.joinTable = null;
        super.filter(searchText(), "", mainConditionString, false);
    }

    @Override
    protected void onViewClicked(View view) {
        //super.onViewClicked(view)

        if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == "click_view_normal") {
            goToPatientDetailActivity((CommonPersonObjectClient) view.getTag(), false);
        } else if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == "click_dosage_status") {
            Toast.makeText(getActivity(), "Here dosage implement", Toast.LENGTH_SHORT).show();
        }

        switch (view.getId()) {
            case R.id.due_only_layout:
                toggleFilterSelection(view);
                break;
        }

    }

    public void goToPatientDetailActivity(CommonPersonObjectClient patient, boolean goToDuePage) {

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
        intent.putExtra("go_to_due_page", goToDuePage);
        this.startActivity(intent);

    }

    @Override
    public void onSyncInProgress(FetchStatus fetchStatus) {

        if (!SyncStatusBroadcastReceiver.getInstance().isSyncing() &&
                (FetchStatus.fetched.equals(fetchStatus) || FetchStatus.nothingFetched.equals(fetchStatus)) &&
                dueFilterActive && dueOnlyLayout != null) {
            Toast.makeText(this.getActivity(), getString(R.string.sync_complete), Toast.LENGTH_SHORT).show();
            refreshSyncProgressSpinner();
        } else {
            super.onSyncInProgress(fetchStatus);
        }
    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {

        if (!SyncStatusBroadcastReceiver.getInstance().isSyncing() &&
                (FetchStatus.fetched.equals(fetchStatus) || FetchStatus.nothingFetched.equals(fetchStatus)) &&
                (dueFilterActive && dueOnlyLayout != null)) {
            Toast.makeText(this.getActivity(), getString(R.string.sync_complete), Toast.LENGTH_SHORT).show();
            refreshSyncProgressSpinner();
        } else {
            super.onSyncComplete(fetchStatus);
        }
    }

    public void toggleFilterSelection(View dueOnlyLayout) {
        if (dueOnlyLayout != null) {
            if (dueOnlyLayout.getTag() == null) {
                dueFilterActive = true;
                dueFilter(dueOnlyLayout);
            } else if (dueOnlyLayout.getTag().toString().equals(DUE_FILTER_TAG)) {
                dueFilterActive = false;
                normalFilter(dueOnlyLayout);
            }
        }
    }

    public void dueFilter(View dueOnlyLayout) {

        dueFilter(presenter().getDueFilterCondition());
        dueOnlyLayout.setTag(DUE_FILTER_TAG);
        switchViews(dueOnlyLayout, false);
    }

    private void normalFilter(View dueOnlyLayout) {
        filter(searchText(), "", presenter().getMainCondition(), false);
        dueOnlyLayout.setTag(null);
        switchViews(dueOnlyLayout, false);
        Toast.makeText(this.getActivity(), R.string.confirm_remove_text, Toast.LENGTH_SHORT).show();
        //clientsView.setVisibility(View.VISIBLE);

    }

    private String searchText() {
        return (getSearchView() == null) ? "" : getSearchView().getText().toString();
    }

    private void switchViews(View dueOnlyLayout, boolean isPress) {
        TextView dueOnlyTextView = dueOnlyLayout.findViewById(R.id.due_only_text_view);
        if (isPress) {
            dueOnlyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_due_filter_on, 0);
        } else {
            dueOnlyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_due_filter_off, 0);
        }
    }

    private String dueFilterAndSortQuery() {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(mainSelect);
        String query = "";
        try {
            if (isValidFilterForFts(commonRepository())) {
                String sql = sqb
                        .searchQueryFts(tablename, joinTable, mainCondition, filters, Sortqueries,
                                clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset());
                sql = sql.replace(CommonFtsObject.idColumn, CommonFtsObject.relationalIdColumn);
                sql = sql.replace(CommonFtsObject.searchTableName(Constants.TABLE_NAME.FAMILY),
                        CommonFtsObject.searchTableName(Constants.TABLE_NAME.CHILD));
                List<String> ids = commonRepository().findSearchIds(sql);
                query = sqb.toStringFts(ids, tablename, CommonRepository.ID_COLUMN, Sortqueries);
                query = sqb.Endquery(query);
            } else {
                sqb.addCondition(filters);
                query = sqb.orderbyCondition(Sortqueries);
                query = sqb.Endquery(sqb.addlimitandOffset(query, clientAdapter.getCurrentlimit(),
                        clientAdapter.getCurrentoffset()));
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return query;
    }

    private String defaultFilterAndSortQuery() {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(mainSelect);
        String query = "";
        try {
            if (isValidFilterForFts(commonRepository())) {
                String myquery = QueryBuilder.getQuery(joinTables, mainCondition, tablename, filters, clientAdapter, Sortqueries);
                List<String> ids = commonRepository().findSearchIds(myquery);
                query = sqb.toStringFts(ids, tablename, CommonRepository.ID_COLUMN, Sortqueries);
                query = sqb.Endquery(query);
            } else {
                sqb.addCondition(filters);
                query = sqb.orderbyCondition(Sortqueries);
                query = sqb.Endquery(sqb.addlimitandOffset(query, clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset()));
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return query;
    }

    @Override
    public void countExecute() {
        if (!dueFilterActive) {
            super.countExecute();
        } else {
            Cursor c = null;

            try {
                SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(countSelect);
                String query = "";
                if (isValidFilterForFts(commonRepository())) {
                    String sql = sqb.countQueryFts(tablename, joinTable, mainCondition, filters);
                    sql = sql.replace(CommonFtsObject.idColumn, CommonFtsObject.relationalIdColumn);
                    sql = sql.replace(CommonFtsObject.searchTableName(Constants.TABLE_NAME.FAMILY),
                            CommonFtsObject.searchTableName(Constants.TABLE_NAME.CHILD));
                    sql = sql + " GROUP BY " + CommonFtsObject.relationalIdColumn;
                    Timber.i(query);

                    clientAdapter.setTotalcount(commonRepository().countSearchIds(sql));
                    Timber.v("total count here is " + clientAdapter.getTotalcount());
                } else {
                    sqb.addCondition(filters);
                    query = sqb.orderbyCondition(Sortqueries);
                    query = sqb.Endquery(query);

                    Timber.i(query);

                    c = commonRepository().rawCustomQueryForAdapter(query);
                    c.moveToFirst();
                    clientAdapter.setTotalcount(c.getInt(0));
                    Timber.v("total count there is " + clientAdapter.getTotalcount());
                }

                clientAdapter.setCurrentlimit(20);
                clientAdapter.setCurrentoffset(0);
            } catch (Exception e) {
                Timber.e(e);
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }

    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        if (id == LOADER_ID) {
            return new CursorLoader(getActivity()) {
                @Override
                public Cursor loadInBackground() {
                    final String COUNT = "count_execute";
                    if (args != null && args.getBoolean(COUNT)) {
                        countExecute();
                    }
                    String query = (dueFilterActive ? dueFilterAndSortQuery() : defaultFilterAndSortQuery());
                    return commonRepository().rawCustomQueryForAdapter(query);
                }
            };
        }
        return super.onCreateLoader(id, args);
    }

    @Override
    public void onResume() {
        super.onResume();
        Toolbar toolbar = view.findViewById(org.smartregister.R.id.register_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetStartWithNavigation(0);
        NavigationMenu.getInstance(getActivity(), null, toolbar);
    }

    @Override
    protected void refreshSyncProgressSpinner() {
        if (syncProgressBar != null) {
            syncProgressBar.setVisibility(View.GONE);
        }
        if (syncButton != null) {
            syncButton.setVisibility(View.GONE);
        }
    }
}