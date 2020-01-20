package org.smartregister.addo.contract;

import org.smartregister.addo.domain.Entity;
import org.smartregister.domain.Response;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.util.List;
import java.util.Map;

public interface AdvancedSearchContract  {
    interface Presenter {
        void search(Map<String, String> searchMap, boolean isLocal);
    }

    interface View extends BaseRegisterFragmentContract.View {
        void showResults(List<Entity> members);
    }

    interface Interactor {
        void search(Map<String, String> editMap, InteractorCallBack callBack);
    }

    interface InteractorCallBack {
        void onResultsFound(Response<String> response);
    }
}
