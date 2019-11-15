package org.smartregister.addo.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.smartregister.addo.R;
import org.smartregister.addo.application.AddoApplication;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Logout(View v){
        AddoApplication.getInstance().logoutCurrentUser();
    }

}
