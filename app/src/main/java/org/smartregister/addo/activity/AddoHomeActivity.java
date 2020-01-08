package org.smartregister.addo.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.addo.BuildConfig;
import org.smartregister.addo.application.AddoApplication;
import org.smartregister.addo.custom_views.NavigationMenu;
import org.smartregister.addo.fragment.FamilyRegisterFragment;
import org.smartregister.addo.listeners.FamilyRegisterBottomNavigationListener;
import org.smartregister.addo.util.Constants;
import org.smartregister.family.activity.BaseFamilyRegisterActivity;
import org.smartregister.family.model.BaseFamilyRegisterModel;
import org.smartregister.family.presenter.BaseFamilyRegisterPresenter;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.simprint.SimPrintsIdentification;
import org.smartregister.simprint.SimPrintsIdentifyActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.ArrayList;

public class AddoHomeActivity extends BaseFamilyRegisterActivity {

    private String action = null;
    private static final int IDENTIFY_RESULT_CODE = 4061;
    private String sessionId = null;


    public void startFamilyRegisterForm(){
        Intent intent = new Intent(this, AddoHomeActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.ACTION, Constants.ACTION.START_REGISTRATION);
        startActivity(intent);
    }

    public void startSimprintsId(){

        // This is where the session starts, need to find a way to define this session for the confirmation
        SimPrintsIdentifyActivity.StartSimprintsIdentifyActivity(AddoHomeActivity.this,
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
        return new FamilyRegisterFragment();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    protected void onResumption() {
        super.onResumption();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode != RESULT_OK && StringUtils.isNotBlank(action)) {
            finish();
        }else if (requestCode == IDENTIFY_RESULT_CODE && resultCode == RESULT_OK){

            sessionId = data.getStringExtra("sessionId");
            ArrayList<SimPrintsIdentification> identifications = new ArrayList<>();
            if (data != null){
                identifications = (ArrayList<SimPrintsIdentification>) data.getSerializableExtra("intent_data");
            }

            ArrayList<String> guid = new ArrayList<>();
            SimPrintsIdentification simPrintsIdentification;
            if (identifications.size() > 0){
                //simPrintsIdentification = identifications.get(0);

                for (int i=0; i < identifications.size(); i++) {
                    guid.add(identifications.get(i).getGuid());
                }

            }
            FamilyRegisterFragment fragment = (FamilyRegisterFragment) mBaseFragment;
            fragment.onIdentificationFromSimPrints(identifications, sessionId);
        }
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
