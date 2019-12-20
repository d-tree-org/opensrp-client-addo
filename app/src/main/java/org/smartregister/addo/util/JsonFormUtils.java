package org.smartregister.addo.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Pair;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.addo.R;
import org.smartregister.addo.application.AddoApplication;
import org.smartregister.addo.domain.FamilyMember;
import org.smartregister.addo.model.FingerPrintScanResultModel;
import org.smartregister.addo.repository.AddoRepository;
import org.smartregister.clientandeventmodel.Address;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.Photo;
import org.smartregister.domain.ProfileImage;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.ImageRepository;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.FormUtils;
import org.smartregister.util.ImageUtils;
import org.smartregister.view.LocationPickerView;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import timber.log.Timber;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.smartregister.util.AssetHandler.jsonStringToJava;

/**
 * Created by keyman on 13/11/2018.
 */
public class JsonFormUtils extends org.smartregister.family.util.JsonFormUtils {
    public static final String METADATA = "metadata";
    public static final String TITLE = "title";
    public static final String ENCOUNTER_TYPE = "encounter_type";
    public static final int REQUEST_CODE_GET_JSON = 2244;
    public static final String CURRENT_OPENSRP_ID = "current_opensrp_id";
    public static final String READ_ONLY = "read_only";
    private static final String TAG = org.smartregister.util.JsonFormUtils.class.getCanonicalName();
    private static HashMap<String, String> actionMap = null;
    private static HashMap<String, String> JSON_DB_MAP;
    //private static Flavor flavor = new JsonFormUtilsFlv();


    public static JSONObject getBirthCertFormAsJson(JSONObject form, String baseEntityId, String currentLocationId, String dateOfBirthString) throws Exception {

        if (form == null) {
            return null;
        }
        //dateOfBirthString = dateOfBirthString.contains("y") ? dateOfBirthString.substring(0, dateOfBirthString.indexOf("y")) : "";
        form.getJSONObject(METADATA).put(ENCOUNTER_LOCATION, currentLocationId);
        form.put(ENTITY_ID, baseEntityId);
        JSONArray field = fields(form);
        JSONObject mindate = getFieldJSONObject(field, "birth_cert_issue_date");
        int days = getDayFromDate(dateOfBirthString);
        //if(mindate!=null){
        mindate.put("min_date", "today-" + days + "d");
        //}
        return form;

    }

    public static JSONObject getEcdWithDatePass(JSONObject form, String dateOfBirthString) throws Exception {

        if (form == null) {
            return null;
        }
        JSONArray field = fields(form);
        JSONObject datePass = getFieldJSONObject(field, "date_pass");
        int days = getDayFromDate(dateOfBirthString);
        datePass.put("value", days);
        return form;

    }

    public static JSONObject getPreviousECDAsJson(JSONObject form, String baseEntityId) throws Exception {

        if (form == null) {
            return null;
        }
        form.put(ENTITY_ID, baseEntityId);

        return form;

    }

    public static int getDayFromDate(String dateOfBirth) {
        DateTime date = DateTime.parse(dateOfBirth);
        Days days = Days.daysBetween(date.toLocalDate(), LocalDate.now());
        return days.getDays();
    }

    public static JSONObject getOnsIllnessFormAsJson(JSONObject form, String baseEntityId, String currentLocationId, String dateOfBirthString) throws Exception {

        if (form == null) {
            return null;
        }
        //dateOfBirthString = dateOfBirthString.contains("y") ? dateOfBirthString.substring(0, dateOfBirthString.indexOf("y")) : "";
        form.getJSONObject(METADATA).put(ENCOUNTER_LOCATION, currentLocationId);
        form.put(ENTITY_ID, baseEntityId);
        JSONArray field = fields(form);
        JSONObject mindate = getFieldJSONObject(field, "date_of_illness");
        int days = getDayFromDate(dateOfBirthString);
        //if(mindate!=null){
        mindate.put("min_date", "today-" + days + "d");
        //}
        return form;

    }

    public static Event getECDEvent(String jsonString, String homeVisitId, String entityId) {
        Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonString);
        if (!registrationFormParams.getLeft()) {
            return null;
        }
        JSONObject jsonForm = registrationFormParams.getMiddle();
        JSONArray fields = registrationFormParams.getRight();

        // String entityIdForm = getString(jsonForm, ENTITY_ID);

