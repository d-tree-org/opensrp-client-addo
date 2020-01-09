package org.smartregister.addo.activity;

import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;

import org.json.JSONObject;
import org.smartregister.addo.BuildConfig;
import org.smartregister.addo.R;
import org.smartregister.addo.contract.SimPrintIdentificationRegisterContract;
import org.smartregister.addo.fragment.SimPrintIdentificationRegisterFragment;
import org.smartregister.addo.listeners.SimPrintIdentificationBottomNavigationListener;
import org.smartregister.addo.model.SimPrintIdentificationRegisterModel;
import org.smartregister.addo.presenter.SimPrintIdentificationRegisterPresenter;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.simprint.SimPrintsIdentifyActivity;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.List;

public class SimPrintIdentificationRegisterActivity extends BaseRegisterActivity implements SimPrintIdentificationRegisterContract.View {


    private static final int IDENTIFY_RESULT_CODE = 4061;

    @Override
    public void setNumberClientsFound() {

    }

    @Override
    public void setNumberClientsNotFound() {

    }

    @Override
    public SimPrintIdentificationRegisterContract.Presenter presenter() {
        return (SimPrintIdentificationRegisterContract.Presenter) presenter;
    }

    public void startSimprintsId(){

        // This is where the session starts, need to find a way to define this session for the confirmation
        SimPrintsIdentifyActivity.StartSimprintsIdentifyActivity(SimPrintIdentificationRegisterActivity.this,
                BuildConfig.SIMPRINT_MODULE_ID, IDENTIFY_RESULT_CODE);
    }

    @Override
    protected void initializePresenter() {

        presenter = new SimPrintIdentificationRegisterPresenter(this, new  SimPrintIdentificationRegisterModel());

    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new SimPrintIdentificationRegisterFragment();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    public void startFormActivity(String s, String s1, String s2) {

    }

    @Override
    public void startFormActivity(JSONObject jsonObject) {

    }

    @Override
    protected void onActivityResultExtended(int i, int i1, Intent intent) {

    }

    @Override
    public List<String> getViewIdentifiers() {
        return null;
    }

    @Override
    public void startRegistration() {

    }

    @Override
    protected void registerBottomNavigation() {
        this.bottomNavigationHelper = new BottomNavigationHelper();
        this.bottomNavigationView = (BottomNavigationView)this.findViewById(R.id.bottom_navigation);
        if (this.bottomNavigationView != null) {
            this.bottomNavigationView.setLabelVisibilityMode(1);
            this.bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_clients);
            this.bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_register);
            this.bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_library);
            //this.bottomNavigationView.inflateMenu(R.menu.bottom_navigation);
            this.bottomNavigationHelper.disableShiftMode(this.bottomNavigationView);
            SimPrintIdentificationBottomNavigationListener simPrintIdentificationBottomNavigationListener = new SimPrintIdentificationBottomNavigationListener(this, this.bottomNavigationView);
            this.bottomNavigationView.setOnNavigationItemSelectedListener(simPrintIdentificationBottomNavigationListener);
        }
    }
}

