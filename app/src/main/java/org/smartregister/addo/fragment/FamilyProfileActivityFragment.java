package org.smartregister.addo.fragment;

import android.os.Bundle;

import org.smartregister.addo.model.FamilyProfileActivityModel;
import org.smartregister.addo.presenter.FamilyProfileActivityPresenter;
import org.smartregister.addo.provider.FamilyActivityRegisterProvider;
import org.smartregister.configurableviews.model.View;
import org.smartregister.family.adapter.FamilyRecyclerViewCustomAdapter;
import org.smartregister.family.fragment.BaseFamilyProfileActivityFragment;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.Utils;

import java.util.HashMap;
import java.util.Set;

import timber.log.Timber;

/**
 * This class should be implemented for medication history for the clients
 */
public class FamilyProfileActivityFragment extends BaseFamilyProfileActivityFragment {
    private static final String TAG = FamilyProfileActivityFragment.class.getCanonicalName();

    private android.view.View view;
    private android.view.View dueOnlyLayout;

    @Override
    public void setupViews(android.view.View view) {

        super.setupViews(view);
        this.view = view;

    }

    public static BaseFamilyProfileActivityFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        BaseFamilyProfileActivityFragment fragment = new FamilyProfileActivityFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initializeAdapter(Set<View> visibleColumns) {
        FamilyActivityRegisterProvider familyActivityRegisterProvider = new FamilyActivityRegisterProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new FamilyRecyclerViewCustomAdapter(null, familyActivityRegisterProvider, context().commonrepository(this.tablename), Utils.metadata().familyActivityRegister.showPagination);
        clientAdapter.setCurrentlimit(Utils.metadata().familyActivityRegister.currentLimit);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    protected void initializePresenter() {
        String familyBaseEntityId = getArguments().getString(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
        presenter = new FamilyProfileActivityPresenter(this, new FamilyProfileActivityModel(), null, familyBaseEntityId);
    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {
        //TODO
        Timber.d("setAdvancedSearchFormData");
    }

}