        lastInteractedWith(fields);
        //Client baseClient = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag(org.smartregister.family.util.Utils.context().allSharedPreferences()), entityId);
        Event baseEvent = org.smartregister.util.JsonFormUtils.createEvent(fields, getJSONObject(jsonForm, METADATA), formTag(org.smartregister.family.util.Utils.context().allSharedPreferences()),
                entityId, getString(jsonForm, ENCOUNTER_TYPE), org.smartregister.addo.util.Constants.TABLE_NAME.CHILD);
        baseEvent.addObs((new Obs()).withFormSubmissionField(org.smartregister.addo.util.Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_ID).withValue(homeVisitId)
                .withFieldCode(org.smartregister.addo.util.Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_ID).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));

        tagSyncMetadata(org.smartregister.family.util.Utils.context().allSharedPreferences(), baseEvent);// tag docs
        return baseEvent;

    }

    public static Pair<Client, Event> processBirthAndIllnessForm(AllSharedPreferences allSharedPreferences, String jsonString) {
        try {

            Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonString);
            if (!registrationFormParams.getLeft()) {
                return null;
            }

            JSONObject jsonForm = registrationFormParams.getMiddle();
            JSONArray fields = registrationFormParams.getRight();

            String entityId = getString(jsonForm, ENTITY_ID);

            lastInteractedWith(fields);

            String birthCert = org.smartregister.family.util.JsonFormUtils.getFieldValue(jsonString, "birth_cert");
            if (!TextUtils.isEmpty(birthCert) && birthCert.equalsIgnoreCase("Yes")) {
                JSONObject dobJSONObject = getFieldJSONObject(fields, "birth_notification");
                dobJSONObject.put(Constants.KEY.VALUE, "No");
                fields.put(dobJSONObject);
            }

            Client baseClient = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag(allSharedPreferences), entityId);
            Event baseEvent = org.smartregister.util.JsonFormUtils.createEvent(fields, getJSONObject(jsonForm, METADATA), formTag(allSharedPreferences), entityId, getString(jsonForm, ENCOUNTER_TYPE), org.smartregister.addo.util.Constants.TABLE_NAME.CHILD);
            String illness_acton = org.smartregister.family.util.JsonFormUtils.getFieldValue(jsonString, "action_taken");
            if (!TextUtils.isEmpty(illness_acton)) {
                baseEvent.addObs(new Obs("concept", "text", org.smartregister.addo.util.Constants.FORM_CONSTANTS.ILLNESS_ACTION_TAKEN_LEVEL.CODE, "",
                        toList(actionMap().get(illness_acton)), toList(illness_acton), null, "action_taken"));

            }
            tagSyncMetadata(allSharedPreferences, baseEvent);// tag docs

            return Pair.create(baseClient, baseEvent);
        } catch (Exception e) {
            return null;
        }

    }

    private static HashMap<String, String> actionMap() {
        if (actionMap == null) {
            actionMap = new HashMap<>();
            actionMap.put("Managed", "140959AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            actionMap.put("Referred", "159494AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            actionMap.put("No action taken", "1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        }
        return actionMap;
    }

    public static HashMap<String, String> getChoice(Context context) {
        HashMap<String, String> choices = new HashMap<>();
        choices.put(context.getResources().getString(R.string.yes), "1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        choices.put(context.getResources().getString(R.string.no), "1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        return choices;
    }

    public static HashMap<String, String> getChoiceDietary(Context context) {
        HashMap<String, String> choices = new HashMap<>();
        choices.put(context.getResources().getString(R.string.minimum_dietary_choice_1), "");
        choices.put(context.getResources().getString(R.string.minimum_dietary_choice_2), "");
        choices.put(context.getResources().getString(R.string.minimum_dietary_choice_3), "");
        return choices;
    }

    public static HashMap<String, String> getChoiceMuac(Context context) {
        HashMap<String, String> choices = new HashMap<>();
        choices.put(context.getResources().getString(R.string.muac_choice_1), "160909AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        choices.put(context.getResources().getString(R.string.muac_choice_2), "160910AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        choices.put(context.getResources().getString(R.string.muac_choice_3), "127778AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        return choices;
    }

    public static List<Object> toList(String... vals) {
        List<Object> res = new ArrayList<>();
        res.addAll(Arrays.asList(vals));
        return res;
    }

    public static JSONObject getFormAsJson(JSONObject form,
                                           String formName, String id,
                                           String currentLocationId, String familyID) throws Exception {
        if (form == null) {
            return null;
        }

        String entityId = id;
        form.getJSONObject(METADATA).put(ENCOUNTER_LOCATION, currentLocationId);

        if (Utils.metadata().familyRegister.formName.equals(formName) || Utils.metadata().familyMemberRegister.formName.equals(formName) || formName.equalsIgnoreCase(org.smartregister.addo.util.Constants.JSON_FORM.getChildRegister())) {
            if (StringUtils.isNotBlank(entityId)) {
                entityId = entityId.replace("-", "");
            }

            // Inject opensrp id into the form
            JSONArray field = fields(form);
            JSONObject uniqueId = getFieldJSONObject(field, DBConstants.KEY.UNIQUE_ID);
//            JSONObject insurance_provider = getFieldJSONObject(field, org.smartregister.chw.util.Constants.JsonAssets.INSURANCE_PROVIDER);
            if (uniqueId != null) {
                uniqueId.remove(org.smartregister.family.util.JsonFormUtils.VALUE);
                uniqueId.put(org.smartregister.family.util.JsonFormUtils.VALUE, entityId);
            }

//            if (insurance_provider != null) {
//                insurance_provider.remove(org.smartregister.chw.util.Constants.JsonAssets.INSURANCE_PROVIDER);
//                insurance_provider.put(org.smartregister.chw.util.Constants.JsonAssets.INSURANCE_PROVIDER, insurance_provider);
//            }

            if (!isBlank(familyID)) {
                JSONObject metaDataJson = form.getJSONObject("metadata");
                JSONObject lookup = metaDataJson.getJSONObject("look_up");
                lookup.put("entity_id", "family");
                lookup.put("value", familyID);
            }


        } else {
            Timber.w("Unsupported form requested for launch " + formName);
        }
        Timber.d("form is " + form.toString());
        return form;
    }


    public static Pair<Client, Event> processChildRegistrationForm(AllSharedPreferences allSharedPreferences, String jsonString) {

        try {
            Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonString);

            if (!registrationFormParams.getLeft()) {
                return null;
            }

            JSONObject jsonForm = registrationFormParams.getMiddle();
            JSONArray fields = registrationFormParams.getRight();

            String entityId = getString(jsonForm, ENTITY_ID);
            if (isBlank(entityId)) {
                entityId = generateRandomUUIDString();
            }

            lastInteractedWith(fields);

            dobUnknownUpdateFromAge(fields);

            processChildEnrollMent(jsonForm, fields);

            Client baseClient = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag(allSharedPreferences), entityId);

            Event baseEvent = org.smartregister.util.JsonFormUtils.createEvent(fields, getJSONObject(jsonForm, METADATA), formTag(allSharedPreferences), entityId, getString(jsonForm, ENCOUNTER_TYPE), org.smartregister.addo.util.Constants.TABLE_NAME.CHILD);
            tagSyncMetadata(allSharedPreferences, baseEvent);

            if (baseClient != null || baseEvent != null) {
                String imageLocation = org.smartregister.family.util.JsonFormUtils.getFieldValue(jsonString, Constants.KEY.PHOTO);
                org.smartregister.family.util.JsonFormUtils.saveImage(baseEvent.getProviderId(), baseClient.getBaseEntityId(), imageLocation);
            }

            JSONObject lookUpJSONObject = getJSONObject(getJSONObject(jsonForm, METADATA), "look_up");
            String lookUpEntityId = "";
            String lookUpBaseEntityId = "";
            if (lookUpJSONObject != null) {
                lookUpEntityId = getString(lookUpJSONObject, "entity_id");
                lookUpBaseEntityId = getString(lookUpJSONObject, "value");
            }
            if (lookUpEntityId.equals("family") && StringUtils.isNotBlank(lookUpBaseEntityId)) {
                Client ss = new Client(lookUpBaseEntityId);
                Context context = AddoApplication.getInstance().getContext().applicationContext();
                addRelationship(context, ss, baseClient);
                SQLiteDatabase db = AddoApplication.getInstance().getRepository().getReadableDatabase();
                AddoRepository pathRepository = new AddoRepository(context, AddoApplication.getInstance().getContext());
                EventClientRepository eventClientRepository = new EventClientRepository(pathRepository);
                JSONObject clientjson = eventClientRepository.getClient(db, lookUpBaseEntityId);
                baseClient.setAddresses(getAddressFromClientJson(clientjson));
            }


            return Pair.create(baseClient, baseEvent);
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    private static void processChildEnrollMent(JSONObject jsonForm, JSONArray fields) {

        try {

            JSONObject surnam_familyName_SameObject = getFieldJSONObject(fields, "surname_same_as_family_name");
            JSONArray surnam_familyName_Same_options = getJSONArray(surnam_familyName_SameObject, Constants.JSON_FORM_KEY.OPTIONS);
            JSONObject surnam_familyName_Same_option = getJSONObject(surnam_familyName_Same_options, 0);
            String surnam_familyName_SameString = surnam_familyName_Same_option != null ? surnam_familyName_Same_option.getString(VALUE) : null;

            if (StringUtils.isNotBlank(surnam_familyName_SameString) && Boolean.valueOf(surnam_familyName_SameString)) {
                String familyId = jsonForm.getJSONObject("metadata").getJSONObject("look_up").getString("value");
                CommonPersonObject familyObject = AddoApplication.getInstance().getContext().commonrepository("ec_family").findByCaseID(familyId);
                String lastname = familyObject.getColumnmaps().get(DBConstants.KEY.LAST_NAME);
                JSONObject surname_object = getFieldJSONObject(fields, "surname");
                surname_object.put(VALUE, lastname);
            }
        } catch (Exception e) {
            Timber.e(e);
        }

    }

    private static String processValueWithChoiceIds(JSONObject jsonObject, String value) {
        try {
            //spinner
            if (jsonObject.has("openmrs_choice_ids")) {
                JSONObject choiceObject = jsonObject.getJSONObject("openmrs_choice_ids");

                for (int i = 0; i < choiceObject.names().length(); i++) {
                    if (value.equalsIgnoreCase(choiceObject.getString(choiceObject.names().getString(i)))) {
                        value = choiceObject.names().getString(i);
                    }
                }


            }//checkbox
            else if (jsonObject.has(Constants.JSON_FORM_KEY.OPTIONS)) {
                JSONArray option_array = jsonObject.getJSONArray(Constants.JSON_FORM_KEY.OPTIONS);
                for (int i = 0; i < option_array.length(); i++) {
                    JSONObject option = option_array.getJSONObject(i);
                    if (value.contains(option.getString("key"))) {
                        option.put("value", "true");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    private static void addRelationship(Context context, Client parent, Client child) {
        try {
            String relationships = AssetHandler.readFileFromAssetsFolder(FormUtils.ecClientRelationships, context);
            JSONArray jsonArray = null;

            jsonArray = new JSONArray(relationships);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject rObject = jsonArray.getJSONObject(i);
                if (rObject.has("field") && getString(rObject, "field").equals(ENTITY_ID)) {
                    child.addRelationship(rObject.getString("client_relationship"), parent.getBaseEntityId());
                } /* else {
                    //TODO how to add other kind of relationships
                  } */
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private static ArrayList<Address> getAddressFromClientJson(JSONObject clientjson) {
        ArrayList<Address> addresses = new ArrayList<Address>();
        try {
            JSONArray addressArray = clientjson.getJSONArray("addresses");
            for (int i = 0; i < addressArray.length(); i++) {
                Address address = new Address();
                address.setAddressType(addressArray.getJSONObject(i).getString("addressType"));
                JSONObject addressfields = addressArray.getJSONObject(i).getJSONObject("addressFields");

                Iterator<?> keys = addressfields.keys();

                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    if (addressfields.get(key) instanceof String) {
                        address.addAddressField(key, addressfields.getString(key));
                    }
                }
                addresses.add(address);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    public static void mergeAndSaveClient(ECSyncHelper ecUpdater, Client baseClient) throws Exception {
        JSONObject updatedClientJson = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(baseClient));

        JSONObject originalClientJsonObject = ecUpdater.getClient(baseClient.getBaseEntityId());

        JSONObject mergedJson = org.smartregister.util.JsonFormUtils.merge(originalClientJsonObject, updatedClientJson);

        //TODO Save edit log ?

        ecUpdater.addClient(baseClient.getBaseEntityId(), mergedJson);
    }

    public static void saveImage(String providerId, String entityId, String imageLocation) {
        if (isBlank(imageLocation)) {
            return;
        }

        File file = new File(imageLocation);

        if (!file.exists()) {
            return;
        }

        Bitmap compressedImageFile = FamilyLibrary.getInstance().getCompressor().compressToBitmap(file);
        saveStaticImageToDisk(compressedImageFile, providerId, entityId);

    }

    public static JSONObject getAutoPopulatedJsonEditFormString(String formName, Context context, CommonPersonObjectClient client, String eventType) {
        try {
            JSONObject form = FormUtils.getInstance(context).getFormJson(formName);
            LocationPickerView lpv = new LocationPickerView(context);
            lpv.init();
            // JsonFormUtils.addWomanRegisterHierarchyQuestions(form);
            Timber.d("Form is " + form.toString());
            if (form != null) {
                form.put(org.smartregister.family.util.JsonFormUtils.ENTITY_ID, client.getCaseId());
                form.put(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE, eventType);

                JSONObject metadata = form.getJSONObject(org.smartregister.family.util.JsonFormUtils.METADATA);
                String lastLocationId = LocationHelper.getInstance().getOpenMrsLocationId(lpv.getSelectedItem());

                metadata.put(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_LOCATION, lastLocationId);

                form.put(org.smartregister.family.util.JsonFormUtils.CURRENT_OPENSRP_ID, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false));

                //inject opensrp id into the form
                JSONObject stepOne = form.getJSONObject(org.smartregister.family.util.JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(org.smartregister.family.util.JsonFormUtils.FIELDS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    processPopulatableFields(client, jsonObject);

                }

                org.smartregister.family.util.JsonFormUtils.addLocHierarchyQuestions(form);

                return form;
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return null;
    }

    public static JSONObject getAutoPopulatedJsonEditMemberFormString(String title, String formName, Context context, CommonPersonObjectClient client, String eventType, String familyName, boolean isPrimaryCaregiver) {

        JSON_DB_MAP = new HashMap<>();
        JSON_DB_MAP.put(org.smartregister.addo.util.Constants.JsonAssets.SEX, DBConstants.KEY.GENDER);
        JSON_DB_MAP.put(org.smartregister.addo.util.Constants.JsonAssets.NATIONAL_ID, org.smartregister.addo.util.Constants.JsonAssets.NATIONAL_ID);
        JSON_DB_MAP.put(org.smartregister.addo.util.Constants.JsonAssets.VOTER_ID, AddoDBConstants.VOTER_ID);
        JSON_DB_MAP.put(org.smartregister.addo.util.Constants.JsonAssets.DRIVER_LICENSE, AddoDBConstants.DRIVER_LICENSE);
        JSON_DB_MAP.put(org.smartregister.addo.util.Constants.JsonAssets.PASSPORT, AddoDBConstants.PASSPORT);
        JSON_DB_MAP.put(org.smartregister.addo.util.Constants.JsonAssets.INSURANCE_PROVIDER, AddoDBConstants.INSURANCE_PROVIDER);
        JSON_DB_MAP.put(org.smartregister.addo.util.Constants.JsonAssets.INSURANCE_PROVIDER_OTHER, AddoDBConstants.INSURANCE_PROVIDER_OTHER);
        JSON_DB_MAP.put(org.smartregister.addo.util.Constants.JsonAssets.INSURANCE_PROVIDER_NUMBER,AddoDBConstants.INSURANCE_PROVIDER_NUMBER);
        JSON_DB_MAP.put(org.smartregister.addo.util.Constants.JsonAssets.DISABILITIES, AddoDBConstants.DISABILITIES);
        JSON_DB_MAP.put(org.smartregister.addo.util.Constants.JsonAssets.DISABILITY_TYPE, AddoDBConstants.DISABILITY_TYPE);
        JSON_DB_MAP.put(org.smartregister.addo.util.Constants.JsonAssets.OTHER_LEADER, AddoDBConstants.OTHER_LEADER);

        // get the event and the client from ec model
        try{
            Pair<Event, Client> eventClientPair = getEditMemberLatestProperties(client.getCaseId());

            JSONObject form = FormUtils.getInstance(context).getFormJson(formName);
            LocationPickerView lpv = new LocationPickerView(context);
            lpv.init();
            // JsonFormUtils.addWomanRegisterHierarchyQuestions(form);
            if (form != null) {
                form.put(org.smartregister.family.util.JsonFormUtils.ENTITY_ID, client.getCaseId());
                form.put(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE, eventType);

                JSONObject metadata = form.getJSONObject(org.smartregister.family.util.JsonFormUtils.METADATA);
                String lastLocationId = LocationHelper.getInstance().getOpenMrsLocationId(lpv.getSelectedItem());

                metadata.put(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_LOCATION, lastLocationId);

                form.put(org.smartregister.family.util.JsonFormUtils.CURRENT_OPENSRP_ID, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false));

                //inject opensrp id into the form
                JSONObject stepOne = form.getJSONObject(org.smartregister.family.util.JsonFormUtils.STEP1);

                if (StringUtils.isNotBlank(title)) {
                    stepOne.put(TITLE, title);
                }

                JSONArray jsonArray = stepOne.getJSONArray(org.smartregister.family.util.JsonFormUtils.FIELDS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    try {
                        processFieldsForMemberEdit(client, jsonObject, jsonArray, familyName, isPrimaryCaregiver, eventClientPair.first, eventClientPair.second);
                    } catch (Exception e) {
                        Timber.e(e);
                    }

                }

                return form;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }


    public static void processFieldsForMemberEdit(CommonPersonObjectClient client, JSONObject jsonObject, JSONArray jsonArray, String familyName, boolean isPrimaryCaregiver, Event ecEvent, Client ecClient) throws JSONException {


        switch (jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY).toLowerCase()) {
            case org.smartregister.family.util.Constants.JSON_FORM_KEY.DOB_UNKNOWN:
                computeDOBUnknown(jsonObject, client);
                break;
            case org.smartregister.addo.util.Constants.JsonAssets.AGE:
                computeAge(jsonObject, client);
                break;
            case DBConstants.KEY.DOB:
                computeDOB(jsonObject, client);
                break;

            case org.smartregister.family.util.Constants.KEY.PHOTO:
                computePhoto(jsonObject, client);
                break;

            case DBConstants.KEY.UNIQUE_ID:
                String uniqueId = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false);
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, uniqueId.replace("-", ""));
                break;
/**
 case FINGERPRINT_KEY:
 String simprintsId = ecClient.getIdentifier(SIMPRINTS_GUID) == null ? "" : ecClient.getIdentifier(SIMPRINTS_GUID);
 jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, simprintsId);
 break;
 **/
            case org.smartregister.addo.util.Constants.JsonAssets.PREGNANT_1_YR:
                computePregnantOneYr(jsonObject, ecEvent);
                break;

            case org.smartregister.addo.util.Constants.JsonAssets.SURNAME:
                computeSurname(jsonObject, ecClient, familyName);
                break;

            case org.smartregister.addo.util.Constants.JsonAssets.FAM_NAME:
                computeFamName(client, jsonObject, jsonArray, ecClient, familyName);
                break;

            case org.smartregister.addo.util.Constants.JsonAssets.PRIMARY_CARE_GIVER:
            case org.smartregister.addo.util.Constants.JsonAssets.IS_PRIMARY_CARE_GIVER:
                computePrimaryCareGiver(jsonObject, isPrimaryCaregiver);
                break;

            case org.smartregister.addo.util.Constants.JsonAssets.ID_AVAIL:
                computeIDAvail(jsonObject, ecClient);
                break;

            case org.smartregister.addo.util.Constants.JsonAssets.SERVICE_PROVIDER:
                computeServiceProvider(jsonObject, ecEvent);
                break;

            case org.smartregister.addo.util.Constants.JsonAssets.LEADER:
                computeLeader(jsonObject, ecClient);
                break;

            default:
                String db_key = JSON_DB_MAP.get(jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY).toLowerCase());
                if (StringUtils.isNotBlank(db_key)) {
                    jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), db_key, false));
                } else {
                    jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY), false));
                }
                break;

        }
    }

    private static Pair<Event, Client> getEditMemberLatestProperties(String baseEntityID) {

        Event ecEvent = null;
        Client ecClient = null;


        String query_client = "select json from client where baseEntityId = ? order by updatedAt desc";
        Cursor cursor = AddoApplication.getInstance().getRepository().getReadableDatabase().rawQuery(query_client, new String[]{baseEntityID});
        try {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                ecClient = jsonStringToJava(cursor.getString(0), Client.class);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Timber.e(e, e.toString());
        } finally {
            cursor.close();
        }


        String query_event = String.format("select json from event where baseEntityId = '%s' and eventType in ('%s','%s') order by updatedAt desc limit 1;",
                baseEntityID, org.smartregister.addo.util.Constants.EventType.UPDATE_FAMILY_MEMBER_REGISTRATION, org.smartregister.addo.util.Constants.EventType.FAMILY_MEMBER_REGISTRATION);

        Cursor cursor1 = AddoApplication.getInstance().getRepository().getReadableDatabase().rawQuery(query_event, new String[]{});
        try {
            cursor1.moveToFirst();

            while (!cursor1.isAfterLast()) {
                ecEvent = jsonStringToJava(cursor1.getString(0), Event.class);
                cursor1.moveToNext();
            }
        } catch (Exception e) {
            Timber.e(e, e.toString());
        } finally {
            cursor1.close();
        }

        return Pair.create(ecEvent, ecClient);
    }

    protected static void processPopulatableFields(CommonPersonObjectClient client, JSONObject jsonObject) throws JSONException {

        switch (jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY).toLowerCase()) {
            case Constants.JSON_FORM_KEY.DOB_UNKNOWN:
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.READ_ONLY, false);
                JSONObject optionsObject = jsonObject.getJSONArray(Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);
                optionsObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), Constants.JSON_FORM_KEY.DOB_UNKNOWN, false));

                break;
            case DBConstants.KEY.DOB:

                String dobString = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false);
                if (StringUtils.isNotBlank(dobString)) {
                    Date dob = Utils.dobStringToDate(dobString);
                    if (dob != null) {
                        jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, dd_MM_yyyy.format(dob));
                    }
                }

                break;

            case Constants.KEY.PHOTO:

                Photo photo = ImageUtils.profilePhotoByClientID(client.getCaseId(), Utils.getProfileImageResourceIDentifier());
                if (StringUtils.isNotBlank(photo.getFilePath())) {
                    jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, photo.getFilePath());
                }

                break;

            case DBConstants.KEY.UNIQUE_ID:

                String uniqueId = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false);
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, uniqueId.replace("-", ""));

                break;

            case "fam_name":

                String fam_name = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.FIRST_NAME, false);
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, fam_name);

                break;

            case DBConstants.KEY.VILLAGE_TOWN:

                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.VILLAGE_TOWN, false));

                break;

            case DBConstants.KEY.QUATER_CLAN:

                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.QUATER_CLAN, false));

                break;

            case DBConstants.KEY.STREET:

                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.STREET, false));

                break;

            case DBConstants.KEY.LANDMARK:

                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.LANDMARK, false));

                break;

            case DBConstants.KEY.FAMILY_SOURCE_INCOME:

                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.FAMILY_SOURCE_INCOME, false));

                break;

            case AddoDBConstants.NEAREST_HEALTH_FACILITY:

                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), AddoDBConstants.NEAREST_HEALTH_FACILITY, false));

                break;

            case DBConstants.KEY.GPS:

                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.GPS, false));

                break;

            default:

                Timber.e("ERROR:: Unprocessed Form Object Key " + jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY));

                break;

        }

        if (jsonObject.getString(org.smartregister.family.util.JsonFormUtils.KEY).equalsIgnoreCase(DBConstants.KEY.DOB)) {

            jsonObject.put(org.smartregister.family.util.JsonFormUtils.READ_ONLY, false);
            JSONObject optionsObject = jsonObject.getJSONArray(Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);
            optionsObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false));

        }
    }

    private static void saveStaticImageToDisk(Bitmap image, String providerId, String entityId) {
        if (image == null || isBlank(providerId) || isBlank(entityId)) {
            return;
        }
        OutputStream os = null;
        try {

            if (entityId != null && !entityId.isEmpty()) {
                final String absoluteFileName = DrishtiApplication.getAppDir() + File.separator + entityId + ".JPEG";

                File outputFile = new File(absoluteFileName);
                os = new FileOutputStream(outputFile);
                Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
                if (compressFormat != null) {
                    image.compress(compressFormat, 100, os);
                } else {
                    throw new IllegalArgumentException("Failed to updateFamilyRelations static image, could not retrieve image compression format from name "
                            + absoluteFileName);
                }
                // insert into the db
                ProfileImage profileImage = new ProfileImage();
                profileImage.setImageid(UUID.randomUUID().toString());
                profileImage.setAnmId(providerId);
                profileImage.setEntityID(entityId);
                profileImage.setFilepath(absoluteFileName);
                profileImage.setFilecategory("profilepic");
                profileImage.setSyncStatus(ImageRepository.TYPE_Unsynced);
                ImageRepository imageRepo = Utils.context().imageRepository();
                imageRepo.add(profileImage);
            }

        } catch (FileNotFoundException e) {
            Timber.e("Failed to updateFamilyRelations static image to disk");
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Timber.e("Failed to close static images output stream after attempting to write image");
                }
            }
        }

    }

    protected static Triple<Boolean, JSONObject, JSONArray> validateParameters(String jsonString) {

        JSONObject jsonForm = toJSONObject(jsonString);
        JSONArray fields = fields(jsonForm);

        Triple<Boolean, JSONObject, JSONArray> registrationFormParams = Triple.of(jsonForm != null && fields != null, jsonForm, fields);
        return registrationFormParams;
    }

    public static Event tagSyncMetadata(AllSharedPreferences allSharedPreferences, Event event) {
        String providerId = allSharedPreferences.fetchRegisteredANM();
        event.setProviderId(providerId);
        event.setLocationId(locationId(allSharedPreferences));
        event.setChildLocationId(allSharedPreferences.fetchCurrentLocality());
        event.setTeam(allSharedPreferences.fetchDefaultTeam(providerId));
        event.setTeamId(allSharedPreferences.fetchDefaultTeamId(providerId));

        event.setClientApplicationVersion(FamilyLibrary.getInstance().getApplicationVersion());
        event.setClientDatabaseVersion(FamilyLibrary.getInstance().getDatabaseVersion());

        return event;
    }


    public static Vaccine tagSyncMetadata(AllSharedPreferences allSharedPreferences, Vaccine vaccine) {
        String providerId = allSharedPreferences.fetchRegisteredANM();
        vaccine.setAnmId(providerId);
        vaccine.setLocationId(locationId(allSharedPreferences));
        vaccine.setChildLocationId(allSharedPreferences.fetchCurrentLocality());
        vaccine.setTeam(allSharedPreferences.fetchDefaultTeam(providerId));
        vaccine.setTeamId(allSharedPreferences.fetchDefaultTeamId(providerId));
        return vaccine;
    }

    public static ServiceRecord tagSyncMetadata(AllSharedPreferences allSharedPreferences, ServiceRecord serviceRecord) {
        String providerId = allSharedPreferences.fetchRegisteredANM();
        serviceRecord.setAnmId(providerId);
        serviceRecord.setLocationId(locationId(allSharedPreferences));
        serviceRecord.setChildLocationId(allSharedPreferences.fetchCurrentLocality());
        serviceRecord.setTeam(allSharedPreferences.fetchDefaultTeam(providerId));
        serviceRecord.setTeamId(allSharedPreferences.fetchDefaultTeamId(providerId));
        return serviceRecord;
    }

    /**
     * @param familyID
     * @param allSharedPreferences
     * @param jsonObject
     * @param providerId
     * @return Returns a triple object <b>DateOfDeath as String, BaseEntityID , List of Events </b>that should be processed
     */
    public static Triple<Pair<Date, String>, String, List<Event>> processRemoveMemberEvent(String familyID, AllSharedPreferences allSharedPreferences, JSONObject jsonObject, String providerId) {

        try {

            List<Event> events = new ArrayList<>();

            Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonObject.toString());

            if (!registrationFormParams.getLeft()) {
                return null;
            }

            Date dod = null;


            JSONObject metadata = getJSONObject(registrationFormParams.getMiddle(), METADATA);
            String memberID = getString(registrationFormParams.getMiddle(), ENTITY_ID);

            JSONArray fields = new JSONArray();

            int x = 0;
            while (x < registrationFormParams.getRight().length()) {
                //JSONObject obj = registrationFormParams.getRight().getJSONObject(x);
                String myKey = registrationFormParams.getRight().getJSONObject(x).getString(KEY);

                if (myKey.equalsIgnoreCase(org.smartregister.addo.util.Constants.FORM_CONSTANTS.REMOVE_MEMBER_FORM.DATE_MOVED) ||
                        myKey.equalsIgnoreCase(org.smartregister.addo.util.Constants.FORM_CONSTANTS.REMOVE_MEMBER_FORM.REASON)
                ) {
                    fields.put(registrationFormParams.getRight().get(x));
                }
                if (myKey.equalsIgnoreCase(org.smartregister.addo.util.Constants.FORM_CONSTANTS.REMOVE_MEMBER_FORM.DATE_DIED)) {
                    fields.put(registrationFormParams.getRight().get(x));
                    try {
                        dod = dd_MM_yyyy.parse(registrationFormParams.getRight().getJSONObject(x).getString(VALUE));
                    } catch (Exception e) {
                        Timber.d(e.toString());
                    }
                }
                x++;
            }

            String encounterType = getString(jsonObject, ENCOUNTER_TYPE);

            String eventType;
            String tableName;

            if (encounterType.equalsIgnoreCase(org.smartregister.addo.util.Constants.EventType.REMOVE_CHILD)) {
                eventType = org.smartregister.addo.util.Constants.EventType.REMOVE_CHILD;
                tableName = org.smartregister.addo.util.Constants.TABLE_NAME.CHILD;
            } else if (encounterType.equalsIgnoreCase(org.smartregister.addo.util.Constants.EventType.REMOVE_FAMILY)) {
                eventType = org.smartregister.addo.util.Constants.EventType.REMOVE_FAMILY;
                tableName = org.smartregister.addo.util.Constants.TABLE_NAME.FAMILY;
            } else {
                eventType = org.smartregister.addo.util.Constants.EventType.REMOVE_MEMBER;
                tableName = org.smartregister.addo.util.Constants.TABLE_NAME.FAMILY_MEMBER;
            }

            Event eventMember = JsonFormUtils.createEvent(fields, metadata, formTag(allSharedPreferences), memberID,
                    eventType,
                    tableName
            );
            JsonFormUtils.tagSyncMetadata(Utils.context().allSharedPreferences(), eventMember);
            events.add(eventMember);


            return Triple.of(Pair.create(dod, encounterType), memberID, events);
        } catch (Exception e) {
            Timber.e(e.toString());
            return null;
        }
    }

    public static FamilyMember getFamilyMemberFromRegistrationForm(String jsonString, String familyBaseEntityId, String entityID) throws JSONException {
        FamilyMember member = new FamilyMember();

        Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonString);
        if (!registrationFormParams.getLeft()) {
            return null;
        }

        JSONArray fields = registrationFormParams.getRight();

        member.setFamilyID(familyBaseEntityId);
        member.setMemberID(entityID);
        member.setPhone(getJsonFieldValue(fields, org.smartregister.addo.util.Constants.JsonAssets.FAMILY_MEMBER.PHONE_NUMBER));
        member.setOtherPhone(getJsonFieldValue(fields, org.smartregister.addo.util.Constants.JsonAssets.FAMILY_MEMBER.OTHER_PHONE_NUMBER));
        member.setEduLevel(getJsonFieldValue(fields, org.smartregister.addo.util.Constants.JsonAssets.FAMILY_MEMBER.HIGHEST_EDUCATION_LEVEL));
        member.setPrimaryCareGiver(
                getJsonFieldValue(fields, org.smartregister.addo.util.Constants.JsonAssets.PRIMARY_CARE_GIVER).equalsIgnoreCase("Yes") ||
                        getJsonFieldValue(fields, org.smartregister.addo.util.Constants.JsonAssets.IS_PRIMARY_CARE_GIVER).equalsIgnoreCase("Yes")
        );
        member.setFamilyHead(false);

        return member;
    }

    private static String getJsonFieldValue(JSONArray jsonArray, String key) {
        try {
            JSONObject jsonObject = getFieldJSONObject(jsonArray, key);
            if (jsonObject.has(org.smartregister.family.util.JsonFormUtils.VALUE)) {
                return jsonObject.getString(org.smartregister.family.util.JsonFormUtils.VALUE);
            } else {
                return "";
            }
        } catch (Exception e) {
            Timber.e(e.toString());
        }
        return "";
    }

    public static Pair<List<Client>, List<Event>> processFamilyUpdateRelations(Context context, FamilyMember familyMember, String lastLocationId) throws Exception {
        List<Client> clients = new ArrayList<>();
        List<Event> events = new ArrayList<>();


        ECSyncHelper syncHelper = AddoApplication.getInstance().getEcSyncHelper();
        Client familyClient = syncHelper.convert(syncHelper.getClient(familyMember.getFamilyID()), Client.class);
        Map<String, List<String>> relationships = familyClient.getRelationships();

        if (familyMember.getPrimaryCareGiver()) {
            relationships.put(org.smartregister.addo.util.Constants.RELATIONSHIP.PRIMARY_CAREGIVER, toStringList(familyMember.getMemberID()));
            familyClient.setRelationships(relationships);
        }

        if (familyMember.getFamilyHead()) {
            relationships.put(org.smartregister.addo.util.Constants.RELATIONSHIP.FAMILY_HEAD, toStringList(familyMember.getMemberID()));
            familyClient.setRelationships(relationships);
        }

        clients.add(familyClient);


        JSONObject metadata = FormUtils.getInstance(context)
                .getFormJson(Utils.metadata().familyRegister.formName)
                .getJSONObject(org.smartregister.family.util.JsonFormUtils.METADATA);

        metadata.put(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_LOCATION, lastLocationId);

        FormTag formTag = new FormTag();
        formTag.providerId = Utils.context().allSharedPreferences().fetchRegisteredANM();
        formTag.appVersion = FamilyLibrary.getInstance().getApplicationVersion();
        formTag.databaseVersion = FamilyLibrary.getInstance().getDatabaseVersion();

        Event eventFamily = JsonFormUtils.createEvent(new JSONArray(), metadata, formTag, familyMember.getFamilyID(),
                org.smartregister.addo.util.Constants.EventType.UPDATE_FAMILY_RELATIONS,
                Utils.metadata().familyRegister.tableName);
        JsonFormUtils.tagSyncMetadata(Utils.context().allSharedPreferences(), eventFamily);


        Event eventMember = JsonFormUtils.createEvent(new JSONArray(), metadata, formTag, familyMember.getMemberID(), org.smartregister.addo.util.Constants.EventType.UPDATE_FAMILY_MEMBER_RELATIONS,
                Utils.metadata().familyMemberRegister.tableName);
        JsonFormUtils.tagSyncMetadata(Utils.context().allSharedPreferences(), eventMember);

        eventMember.addObs(new Obs("concept", "text", org.smartregister.addo.util.Constants.FORM_CONSTANTS.CHANGE_CARE_GIVER.PHONE_NUMBER.CODE, "",
                toList(familyMember.getPhone()), new ArrayList<>(), null, DBConstants.KEY.PHONE_NUMBER));

        eventMember.addObs(new Obs("concept", "text", org.smartregister.addo.util.Constants.FORM_CONSTANTS.CHANGE_CARE_GIVER.OTHER_PHONE_NUMBER.CODE, org.smartregister.addo.util.Constants.FORM_CONSTANTS.CHANGE_CARE_GIVER.OTHER_PHONE_NUMBER.PARENT_CODE,
                toList(familyMember.getOtherPhone()), new ArrayList<>(), null, DBConstants.KEY.OTHER_PHONE_NUMBER));

        eventMember.addObs(new Obs("concept", "text", org.smartregister.addo.util.Constants.FORM_CONSTANTS.CHANGE_CARE_GIVER.HIGHEST_EDU_LEVEL.CODE, "",
                toList(getEducationLevels(context).get(familyMember.getEduLevel())), toList(familyMember.getEduLevel()), null, DBConstants.KEY.HIGHEST_EDU_LEVEL));


        events.add(eventFamily);
        events.add(eventMember);

        return Pair.create(clients, events);
    }

    private static List<String> toStringList(String... vals) {
        return new ArrayList<>(Arrays.asList(vals));
    }

    private static HashMap<String, String> getEducationLevels(Context context) {
        HashMap<String, String> educationLevels = new HashMap<>();
        educationLevels.put(context.getResources().getString(R.string.edu_level_none), "1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        educationLevels.put(context.getResources().getString(R.string.edu_level_primary), "1713AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        educationLevels.put(context.getResources().getString(R.string.edu_level_secondary), "1714AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        educationLevels.put(context.getResources().getString(R.string.edu_level_post_secondary), "159785AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        return educationLevels;
    }

    /**
     * Returns a value from json form field
     *
     * @param jsonObject native forms jsonObject
     * @param key        field object key
     * @return value
     */
    public static String getValue(JSONObject jsonObject, String key) {
        try {
            JSONObject formField = com.vijay.jsonwizard.utils.FormUtils.getFieldFromForm(jsonObject, key);
            if (formField != null && formField.has(JsonFormConstants.VALUE)) {
                return formField.getString(JsonFormConstants.VALUE);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return "";
    }

    /**
     * Returns a value from a native forms checkbox field and returns an comma separated string
     *
     * @param jsonObject native forms jsonObject
     * @param key        field object key
     * @return value
     */
    public static String getCheckBoxValue(JSONObject jsonObject, String key) {
        try {
            JSONArray jsonArray = jsonObject.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);

            JSONObject jo = null;
            int x = 0;
            while (jsonArray.length() > x) {
                jo = jsonArray.getJSONObject(x);
                if (jo.getString(JsonFormConstants.KEY).equalsIgnoreCase(key)) {
                    break;
                }
                x++;
            }

            StringBuilder resBuilder = new StringBuilder();
            if (jo != null) {
                // read all the checkboxes
                JSONArray jaOptions = jo.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                int optionSize = jaOptions.length();
                int y = 0;
                while (optionSize > y) {
                    JSONObject options = jaOptions.getJSONObject(y);
                    if (options.getBoolean(JsonFormConstants.VALUE)) {
                        resBuilder.append(options.getString(JsonFormConstants.TEXT)).append(", ");
                    }
                    y++;
                }

                String res = resBuilder.toString();
                res = (res.length() >= 2) ? res.substring(0, res.length() - 2) : "";
                return res;
            }

        } catch (Exception e) {
            Timber.e(e);
        }
        return "";
    }

    public interface Flavor {
        JSONObject getAutoJsonEditMemberFormString(String title, String formName, Context context, CommonPersonObjectClient client, String eventType, String familyName, boolean isPrimaryCaregiver);

        void processFieldsForMemberEdit(CommonPersonObjectClient client, JSONObject jsonObject, JSONArray jsonArray, String familyName, boolean isPrimaryCaregiver, Event ecEvent, Client ecClient) throws JSONException;
    }

    private static Event getEditAncLatestProperties(String baseEntityID) {

        Event ecEvent = null;

        String query_event = String.format("select json from event where baseEntityId = '%s' and eventType in ('%s','%s') order by updatedAt desc limit 1;",
                baseEntityID, org.smartregister.addo.util.Constants.EventType.UPDATE_ANC_REGISTRATION, org.smartregister.addo.util.Constants.EventType.ANC_REGISTRATION);

        Cursor cursor = AddoApplication.getInstance().getRepository().getReadableDatabase().rawQuery(query_event, new String[]{});
        try {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                ecEvent = jsonStringToJava(cursor.getString(0), Event.class);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Timber.e(e, e.toString());
        } finally {
            cursor.close();
        }
        return ecEvent;
    }

    private static JSONObject getFormWithMetaData(String baseEntityID, Context context, String formName, String eventType) {
        JSONObject form = null;
        try {
            form = FormUtils.getInstance(context).getFormJson(formName);
            LocationPickerView lpv = new LocationPickerView(context);
            lpv.init();
            if (form != null) {
                form.put(org.smartregister.family.util.JsonFormUtils.ENTITY_ID, baseEntityID);
                form.put(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE, eventType);

                JSONObject metadata = form.getJSONObject(org.smartregister.family.util.JsonFormUtils.METADATA);
                String lastLocationId = LocationHelper.getInstance().getOpenMrsLocationId(lpv.getSelectedItem());

                metadata.put(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_LOCATION, lastLocationId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return form;

    }

    public static JSONObject getAutoJsonEditAncFormString(String baseEntityID, Context context, String formName, String eventType, String title) {
        try {

            Event event = getEditAncLatestProperties(baseEntityID);
            final List<Obs> observations = event.getObs();
            JSONObject form = getFormWithMetaData(baseEntityID, context, formName, eventType);
            LocationPickerView lpv = new LocationPickerView(context);
            lpv.init();
            if (form != null) {
                JSONObject stepOne = form.getJSONObject(org.smartregister.family.util.JsonFormUtils.STEP1);

                if (StringUtils.isNotBlank(title)) {
                    stepOne.put(TITLE, title);
                }
                JSONArray jsonArray = stepOne.getJSONArray(org.smartregister.family.util.JsonFormUtils.FIELDS);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase("last_menstrual_period") ||
                            jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase("delivery_method")) {
                        jsonObject.put(JsonFormUtils.READ_ONLY, true);
                    }
                    try {
                        for (Obs obs : observations) {
                            if (obs.getFormSubmissionField().equalsIgnoreCase(jsonObject.getString(JsonFormUtils.KEY))) {
                                if (jsonObject.getString("type").equals("spinner"))
                                    jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, obs.getHumanReadableValues().get(0));
                                else
                                    jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, obs.getValue());
                            }
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }

                return form;
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return null;
    }

    private static void computeAge(JSONObject jsonObject, CommonPersonObjectClient client) throws JSONException {
        String dobString = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false);
        dobString = org.smartregister.family.util.Utils.getDuration(dobString);
        dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : "0";
        jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Integer.valueOf(dobString));
    }

    private static void computeDOBUnknown(JSONObject jsonObject, CommonPersonObjectClient client) throws JSONException {
        jsonObject.put(org.smartregister.family.util.JsonFormUtils.READ_ONLY, false);
        JSONObject optionsObject = jsonObject.getJSONArray(org.smartregister.family.util.Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);
        optionsObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), org.smartregister.family.util.Constants.JSON_FORM_KEY.DOB_UNKNOWN, false));
    }


    private static void computeDOB(JSONObject jsonObject, CommonPersonObjectClient client) throws JSONException {

        String dobString = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false);
        if (StringUtils.isNotBlank(dobString)) {
            Date dob = Utils.dobStringToDate(dobString);
            if (dob != null) {
                jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, JsonFormUtils.dd_MM_yyyy.format(dob));
            }
        }
    }

    private static void computePhoto(JSONObject jsonObject, CommonPersonObjectClient client) throws JSONException {

        Photo photo = ImageUtils.profilePhotoByClientID(client.getCaseId(), Utils.getProfileImageResourceIDentifier());
        if (StringUtils.isNotBlank(photo.getFilePath())) {
            jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, photo.getFilePath());
        }
    }

    private static void computePregnantOneYr(JSONObject jsonObject, Event ecEvent) throws JSONException {
        if (ecEvent != null) {
            String id = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
            for (Obs obs : ecEvent.getObs()) {
                if (obs.getValues() != null && obs.getFieldCode().contains(id)) {
                    jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, obs.getHumanReadableValues().get(0));
                }
            }
        }
    }

    private static void computeSurname(JSONObject jsonObject, Client ecClient, String familyName) throws JSONException {
        if (ecClient != null) {
            jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE,
                    (ecClient.getLastName() == null ? familyName : ecClient.getLastName()));
        }
    }

    private static void computeFamName(CommonPersonObjectClient client, JSONObject jsonObject, JSONArray jsonArray, Client ecClient, String familyName) throws JSONException {

        if (ecClient.getLastName() != null) {

            final String SAME_AS_FAM_NAME = "same_as_fam_name";
            final String SURNAME = "surname";

            jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, familyName);

            String lastName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.LAST_NAME, false);

            JSONObject sameAsFamName = JsonFormUtils.getFieldJSONObject(jsonArray, SAME_AS_FAM_NAME);
            JSONObject sameOptions = sameAsFamName.getJSONArray(org.smartregister.family.util.Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);

            if (familyName.equals(lastName)) {
                sameOptions.put(org.smartregister.family.util.JsonFormUtils.VALUE, true);
            } else {
                sameOptions.put(org.smartregister.family.util.JsonFormUtils.VALUE, false);
            }

            JSONObject surname = JsonFormUtils.getFieldJSONObject(jsonArray, SURNAME);
            if (!familyName.equals(lastName)) {
                surname.put(org.smartregister.family.util.JsonFormUtils.VALUE, lastName);
            } else {
                surname.put(org.smartregister.family.util.JsonFormUtils.VALUE, "");
            }

        }
    }

    private static void computePrimaryCareGiver(JSONObject jsonObject, boolean isPrimaryCaregiver) throws JSONException {
        if (isPrimaryCaregiver) {
            jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, "Yes");
            jsonObject.put(org.smartregister.family.util.JsonFormUtils.READ_ONLY, true);
        } else {
            jsonObject.put(org.smartregister.family.util.JsonFormUtils.VALUE, "No");
        }
    }

    private static void computeIDAvail(JSONObject jsonObject, Client ecClient) throws JSONException {

        if (ecClient != null) {
            for (int i = 0; i < jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).length(); i++) {
                JSONObject obj = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).getJSONObject(i);
                String key = obj.getString(JsonFormConstants.KEY);

                String val = (String) ecClient.getAttribute("id_avail");

                if (val != null && key != null && val.contains(key)) {
                    obj.put(JsonFormConstants.VALUE, true);
                } else {
                    obj.put(JsonFormConstants.VALUE, false);
                }
            }
        }
    }

    private static void computeServiceProvider(JSONObject jsonObject, Event ecEvent) throws JSONException {
        if (ecEvent != null) {
            for (int i = 0; i < jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).length(); i++) {
                JSONObject obj = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).getJSONObject(i);
                String id = obj.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
                for (Obs obs : ecEvent.getObs()) {
                    if (obs.getValues() != null && obs.getValues().contains(id)) {
                        obj.put(JsonFormConstants.VALUE, true);
                    }
                }
            }
        }
    }

    private static void computeLeader(JSONObject jsonObject, Client ecClient) throws JSONException {
        if (ecClient != null) {
            for (int i = 0; i < jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).length(); i++) {
                JSONObject obj = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME).getJSONObject(i);
                String key = obj.getString(JsonFormConstants.KEY);

                String val = (String) ecClient.getAttribute("Community_Leader");

                if (val != null && key != null && val.contains(key)) {
                    obj.put(JsonFormConstants.VALUE, true);
                } else {
                    obj.put(JsonFormConstants.VALUE, false);
                }
            }
        }
    }

    public static String lookForClientsUniqueId(String guid){

        //baseEntityId
        String UniqueId = "";
        Client mClient = null;

        String query_client = "select json from client where json LIKE '%"+guid+"%' ";
        Cursor cursor = AddoApplication.getInstance().getRepository().getReadableDatabase().rawQuery(query_client, null);

        try {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                mClient = jsonStringToJava(cursor.getString(0), Client.class);
                UniqueId = mClient.getIdentifier("opensrp_id");
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Timber.e(e, e.toString());
        } finally {
            cursor.close();
        }

        return UniqueId;
    }

    public static String lookForClientsBaseEntityId(String guid){

        //baseEntityId
        String UniqueId = "";
        Client mClient = null;

        String query_client = "select json from client where json LIKE '%"+guid+"%' ";
        Cursor cursor = AddoApplication.getInstance().getRepository().getReadableDatabase().rawQuery(query_client, null);

        try {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                mClient = jsonStringToJava(cursor.getString(0), Client.class);
                UniqueId = mClient.getBaseEntityId();
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Timber.e(e, e.toString());
        } finally {
            cursor.close();
        }

        return UniqueId;
    }

    public static FingerPrintScanResultModel lookForWithGuid(String guid){

        //baseEntityId
        FingerPrintScanResultModel client = null;
        Client mClient = null;

        String query_client = "select json from client where json LIKE '%"+guid+"%' ";
        Cursor cursor = AddoApplication.getInstance().getRepository().getReadableDatabase().rawQuery(query_client, null);

        try {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                mClient = jsonStringToJava(cursor.getString(0), Client.class);
                //UniqueId = mClient.getIdentifier("opensrp_id");
                CommonPersonObject personObject = getCommonRepository(org.smartregister.family.util.Utils.metadata()
                        .familyMemberRegister.tableName).findByBaseEntityId(mClient.getBaseEntityId());
                final CommonPersonObjectClient client2 = new CommonPersonObjectClient(personObject.getCaseId(), personObject.getDetails(), "");
                client2.setColumnmaps(personObject.getColumnmaps());


                String firstName = org.smartregister.family.util.Utils.getValue(client2.getColumnmaps(), "first_name", true);
                String middleName = org.smartregister.family.util.Utils.getValue(client2.getColumnmaps(), "middle_name", true);
                String lastName = org.smartregister.family.util.Utils.getValue(client2.getColumnmaps(), "last_name", true);
                String gender = org.smartregister.family.util.Utils.getValue(client2.getColumnmaps(), "gender", true);
                String dob = org.smartregister.family.util.Utils.getValue(client2.getColumnmaps(), "dob", false);
                //String familyBaseEntity = org.smartregister.family.util.Utils.getValue(client2.getColumnmaps(), "relational_id", false);
                //String village = personObject.getCaseId();
                String familyBaseEntity = "Village";
                String dobString = org.smartregister.family.util.Utils.getDuration(dob);
                dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;
                String patientName = org.smartregister.family.util.Utils.getName(firstName, middleName, lastName);
                patientName = patientName + ", " + dobString;

                client = new FingerPrintScanResultModel(patientName, gender, familyBaseEntity);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Timber.e(e, e.toString());
        } finally {
            cursor.close();
        }

        return client;
    }

    public static CommonRepository getCommonRepository(String tableName) {
        return org.smartregister.family.util.Utils.context().commonrepository(tableName);
    }

    public static String getValueOfClientsFields(Map<String, String> map, String field){
        return org.smartregister.family.util.Utils.getValue(map, field, false);
    }


    public static Intent getAncPncStartFormIntent(JSONObject jsonForm, Context context) {
        Intent intent = new Intent(context, org.smartregister.family.util.Utils.metadata().familyMemberFormActivity);
        intent.putExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

        Form form = new Form();
        form.setActionBarBackground(R.color.family_actionbar);
        form.setWizard(false);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        return intent;
    }

}