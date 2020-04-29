package org.smartregister.addo.application;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.SyncConfiguration;
import org.smartregister.SyncFilter;
import org.smartregister.addo.BuildConfig;
import org.smartregister.addo.util.Utils;
import org.smartregister.location.helper.LocationHelper;

import java.util.List;

/**
 * Author : Isaya Mollel on 2019-10-18.
 */
public class AddoSyncConfiguration extends SyncConfiguration {

    @Override
    public int getSyncMaxRetries() {
        return BuildConfig.MAX_SYNC_RETRIES;
    }

    @Override
    public SyncFilter getSyncFilterParam() {
        return SyncFilter.LOCATION;
    }

    @Override
    public String getSyncFilterValue() {
        String providerId = Utils.context().allSharedPreferences().fetchRegisteredANM();
        String userLocationId = Utils.context().allSharedPreferences().fetchUserLocalityId(providerId);

        List<String> locationIds = LocationHelper.getInstance().locationsFromHierarchy(true, null);
        if (!org.smartregister.util.Utils.isEmptyCollection(locationIds)) {
            return StringUtils.join(locationIds, ",");
        }
        return "";
    }

    @Override
    public int getUniqueIdSource() {
        return BuildConfig.OPENMRS_UNIQUE_ID_SOURCE;
    }

    @Override
    public int getUniqueIdBatchSize() {
        return BuildConfig.OPENMRS_UNIQUE_ID_BATCH_SIZE;
    }

    @Override
    public int getUniqueIdInitialBatchSize() {
        return BuildConfig.OPENMRS_UNIQUE_ID_INITIAL_BATCH_SIZE;
    }

    @Override
    public boolean isSyncSettings() {
        return BuildConfig.IS_SYNC_SETTINGS;
    }

    @Override
    public SyncFilter getEncryptionParam() {
        /*
        Boresha Afya uses SyncFilter.LOCATION as an encryption parameter changing that to TEAM_ID
        instead causes the app to work for our server (Afyatek aws)
        */
        //return SyncFilter.LOCATION;
        return SyncFilter.LOCATION;
    }

    @Override
    public boolean isSyncUsingPost() {
        return true;
    }

    @Override
    public boolean updateClientDetailsTable() {
        return false;
    }

}
