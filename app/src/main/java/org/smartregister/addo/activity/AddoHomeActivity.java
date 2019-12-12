package org.smartregister.addo.activity;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.Toast;

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
import org.smartregister.simprint.SimPrintsHelper;
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

            /**String guid = null;
            SimPrintsIdentification simPrintsIdentification;
            if (identifications.size() > 0){
                simPrintsIdentification = identifications.get(0);
                guid = simPrintsIdentification.getGuid();
            } **/

            ArrayList<String> guid = new ArrayList<>();
            SimPrintsIdentification simPrintsIdentification;
            if (identifications.size() > 0){
                //simPrintsIdentification = identifications.get(0);

                for (int i=0; i < identifications.size(); i++) {
                    guid.add(identifications.get(i).getGuid());
                }

            }
            //onFingerprintSuccesfullyScanned(guid);
//            Toast.makeText(this, "Welcome back from Simprints ID "+guid, Toast.LENGTH_LONG).show();
            FamilyRegisterFragment fragment = (FamilyRegisterFragment) mBaseFragment;
            fragment.onIdentificationFromSimPrints(identifications, sessionId);
        }
    }



    private void onFingerprintSuccesfullyScanned(ArrayList<String> guid){

        if (guid.isEmpty()){
            Toast.makeText(this, "User not Found!", Toast.LENGTH_LONG).show();
        } else {

            /**String id = org.smartregister.addo.util.JsonFormUtils.lookForClientsUniqueId(guid);

            Intent intent = new Intent(AddoHomeActivity.this, org.smartregister.addo.activity.FingerprintScanResultActivity.class);
            ArrayList<FingerPrintScanResultModel> clients = new ArrayList<FingerPrintScanResultModel>();
            clients.add(new FingerPrintScanResultModel("Kassim Sheghembe", "Sheghembe Family"));
            clients.add(new FingerPrintScanResultModel("Isaya Molel", "Mollel Family"));
            clients.add(new FingerPrintScanResultModel("Stella Mareale", "Marealle Family"));
            clients.add(new FingerPrintScanResultModel("Gloria Kahamba", "Kahamba Family"));
            intent.putParcelableArrayListExtra("clients", clients);
            startActivity(intent); **/


            /**if (StringUtils.isNotEmpty(id)) {

                //Here we should use the FingerprintResult Scan Fragment instead of Family register
                FamilyRegisterFragment fragment = (FamilyRegisterFragment) mBaseFragment;
                fragment.fingerprintScannedSuccessfully(id);
                Intent intent = new Intent(AddoHomeActivity.this, SimPrintConfirmationService.class);
                intent.putExtra("guid", guid);
                intent.putExtra("sessionId", sessionId);
                startActivity(intent);

            } else {
                Toast.makeText(this, "No client with GUI "+guid+" was found!", Toast.LENGTH_SHORT).show();
                SimPrintsHelper simPrintsHelper = new SimPrintsHelper(BuildConfig.SIMPRINT_PROJECT_ID, BuildConfig.SIMPRINT_USER_ID);
                simPrintsHelper.confirmIdentity(AddoHomeActivity.this, sessionId, guid);
            }

**/
            //fragment.setSearchTerm(id);
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

    /**
     * Background service to han
     */

    public class SimPrintConfirmationService extends IntentService {

        public SimPrintConfirmationService() {
            super("SimPrintConfirmationService");
        }

        @Override
        protected void onHandleIntent(@Nullable Intent intent) {
            String guid = intent.getStringExtra("guid");
            String sessionId = intent.getStringExtra("sessionId");

            SimPrintsHelper simPrintsHelper = new SimPrintsHelper(BuildConfig.SIMPRINT_PROJECT_ID, BuildConfig.SIMPRINT_USER_ID);
            simPrintsHelper.confirmIdentity(AddoHomeActivity.this, sessionId, guid);

        }
    }
}
