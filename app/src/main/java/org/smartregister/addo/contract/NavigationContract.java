package org.smartregister.addo.contract;

import android.app.Activity;

import org.smartregister.addo.model.NavigationOption;

import java.util.Date;
import java.util.List;

public interface NavigationContract {

    interface Presenter {
        NavigationContract.View getNavigationView();
        void refreshNavigationCount(Activity activity);
        void refreshLastSync();
        void displayCurrentUser();
        void sync(Activity activity);
        List<NavigationOption> getOptions();

    }

    interface View {

        void prepareView(Activity activity);
        void  refreshLastSync(Date lastSync);
        void refreshCurrentUser(String name);
        void logout(Activity activity);
        void refreshCount();
        void displayToast(Activity activity, String message);

    }

    interface Model {
        List<NavigationOption> getNavigationItems();

        String getCurrentUser();
    }

    interface Interactor {

        Date getLastSync();
        void getRegisterCount(String tableName, InteractorCallback<Integer> callBack);
        Date sync();

    }

    interface InteractorCallback<T> {
        void onResult(T result);
        void onError(Exception e);
    }
}
