package org.smartregister.addo.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import org.smartregister.addo.BuildConfig;
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
import org.smartregister.simprint.SimPrintsHelper;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class SimPrintIdentificationRegisterFragment extends BaseRegisterFragment implements SimPrintResultFragmentContract.View {


    @Override
    protected int getLayout() {
        return R.layout.activity_simprint_identification;
    }

 /**   @Nullable
    @Override
    public android.view.View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        android.view.View view = inflater.inflate(R.layout.fingerprint_identification_profile, container, false);
        rootView = view;//handle to the root




        setupViews(view);
        return view;
    } **/

    @Override
    public void setupViews(android.view.View view) {
        super.setupViews(view);

        rootView = view;

        Toolbar toolbar = view.findViewById(R.id.register_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);

        toolbar.setContentInsetStartWithNavigation(0);
        toolbar.setVisibility(android.view.View.VISIBLE);

       // NavigationMenu.getInstance(this.getActivity(), null, toolbar);

        android.view.View navBarContainer = view.findViewById(R.id.register_nav_bar_container);
        navBarContainer.setFocusable(false);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        android.view.View searchBarLayout = view.findViewById(R.id.search_bar_layout);
        searchBarLayout.setLayoutParams(params);
        searchBarLayout.setBackgroundResource(R.color.addo_primary);
        searchBarLayout.setPadding(
                searchBarLayout.getPaddingLeft(),
                searchBarLayout.getPaddingTop(),
                searchBarLayout.getPaddingRight(),
                (int) org.smartregister.addo.util.Utils.convertDpToPixel(10.0F, this.getActivity())
        );

        CustomFontTextView titleView = view.findViewById(R.id.txt_title_label);
        if (titleView != null) {
            titleView.setText(R.string.fingerprint_identification_title);
            titleView.setPadding(0, titleView.getTop(), 0, titleView.getPaddingBottom());
        }
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
        //ArrayList<String> ids = this.getActivity().getIntent().getStringArrayListExtra("clients");
        ArrayList<String> ids = getBaseEntityIds();
        this.presenter = new SimPrintIdentificationFragmentPresenter(this, new SimPrintIdentificationFragmentModel(),
                null, ids);
    }

    private ArrayList<String> getBaseEntityIds() {
        HashMap<String, String> baseidsGuids = (HashMap<String, String>) this.getActivity().getIntent().getSerializableExtra("baseids_guids");
        return new ArrayList<String>(baseidsGuids.keySet());
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
        switch (view.getId()) {
            case R.id.patient_column:
                if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == "click_view_normal") {
                    goToFamilyProfileActivity(view);
                }
                break;
            case R.id.next_arrow:
                if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == "click_next_arrow") {
                    goToFamilyProfileActivity(view);
                }
                break;
            case R.id.textview_none_of_above:
                if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == "click_none_of_above") {
                    handleNoneSelected(view);
                }
                break;
            default:
                break;
        }

    }

    public void goToFamilyProfileActivity(android.view.View view) {
        if (view.getTag() instanceof CommonPersonObjectClient) {
            CommonPersonObjectClient pc = (CommonPersonObjectClient) view.getTag();

            //SimPrint Confirmation of the selected client
            String baseEntityId = pc.entityId();
            String simPrintsGuid = getSimPrintGuid(baseEntityId);
            String sessionid = this.getActivity().getIntent().getStringExtra("session_id");
            Utils.startAsyncTask(new ConfirmIdentificationTask(this.getContext(), sessionid, simPrintsGuid), null);

            // Get values to start the family profile
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
            this.getActivity().finish();
        }
    }

    public void handleNoneSelected(android.view.View view) {
        String sessionid = this.getActivity().getIntent().getStringExtra("session_id");
        // A call back to SimPrint to notify that none of the item on the list was selected
        Utils.startAsyncTask(new ConfirmIdentificationTask(this.getContext(), sessionid, "none_selected"), null);

        this.getActivity().finish();
    }

    private void confirmSelectedGuid(Context context, String sessionid, String simPrintsGuid) {
        SimPrintsHelper simPrintsHelper = new SimPrintsHelper(BuildConfig.SIMPRINT_PROJECT_ID, BuildConfig.SIMPRINT_USER_ID);
        simPrintsHelper.confirmIdentity(context, sessionid, simPrintsGuid);
    }

    private String getSimPrintGuid(String baseEntityId) {
        HashMap<String, String> baseidsGuids = (HashMap<String, String>) this.getActivity().getIntent().getSerializableExtra("baseids_guids");
        return baseidsGuids.get(baseEntityId);
    }

    @Override
    public RecyclerViewPaginatedAdapter getClientsCursorAdapter() {
        return super.getClientsCursorAdapter();
    }

    ////////////////////////////////////////////////////////////////////
    //      Inner Class | SimPrints Identification Confirmation
    ///////////////////////////////////////////////////////////////////

    private class ConfirmIdentificationTask extends AsyncTask<Void, Void, Void> {

        private String sessiodId;
        private String selectedGuid;
        private Context context;

        public ConfirmIdentificationTask(Context context, String sessiodId, String selectedGuid) {
            this.sessiodId = sessiodId;
            this.selectedGuid = selectedGuid;
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            confirmSelectedGuid(context, sessiodId, selectedGuid);
            return null;
        }
    }


}