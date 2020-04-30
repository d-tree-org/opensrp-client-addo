package org.smartregister.addo.contract;

import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.util.Set;

public interface ScanFingerPrintFragmentContract {

    interface View extends BaseRegisterFragmentContract.View {
        void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns);

        ScanFingerPrintFragmentContract.Presenter presenter();
    }

    interface Presenter extends BaseRegisterFragmentContract.Presenter {

    }
}
