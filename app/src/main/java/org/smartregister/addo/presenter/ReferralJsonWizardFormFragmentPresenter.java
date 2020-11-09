package org.smartregister.addo.presenter;

import android.content.Intent;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.presenters.JsonWizardFormFragmentPresenter;
import com.vijay.jsonwizard.utils.FormUtils;

import org.smartregister.addo.R;
import org.smartregister.addo.fragment.ReferralJsonWizardFormFragment;

public class ReferralJsonWizardFormFragmentPresenter extends JsonWizardFormFragmentPresenter {
    private FormUtils formUtils = new FormUtils();

    public ReferralJsonWizardFormFragmentPresenter(JsonFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
    }

    @Override
    public void onSaveClick(LinearLayout mainView) {
        validateAndWriteValues();
        checkAndStopCountdownAlarm();
        boolean isFormValid = isFormValid();
        if (isFormValid || Boolean.valueOf(mainView.getTag(com.vijay.jsonwizard.R.id.skip_validation).toString())) {
            Intent returnIntent = new Intent();
            getView().onFormFinish();
            returnIntent.putExtra("json", formUtils.addFormDetails(getView().getCurrentJsonState()));
            returnIntent.putExtra(JsonFormConstants.SKIP_VALIDATION,
                    Boolean.valueOf(mainView.getTag(com.vijay.jsonwizard.R.id.skip_validation).toString()));
            getView().finishWithResult(returnIntent);
        } else {
            if (showErrorsOnSubmit()) {
                launchErrorDialog();
                getView().showToast(getView().getContext().getResources()
                        .getString(com.vijay.jsonwizard.R.string.json_form_error_msg, getInvalidFields().size()));
            } else {
                getView().showSnackBar(getView().getContext().getResources()
                        .getString(com.vijay.jsonwizard.R.string.json_form_error_msg, getInvalidFields().size()));

            }
        }
    }

    @Override
    public boolean onNextClick(LinearLayout mainView) {
        validateAndWriteValues();
        checkAndStopCountdownAlarm();
        boolean validateOnSubmit = validateOnSubmit();
        if (validateOnSubmit && getIncorrectlyFormattedFields().isEmpty()) {
            return moveToNextWizardStep();
        } else if (isFormValid()) {
            return moveToNextWizardStep();
        } else {
            getView().showSnackBar(getView().getContext().getResources()
                    .getString(com.vijay.jsonwizard.R.string.json_form_on_next_error_msg));
        }
        return false;
    }

    protected boolean moveToNextWizardStep() {
        if (!"".equals(mStepDetails.optString(JsonFormConstants.NEXT))) {
            ReferralJsonWizardFormFragment next = ReferralJsonWizardFormFragment.getFormFragment(mStepDetails.optString(JsonFormConstants.NEXT));
            getView().hideKeyBoard();
            getView().transactThis(next);
        }
        return false;
    }

    @Override
    public void setUpToolBar() {
        String mStepName = getView().getArguments().getString("stepName");
        getView().setActionBarTitle(mStepDetails.optString(JsonFormConstants.STEP_TITLE));
        getView().setToolbarTitleColor(R.color.white);
        if (mStepDetails.has("bottom_navigation")) {
            getView().updateVisibilityOfNextAndSave(false, false);
            return;
        }
        if (!mStepName.equals(JsonFormConstants.FIRST_STEP_NAME)) {
            getView().setUpBackButton();
        }

        if (mStepDetails.has("next")) {
            getView().updateVisibilityOfNextAndSave(true, false);
        } else if (mStepDetails.has("hide-save-button")) {
            getView().updateVisibilityOfNextAndSave(false, false);
            getView().previousAndNextButtonVisibility(true, false);
        } else {
            getView().updateVisibilityOfNextAndSave(false, true);
        }
    }
}
