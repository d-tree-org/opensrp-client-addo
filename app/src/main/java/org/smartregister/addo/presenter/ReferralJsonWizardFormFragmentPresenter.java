package org.smartregister.addo.presenter;

import android.content.Intent;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.presenters.JsonWizardFormFragmentPresenter;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.addo.R;
import org.smartregister.addo.fragment.ReferralJsonWizardFormFragment;
import org.smartregister.addo.util.FnList;

import timber.log.Timber;

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
            // Check if it is a medicine dispensing form and if the medicines have been dispensed
            JSONObject currentFormState = getCurrentFormState();
            if (isDispensingForm(currentFormState)) {
                String medicinesSelected = getSelectedMedsString(currentFormState);
                navigateToNextFormFragment(mStepDetails.optString(JsonFormConstants.NEXT), medicinesSelected);
            } else {
                navigateToNextFormFragment(mStepDetails
                        .optString(JsonFormConstants.NEXT), null);
            }
        }
        return false;
    }

    private void navigateToNextFormFragment(String step, String meds) {
        ReferralJsonWizardFormFragment next = ReferralJsonWizardFormFragment.getFormFragment(step, meds);
        getView().hideKeyBoard();
        getView().transactThis(next);
    }

    private JSONObject getCurrentFormState() {
        JSONObject currentFormState = null;
        try {
            currentFormState = new JSONObject(getView().getCurrentJsonState());
        } catch (JSONException jsonException) {
            Timber.e(jsonException);
        }
        return currentFormState;
    }

    private boolean isDispensingForm(JSONObject currentFormState) {
        boolean isDispensingForm = false;
        String encounterType;
        if (currentFormState != null) {
            encounterType = currentFormState.optString(JsonFormConstants.ENCOUNTER_TYPE);
            if (encounterType.equalsIgnoreCase("ADDO Visit - Dispense Medicine Child") ||
                    encounterType.equalsIgnoreCase("ADDO Visit - Dispense Medicine ANC") ||
                    encounterType.equalsIgnoreCase("ADDO Visit - Dispense Medicine Adolescent") ||
                    encounterType.equalsIgnoreCase("ADDO Visit - Dispense Medicine") ||
                    encounterType.equalsIgnoreCase("ADDO Visit - Attend Prescriptions from Facility or Malaria Lab Results")) {
                isDispensingForm = true;
            }
        }

        return isDispensingForm;
    }

    // Here we should only return the string, all the way to the fragment, and this stuff should happen at the fragment
    private String getSelectedMedsString(JSONObject currentFormState) {
        try {
            JSONObject medicineStep = currentFormState.getJSONObject("step3");
            JSONArray medicineStepFields = medicineStep.getJSONArray(JsonFormConstants.FIELDS);

            return FnList.generate(medicineStepFields::getJSONObject)
                    .filter(field -> field.getString(JsonFormConstants.KEY).equals("not_dispensed_meds"))
                    .reduce(null, (o, t) -> t.getString(JsonFormConstants.VALUE).equalsIgnoreCase("[]") ? null : t.getString(JsonFormConstants.VALUE));

        } catch (JSONException e) {
            Timber.e(e);
            return null;
        }
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
