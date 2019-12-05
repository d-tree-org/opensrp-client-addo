package org.smartregister.addo.contract;

import android.content.Context;

import org.smartregister.addo.model.FingerPrintScanResultModel;

import java.util.ArrayList;

public interface SimPrintIdentificationResultContract {

    interface Presenter {
        SimPrintIdentificationResultContract.View getView();
        void fetchClients();
        String familyBaseEntityId();
    }

    interface View {
        Context getApplicationContext();
        void setNumberClientsFound();
        void setNumberClientsNotFound();
        SimPrintIdentificationResultContract.Presenter presenter();
    }

    interface Model {
        ArrayList<FingerPrintScanResultModel> getClientsToDisplay(ArrayList<String> ids);
    }
}
