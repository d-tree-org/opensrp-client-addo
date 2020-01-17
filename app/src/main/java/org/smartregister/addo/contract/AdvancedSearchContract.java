package org.smartregister.addo.contract;

import android.database.Cursor;

//import org.smartregister.addo.cursor.AdvancedMatrixCursor;
import org.opensrp.api.domain.BaseEntity;
import org.smartregister.addo.domain.FamilyMember;
import org.smartregister.chw.anc.presenter.BaseAncRegisterFragmentPresenter;
import org.smartregister.domain.Response;
import org.smartregister.family.contract.FamilyRegisterFragmentContract;
import org.smartregister.view.contract.BaseRegisterFragmentContract;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface AdvancedSearchContract  {
    interface Presenter {
        void search(Map<String, String> searchMap, boolean isLocal);
    }

    interface View extends BaseRegisterFragmentContract.View {
        void showResults(List<BaseEntity> members);
    }

    /*interface Model {

        Map<String, String> createEditMap(Map<String, String> searchMap);

        String createSearchString(Map<String, String> searchMap);

        String getMainConditionString(Map<String, String> editMap);

        AdvancedMatrixCursor createMatrixCursor(Response<String> response);

    }*/


    interface Interactor {
        void search(Map<String, String> editMap, InteractorCallBack callBack);
    }

    interface InteractorCallBack {
        void onResultsFound(Response<String> response);
    }
}
