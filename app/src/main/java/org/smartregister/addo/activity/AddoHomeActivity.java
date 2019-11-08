package org.smartregister.addo.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import org.smartregister.addo.application.AddoApplication;
import org.smartregister.addo.custom_view.NavigationMenu;
import org.smartregister.addo.fragment.FamilyRegisterFragment;
import org.smartregister.addo.listeners.FamilyRegisterBottomNavigationListener;
import org.smartregister.addo.util.Constants;
import org.smartregister.family.activity.BaseFamilyRegisterActivity;
import org.smartregister.family.model.BaseFamilyRegisterModel;
import org.smartregister.family.presenter.BaseFamilyRegisterPresenter;
import org.smartregister.view.fragment.BaseRegisterFragment;

public class AddoHomeActivity extends BaseFamilyRegisterActivity {

    private String action = null;
    public void startFamilyRegisterForm(){
        Intent intent = new Intent(this, AddoHomeActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.ACTION, Constants.ACTION.START_REGISTRATION);
        startActivity(intent);
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
