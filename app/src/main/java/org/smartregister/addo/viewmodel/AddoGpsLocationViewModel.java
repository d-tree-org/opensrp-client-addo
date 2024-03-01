package org.smartregister.addo.viewmodel;

import android.location.Location;
import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.smartregister.addo.repository.GpsLocationRepository;

/**
 * Created by Kassim Sheghembe on 2023-11-16
 */
public class AddoGpsLocationViewModel extends ViewModel {
    private GpsLocationRepository locationRepository;
    public AddoGpsLocationViewModel() {
        locationRepository = GpsLocationRepository.getInstance();
    }

    public LiveData<Location> getLocationLiveData() {
        return locationRepository.getLocationLiveData();
    }

}
