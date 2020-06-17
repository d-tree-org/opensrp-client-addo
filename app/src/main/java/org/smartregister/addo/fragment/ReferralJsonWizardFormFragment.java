package org.smartregister.addo.fragment;

import android.content.Context;
import android.os.Bundle;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.addo.presenter.ReferralJsonWizardFormFragmentPresenter;

import timber.log.Timber;

public class ReferralJsonWizardFormFragment extends JsonWizardFormFragment {

    public static ReferralJsonWizardFormFragment getFormFragment(String stepName) {
        ReferralJsonWizardFormFragment jsonFormFragment = new ReferralJsonWizardFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    public void customClick(Context context, String behaviour){
        //save
        save();
    }

    @Override
    protected ReferralJsonWizardFormFragmentPresenter createPresenter() {
        return new ReferralJsonWizardFormFragmentPresenter(this, JsonFormInteractor.getInstance());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getJsonApi().isPreviousPressed()) {
            skipStepsOnNextPressed();
        } else {
            skipStepOnPreviousPressed();
        }
    }

    /**
     * Skips blank by relevance steps when next is clicked on the json wizard forms.
     */
    public void skipStepsOnNextPressed() {
        if (skipBlankSteps()) {
            JSONObject formStep = getStep(getArguments().getString(JsonFormConstants.STEPNAME));
            String next = formStep.optString(JsonFormConstants.NEXT, "");
            if (StringUtils.isNotEmpty(next)) {
                checkIfStepIsBlank(formStep);
                if (shouldSkipStep()) {
                    next();
                }
            }
        }
    }

    /**
     * Skips blank by relevance steps when previous is clicked on the json wizard forms.
     */
    public void skipStepOnPreviousPressed() {
        if (skipBlankSteps()) {
            JSONObject currentFormStep = getStep(getArguments().getString(JsonFormConstants.STEPNAME));
            String next = currentFormStep.optString(JsonFormConstants.NEXT, "");
            int currentFormStepNumber = getFormStepNumber(next);
            for (int i = currentFormStepNumber; i >= 1; i--) {
                JSONObject formStep = getJsonApi().getmJSONObject().optJSONObject(JsonFormConstants.STEP + i);
                if (formStep != null) {
                    checkIfStepIsBlank(formStep);
                    if (shouldSkipStep()) {
                        getFragmentManager().popBackStack();
                    } else {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Checks if a given step is blank due to relevance hidding all the widgets
     *
     * @param formStep {@link JSONObject}
     */
    private void checkIfStepIsBlank(JSONObject formStep) {
        try {
            if (formStep.has(JsonFormConstants.FIELDS)) {
                JSONArray fields = formStep.getJSONArray(JsonFormConstants.FIELDS);
                for (int i = 0; i < fields.length(); i++) {
                    JSONObject field = fields.getJSONObject(i);
                    if (field.has(JsonFormConstants.TYPE) && !JsonFormConstants.HIDDEN.equals(field.getString(JsonFormConstants.TYPE))) {
                        boolean isVisible = field.optBoolean(JsonFormConstants.IS_VISIBLE, true);
                        if (isVisible) {
                            setShouldSkipStep(false);
                            break;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Timber.e(e, "%s --> checkIfStepIsBlank", this.getClass().getCanonicalName());
        }
    }

    /**
     * Returns the current form step number when given than steps next step number.
     * This number is used to figure out which steps to pop when previous is clicked.
     *
     * @param nextFormNumber {@link String}
     * @return formNumber {@link Integer}
     */
    private int getFormStepNumber(String nextFormNumber) {
        int formNumber = 0;
        if (StringUtils.isNotBlank(nextFormNumber)) {
            int currentFormNumber = Integer.parseInt(nextFormNumber.substring(4, 5)) - 1;
            if (currentFormNumber > 0) {
                formNumber = currentFormNumber;
            } else if (currentFormNumber == 0) {
                formNumber = 1;
            }
        }
        return formNumber;
    }

}
