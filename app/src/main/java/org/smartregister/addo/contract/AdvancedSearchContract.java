package org.smartregister.addo.contract;

import org.smartregister.addo.domain.Entity;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.util.List;

public interface AdvancedSearchContract  {
    interface Presenter {
        void search(String searchText);
    }

    interface View extends BaseRegisterFragmentContract.View {
        void showResults(List<Entity> members);
    }

    interface Interactor {
        void search(String searchText, InteractorCallBack callBack);
    }

    interface InteractorCallBack {
        void onResultsFound(List<Entity> members);
    }
}
