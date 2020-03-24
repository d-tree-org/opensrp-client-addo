package org.smartregister.addo.listeners;

import android.app.Activity;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.smartregister.addo.R;
import org.smartregister.addo.activity.SimPrintIdentificationRegisterActivity;
import org.smartregister.listener.BottomNavigationListener;

public class SimPrintIdentificationBottomNavigationListener extends BottomNavigationListener {

    private Activity context;
    private BottomNavigationView bottomNavigationView;

    public SimPrintIdentificationBottomNavigationListener(Activity context, BottomNavigationView bottomNavigationView) {
        super(context);
        this.context = context;
        this.bottomNavigationView = bottomNavigationView;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        SimPrintIdentificationRegisterActivity activity = (SimPrintIdentificationRegisterActivity) context;

        if (item.getItemId() == R.id.action_fingerprint) {
            activity.startSimprintsId();
        } else if (item.getItemId() == R.id.action_search) {
            Toast.makeText(context, "Clicked the search item.", Toast.LENGTH_SHORT).show();
        }

        return false;
    }
}
