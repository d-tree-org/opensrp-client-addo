package org.smartregister.addo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.smartregister.CoreLibrary;
import org.smartregister.addo.R;
import org.smartregister.addo.viewmodel.MonthlyActivitiesViewModel;

public class MonthlyIndicatorsDashboard extends Fragment {

    private String anmUser;
    private MonthlyActivitiesViewModel model;
    TextView currentMonthVisits;
    TextView currentMonthChildren;
    TextView currentMonthAdolescent;
    TextView currentMonthANC;
    TextView currentMonthPNC;
    TextView currentMonthOthers;
    TextView currentMonthFacilityReferrals;
    TextView currentMonthCompletedFacilityReferrals;
        TextView currentMonthAttendedClients;

    TextView lastMonthVisits;
    TextView lastMonthFacilityReferrals;
    TextView lastMonthCompletedReferrals;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        anmUser = CoreLibrary.getInstance().context().allSharedPreferences().fetchRegisteredANM();
        model = ViewModelProviders.of(requireActivity()).get(MonthlyActivitiesViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_monthly_indicators, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);

        observeIndicators();
    }

    private void setupViews(View view){
        currentMonthVisits = view.findViewById(R.id.current_month_visits);
        currentMonthChildren = view.findViewById(R.id.current_month_children_count);
        currentMonthAdolescent = view.findViewById(R.id.current_month_adolescent_count);
        currentMonthANC = view.findViewById(R.id.current_month_anc_count);
        currentMonthPNC = view.findViewById(R.id.current_month_pnc_count);
        currentMonthOthers = view.findViewById(R.id.current_month_other_count);
        currentMonthFacilityReferrals = view.findViewById(R.id.current_month_issued_referrals_count);
        currentMonthCompletedFacilityReferrals = view.findViewById(R.id.current_month_completed_referrals_count);
        currentMonthAttendedClients = view.findViewById(R.id.current_month_clients);
        lastMonthVisits = view.findViewById(R.id.last_month_visits_count);
        lastMonthFacilityReferrals = view.findViewById(R.id.last_month_issued_referrals_count);
        lastMonthCompletedReferrals = view.findViewById(R.id.last_month_completed_referrals_count);
    }

    private void observeIndicators(){
        model.getCurrentMonthVisitLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                currentMonthVisits.setText(s);
            }
        });

        model.getCurrentMonthClientsLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                currentMonthAttendedClients.setText(s);
            }
        });

        model.getCurrentMonthChildrenLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                currentMonthChildren.setText(s);
            }
        });

        model.getCurrentMonthAdolescentLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                currentMonthAdolescent.setText(s);
            }
        });

        model.getCurrentMonthANCLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                currentMonthANC.setText(s);
            }
        });

        model.getCurrentMonthPNCLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                currentMonthPNC.setText(s);
            }
        });

        model.getCurrentMonthOthersLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                currentMonthOthers.setText(s);
            }
        });

        model.getCurrentMonthFacilityReferralsLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                currentMonthFacilityReferrals.setText(s);
            }
        });

        model.getCurrentMonthCompletedFacilityReferralsLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                currentMonthCompletedFacilityReferrals.setText(s);
            }
        });

        model.getLastMonthVisitLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                lastMonthVisits.setText(s);
            }
        });

        model.getLastMonthFacilityReferralsLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                lastMonthFacilityReferrals.setText(s);
            }
        });

        model.getLastMonthCompletedReferralsLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                lastMonthCompletedReferrals.setText(s);
            }
        });

    }

}
