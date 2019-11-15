package org.smartregister.addo.presenter;

import android.app.Activity;

import org.smartregister.addo.contract.NavigationContract;
import org.smartregister.addo.interactor.NavigationInteractor;
import org.smartregister.addo.model.NavigationModel;
import org.smartregister.addo.model.NavigationOption;
import org.smartregister.addo.util.Constants;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.SyncServiceJob;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class NavigationPresenter implements NavigationContract.Presenter {

    private NavigationContract.Model mModel;
    private NavigationContract.Interactor mInteractor;
    private WeakReference<NavigationContract.View> mView;

    private HashMap<String, String> tableMap = new HashMap<>();

    public NavigationPresenter(NavigationContract.View view) {
        mView = new WeakReference<>(view);
        mInteractor = NavigationInteractor.getInstance();
        mModel = NavigationModel.getInstance();
        initialize();
    }

    private void initialize() {
        tableMap.put(Constants.DrawerMenu.ALL_FAMILIES, Constants.TABLE_NAME.FAMILY);
        tableMap.put(Constants.DrawerMenu.CHILD_CLIENTS, Constants.TABLE_NAME.CHILD);
    }

    @Override
    public NavigationContract.View getNavigationView() {
        return getView();
    }

    private NavigationContract.View getView() {
        return this.mView != null ? this.mView.get() : null;
    }

    @Override
    public void refreshNavigationCount(Activity activity) {
        int x = 0;
        while (x < mModel.getNavigationItems().size()) {
            final int finalX = x;
            mInteractor.getRegisterCount(tableMap.get(mModel.getNavigationItems().get(x).getMenuTitle()), new NavigationContract.InteractorCallback<Integer>() {
                @Override
                public void onResult(Integer result) {
                    mModel.getNavigationItems().get(finalX).setRegisterCount(result);
                    getNavigationView().refreshCount();
                }

                @Override
                public void onError(Exception e) {

                    Timber.e("Error Counting for %s", tableMap.get(mModel.getNavigationItems().get(finalX).getMenuTitle()));

                }
            });

            x++;
        }
    }

    @Override
    public void refreshLastSync() {
        getNavigationView().refreshLastSync(mInteractor.sync());
    }

    @Override
    public void sync(Activity activity) {
        ImageUploadServiceJob.scheduleJobImmediately(ImageUploadServiceJob.TAG);
        SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
    }

    @Override
    public List<NavigationOption> getOptions() {
        return mModel.getNavigationItems();
    }

    @Override
    public void displayCurrentUser() {
        getNavigationView().refreshCurrentUser(mModel.getCurrentUser());
    }
}
