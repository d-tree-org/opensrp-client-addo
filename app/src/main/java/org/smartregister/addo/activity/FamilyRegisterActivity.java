package org.smartregister.addo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;

import org.smartregister.addo.BuildConfig;
import org.smartregister.addo.R;
import org.smartregister.addo.application.AddoApplication;
import org.smartregister.addo.fragment.FamilyRegisterFragment;
import org.smartregister.addo.listeners.AddoBottomNavigationListener;
import org.smartregister.addo.listeners.FamilyBottomNavigationListener;
import org.smartregister.addo.util.Constants;
import org.smartregister.addo.custom_views.NavigationMenu;
import org.smartregister.family.activity.BaseFamilyRegisterActivity;
import org.smartregister.family.model.BaseFamilyRegisterModel;
import org.smartregister.family.presenter.BaseFamilyRegisterPresenter;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.fragment.BaseRegisterFragment;

public class FamilyRegisterActivity extends BaseFamilyRegisterActivity {

    private String action = null;

    public static void startFamilyRegisterForm(Activity activity) {
        Intent intent = new Intent(activity, FamilyRegisterActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.ACTION, Constants.ACTION.START_REGISTRATION);
        activity.startActivity(intent);
    }

    public static void registerBottomNavigation(
            BottomNavigationHelper bottomNavigationHelper,
            BottomNavigationView bottomNavigationView,
            Activity activity
    ) {

        if (bottomNavigationView != null) {
            bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

            bottomNavigationView.getMenu().clear();
            bottomNavigationView.inflateMenu(R.menu.bottom_nav_family_menu);
            bottomNavigationHelper.disableShiftMode(bottomNavigationView);
            bottomNavigationView.setOnNavigationItemSelectedListener(new AddoBottomNavigationListener(activity));
        }

        if (!BuildConfig.SUPPORT_QR)
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_scan_qr);

        if (!BuildConfig.SUPPORT_REPORT)
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_job_aids);

    }

    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();

        if (!BuildConfig.SUPPORT_QR)
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_scan_qr);

        if (!BuildConfig.SUPPORT_REPORT)
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_job_aids);

        bottomNavigationView.setOnNavigationItemSelectedListener(new FamilyBottomNavigationListener(this, bottomNavigationView));
    }

    @Override
    protected void initializePresenter() {
        presenter = new BaseFamilyRegisterPresenter(this, new BaseFamilyRegisterModel());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
        AddoApplication.getInstance().notifyAppContextChange(); // initialize the language (bug in translation)

        action = getIntent().getStringExtra(Constants.ACTIVITY_PAYLOAD.ACTION);
        if (action != null && action.equals(Constants.ACTION.START_REGISTRATION)) {
            startRegistration();
        }
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new FamilyRegisterFragment();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }
}

