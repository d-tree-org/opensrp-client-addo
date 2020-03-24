package org.smartregister.addo.activity;


import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

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
