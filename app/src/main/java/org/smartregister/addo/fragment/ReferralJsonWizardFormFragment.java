package org.smartregister.addo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.smartregister.addo.R;
import org.smartregister.addo.presenter.ReferralJsonWizardFormFragmentPresenter;

public class ReferralJsonWizardFormFragment extends JsonWizardFormFragment {

    public static ReferralJsonWizardFormFragment getFormFragment(String stepName) {
        ReferralJsonWizardFormFragment jsonFormFragment = new ReferralJsonWizardFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    public void customClick(Context context, String behaviour){
        Toast.makeText(context, getResources().getString(R.string.referral_submitted), Toast.LENGTH_LONG).show();
        //save
        save();
    }

    @Override
    protected ReferralJsonWizardFormFragmentPresenter createPresenter() {
        return new ReferralJsonWizardFormFragmentPresenter(this, JsonFormInteractor.getInstance());
    }

}
