package org.smartregister.addo.application;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;

import com.evernote.android.job.JobManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.P2POptions;
import org.smartregister.addo.BuildConfig;
import org.smartregister.addo.activity.FamilyProfileActivity;
import org.smartregister.addo.activity.LoginActivity;
import org.smartregister.addo.helper.RulesEngineHelper;
import org.smartregister.addo.job.AddoJobCreator;
import org.smartregister.addo.repository.AddoRepository;
import org.smartregister.addo.service.AddoAuthorizationService;
import org.smartregister.addo.sync.AddoClientProcessor;
import org.smartregister.addo.util.ChildDBConstants;
import org.smartregister.addo.util.Constants;
import org.smartregister.addo.util.CoreConstants;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.pnc.PncLibrary;
import org.smartregister.chw.referral.ReferralLibrary;
import org.smartregister.chw.referral.domain.ReferralMetadata;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.activity.FamilyWizardFormActivity;
import org.smartregister.family.domain.FamilyMetadata;
import org.smartregister.family.util.DBConstants;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.reporting.ReportingLibrary;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.Repository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.simprint.SimPrintsLibrary;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;

import org.smartregister.view.activity.DrishtiApplication;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import timber.log.Timber;

/**
 * Author : Isaya Mollel on 2019-10-18.
 */
public class AddoApplication extends DrishtiApplication {

    private JsonSpecHelper jsonSpecHelper;
    private ECSyncHelper ecSyncHelper;
    private static CommonFtsObject commonFtsObject;
    private static ClientProcessorForJava clientProcessor;
    private RulesEngineHelper rulesEngineHelper;
    private FirebaseAnalytics mFirebaseAnalytics;

    public static synchronized AddoApplication getInstance() {
        return (AddoApplication) mInstance;
    }

    public static JsonSpecHelper getJsonSpecHelper() {
        return getInstance().jsonSpecHelper;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());

        /*
            issyzac appcenter key -> d247b350-e934-46e9-abae-abbb8ed47c33
            Dtree appcenter key-> 00e04fdd-ab2d-47ee-b7a3-38986474a7f6
            AppCenter.start(this, "00e04fdd-ab2d-47ee-b7a3-38986474a7f6", Analytics.class, Crashes.class);
        */

        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        //Initialize FirebaseAnalytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //Initialize Modules
        P2POptions p2POptions = new P2POptions(true);
        p2POptions.setAuthorizationService(new AddoAuthorizationService());

        CoreLibrary.init(context, new AddoSyncConfiguration(), BuildConfig.BUILD_TIMESTAMP, p2POptions);
        CoreLibrary.getInstance().setEcClientFieldsFile(Constants.EC_CLIENT_FIELDS);

        ConfigurableViewsLibrary.init(context);
        FamilyLibrary.init(context, getMetadata(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);

        FamilyLibrary.getInstance().setClientProcessorForJava(AddoClientProcessor.getInstance(getApplicationContext()));
        SimPrintsLibrary.init(mInstance, BuildConfig.SIMPRINT_PROJECT_ID, BuildConfig.SIMPRINT_MODULE_ID, getRepository());
        ReferralMetadata referralMetadata = new ReferralMetadata();
        referralMetadata.setLocationIdMap(new HashMap<>());
        ReferralLibrary.init(context, getRepository(), referralMetadata, BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        AncLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        PncLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        // Init Reporting library
        ReportingLibrary.init(context, getRepository(), null, BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);

        this.jsonSpecHelper = new JsonSpecHelper(this);

        JobManager.create(this).addJobCreator(new AddoJobCreator());

        //initOfflineSchedules();
        //scheduleJobs();
        LocationHelper.init(new ArrayList<>(Arrays.asList(BuildConfig.ALLOWED_LOCATION_LEVELS)), BuildConfig.DEFAULT_LOCATION);
        SyncStatusBroadcastReceiver.init(this);

        CoreConstants.JSON_FORM.setLocaleAndAssetManager(getCurrentLocale(), getAssets());

        Configuration configuration = getApplicationContext().getResources().getConfiguration();
        String language;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            language = configuration.getLocales().get(0).getLanguage();
        } else {
            language = configuration.locale.getLanguage();
        }

        if (language.equals(Locale.FRENCH.getLanguage())) {
            saveLanguage(Locale.FRENCH.getLanguage());
        }

    }

