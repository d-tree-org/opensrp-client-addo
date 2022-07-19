package org.smartregister.addo.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import org.smartregister.AllConstants;
import org.smartregister.addo.BuildConfig;
import org.smartregister.addo.R;
import org.smartregister.addo.util.AddoSwitchConstants;
import org.smartregister.addo.util.Utils;
import org.smartregister.repository.AllSharedPreferences;

import java.net.MalformedURLException;
import java.net.URL;

import timber.log.Timber;

public class EnvironmentSelectDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final String[] listOfEnvironment = new String[]{"Production", "Test"};
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Select the environment you want to switch to");
        builder.setSingleChoiceItems(R.array.addo_environment, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setSelectedEnvironment(listOfEnvironment[which]);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setDefaultEnvironment();
            }
        });

        return builder.create();
    }

    private void setSelectedEnvironment(String selectedEnvironment) {
        if (selectedEnvironment.equalsIgnoreCase(AddoSwitchConstants.PRODUCTION_ENV)) {
            updateUrl(BuildConfig.opensrp_url_production);
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString(AddoSwitchConstants.ADDO_ENVIRONMENT, AddoSwitchConstants.PRODUCTION_ENV).apply();
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(AddoSwitchConstants.IS_PRODUCTION_ENABLED, true).apply();


        } else {
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString(AddoSwitchConstants.ADDO_ENVIRONMENT, AddoSwitchConstants.TEST_ENV).apply();
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(AddoSwitchConstants.IS_PRODUCTION_ENABLED, false).apply();
            updateUrl(BuildConfig.opensrp_url_debug);
        }
        restartLoginActivity();
    }

    // To be safe lets set the default environment to Production
    private void setDefaultEnvironment() {
        updateUrl(BuildConfig.opensrp_url_production);
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString(AddoSwitchConstants.ADDO_ENVIRONMENT, AddoSwitchConstants.PRODUCTION_ENV).apply();
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(AddoSwitchConstants.IS_PRODUCTION_ENABLED, true).apply();
        restartLoginActivity();
    }

    private void updateUrl(String baseUrl) {
        try {

            AllSharedPreferences allSharedPreferences = Utils.getAllSharedPreferences();

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

    private void restartLoginActivity() {
        Intent intent = requireActivity().getIntent();
        requireActivity().finish();
        requireActivity().startActivity(intent);
    }
}
