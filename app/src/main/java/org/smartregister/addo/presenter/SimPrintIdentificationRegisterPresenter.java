package org.smartregister.addo.presenter;


import org.smartregister.addo.contract.SimPrintIdentificationRegisterContract;
import org.smartregister.family.contract.FamilyRegisterContract;
import org.smartregister.family.interactor.FamilyRegisterInteractor;

import java.lang.ref.WeakReference;
import java.util.List;

public class SimPrintIdentificationRegisterPresenter implements SimPrintIdentificationRegisterContract.Presenter {



    protected WeakReference<SimPrintIdentificationRegisterContract.View> viewReference;
    protected FamilyRegisterContract.Interactor interactor;
    protected SimPrintIdentificationRegisterContract.Model model;

    public SimPrintIdentificationRegisterPresenter(SimPrintIdentificationRegisterContract.View view, SimPrintIdentificationRegisterContract.Model model) {
        viewReference = new WeakReference<>(view);
        interactor = new FamilyRegisterInteractor();
        this.model = model;
    }

    public void setModel(SimPrintIdentificationRegisterContract.Model model) {
        this.model = model;
    }

    @Override
    public void saveLanguage(String language) {

        model.saveLanguage(language);
        if (getView() != null)
            getView().displayToast(language + " selected");
    }

    private SimPrintIdentificationRegisterContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }

    @Override
    public void startForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception {

    }

    @Override
    public void saveForm(String jsonString, boolean isEditMode) {

    }

    @Override
    public void closeFamilyRecord(String jsonString) {

    }

    @Override
    public void registerViewConfigurations(List<String> list) {

    }

    @Override
    public void unregisterViewConfiguration(List<String> list) {

    }

    @Override
    public void onDestroy(boolean b) {

    }

    @Override
    public void updateInitials() {

        String initials = model.getInitials();
        if (initials != null && getView() != null) {
            getView().updateInitialsText(initials);
        }

    }
}
