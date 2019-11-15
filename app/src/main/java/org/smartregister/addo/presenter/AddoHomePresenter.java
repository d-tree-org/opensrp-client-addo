package org.smartregister.addo.presenter;

import org.smartregister.addo.contract.AddoHomeContract;
import org.smartregister.addo.contract.AddoHomeContract.View;

import java.lang.ref.WeakReference;
import java.util.List;

public class AddoHomePresenter implements AddoHomeContract.Presenter {

    private WeakReference<View> viewReference;
    private AddoHomeContract.Model model;
    private AddoHomeContract.Interactor interactor;

    public AddoHomePresenter(AddoHomeContract.View addoHomeView, AddoHomeContract.Model model,
                             AddoHomeContract.Interactor interactor) {

        this.viewReference = new WeakReference(addoHomeView);
        this.model = model;
        this.interactor = interactor;

    }


    private View getView() {

        return this.viewReference != null ? this.viewReference.get() : null;
    }

    @Override
    public void saveLanguage(String language) {
        this.model.saveLanguage(language);
        this.getView().displayToast(language + "Updated");
    }

    @Override
    public void startForm(String var1, String var2, String var3, String var4) throws Exception {

    }

    @Override
    public void saveForm(String var1, boolean var2) {

    }

    @Override
    public void closeFamilyRecord(String var1) {

    }

    @Override
    public void registerViewConfigurations(List<String> list) {
        this.model.registerViewConfigurations(list);
    }

    @Override
    public void unregisterViewConfiguration(List<String> list) {
        this.model.unregisterViewConfiguration(list);
    }

    @Override
    public void onDestroy(boolean b) {

    }

    @Override
    public void updateInitials() {
        String initials = this.model.getInitials();
        if ( initials != null) {
            this.getView().updateInitialsText(initials);
        }
    }
}
