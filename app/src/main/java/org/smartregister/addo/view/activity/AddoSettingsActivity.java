package org.smartregister.addo.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.smartregister.addo.R;
import org.smartregister.addo.repository.AddoSharedPreferences;
import org.smartregister.addo.util.Constants;
import org.smartregister.simprint.OnDialogButtonClick;
import org.smartregister.view.activity.SettingsActivity;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddoSettingsActivity extends SettingsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new AddoPreferenceFragment()).commit();
    }

    public static class AddoPreferenceFragment extends MyPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.addopreference);

            Preference addoEnvironmentPreference = findPreference("enable_production");

            if (addoEnvironmentPreference != null) {

                final SwitchPreference addoSwitchPreference = (SwitchPreference) addoEnvironmentPreference;

                final boolean[] userAgreed = {false};

                addoSwitchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        String environment = preference.getSharedPreferences().getString(Constants.ENVIRONMENT_CONFIG.OPENSRP_ADDO_ENVIRONMENT, "test");

                        if(newValue instanceof Boolean && ((Boolean) newValue != preference.getSharedPreferences().getBoolean("enable_production", false))) {
                            if ("test".equalsIgnoreCase(environment)) {
                                confirmSwitchingEnvironment(getActivity(), new OnDialogButtonClick() {
                                    @Override
                                    public void onOkButtonClick() {
                                        switchToProduction();
                                        preference.getSharedPreferences().edit().putBoolean("enable_production", true).commit();
                                        addoSwitchPreference.setChecked(true);
                                        userAgreed[0] = true;
                                    }

                                    @Override
                                    public void onCancelButtonClick() {
                                        addoSwitchPreference.setChecked(false);
                                        userAgreed[0] = false;
                                    }
                                }, "Production");

                            } else {
                                confirmSwitchingEnvironment(getActivity(), new OnDialogButtonClick() {
                                    @Override
                                    public void onOkButtonClick() {
                                        switchToTest();
                                        preference.getSharedPreferences().edit().putBoolean("enable_production", false).commit();
                                        addoSwitchPreference.setChecked(false);
                                        userAgreed[0] = true;
                                    }

                                    @Override
                                    public void onCancelButtonClick() {
                                        addoSwitchPreference.setChecked(true);
                                        userAgreed[0] = false;
                                    }
                                }, "Test");

                            }

                        }

                        return userAgreed[0];

                    }
                });
            }

        }

        private void switchToProduction() {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            AddoSharedPreferences addoSharedPreferences = new AddoSharedPreferences(sharedPreferences);
            addoSharedPreferences.updateOpensrpADDOEnvironment("production");
            writeEnvironmentConfigurations("production");
            Toast.makeText(getActivity(), "I am switching to production " + addoSharedPreferences.getOpensrpADDOEnvironment(), Toast.LENGTH_SHORT).show();
        }

        private void switchToTest() {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            AddoSharedPreferences addoSharedPreferences = new AddoSharedPreferences(sharedPreferences);
            addoSharedPreferences.updateOpensrpADDOEnvironment("test");
            writeEnvironmentConfigurations("test");
            Toast.makeText(getActivity(), "I am switching to test", Toast.LENGTH_SHORT).show();
        }

        private void confirmSwitchingEnvironment(Context context, final OnDialogButtonClick onDialogButtonClick, String environment) {
            final AlertDialog alert = new AlertDialog.Builder(context, R.style.SettingsAlertDialog).create();
            View title_view = getActivity().getLayoutInflater().inflate(R.layout.custom_dialog_title, null);
            alert.setCustomTitle(title_view);
            alert.setMessage(String.format(getString(R.string.switch_environment_message), environment));
            alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Toast.makeText(context, context.getResources().getString(R.string.referral_submitted), Toast.LENGTH_LONG).show();
                    onDialogButtonClick.onOkButtonClick();
                    alert.dismiss();
                }
            });
            alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onDialogButtonClick.onCancelButtonClick();
                    alert.dismiss();
                }
            });
            alert.show();
        }

        private void clearDeviceData() {
            
        }

        private void writeEnvironmentConfigurations(String env) {
            String folder = Environment.getExternalStorageDirectory() + File.separator +
                    "addo_environment_config" + "/";

            // Create a configuration file if it does not exist yet
            File directory = new File(folder);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            Map<String, Object> newEnv = new HashMap<String, Object>();
            newEnv.put(Constants.ENVIRONMENT_CONFIG.OPENSRP_ADDO_ENVIRONMENT, env);
            try {
                Yaml yaml = new Yaml();
                FileWriter writer = new FileWriter(directory + "/environment_conf.yml");
                yaml.dump(newEnv, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}