package org.smartregister.addo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.MenuRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import org.json.JSONObject;
import org.smartregister.addo.R;
import org.smartregister.addo.application.AddoApplication;
import org.smartregister.addo.custom_views.NavigationMenu;
import org.smartregister.addo.fragment.AddoHomeFragment;
import org.smartregister.addo.fragment.MonthlyActivitiesRegisterFragment;
import org.smartregister.addo.presenter.MonthlyActivitiesRegisterPresenter;
import org.smartregister.addo.util.Constants;
import org.smartregister.addo.util.Utils;
import org.smartregister.family.activity.BaseFamilyRegisterActivity;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.reporting.ReportingLibrary;
import org.smartregister.reporting.job.RecurringIndicatorGeneratingJob;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MonthlyActivitiesRegisterActivity extends BaseRegisterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        NavigationMenu.getInstance(this, null, null);

        AddoApplication.getInstance().notifyAppContextChange();

        String indicatorsConfigFile = "config/indicator-definitions.yml";
        ReportingLibrary.getInstance().initIndicatorData(indicatorsConfigFile, AddoApplication.getInstance().getRepository().getReadableDatabase(AddoApplication.getInstance().getPassword()));

        RecurringIndicatorGeneratingJob.scheduleJobImmediately(RecurringIndicatorGeneratingJob.TAG);
    }

    @Override
    protected void setupViews() {
        super.setupViews();
    }

    @Override
    protected void registerBottomNavigation() {
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);
    }

    @MenuRes
    public int getMenuResource() {
        return org.smartregister.family.R.menu.bottom_nav_family_menu;
    }

    @Override
    protected void initializePresenter() {
        presenter = new MonthlyActivitiesRegisterPresenter();
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new MonthlyActivitiesRegisterFragment();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        Fragment fg  = new MonthlyActivitiesRegisterFragment();
        return new Fragment[]{fg};
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
        return Arrays.asList(Utils.metadata().familyRegister.config);
    }

    @Override
    public void startRegistration() {

    }

    @Override
    protected void onResumption() {
        super.onResumption();
        NavigationMenu menu = NavigationMenu.getInstance(this, null, null);
        if (menu != null) {
            menu.getNavigationAdapter().setSelectedView(Constants.DrawerMenu.MONTHLY_ACTIVITY);
        }
    }

    @Override
    public void startFormActivity(String formName, String entityId, Map<String, String> metaData) {

    }

    public void loadDashboard(){
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        MonthlyActivitiesRegisterFragment fragment = new MonthlyActivitiesRegisterFragment();
        t.replace(R.id.dashboard_fragment, fragment);
        t.commit();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
