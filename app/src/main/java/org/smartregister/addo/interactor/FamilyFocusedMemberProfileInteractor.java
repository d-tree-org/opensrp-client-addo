package org.smartregister.addo.interactor;

import androidx.annotation.VisibleForTesting;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.smartregister.addo.contract.FamilyFocusedMemberProfileContract;
import org.smartregister.addo.dao.AdolescentDao;
import org.smartregister.addo.dao.AncDao;
import org.smartregister.addo.dao.PNCDao;
import org.smartregister.addo.dao.VisitDao;
import org.smartregister.addo.util.CoreConstants;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.repository.VisitRepository;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.anc.util.JsonFormUtils;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.anc.util.VisitUtils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.FormUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class FamilyFocusedMemberProfileInteractor implements FamilyFocusedMemberProfileContract.Interactor {

    private AppExecutors appExecutors;
    private String relationalId;
    private FormUtils formUtils;

    @VisibleForTesting
    FamilyFocusedMemberProfileInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public FamilyFocusedMemberProfileInteractor() {
        this(new AppExecutors());
    }


    @Override
    public void onDestroy(boolean isChangingConfiguration) {

    }

    @Override
    public void refreshProfileView(final String baseEntityId, final FamilyFocusedMemberProfileContract.InteractorCallBack callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final CommonPersonObject personObject = getCommonRepository(Utils.metadata().familyMemberRegister.tableName).findByBaseEntityId(baseEntityId);
                final CommonPersonObjectClient pClient = new CommonPersonObjectClient(personObject.getCaseId(),
                        personObject.getDetails(), "");
                pClient.setColumnmaps(personObject.getColumnmaps());

                if (pClient != null)
                    relationalId = Utils.getValue(pClient.getColumnmaps(), DBConstants.KEY.RELATIONAL_ID, false);

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.refreshProfileTopSection(pClient);
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    public CommonRepository getCommonRepository(String tableName) {
        return Utils.context().commonrepository(tableName);
    }

    @Override
    public void submitVisit(final boolean editMode, final String memberID, Map<String, String> formForSubmission, FamilyFocusedMemberProfileContract.InteractorCallBack callBack) {
        final Runnable runnable = () -> {
            boolean result = true;
            try {
                submitVisit(editMode, memberID, formForSubmission, "");
            } catch (Exception e) {
                Timber.e(e);
                result = false;
            }

            final boolean finalResult = result;
            appExecutors.mainThread().execute(() -> callBack.onSubmitted(finalResult));
        };

        appExecutors.diskIO().execute(runnable);
    }

    protected void submitVisit(final boolean editMode, final String memberID, final Map<String, String> dsForm, String parentEventType) throws Exception {
        // create a map of the different types
        String payloadType = null;
        String payloadDetails = null;

        if (!dsForm.isEmpty()) {
            String type = StringUtils.isBlank(parentEventType) ? getEncounterType(memberID) : getEncounterType(memberID);

            // persist to database
            Visit visit = saveVisit(editMode, memberID, type, dsForm, parentEventType);
            if (visit != null) {
                saveVisitDetails(visit, payloadType, payloadDetails);
            }
        }
    }

    protected @Nullable Visit saveVisit(boolean editMode, String memberID, String encounterType,
                                        final Map<String, String> jsonString,
                                        String parentEventType
    ) throws Exception {

        AllSharedPreferences allSharedPreferences = AncLibrary.getInstance().context().allSharedPreferences();

        String derivedEncounterType = StringUtils.isBlank(parentEventType) ? encounterType : "";
        Event baseEvent = JsonFormUtils.processVisitJsonForm(allSharedPreferences, memberID, derivedEncounterType, jsonString, getTableName(memberID));
        String formEncounterType = getEncounterType(jsonString);
        // only tag the first event with the date & add the actual encounter type as a detail
        if (StringUtils.isBlank(parentEventType)) {
            prepareEvent(baseEvent, formEncounterType);
        }

        if (baseEvent != null) {
            baseEvent.setFormSubmissionId(JsonFormUtils.generateRandomUUIDString());
            JsonFormUtils.tagEvent(allSharedPreferences, baseEvent);
            baseEvent.setLocationId(getClientLocationId(relationalId));

            String visitID = (editMode) ?
                    visitRepository().getLatestVisit(memberID, getEncounterType(memberID)).getVisitId() :
                    JsonFormUtils.generateRandomUUIDString();

            Visit visit = NCUtils.eventToVisit(baseEvent, visitID);
            visit.setPreProcessedJson(new Gson().toJson(baseEvent));
            visit.setParentVisitID(getParentVisitEventID(visit, parentEventType));

            visitRepository().addVisit(visit);
            return visit;
        }
        return null;
    }

    protected void saveVisitDetails(Visit visit, String payloadType, String payloadDetails) {
        if (visit.getVisitDetails() == null) return;

        for (Map.Entry<String, List<VisitDetail>> entry : visit.getVisitDetails().entrySet()) {
            if (entry.getValue() != null) {
                for (VisitDetail d : entry.getValue()) {
                    d.setPreProcessedJson(payloadDetails);
                    d.setPreProcessedType(payloadType);
                    AncLibrary.getInstance().visitDetailsRepository().addVisitDetails(d);
                }
            }
        }
    }

    protected String getParentVisitEventID(Visit visit, String parentEventType) {
        return visitRepository().getParentVisitEventID(visit.getBaseEntityId(), parentEventType, visit.getDate());
    }

    protected void prepareEvent(Event baseEvent, String encounterType) {
        if (baseEvent != null) {
            // add anc date obs and last
            List<Object> list = new ArrayList<>();
            list.add(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
            baseEvent.addObs(new Obs("concept", "text", "addo_visit_date", "",
                    list, new ArrayList<>(), null, "addo_visit_date"));
            List<Object> encounterList = new ArrayList<>();
            encounterList.add(encounterType);
            baseEvent.addObs(new Obs("concept", "text", "encounter_type", "",
                    encounterList, new ArrayList<>(), null, "encounter_type"));
        }
    }

    public VisitRepository visitRepository() {
        return AncLibrary.getInstance().visitRepository();
    }

    protected String getEncounterType(String baseEntityId) {
        if (AncDao.isANCMember(baseEntityId)) {
            return CoreConstants.EventType.ANC_ADDO_VISIT;
        } else if (PNCDao.isPNCMember(baseEntityId)) {
            return CoreConstants.EventType.PNC_ADDO_VISIT;
        } else if (AdolescentDao.isAdolescentMember(baseEntityId)) {
            return CoreConstants.EventType.ADOLESCENT_ADDO_VISIT;
        } else {
            return CoreConstants.EventType.CHILD_ADDO_VISIT;
        }
    }

    protected String getEncounterType(Map<String, String> jsonMap) {
        // Since we're handling a form at a time, get one key = Encounter Type
        return (jsonMap.size() == 1) ? jsonMap.keySet().iterator().next() : "";
    }

    protected String getTableName(String baseEntityId) {
        if (AncDao.isANCMember(baseEntityId)) {
            return Constants.TABLES.ANC_MEMBERS;
        } else if (PNCDao.isPNCMember(baseEntityId)) {
            return Constants.TABLES.PREGNANCY_OUTCOME;
        } else if (AdolescentDao.isAdolescentMember(baseEntityId)) {
            return org.smartregister.addo.util.Constants.TABLE_NAME.ADOLESCENT;
        } else {
            return Constants.TABLES.EC_CHILD;
        }
    }

    private String getClientLocationId(String baseEntityId) {
        final CommonPersonObject familyObject = getCommonRepository(Utils.metadata().familyRegister.tableName).findByBaseEntityId(baseEntityId);
        final CommonPersonObjectClient pClient = new CommonPersonObjectClient(familyObject.getCaseId(),
                familyObject.getDetails(), "");
        pClient.setColumnmaps(familyObject.getColumnmaps());

        String villageTown = Utils.getValue(pClient.getColumnmaps(), DBConstants.KEY.VILLAGE_TOWN, false);

        return LocationHelper.getInstance().getOpenMrsLocationId(villageTown);
    }

    @Override
    public void checkIfTasksDoneWithin24H(String baseEntityId, FamilyFocusedMemberProfileContract.InteractorCallBack callBack) {
        // Check whether the 3 tasks were performed recently
        String visitType = getEncounterType(baseEntityId);
        boolean screeningDone = checkIfTaskDoneWithin24H(getFocusScreeningForm(visitType), baseEntityId, visitType);
        boolean commoditiesGiven = checkIfTaskDoneWithin24H(getFormUtils().getFormJson(CoreConstants.JSON_FORM.getAddoCommodities()), baseEntityId, visitType);
        boolean dispenseDone = checkIfTaskDoneWithin24H(getFormUtils().getFormJson(CoreConstants.JSON_FORM.getAddoAttendPrescriptionsFromHf()), baseEntityId, visitType);

        if (screeningDone) {
            callBack.showScreeningDone(true);
        }
        if (commoditiesGiven) {
            callBack.showCommoditiesGiven(true);
        }
        if (dispenseDone){
            callBack.showDispenseOrLabTestsDone(true);
        }
    }

    private JSONObject getFocusScreeningForm(String visitType) {
        JSONObject jsonObject;
        switch (visitType) {
            case CoreConstants.EventType.ANC_ADDO_VISIT:
                jsonObject = getFormUtils().getFormJson(CoreConstants.JSON_FORM.getAncAddoDangerSigns());
                break;
            case CoreConstants.EventType.PNC_ADDO_VISIT:
                jsonObject = getFormUtils().getFormJson(CoreConstants.JSON_FORM.getPncAddoDangerSigns());
                break;
            case CoreConstants.EventType.CHILD_ADDO_VISIT:
                jsonObject = getFormUtils().getFormJson(CoreConstants.JSON_FORM.getChildAddoDangerSigns());
                break;
            case CoreConstants.EventType.ADOLESCENT_ADDO_VISIT:
                jsonObject = getFormUtils().getFormJson(CoreConstants.JSON_FORM.getAdolescentAddoScreening());
                break;
            default:
                jsonObject = null;
                break;
        }
        return jsonObject;
    }

    private boolean checkIfTaskDoneWithin24H(JSONObject jsonObject, String baseEntityId, String visitType) {
        if (jsonObject != null) {
            String encounter = jsonObject.optString(org.smartregister.addo.util.JsonFormUtils.ENCOUNTER_TYPE);
            Visit latestVisit = VisitDao.getLatestVisit(baseEntityId, visitType, encounter);
            return VisitUtils.isVisitWithin24Hours(latestVisit);
        }
        return false;
    }

    private FormUtils getFormUtils() {
        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(Utils.context().applicationContext());
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return formUtils;
    }

}
