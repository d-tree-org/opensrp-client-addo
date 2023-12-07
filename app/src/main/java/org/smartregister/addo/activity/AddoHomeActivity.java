package org.smartregister.addo.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import org.smartregister.addo.R;
import org.smartregister.addo.application.AddoApplication;
import org.smartregister.addo.custom_views.NavigationMenu;
import org.smartregister.addo.fragment.AddoHomeFragment;
import org.smartregister.addo.fragment.AddoVillageClientsFragment;
import org.smartregister.addo.fragment.AdvancedSearchFragment;
import org.smartregister.addo.service.GpsLocationService;
import org.smartregister.addo.listeners.AddoBottomNavigationListener;
import org.smartregister.addo.util.Constants;
import org.smartregister.family.activity.BaseFamilyRegisterActivity;
import org.smartregister.family.model.BaseFamilyRegisterModel;
import org.smartregister.family.presenter.BaseFamilyRegisterPresenter;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.lang.ref.WeakReference;

public class AddoHomeActivity extends BaseFamilyRegisterActivity {

    private String action = null;
    private static final int IDENTIFY_RESULT_CODE = 4061;
    private String sessionId = null;
    private WeakReference<AdvancedSearchFragment> advancedSearchFragmentWR;

    public static final int LOCATION_SETTINGS_REQUEST_CODE = 1812;
    public static final int LOCATION_PERMISSIONS_REQUEST_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
        AddoApplication.getInstance().notifyAppContextChange();

        action = getIntent().getStringExtra(Constants.ACTIVITY_PAYLOAD.ACTION);
        if (action != null && action.equals(Constants.ACTION.START_REGISTRATION)) {
            startRegistration();
        }

        if (!hasLocationPermission()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSIONS_REQUEST_CODE);
        } else {
            startGpsLocationService();
        }

    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void initializePresenter() {
        presenter = new BaseFamilyRegisterPresenter(this, new BaseFamilyRegisterModel());
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new AddoHomeFragment();
    }

    @Override
    protected Fragment[] getOtherFragments() {

        Fragment[] fragments = new Fragment[3];
        fragments[0] = AdvancedSearchFragment.newInstance(true);
        fragments[1] = new AddoVillageClientsFragment();
        fragments[2] = AdvancedSearchFragment.newInstance(false);

        return fragments;
    }

    @Override
    protected void onResumption() {
 //       super.onResumption();
        NavigationMenu menu = NavigationMenu.getInstance(this, null, null);
        if (menu != null) {
            menu.getNavigationAdapter()
                    .setSelectedView(Constants.DrawerMenu.ALL_FAMILIES);
        }

        if (hasLocationPermission() && isLocationEnabled()) {
            startGpsLocationService();
        }
    }


    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();

        bottomNavigationView.getMenu().removeItem(org.smartregister.R.id.action_search);
        bottomNavigationView.getMenu().removeItem(org.smartregister.R.id.action_clients);
        bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_scan_qr);
        bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_register);
        bottomNavigationView.getMenu().removeItem(R.id.action_fingerprint);
        AddoBottomNavigationListener listener = new AddoBottomNavigationListener(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(listener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.RQ_CODE.STORAGE_PERMISIONS && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            NavigationMenu navigationMenu = NavigationMenu.getInstance(this, null, null);
            if (navigationMenu != null) {
                //navigationMenu.startP2PActivity(this);
            }
        }

        if (requestCode == LOCATION_PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGpsLocationService();
            }
        }

        if (requestCode == LOCATION_SETTINGS_REQUEST_CODE) {
            startGpsLocationService();
        }
    }

    private void startGpsLocationService() {

        if (!isLocationEnabled()) {
            showLocationServiceDisabledDialog();
            return;
        }

        Intent intent = new Intent(this, GpsLocationService.class);
        startService(intent);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    private void showLocationServiceDisabledDialog() {

        AlertDialog.Builder alertBulder = new AlertDialog.Builder(this);
        alertBulder.setTitle(getString(R.string.location_service_disabled_title));
        alertBulder.setMessage(getString(R.string.location_service_disabled_message));

        alertBulder.setPositiveButton(getString(R.string.enable), (dialog, which) -> {
            dialog.dismiss();
            Intent settingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(settingsIntent, LOCATION_SETTINGS_REQUEST_CODE);
        });

        AlertDialog dialog = alertBulder.create();
        dialog.show();
    }

}
