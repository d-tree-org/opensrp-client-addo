package org.smartregister.addo.repository;

import android.content.SharedPreferences;

import org.smartregister.repository.AllSharedPreferences;

public class AddoSharedPreferences extends AllSharedPreferences {

    public static final String OPENSRP_ADDO_ENVIRONMENT = "opensrp_addo_environment";
    private SharedPreferences preferences;

    public AddoSharedPreferences(SharedPreferences preferences) {
        super(preferences);
        this.preferences = preferences;
    }

    public void updateOpensrpADDOEnvironment(String environmentValue) {
        preferences.edit().putString(OPENSRP_ADDO_ENVIRONMENT, environmentValue).commit();
    }

    public String getOpensrpADDOEnvironment() {
        return preferences.getString(OPENSRP_ADDO_ENVIRONMENT, "test");
    }

}