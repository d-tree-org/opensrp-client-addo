package org.smartregister.addo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import org.smartregister.addo.R;
import org.smartregister.addo.contract.ScanFingerPrintFragmentContract;
import org.smartregister.addo.presenter.ScanFingerPrintFragmentPresenter;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;
import java.util.Set;

public class ScanFingerPrintFragment extends BaseRegisterFragment implements ScanFingerPrintFragmentContract.View {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_scan_fingerprint, container, false);
        this.rootView = view;
        setupViews(view);

        return  view;
    }

    @Override
    public void setupViews(View view) {
        this.rootView = view;

        Toolbar toolbar = view.findViewById(R.id.addo_fp_scan_toobar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);

        toolbar.setContentInsetStartWithNavigation(0);

        ImageButton backButton = view.findViewById(R.id.btn_back_to_villages);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void initializePresenter() {

        if (getActivity() == null) {
            return;
        }

        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new ScanFingerPrintFragmentPresenter(this, viewConfigurationIdentifier);

    }

    @Override
    public void setUniqueID(String qrCode) {

    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> advancedSearchFormData) {

    }

    @Override
    protected String getMainCondition() {
        return null;
    }

    @Override
    protected String getDefaultSortQuery() {
        return null;
    }

    @Override
    protected void startRegistration() {

    }

    @Override
    protected void onViewClicked(View view) {

    }

    @Override
    public void showNotFoundPopup(String opensrpId) {

    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {

    }

    @Override
    public ScanFingerPrintFragmentContract.Presenter presenter() {
        return null;
    }
}
