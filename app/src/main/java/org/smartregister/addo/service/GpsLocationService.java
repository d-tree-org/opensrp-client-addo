package org.smartregister.addo.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import org.smartregister.addo.repository.GpsLocationRepository;

import timber.log.Timber;

/**
 * Created by Kassim Sheghembe on 2023-11-15
 */
public class GpsLocationService extends Service {


    private LocationManager locationManager;
    private LocationListener locationListener;

    private GpsLocationRepository addoGpsLocationRepository;

    private static final String TAG = "GpsLocationService";
    private static final int ACCURACY_THRESHOLD = 50;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        addoGpsLocationRepository = GpsLocationRepository.getInstance();
        if (hasLocationPermission()) {
            startLocationUpdates();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationUpdates() {

        locationListener = location -> {
            if (location.getAccuracy() <= ACCURACY_THRESHOLD) {
                addoGpsLocationRepository.setLocationLiveData(location);
                Timber.d("Location accuracy is less than %s", ACCURACY_THRESHOLD);
                stopLocationUpdates();
            } else {
                Timber.d("Location accuracy is greater than %s", ACCURACY_THRESHOLD);
                addoGpsLocationRepository.setLocationLiveData(location);
            }
        };

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 15, locationListener);

        // Remove this for now, to get more accurate location we use GPS.
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 15, locationListener);

    }

    private void stopLocationUpdates() {
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}
