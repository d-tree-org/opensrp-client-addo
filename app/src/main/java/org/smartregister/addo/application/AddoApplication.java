package org.smartregister.addo.application;

import android.content.Intent;

import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.P2POptions;
import org.smartregister.addo.BuildConfig;
import org.smartregister.addo.activity.LoginActivity;
import org.smartregister.addo.service.AddoAuthorizationService;
import org.smartregister.addo.util.Constants;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.DrishtiApplication;

/**
 * Author : Isaya Mollel on 2019-10-18.
 */
public class AddoApplication extends DrishtiApplication {

    private JsonSpecHelper jsonSpecHelper;

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
        //context.updateCommonFtsObject(createCommonFtsObject());

        this.jsonSpecHelper = new JsonSpecHelper(this);

        //Initialize Modules
        P2POptions p2POptions = new P2POptions(true);
        p2POptions.setAuthorizationService(new AddoAuthorizationService());

        CoreLibrary.init(context, new AddoSyncConfiguration(), BuildConfig.BUILD_TIMESTAMP, p2POptions);
        CoreLibrary.getInstance().setEcClientFieldsFile(Constants.EC_CLIENT_FIELDS);

        setServerURL();

    }

    public void setServerURL() {
        AllSharedPreferences preferences = Utils.getAllSharedPreferences();
        preferences.savePreference(AllConstants.DRISHTI_BASE_URL, BuildConfig.opensrp_url);
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
}
