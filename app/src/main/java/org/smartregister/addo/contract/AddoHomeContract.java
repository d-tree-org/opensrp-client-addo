package org.smartregister.addo.contract;

import org.json.JSONObject;
import org.smartregister.view.contract.BaseRegisterContract;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.util.Date;
import java.util.List;

public interface AddoHomeContract {

    interface Model {
        void registerViewConfigurations(List<String> var1);
        void unregisterViewConfiguration(List<String> var1);
        void saveLanguage(String var1);
        String getLocationId(String var1);
        JSONObject getFormAsJson(String var1, String var2, String var3) throws Exception;
        String getInitials();
    }

    interface View extends BaseRegisterContract.View {
        AddoHomeContract.Presenter getPresenter();
    }

    interface Presenter extends BaseRegisterContract.Presenter {
        void saveLanguage(String var1);
        void startForm(String var1, String var2, String var3, String var4) throws Exception;
        void saveForm(String var1, boolean var2);
        void closeFamilyRecord(String var1);
    }

    interface Interactor {

        Date getLastSync();

    }
}
