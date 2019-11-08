package org.smartregister.addo.listeners;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

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

        if (item.getItemId() == org.smartregister.family.R.id.action_register) {
            activity.startFamilyRegisterForm();
        }

        return false;
    }
}
