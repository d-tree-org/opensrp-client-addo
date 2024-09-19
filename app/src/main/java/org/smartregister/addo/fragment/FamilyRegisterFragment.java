package org.smartregister.addo.fragment;


import static org.smartregister.addo.util.Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID;
import static org.smartregister.addo.util.Constants.INTENT_KEY.FAMILY_HEAD;
import static org.smartregister.addo.util.Constants.INTENT_KEY.FAMILY_NAME;
import static org.smartregister.addo.util.Constants.INTENT_KEY.GO_TO_DUE_PAGE;
import static org.smartregister.addo.util.Constants.INTENT_KEY.PRIMARY_CAREGIVER;
import static org.smartregister.addo.util.Constants.INTENT_KEY.VILLAGE_SELECTED;
import static org.smartregister.addo.util.Constants.INTENT_KEY.VILLAGE_TOWN;

import android.content.Intent;
import android.view.View;

import org.smartregister.addo.provider.VerificationFamilyProvider;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.contract.FamilyRegisterFragmentContract;
import org.smartregister.family.model.BaseFamilyRegisterFramentModel;
import org.smartregister.family.presenter.BaseFamilyRegisterFragmentPresenter;
import org.smartregister.family.provider.FamilyRegisterProvider;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

public class FamilyRegisterFragment extends BaseRegisterFragment implements FamilyRegisterFragmentContract.View {


    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }

        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new BaseFamilyRegisterFragmentPresenter(this, new BaseFamilyRegisterFramentModel(), viewConfigurationIdentifier) {
            @Override
            public String getDefaultSortQuery() {
                return "";
            }
        };
    }

    @Override
    public void setUniqueID(String s) {

    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {

    }

    @Override
    protected String getMainCondition() {
        return "";
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

        if (view != null) {
            CommonPersonObjectClient selectedFamily = (CommonPersonObjectClient) view.getTag();

            Intent intent = new Intent(getContext(), org.smartregister.family.util.Utils.metadata().profileActivity);
            intent.putExtra(FAMILY_BASE_ENTITY_ID, selectedFamily.getCaseId());
            intent.putExtra(FAMILY_HEAD,
                    org.smartregister.family.util.Utils.getValue(selectedFamily.getColumnmaps(), org.smartregister.family.util.DBConstants.KEY.FAMILY_HEAD, false));//"family_head", false));
            intent.putExtra(PRIMARY_CAREGIVER,
                    org.smartregister.family.util.Utils.getValue(selectedFamily.getColumnmaps(), org.smartregister.family.util.DBConstants.KEY.PRIMARY_CAREGIVER, false));//"primary_caregiver", false));
            intent.putExtra(VILLAGE_TOWN,
                    org.smartregister.family.util.Utils.getValue(selectedFamily.getColumnmaps(), org.smartregister.family.util.DBConstants.KEY.VILLAGE_TOWN, false));//"village_town", false));
            intent.putExtra(FAMILY_NAME,
                    org.smartregister.family.util.Utils.getValue(selectedFamily.getColumnmaps(), org.smartregister.family.util.DBConstants.KEY.FIRST_NAME, false));//"first_name", false));
            intent.putExtra(GO_TO_DUE_PAGE, false);
            intent.putExtra(GO_TO_DUE_PAGE, false);
            Objects.requireNonNull(getContext()).startActivity(intent);


        }

    }

    @Override
    public void showNotFoundPopup(String s) {

    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        super.onSyncComplete(fetchStatus);
    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        FamilyRegisterProvider chwRegisterProvider = new VerificationFamilyProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, chwRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    public FamilyRegisterFragmentContract.Presenter presenter() {
        return null;
    }

}
