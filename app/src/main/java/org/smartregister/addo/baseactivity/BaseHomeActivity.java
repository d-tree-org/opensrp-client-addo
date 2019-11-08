package org.smartregister.addo.baseactivity;

import org.smartregister.addo.contract.AddoHomeContract;
import org.smartregister.view.activity.BaseRegisterActivity;

public abstract class BaseHomeActivity extends BaseRegisterActivity {

    public BaseHomeActivity(){}

    public AddoHomeContract.Presenter getPresenter(){
        return (AddoHomeContract.Presenter) this.presenter;
    }

}
