package org.smartregister.addo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import org.smartregister.addo.R;
import org.smartregister.addo.presenter.LoginPresenter;
import org.smartregister.addo.view.activity.AddoSettingsActivity;
import org.smartregister.task.SaveTeamLocationsTask;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.BaseLoginActivity;
import org.smartregister.view.contract.BaseLoginContract;

/**
 * Author : Isaya Mollel on 2019-10-18.
 */
public class LoginActivity extends BaseLoginActivity implements BaseLoginContract.View {

    public static final String TAG = BaseLoginActivity.class.getCanonicalName();

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initializePresenter() {
        mLoginPresenter = new LoginPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void goToHome(boolean b) {
        //Take user to a home page
        if (b){
            Utils.startAsyncTask(new SaveTeamLocationsTask(), null);
        }
        goToMainActivity(b);
        finish();

    }

    private void goToMainActivity(boolean b){
        Intent intent = new Intent(this, AddoHomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoginPresenter.processViewCustomizations();
        if (!mLoginPresenter.isUserLoggedOut()){
            goToHome(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().toString().equalsIgnoreCase("Settings")) {
            startActivity(new Intent(this, AddoSettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
