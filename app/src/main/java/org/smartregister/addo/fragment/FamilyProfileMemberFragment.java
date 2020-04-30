package org.smartregister.addo.fragment;

import android.content.Intent;
import android.os.Bundle;

import org.smartregister.addo.R;
import org.smartregister.addo.activity.AboveFiveChildProfileActivity;
import org.smartregister.addo.activity.ChildProfileActivity;
import org.smartregister.addo.activity.FamilyOtherMemberProfileActivity;
import org.smartregister.addo.activity.FamilyProfileActivity;
import org.smartregister.addo.model.FamilyProfileMemberModel;
import org.smartregister.addo.provider.AddoMemberRegisterProvider;
import org.smartregister.addo.util.ChildDBConstants;
import org.smartregister.addo.util.ChildUtils;
import org.smartregister.addo.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;
import org.smartregister.family.presenter.BaseFamilyProfileMemberPresenter;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

import timber.log.Timber;

import static org.smartregister.addo.util.Constants.INTENT_KEY.IS_COMES_FROM_FAMILY;

public class FamilyProfileMemberFragment extends BaseFamilyProfileMemberFragment {

    private static final String TAG = FamilyProfileMemberFragment.class.getCanonicalName();

    public static BaseFamilyProfileMemberFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        BaseFamilyProfileMemberFragment fragment = new FamilyProfileMemberFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initializePresenter() {
        String familyBaseEntityId = getArguments().getString(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
        String familyHead = getArguments().getString(Constants.INTENT_KEY.FAMILY_HEAD);
        String primaryCareGiver = getArguments().getString(Constants.INTENT_KEY.PRIMARY_CAREGIVER);
        presenter = new BaseFamilyProfileMemberPresenter(this, new FamilyProfileMemberModel(), null, familyBaseEntityId, familyHead, primaryCareGiver);
    }

    @Override
    public void initializeAdapter(Set<View> visibleColumns, String familyHead, String primaryCaregiver) {
        AddoMemberRegisterProvider addoMemberRegisterProvider = new AddoMemberRegisterProvider(this.getActivity(), this.commonRepository(), visibleColumns, this.registerActionHandler, this.paginationViewHandler, familyHead, primaryCaregiver);
        this.clientAdapter = new RecyclerViewPaginatedAdapter(null, addoMemberRegisterProvider, this.context().commonrepository(this.tablename));
        this.clientAdapter.setCurrentlimit(20);
        this.clientsView.setAdapter(this.clientAdapter);
    }

    @Override
    protected void onViewClicked(android.view.View view) {
        super.onViewClicked(view);
        switch (view.getId()) {
            case R.id.patient_column:
                if (view.getTag() != null && view.getTag(org.smartregister.family.R.id.VIEW_ID) == CLICK_VIEW_NORMAL) {
                    ((FamilyProfileActivity) Objects.requireNonNull(getActivity())).goToProfileActivity(view, getArguments());
                }
                break;
            case R.id.next_arrow:
                if (view.getTag() != null && view.getTag(org.smartregister.family.R.id.VIEW_ID) == CLICK_VIEW_NEXT_ARROW) {
                    ((FamilyProfileActivity) Objects.requireNonNull(getActivity())).goToProfileActivity(view, getArguments());
                }
            default:
                break;
        }
    }

    public void goToProfileActivity(android.view.View view) {
        if (view.getTag() instanceof CommonPersonObjectClient) {
            CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) view.getTag();
            String entityType = Utils.getValue(commonPersonObjectClient.getColumnmaps(), ChildDBConstants.KEY.ENTITY_TYPE, false);
            if (org.smartregister.addo.util.Constants.TABLE_NAME.FAMILY_MEMBER.equals(entityType)) {
                goToOtherMemberProfileActivity(commonPersonObjectClient);
            } else {
                goToChildProfileActivity(commonPersonObjectClient);
            }
        }
    }

    public void goToOtherMemberProfileActivity(CommonPersonObjectClient patient) {
        Intent intent = new Intent(getActivity(), FamilyOtherMemberProfileActivity.class);
        intent.putExtras(getArguments());
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        intent.putExtra(org.smartregister.addo.util.Constants.INTENT_KEY.CHILD_COMMON_PERSON, patient);
        intent.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, ((BaseFamilyProfileMemberPresenter) presenter).getFamilyHead());
        intent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, ((BaseFamilyProfileMemberPresenter) presenter).getPrimaryCaregiver());
        startActivity(intent);
    }

    public void goToChildProfileActivity(CommonPersonObjectClient patient) {
        String dobString = Utils.getDuration(Utils.getValue(patient.getColumnmaps(), DBConstants.KEY.DOB, false));
        Integer yearOfBirth = ChildUtils.dobStringToYear(dobString);
        Intent intent;
        if (yearOfBirth != null && yearOfBirth >= 5) {
            intent = new Intent(getActivity(), AboveFiveChildProfileActivity.class);
        } else {
            intent = new Intent(getActivity(), ChildProfileActivity.class);
        }
        intent.putExtras(getArguments());
        intent.putExtra(IS_COMES_FROM_FAMILY, true);
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        startActivity(intent);
    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {
        Timber.v("setAdvancedSearchFormData");
    }

}