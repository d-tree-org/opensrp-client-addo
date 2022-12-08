package org.smartregister.addo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import org.smartregister.AllConstants;
import org.smartregister.addo.BuildConfig;
import org.smartregister.addo.R;
import org.smartregister.addo.fragment.EnvironmentSelectDialogFragment;
import org.smartregister.addo.presenter.LoginPresenter;
import org.smartregister.addo.util.AddoSwitchConstants;
import org.smartregister.addo.util.Constants;
import org.smartregister.addo.view.activity.AddoSettingsActivity;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.task.SaveTeamLocationsTask;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.BaseLoginActivity;
import org.smartregister.view.contract.BaseLoginContract;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import timber.log.Timber;

/**
 * Author : Isaya Mollel on 2019-10-18.
 */
public class LoginActivity extends BaseLoginActivity implements BaseLoginContract.View {

    public static final String TAG = BaseLoginActivity.class.getCanonicalName();
    private TextView tvNameEnv;
    private Button btnLogin;

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

        tvNameEnv = findViewById(R.id.at_name);
        btnLogin = findViewById(R.id.login_login_btn);
        ActivityCompat.requestPermissions(LoginActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
        setServerURL();
    }

    @Override
    public void goToHome(boolean b) {
        //Take user to a home page
        if (b) {
            //TODO: Change this to Async Task
            //Utils.startAsyncTask(new SaveTeamLocationsTask(), null);
        }
        goToMainActivity(b);
        finish();

    }

    private void goToMainActivity(boolean b) {
        Intent intent = new Intent(this, AddoHomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoginPresenter.processViewCustomizations();
        if (!mLoginPresenter.isUserLoggedOut()) {
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

    public void setServerURL() {
        try {

            AllSharedPreferences sharedPreferences = org.smartregister.util.Utils.getAllSharedPreferences();
            if (!sharedPreferences.getPreference(AddoSwitchConstants.ADDO_ENVIRONMENT).isEmpty()) {
                if ("Production".equalsIgnoreCase(sharedPreferences.getPreference(AddoSwitchConstants.ADDO_ENVIRONMENT))) {
                    updateEnvironmentUrl(BuildConfig.opensrp_url_production);
                    setAppNameProductionEnvironment("production");
                } else {
                    updateEnvironmentUrl(BuildConfig.opensrp_url_debug);
                    setAppNameProductionEnvironment("test");
                }
            } else {
                EnvironmentSelectDialogFragment switchFrag = new EnvironmentSelectDialogFragment();
                switchFrag.show(this.getSupportFragmentManager(), "switch_env");
                Toast.makeText(this, "Environment not configured yet, please choose environment ...", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAppNameProductionEnvironment(String nameAndEnvironment) {

        if ("test".equalsIgnoreCase(nameAndEnvironment)) {

            SpannableString spannableString = new SpannableString("Afya-Tek \u2022");
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getActivityContext().getResources().getColor(R.color.test_env_color));
            spannableString.setSpan(new RelativeSizeSpan(1.5f), 9, spannableString.length(), 0);
            //BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(Color.MAGENTA);
            spannableString.setSpan(foregroundColorSpan, 9, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //spannableString.setSpan(backgroundColorSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvNameEnv.setText(spannableString);
            btnLogin.setBackgroundColor(getResources().getColor(R.color.test_env_color));

        } else {

            SpannableString spannableString = new SpannableString("Afya-Tek \u2022");
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getActivityContext().getResources().getColor(R.color.login_background_color));
            spannableString.setSpan(new RelativeSizeSpan(1.5f), 9, spannableString.length(), 0);
            //BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(Color.GREEN);
            spannableString.setSpan(foregroundColorSpan, 9, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //spannableString.setSpan(backgroundColorSpan, 9, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvNameEnv.setText(spannableString);
            btnLogin.setBackgroundColor(getResources().getColor(R.color.login_background_color));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Arrays.asList(permissions).contains(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setServerURL();
            }

        } else {
            Utils.getAllSharedPreferences().savePreference(AllConstants.DRISHTI_BASE_URL, BuildConfig.opensrp_url_production);
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean(Constants.ENVIRONMENT_CONFIG.PREFERENCE_PRODUCTION_ENVIRONMENT_SWITCH, true).commit();
            Utils.getAllSharedPreferences().savePreference(Constants.ENVIRONMENT_CONFIG.OPENSRP_ADDO_ENVIRONMENT, "production");
            setAppNameProductionEnvironment("production");
        }
    }

    private void updateEnvironmentUrl(String baseUrl) {
        try {

            AllSharedPreferences allSharedPreferences = org.smartregister.util.Utils.getAllSharedPreferences();

            URL url = new URL(baseUrl);

            String base = url.getProtocol() + "://" + url.getHost();
            int port = url.getPort();

            Timber.i("Base URL: %s", base);
            Timber.i("Port: %s", port);

            allSharedPreferences.saveHost(base);
            allSharedPreferences.savePort(port);

            allSharedPreferences.savePreference(AllConstants.DRISHTI_BASE_URL, baseUrl);

            Timber.i("Saved URL: %s", allSharedPreferences.fetchHost(""));
            Timber.i("Port: %s", allSharedPreferences.fetchPort(0));
        } catch (MalformedURLException e) {
            Timber.e("Malformed Url: %s", baseUrl);
        }
    }
}
