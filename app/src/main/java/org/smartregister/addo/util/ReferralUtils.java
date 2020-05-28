package org.smartregister.addo.util;

import android.content.Context;
import android.widget.Toast;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.addo.R;
import org.smartregister.addo.application.AddoApplication;
import org.smartregister.domain.Task;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

public class ReferralUtils {

    public static void createReferralTask(Context context, String baseEntityId, String focus, String jsonString, String villageTown) {
        Task task = new Task();
        task.setIdentifier(UUID.randomUUID().toString());

        String referralProblems = getReferralProblems(jsonString);
        AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
        LocationHelper locationHelper = LocationHelper.getInstance();

        task.setPlanIdentifier(CoreConstants.REFERRAL_PLAN_ID);
        task.setGroupIdentifier(locationHelper.getOpenMrsLocationId(villageTown));
        task.setStatus(Task.TaskStatus.READY);
        task.setBusinessStatus(CoreConstants.BUSINESS_STATUS.REFERRED);
        task.setPriority(1);
        task.setCode(CoreConstants.JsonAssets.REFERRAL_CODE);
        task.setDescription(referralProblems);
        task.setFocus(focus);
        task.setForEntity(baseEntityId);
        DateTime now = new DateTime();
        task.setExecutionStartDate(now);
        task.setAuthoredOn(now);
        task.setLastModified(now);
        task.setOwner(allSharedPreferences.fetchRegisteredANM());
        task.setSyncStatus(BaseRepository.TYPE_Created);
        task.setRequester(allSharedPreferences.getANMPreferredName(allSharedPreferences.fetchRegisteredANM()));
        task.setLocation(locationHelper.getOpenMrsLocationId(villageTown));
        if (hasReferralTask(CoreConstants.REFERRAL_PLAN_ID, locationHelper.getOpenMrsLocationId(villageTown), baseEntityId, CoreConstants.JsonAssets.REFERRAL_CODE)) {
            AddoApplication.getInstance().getTaskRepository().addOrUpdate(task);
            Toast.makeText(context, context.getResources().getString(R.string.referral_submitted), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.client_already_has_referral), Toast.LENGTH_LONG).show();
        }
    }

    private static String getReferralProblems(String jsonString) {
        String referralProblems = "";
        List<String> formValues = new ArrayList<>();
        try {
            JSONObject problemJson = new JSONObject(jsonString);
            JSONArray fields = FormUtils.getMultiStepFormFields(problemJson);
            for (int i = 0; i < fields.length(); i++) {
                JSONObject field = fields.getJSONObject(i);
                if (field.optBoolean(CoreConstants.JsonAssets.IS_PROBLEM, true)) {
                    if (field.has(JsonFormConstants.TYPE) && JsonFormConstants.CHECK_BOX.equals(field.getString(JsonFormConstants.TYPE))) {
                        if (field.has(JsonFormConstants.OPTIONS_FIELD_NAME)) {
                            JSONArray options = field.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                            String values = getCheckBoxSelectedOptions(options);
                            if (StringUtils.isNotEmpty(values) && !values.equalsIgnoreCase("Yes")) {
                                formValues.add(values);
                            }
                        }
                    } else if (field.has(JsonFormConstants.TYPE) && JsonFormConstants.RADIO_BUTTON.equals(field.getString(JsonFormConstants.TYPE))) {
                        if (field.has(JsonFormConstants.OPTIONS_FIELD_NAME) && field.has(JsonFormConstants.VALUE)) {
                            JSONArray options = field.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                            String value = field.getString(JsonFormConstants.VALUE);
                            String values = getRadioButtonSelectedOptions(options, value);
                            if (StringUtils.isNotEmpty(values)) {
                                formValues.add(values);
                            }
                        }
                    }
                }
            }

            referralProblems = StringUtils.join(formValues, ", ");
        } catch (JSONException e) {
            Timber.e(e, "CoreReferralUtils --> getReferralProblems");
        }
        return referralProblems;
    }

    private static String getRadioButtonSelectedOptions(@NotNull JSONArray options, String value) {
        String selectedOptionValues = "";
        try {
            for (int i = 0; i < options.length(); i++) {
                JSONObject option = options.getJSONObject(i);
                if ((option.has(JsonFormConstants.KEY) && value.equals(option.getString(JsonFormConstants.KEY))) && (option.has(JsonFormConstants.TEXT) && StringUtils.isNotEmpty(option.getString(JsonFormConstants.VALUE)))) {
                    selectedOptionValues = option.getString(JsonFormConstants.TEXT);
                }
            }
        } catch (JSONException e) {
            Timber.e(e, "CoreReferralUtils --> getSelectedOptions");
        }

        return selectedOptionValues;
    }

    private static String getCheckBoxSelectedOptions(@NotNull JSONArray options) {
        String selectedOptionValues = "";
        List<String> selectedValue = new ArrayList<>();
        try {
            for (int i = 0; i < options.length(); i++) {
                JSONObject option = options.getJSONObject(i);
                boolean useItem = true;

                if (option.optBoolean(CoreConstants.IGNORE, false)) {
                    useItem = false;
                }

                if (option.has(JsonFormConstants.VALUE) && Boolean.valueOf(option.getString(JsonFormConstants.VALUE))
                        && useItem) { //Don't add values for  items with other
                    selectedValue.add(option.getString(JsonFormConstants.TEXT));
                }
            }
            selectedOptionValues = StringUtils.join(selectedValue, ", ");
        } catch (JSONException e) {
            Timber.e(e, "CoreReferralUtils --> getSelectedOptions");
        }

        return selectedOptionValues;
    }

    private static boolean hasReferralTask(String planId, String groupId, String forEntity, String code) {

        return AddoApplication.getInstance().getTaskRepository().getTasksByEntityAndCode(planId, groupId, forEntity, code).isEmpty();
    }
}
