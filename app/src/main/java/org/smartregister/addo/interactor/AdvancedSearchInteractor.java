package org.smartregister.addo.interactor;

import androidx.annotation.VisibleForTesting;

import org.smartregister.addo.contract.AdvancedSearchContract;
import org.smartregister.addo.dao.FamilyDao;
import org.smartregister.addo.domain.Entity;
import org.smartregister.family.util.AppExecutors;
import java.util.List;

public class AdvancedSearchInteractor implements AdvancedSearchContract.Interactor {
    private AppExecutors appExecutors;

    public AdvancedSearchInteractor() {
        this(new AppExecutors());
    }

    @VisibleForTesting
    AdvancedSearchInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    @Override
    public void search(final String searchText, final AdvancedSearchContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                final List<Entity> response = localSearch(searchText);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onResultsFound(response);
                    }
                });
            }
        };

        appExecutors.networkIO().execute(runnable);
    }
    private List<Entity> localSearch(String searchText) {
        return FamilyDao.search(searchText);
    }
}
