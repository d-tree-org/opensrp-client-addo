package org.smartregister.addo.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.smartregister.addo.BuildConfig;
import org.smartregister.addo.application.AddoApplication;
import org.smartregister.addo.custom_views.NavigationMenu;
import org.smartregister.addo.fragment.AddoHomeFragment;
import org.smartregister.addo.fragment.AdvancedSearchFragment;
import org.smartregister.addo.fragment.ScanFingerPrintFragment;
import org.smartregister.addo.listeners.FamilyRegisterBottomNavigationListener;
import org.smartregister.addo.util.Constants;
import org.smartregister.family.activity.BaseFamilyRegisterActivity;
import org.smartregister.family.model.BaseFamilyRegisterModel;
import org.smartregister.family.presenter.BaseFamilyRegisterPresenter;
import org.smartregister.simprint.SimPrintsIdentifyActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.lang.ref.WeakReference;

public class AddoHomeActivity extends BaseFamilyRegisterActivity {

    private String action = null;
    private static final int IDENTIFY_RESULT_CODE = 4061;
    private String sessionId = null;
    private WeakReference<AdvancedSearchFragment> advancedSearchFragmentWR;


    public void startFamilyRegisterForm(){
        Intent intent = new Intent(this, AddoHomeActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.ACTION, Constants.ACTION.START_REGISTRATION);
        startActivity(intent);
    }

    public void startSimprintsId(){

        // This is where the session starts, need to find a way to define this session for the confirmation
        SimPrintsIdentifyActivity.startSimprintsIdentifyActivity(AddoHomeActivity.this,
                BuildConfig.SIMPRINT_MODULE_ID, IDENTIFY_RESULT_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
        AddoApplication.getInstance().notifyAppContextChange();

        action = getIntent().getStringExtra(Constants.ACTIVITY_PAYLOAD.ACTION);
        if (action != null && action.equals(Constants.ACTION.START_REGISTRATION)) {
            startRegistration();
        }

    }

    @Override
    protected void initializePresenter() {
        presenter = new BaseFamilyRegisterPresenter(this, new BaseFamilyRegisterModel());
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new AddoHomeFragment();
    }

    /*@Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }*/

    @Override
    protected Fragment[] getOtherFragments() {

        Fragment[] fragments = new Fragment[2];
        fragments[0] = new AdvancedSearchFragment();
        fragments[1] = new ScanFingerPrintFragment();

        return fragments;
    }

    protected AdvancedSearchFragment getAdvancedSearchFragment() {

        if (advancedSearchFragmentWR == null || advancedSearchFragmentWR.get() == null) {
            advancedSearchFragmentWR = new WeakReference<>(new AdvancedSearchFragment());
        }

        return advancedSearchFragmentWR.get();
    }

    @Override
    protected void onResumption() {
 //       super.onResumption();
        NavigationMenu.getInstance(this,null, null).getNavigationAdapter()
                .setSelectedView(Constants.DrawerMenu.ALL_FAMILIES);
    }


    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();

        bottomNavigationView.getMenu().removeItem(org.smartregister.R.id.action_search);
        bottomNavigationView.getMenu().removeItem(org.smartregister.R.id.action_clients);
        bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_scan_qr);
        bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_register);
        FamilyRegisterBottomNavigationListener listener = new FamilyRegisterBottomNavigationListener(this, bottomNavigationView);
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
    }
}
