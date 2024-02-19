package org.smartregister.addo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.addo.presenter.ReferralJsonWizardFormFragmentPresenter;
import org.smartregister.addo.util.CoreJsonFormUtils;
import org.smartregister.addo.util.FnInterfaces.KeyValue;
import org.smartregister.addo.util.FnList;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ReferralJsonWizardFormFragment extends JsonWizardFormFragment {

    public static ReferralJsonWizardFormFragment getFormFragment(String stepName, String meds) {
        ReferralJsonWizardFormFragment jsonFormFragment = new ReferralJsonWizardFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, stepName);
        if (meds != null) {
            bundle.putString("medicine_selected", meds);
        }
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    public void customClick(Context context, String behaviour) {
        //save
        save();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            String stepName = getArguments().getString(JsonFormConstants.STEPNAME);
            String medicineSelectedString = getArguments().getString("medicine_selected");
            if (StringUtils.isNotBlank(medicineSelectedString) && "step4".equalsIgnoreCase(stepName)) {
                JSONObject step = getStep(stepName);
                List<KeyValue<String, String>> selectedMeds = getSelectedMeds(medicineSelectedString);
                checkForMedsDispensedAndModifyForm(step, selectedMeds);
            }

        }
        super.onViewCreated(view, savedInstanceState);
    }

    private void checkForMedsDispensedAndModifyForm(JSONObject step, List<KeyValue<String, String>> selectedMeds) {

        JSONArray fields = null;
        try {
            fields = step.getJSONArray(JsonFormConstants.FIELDS);
            modifyFields(fields, selectedMeds);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Modifies the fields in the form to include the selected meds
     *
     * @param stepFields  {@link JSONArray}
     * @param selectedMeds {@link List<KeyValue<String,String>>}
     */
    private void modifyFields(JSONArray stepFields, List<KeyValue<String, String>> selectedMeds) throws JSONException {
        JSONArray medsNotInStockAffordableFields = CoreJsonFormUtils.medsNotInStockAffordableFields("danger_signs_medication_child", getContext());
        JSONArray modifiedFields = FnList
                .generate((integer) -> {
                    assert medsNotInStockAffordableFields != null;
                    if (integer < medsNotInStockAffordableFields.length()) {
                        // If null is there throw an exceptions
                        return (JSONObject) medsNotInStockAffordableFields.get(integer);
                    }
                    return null;
                })
                .reduce(new JSONArray(),
                        (o, t) -> {
                            JSONArray newOptions = FnList
                                    .from(selectedMeds)
                                    .reduce(new JSONArray(), (retArr, optOb) -> {
                                        JSONObject retObj = new JSONObject();
                                        retObj.put("key", optOb.key);
                                        retObj.put("openmrs_entity_parent", "");
                                        retObj.put("openmrs_entity", "concept");
                                        retObj.put("openmrs_entity_id", optOb.key);
                                        retObj.put("text", optOb.value);
                                        retObj.put("text_size", "18px");
                                        retObj.put("value", "false");
                                        retArr.put(retObj);
                                        return retArr;
                                    });

                            t.put("options", newOptions);
                            o.put(t);
                            return o;
                        });

        if (modifiedFields.length() > 0) {
            for (int i = 0; i < modifiedFields.length(); i++) {
                JSONObject lastItemField =  (JSONObject) stepFields.get(stepFields.length() - 1);
                stepFields.put(stepFields.length() - 1, modifiedFields.get(i));
                stepFields.put(lastItemField);
            }
        }

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
            // Remove added field so that it is not added again
            if (getArguments() != null) {
                String stepName = getArguments().getString(JsonFormConstants.STEPNAME);
                if (StringUtils.isNotBlank(stepName) && "step3".equalsIgnoreCase(stepName)) {
                    JSONObject currentFormStep = getStep("step4");
                    removeAddedFields(currentFormStep);
                }
            }
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

    private void removeAddedFields(JSONObject currentFormStep) {

        JSONArray fields = currentFormStep.optJSONArray(JsonFormConstants.FIELDS);
        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.optJSONObject(i);
            if (field != null && field.has(JsonFormConstants.KEY) &&
                    ("meds_not_in_stock".equals(field.optString(JsonFormConstants.KEY)) ||
                            "meds_not_affordable".equals(field.optString(JsonFormConstants.KEY)))) {
                fields.remove(i);
                i--;
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

    private List<KeyValue<String, String>> getSelectedMeds(String medicineSelectedString) {
        ArrayList<KeyValue<String, String>> medicinesSelected = new ArrayList<>();
        if (medicineSelectedString == null) {
            return medicinesSelected;
        }
        try {
            JSONArray fieldValueJsonArray = new JSONArray(medicineSelectedString);

            medicinesSelected = FnList
                    .generate(fieldValueJsonArray::getJSONObject)
                    .reduce(new ArrayList<>(), (o, t) -> {
                        o.add(new KeyValue<>(t.getString("key"), t.getString("text")));
                        return o;
                    });
        } catch (JSONException e) {
            Timber.e(e);
        }
        return medicinesSelected;
    }

}