    @Override
    public void logoutCurrentUser() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getApplicationContext().startActivity(intent);
        context.userService().logoutSession();
    }

    public Context getContext(){
        return context;
    }

    public FirebaseAnalytics getFirebaseAnalytics(){
        if (mFirebaseAnalytics == null)
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        return mFirebaseAnalytics;
    }

    public void saveLanguage(String language) {
        AllSharedPreferences allSharedPreferences = AddoApplication.getInstance().getContext().allSharedPreferences();
        allSharedPreferences.saveLanguagePreference(language);
    }

    @Override
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new AddoRepository(getInstance().getApplicationContext(), context);
            }
        } catch (UnsatisfiedLinkError e) {
            Timber.e(e);
        }
        return repository;
    }

    public void notifyAppContextChange() {
        FamilyLibrary.getInstance().setMetadata(getMetadata());
    }

    private FamilyMetadata getMetadata() {
        FamilyMetadata metadata = new FamilyMetadata(FamilyWizardFormActivity.class, FamilyWizardFormActivity.class, FamilyProfileActivity.class, Constants.IDENTIFIER.UNIQUE_IDENTIFIER_KEY, false);
        metadata.updateFamilyRegister(Constants.JSON_FORM.getFamilyRegister(), Constants.TABLE_NAME.FAMILY, Constants.EventType.FAMILY_REGISTRATION, Constants.EventType.UPDATE_FAMILY_REGISTRATION, Constants.CONFIGURATION.FAMILY_REGISTER, Constants.RELATIONSHIP.FAMILY_HEAD, Constants.RELATIONSHIP.PRIMARY_CAREGIVER);
        metadata.updateFamilyMemberRegister(Constants.JSON_FORM.getFamilyMemberRegister(), Constants.TABLE_NAME.FAMILY_MEMBER, Constants.EventType.FAMILY_MEMBER_REGISTRATION, Constants.EventType.UPDATE_FAMILY_MEMBER_REGISTRATION, Constants.CONFIGURATION.FAMILY_MEMBER_REGISTER, Constants.RELATIONSHIP.FAMILY);
        metadata.updateFamilyDueRegister(Constants.TABLE_NAME.CHILD, Integer.MAX_VALUE, false);
        metadata.updateFamilyActivityRegister(Constants.TABLE_NAME.CHILD_ACTIVITY, Integer.MAX_VALUE, false);
        metadata.updateFamilyOtherMemberRegister(Constants.TABLE_NAME.FAMILY_MEMBER, Integer.MAX_VALUE, false);
        return metadata;
    }

    public ECSyncHelper getEcSyncHelper() {
        if (ecSyncHelper == null) {
            ecSyncHelper = ECSyncHelper.getInstance(getApplicationContext());
        }

        return ecSyncHelper;
    }

    public static Locale getCurrentLocale() {
        return mInstance == null ? Locale.getDefault() : mInstance.getResources().getConfiguration().locale;
    }


    public static CommonFtsObject createCommonFtsObject() {
        if (commonFtsObject == null) {
            commonFtsObject = new CommonFtsObject(getFtsTables());
            for (String ftsTable : commonFtsObject.getTables()) {
                commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields(ftsTable));
                commonFtsObject.updateSortFields(ftsTable, getFtsSortFields(ftsTable));
            }
        }
        return commonFtsObject;
    }

    private static String[] getFtsTables() {
        return new String[]{Constants.TABLE_NAME.FAMILY, Constants.TABLE_NAME.FAMILY_MEMBER, Constants.TABLE_NAME.CHILD};
    }

    private static String[] getFtsSearchFields(String tableName) {
        if (tableName.equals(Constants.TABLE_NAME.FAMILY)) {
            return new String[]{DBConstants.KEY.BASE_ENTITY_ID, DBConstants.KEY.VILLAGE_TOWN, DBConstants.KEY.FIRST_NAME,
                    DBConstants.KEY.LAST_NAME, DBConstants.KEY.UNIQUE_ID};
        } else if (tableName.equals(Constants.TABLE_NAME.FAMILY_MEMBER) || tableName.equals(Constants.TABLE_NAME.CHILD)) {
            return new String[]{DBConstants.KEY.BASE_ENTITY_ID, DBConstants.KEY.FIRST_NAME, DBConstants.KEY.MIDDLE_NAME,
                    DBConstants.KEY.LAST_NAME, DBConstants.KEY.UNIQUE_ID, DBConstants.KEY.DOD};
        }
        return null;
    }

    private static String[] getFtsSortFields(String tableName) {
        if (tableName.equals(Constants.TABLE_NAME.FAMILY)) {
            return new String[]{DBConstants.KEY.LAST_INTERACTED_WITH, DBConstants.KEY.DATE_REMOVED, DBConstants.KEY.FAMILY_HEAD, DBConstants.KEY.PRIMARY_CAREGIVER};
        } else if (tableName.equals(Constants.TABLE_NAME.FAMILY_MEMBER)) {
            return new String[]{DBConstants.KEY.DOB, DBConstants.KEY.DOD, DBConstants.KEY
                    .LAST_INTERACTED_WITH, DBConstants.KEY.DATE_REMOVED};
        } else if (tableName.equals(Constants.TABLE_NAME.CHILD)) {
            return new String[]{ChildDBConstants.KEY.LAST_HOME_VISIT, ChildDBConstants.KEY.VISIT_NOT_DONE, DBConstants.KEY
                    .LAST_INTERACTED_WITH, ChildDBConstants.KEY.DATE_CREATED, DBConstants.KEY.DATE_REMOVED, DBConstants.KEY.DOB};
        }
        return null;
    }


    public RulesEngineHelper getRulesEngineHelper() {
        if (rulesEngineHelper == null) {
            rulesEngineHelper = new RulesEngineHelper(getApplicationContext());
        }
        return rulesEngineHelper;
    }

    public static ClientProcessorForJava getClientProcessor(android.content.Context context) {
        if (clientProcessor == null) {
            //clientProcessor = AddoClientProcessor.getInstance(context);
            clientProcessor = FamilyLibrary.getInstance().getClientProcessorForJava();
        }
        return clientProcessor;
    }

    public AllCommonsRepository getAllCommonsRepository(String table) {
        return AddoApplication.getInstance().getContext().allCommonsRepositoryobjects(table);
    }

    public TaskRepository getTaskRepository() {
        return CoreLibrary.getInstance().context().getTaskRepository();
    }
}
