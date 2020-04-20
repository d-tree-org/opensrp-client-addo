package org.smartregister.addo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.addo.R;
import org.smartregister.addo.activity.AddoHomeActivity;
import org.smartregister.addo.contract.ScanFingerPrintFragmentContract;
import org.smartregister.addo.presenter.ScanFingerPrintFragmentPresenter;
import org.smartregister.addo.util.Constants;
import org.smartregister.simprint.SimPrintsIdentifyActivity;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;
import java.util.Set;

import static org.smartregister.addo.BuildConfig.SIMPRINT_MODULE_ID;

public class ScanFingerPrintFragment extends BaseRegisterFragment implements ScanFingerPrintFragmentContract.View {

    private AddoHomeActivity.AddoHomeSharedViewModel model;
    private static String SIMPRINTS_MODULE_ID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_scan_fingerprint, container, false);
        model = new ViewModelProvider(requireActivity(), new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new AddoHomeActivity.AddoHomeSharedViewModel();
            }
        }).get(AddoHomeActivity.AddoHomeSharedViewModel.class);
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
        backButton.setOnClickListener(registerActionHandler);
        TextView returnToVillage = view.findViewById(R.id.return_to_village_txt);
        returnToVillage.setOnClickListener(registerActionHandler);

        TextView tvVillageSelected = view.findViewById(R.id.selected_village);
        model.getSelectedVillage().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tvVillageSelected.setText(s);
                SIMPRINTS_MODULE_ID = s.toLowerCase(); // Module id/ village name in lower case
            }
        });

        TextView scanFp = view.findViewById(R.id.scan_fp_txt);
        scanFp.setOnClickListener(registerActionHandler);

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
        if (view.getId() == R.id.btn_back_to_villages || view.getId() == R.id.return_to_village_txt) {
            ((BaseRegisterActivity) getActivity()).switchToFragment(0);
        } else if (view.getId() == R.id.scan_fp_txt) {
            Toast.makeText(getActivity(), "We start scanning now", Toast.LENGTH_SHORT).show();
            startScannig();
        }
    }

    private void startScannig() {
        if (!StringUtils.isEmpty(SIMPRINTS_MODULE_ID)) {
            SimPrintsIdentifyActivity.startSimprintsIdentifyActivity(getActivity(),
                    SIMPRINTS_MODULE_ID, Constants.SIMPRINTS_IDENTIFICATION.IDENTIFY_RESULT_CODE);
        } else {
            SimPrintsIdentifyActivity.startSimprintsIdentifyActivity(getActivity(),
                    SIMPRINT_MODULE_ID, Constants.SIMPRINTS_IDENTIFICATION.IDENTIFY_RESULT_CODE);
        }
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
