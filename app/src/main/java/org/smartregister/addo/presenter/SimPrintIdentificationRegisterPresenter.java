package org.smartregister.addo.presenter;


import org.smartregister.addo.activity.SimPrintIdentificationRegisterActivity;
import org.smartregister.addo.contract.SimPrintIdentificationRegisterContract;
import org.smartregister.addo.model.FingerPrintScanResultModel;

public class SimPrintScanResultPresenter implements SimPrintIdentificationRegisterContract.Presenter {

    protected SimPrintIdentificationRegisterContract.View view;
    protected FingerPrintScanResultModel model;
    protected String baseEntityId;

    public SimPrintScanResultPresenter(SimPrintIdentificationRegisterActivity view, FingerPrintScanResultModel model, String baseEntityId) {
        this.view = view;
        this.model = model;
        this.baseEntityId = baseEntityId;
    }

    @Override
    public SimPrintIdentificationRegisterContract.View getView() {
        return null;
    }

    @Override
    public void fetchClients() {
        org.smartregister.family.util.Utils.context().commonrepository("ec_family");
    }

    @Override
    public String familyBaseEntityId() {
        return null;
    }
}
