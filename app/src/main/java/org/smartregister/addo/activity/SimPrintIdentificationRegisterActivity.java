package org.smartregister.addo.activity;

import android.content.Intent;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.v4.app.Fragment;

import org.json.JSONObject;
import org.smartregister.addo.contract.SimPrintIdentificationRegisterContract;
import org.smartregister.addo.fragment.SimPrintIdentificationRegisterFragment;
import org.smartregister.addo.model.SimPrintIdentificationRegisterModel;
import org.smartregister.addo.presenter.SimPrintIdentificationRegisterPresenter;
import org.smartregister.family.listener.FamilyBottomNavigationListener;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.List;

public class SimPrintIdentificationRegisterActivity extends BaseRegisterActivity implements SimPrintIdentificationRegisterContract.View {



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
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);

        if (bottomNavigationView != null) {
            bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_clients);
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_register);
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_search);
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_library);

            bottomNavigationView.inflateMenu(org.smartregister.family.R.menu.bottom_nav_family_menu);

            bottomNavigationHelper.disableShiftMode(bottomNavigationView);

            FamilyBottomNavigationListener familyBottomNavigationListener = new FamilyBottomNavigationListener(this);
            bottomNavigationView.setOnNavigationItemSelectedListener(familyBottomNavigationListener);

        }
    }
}

