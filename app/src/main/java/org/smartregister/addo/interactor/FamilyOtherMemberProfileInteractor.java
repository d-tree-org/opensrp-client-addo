package org.smartregister.addo.interactor;

import androidx.annotation.VisibleForTesting;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.smartregister.addo.contract.FamilyOtherMemberProfileExtendedContract;
import org.smartregister.addo.util.CoreConstants;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.repository.VisitRepository;
import org.smartregister.chw.anc.util.JsonFormUtils;
import org.smartregister.chw.anc.util.NCUtils;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class FamilyOtherMemberProfileInteractor implements FamilyOtherMemberProfileExtendedContract.Interactor {

    private AppExecutors appExecutors;
    private String relationalId;

    @VisibleForTesting
    FamilyOtherMemberProfileInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public FamilyOtherMemberProfileInteractor(String relationalId) {
        this(new AppExecutors());
        this.relationalId = relationalId;
    }



    @Override
    public void onDestroy(boolean b) {

    }

    public CommonRepository getCommonRepository(String tableName) {
        return Utils.context().commonrepository(tableName);
    }

    @Override
    public void submitVisit(boolean editMode, String memberID, Map<String, String> formForSubmission, FamilyOtherMemberProfileExtendedContract.InteractorCallBack callBack) {
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

        // only tag the first event with the date
        if (StringUtils.isBlank(parentEventType)) {
            prepareEvent(baseEvent);
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

    protected void prepareEvent(Event baseEvent) {
        if (baseEvent != null) {
            // add anc date obs and last
            List<Object> list = new ArrayList<>();
            list.add(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
            baseEvent.addObs(new Obs("concept", "text", "addo_visit_date", "",
                    list, new ArrayList<>(), null, "addo_visit_date"));
        }
    }

    public VisitRepository visitRepository() {
        return AncLibrary.getInstance().visitRepository();
    }

    protected String getEncounterType(String baseEntityId) {
        return CoreConstants.EventType.OTHER_MEMBER_ADDO_VISIT;
    }

    protected String getTableName(String baseEntityId) {
        return CoreConstants.TABLE_NAME.FAMILY_MEMBER;
    }

    private String getClientLocationId(String baseEntityId) {
        final CommonPersonObject familyObject = getCommonRepository(Utils.metadata().familyRegister.tableName).findByBaseEntityId(baseEntityId);
        final CommonPersonObjectClient pClient = new CommonPersonObjectClient(familyObject.getCaseId(),
                familyObject.getDetails(), "");
        pClient.setColumnmaps(familyObject.getColumnmaps());

        String villageTown = Utils.getValue(pClient.getColumnmaps(), DBConstants.KEY.VILLAGE_TOWN, false);

        return LocationHelper.getInstance().getOpenMrsLocationId(villageTown);
    }

}
