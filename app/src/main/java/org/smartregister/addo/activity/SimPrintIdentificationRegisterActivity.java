package org.smartregister.addo.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;
import org.smartregister.addo.R;
import org.smartregister.addo.contract.SimPrintIdentificationResultContract;
import org.smartregister.addo.fragment.FamilyProfileMemberFragment;
import org.smartregister.addo.model.FingerPrintScanResultModel;
import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;
import org.smartregister.family.util.Constants;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.view.activity.BaseProfileActivity;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.ArrayList;
import java.util.List;

public class SimPrintIdentificationResultActivity extends BaseRegisterActivity implements SimPrintIdentificationResultContract.View {


    @Override
    public void setNumberClientsFound() {
        
    }

    @Override
    public void setNumberClientsNotFound() {

    }

    @Override
    public SimPrintIdentificationResultContract.Presenter presenter() {
        return null;
    }

    @Override
    protected void initializePresenter() {

    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return null;
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    public void startFormActivity(String s, String s1, String s2) {

    }

    @Override
    public void startFormActivity(JSONObject jsonObject) {

    }

    @Override
    protected void onActivityResultExtended(int i, int i1, Intent intent) {

    }

    @Override
    public List<String> getViewIdentifiers() {
        return null;
    }

    @Override
    public void startRegistration() {

    }
}

