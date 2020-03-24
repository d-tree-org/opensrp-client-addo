package org.smartregister.addo.listeners;

import android.app.Activity;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.smartregister.addo.R;
import org.smartregister.addo.activity.AddoHomeActivity;
import org.smartregister.listener.BottomNavigationListener;

public class FamilyRegisterBottomNavigationListener extends BottomNavigationListener {

    private Activity context;
    private BottomNavigationView bottomNavigationView;

    public FamilyRegisterBottomNavigationListener(Activity context, BottomNavigationView bottomNavigationView) {
        super(context);
        this.context = context;
        this.bottomNavigationView = bottomNavigationView;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        AddoHomeActivity activity = (AddoHomeActivity) context;

        if (item.getItemId() == R.id.action_fingerprint) {
            activity.startSimprintsId();
        }

        return false;
    }
}
