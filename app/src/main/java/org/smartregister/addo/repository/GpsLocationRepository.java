package org.smartregister.addo.repository;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Created by Kassim Sheghembe on 2023-11-16
 */
public class GpsLocationRepository {

    private MutableLiveData<Location> locationLiveData = new MutableLiveData<>();

    public void setLocationLiveData(Location location) {
        locationLiveData.setValue(location);
    }

    public LiveData<Location> getLocationLiveData() {
        return locationLiveData;
    }

    private static GpsLocationRepository instance;

    public static GpsLocationRepository getInstance() {
        if (instance == null) {
            instance = new GpsLocationRepository();
        }
        return instance;
    }
}
