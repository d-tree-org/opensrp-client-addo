package org.smartregister.addo.presenter;

import android.app.Activity;
import android.widget.Toast;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.addo.R;
import org.smartregister.addo.contract.FamilyFocusedMemberProfileContract;
import org.smartregister.addo.contract.FamilyOtherMemberProfileExtendedContract;
import org.smartregister.addo.contract.FamilyProfileExtendedContract;
import org.smartregister.addo.interactor.FamilyFocusedMemberProfileInteractor;
import org.smartregister.addo.interactor.FamilyOtherMemberProfileInteractor;
import org.smartregister.addo.interactor.FamilyProfileInteractor;
import org.smartregister.addo.model.FamilyProfileModel;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.contract.FamilyOtherMemberContract;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.presenter.BaseFamilyOtherMemberProfileActivityPresenter;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.util.Utils.getName;

public class FamilyOtherMemberActivityPresenter extends BaseFamilyOtherMemberProfileActivityPresenter
        implements FamilyOtherMemberProfileExtendedContract.Presenter,
        FamilyProfileContract.InteractorCallBack,
        FamilyProfileExtendedContract.PresenterCallBack, FamilyOtherMemberProfileExtendedContract.InteractorCallBack {

    private static final String TAG = FamilyOtherMemberActivityPresenter.class.getCanonicalName();

    private WeakReference<FamilyOtherMemberProfileExtendedContract.View> viewReference;
    private String familyBaseEntityId;
    private String familyName;

    private FamilyProfileContract.Interactor profileInteractor;
    private FamilyProfileContract.Model profileModel;
    private FamilyOtherMemberProfileInteractor interactor;

    public FamilyOtherMemberActivityPresenter(FamilyOtherMemberProfileExtendedContract.View view, FamilyOtherMemberContract.Model model,
                                              String viewConfigurationIdentifier, String familyBaseEntityId, String baseEntityId,
                                              String familyHead, String primaryCaregiver, String villageTown, String familyName) {
        super(view, model, viewConfigurationIdentifier, baseEntityId, familyHead, primaryCaregiver, villageTown);
        viewReference = new WeakReference<>(view);
        this.familyBaseEntityId = familyBaseEntityId;
        this.familyName = familyName;

        this.profileInteractor = new FamilyProfileInteractor();
        this.profileModel = new FamilyProfileModel(familyName);
        this.interactor = new FamilyOtherMemberProfileInteractor(familyBaseEntityId);

        verifyHasPhone();
        //initializeServiceStatus();
    }

    public String getFamilyBaseEntityId() {
        return familyBaseEntityId;
    }

    public String getFamilyName() {
        return familyName;
    }

    @Override
    public void submitVisit(Map<String, String> formForSubmission) {
        if (viewReference.get() != null) {
            viewReference.get().showProgressDialog(R.string.submit);
            interactor.submitVisit(false, baseEntityId, formForSubmission, this);
        }
    }

    @Override
    public void refreshProfileTopSection(CommonPersonObjectClient client) {
        super.refreshProfileTopSection(client);
        if (client != null && client.getColumnmaps() != null) {
            String firstName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
            String middleName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true);
            String lastName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.LAST_NAME, true);
            int age = Utils.getAgeFromDate(Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, true));

            this.getView().setProfileName(MessageFormat.format("{0}, {1}", getName(getName(firstName, middleName), lastName), age));
            this.getView().setProfileImage(baseEntityId, "ec_family_member");
        }
    }

    @Override
    public void onSubmitted(boolean successful) {
        if (successful) {
            viewReference.get().hideProgressDialog();
            Toast.makeText((Activity) this.getView(), R.string.submitted_for_onsubmit, Toast.LENGTH_SHORT).show();
        } else {
            viewReference.get().hideProgressDialog();
            Toast.makeText((Activity) this.getView(), R.string.not_submitted_for_onsubmit, Toast.LENGTH_SHORT).show();
        }
    }

    public void startFormForEdit(CommonPersonObjectClient commonPersonObject) {
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {
        //TODO Implement
        Timber.d("onUniqueIdFetched unimplemented");
    }

    @Override
    public void onNoUniqueId() {
        //TODO Implement
        Timber.d("onNoUniqueId unimplemented");
    }

    @Override
    public void onRegistrationSaved(boolean b, boolean b1, FamilyEventClient familyEventClient) {

    }

    @Override
    public void verifyHasPhone() {
        ((FamilyProfileInteractor) profileInteractor).verifyHasPhone(familyBaseEntityId, this);
    }

    @Override
    public void notifyHasPhone(boolean hasPhone) {

    }

    public FamilyOtherMemberProfileExtendedContract.View getView() {
        if (viewReference != null) {
            return viewReference.get();
        } else {
            return null;
        }
    }

}