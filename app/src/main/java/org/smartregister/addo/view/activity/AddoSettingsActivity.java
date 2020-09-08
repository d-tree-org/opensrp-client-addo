package org.smartregister.addo.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;
import org.smartregister.addo.R;
import org.smartregister.addo.repository.AddoSharedPreferences;
import org.smartregister.addo.util.Constants;
import org.smartregister.addo.util.FileUtils;
import org.smartregister.simprint.OnDialogButtonClick;
import org.smartregister.view.activity.SettingsActivity;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class AddoSettingsActivity extends SettingsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(AddoSettingsActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                prepareSwitchFolder();
            }
        } else {
            prepareSwitchFolder();
        }

        getFragmentManager().beginTransaction().replace(android.R.id.content, new AddoPreferenceFragment()).commit();
    }


    public void prepareSwitchFolder(){
        String rootFolder = "Addo";
        createFolders(rootFolder, false);
        boolean onSdCard = FileUtils.canWriteToExternalDisk();
        if (onSdCard)
            createFolders(rootFolder, true);
    }

    public void createFolders(String rootFolder, boolean onSdCard){
        try {
            FileUtils.createDirectory(rootFolder, onSdCard);
        } catch (Exception e) {
            Timber.v(e);
        }
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
            clearApplicationData();
            Toast.makeText(getActivity(), "I am switching to production " + addoSharedPreferences.getOpensrpADDOEnvironment(), Toast.LENGTH_SHORT).show();
        }

        private void switchToTest() {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            AddoSharedPreferences addoSharedPreferences = new AddoSharedPreferences(sharedPreferences);
            addoSharedPreferences.updateOpensrpADDOEnvironment("test");
            writeEnvironmentConfigurations("test");
            clearApplicationData();
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

        public void clearApplicationData() {
            File appDir = new File(Environment.getDataDirectory() + File.separator + "data/org.smartregister.addo");
            if(appDir.exists()){
                String[] children = appDir.list();
                for(String s : children){
                    if(!s.equals("lib")){
                        deleteDir(new File(appDir, s));
                        Log.i("TAG", "File /data/data/APP_PACKAGE/" + s +" DELETED");
                    }
                }
            } else {
                // TODO
            }

            getActivity().finish();
            System.exit(0);

        }

        public static boolean deleteDir(File dir) {
            if (dir != null && dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }

            return dir.delete();
        }

        private void writeEnvironmentConfigurations(String env) {

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("env", env);

                File file = new File(Environment.getExternalStorageDirectory()+ File.separator + "Addo", "env_switch.json");

                FileWriter fileWriter = new FileWriter(file);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(jsonObject.toString());
                bufferedWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}