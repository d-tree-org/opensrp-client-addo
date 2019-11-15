package org.smartregister.addo.interactor;

import android.database.Cursor;

import org.smartregister.addo.application.AddoApplication;
import org.smartregister.addo.contract.NavigationContract;
import org.smartregister.addo.util.ChildDBConstants;
import org.smartregister.addo.util.Constants;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;

import java.text.MessageFormat;
import java.util.Date;

import timber.log.Timber;

public class NavigationInteractor implements NavigationContract.Interactor {

    private static NavigationInteractor instance;

    AppExecutors appExecutors = new AppExecutors();

    public NavigationInteractor(){}

    public static NavigationInteractor getInstance() {

        if (instance == null) {
            instance = new NavigationInteractor();
        }

        return instance;

    }

    private CommonRepository commonRepository(String tableName) {
        return AddoApplication.getInstance().getContext().commonrepository(tableName);
    }

    @Override
    public Date sync() {
        Date res = null;

        try {
            res = new Date(getLastCheckTimeStamp());
        } catch (Exception e) {
            Timber.e(e);
        }
        return res;
    }

    private Long getLastCheckTimeStamp() {
        return AddoApplication.getInstance().getEcSyncHelper().getLastCheckTimeStamp();
    }

    private int getCount(String tableName) {

        int count;

        Cursor c = null;
        String mainCondition;

        if (tableName.equalsIgnoreCase(Constants.TABLE_NAME.CHILD)) {
            mainCondition = String.format(" where %s is null AND %s", DBConstants.KEY.DATE_REMOVED, ChildDBConstants.childAgeLimitFilter());
        } else if (tableName.equalsIgnoreCase(Constants.TABLE_NAME.FAMILY)) {
            mainCondition = String.format(" where %s is null ", DBConstants.KEY.DATE_REMOVED);
        } else {
            mainCondition = " where 1 = 1 ";
        }

        try {
            SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();
            String query = MessageFormat.format("SELECT COUNT(*) from {0} {1} ", tableName, mainCondition);
            query = sqb.Endquery(query);
            c = commonRepository(tableName).rawCustomQueryForAdapter(query);

            if (c.moveToFirst()) {
                count = c.getInt(0);
            } else {
                count = 0;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return count;

    }

    @Override
    public Date getLastSync() {
        return null;
    }

    @Override
    public void getRegisterCount(final String tableName, final NavigationContract.InteractorCallback<Integer> callBack) {
        if (callBack != null) {
            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Integer finalCount = getCount(tableName);
                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onResult(finalCount);
                            }
                        });
                    } catch (final Exception e) {
                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onError(e);
                            }
                        });
                    }
                }
            });
        }
    }
}
