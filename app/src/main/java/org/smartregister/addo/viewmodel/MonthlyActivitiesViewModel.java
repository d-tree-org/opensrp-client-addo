package org.smartregister.addo.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import org.smartregister.addo.repository.MonthlyReportRepository;

public class MonthlyActivitiesViewModel extends AndroidViewModel {

    MonthlyReportRepository repository = new MonthlyReportRepository();

    MutableLiveData<String> currentMonthVisitLiveData = new MutableLiveData<>();
    MutableLiveData<String> currentMonthChildrenLiveData = new MutableLiveData<>();
    MutableLiveData<String> currentMonthAdolescentLiveData = new MutableLiveData<>();
    MutableLiveData<String> currentMonthANCLiveData = new MutableLiveData<>();
    MutableLiveData<String> currentMonthPNCLiveData = new MutableLiveData<>();
    MutableLiveData<String> currentMonthOthersLiveData = new MutableLiveData<>();
    MutableLiveData<String> currentMonthFacilityReferralsLiveData = new MutableLiveData<>();
    MutableLiveData<String> currentMonthCompletedFacilityReferralsLiveData = new MutableLiveData<>();

    MutableLiveData<String> lastMonthVisitLiveData = new MutableLiveData<>();
    MutableLiveData<String> lastMonthFacilityReferralsLiveData = new MutableLiveData<>();
    MutableLiveData<String> lastMonthCompletedReferralsLiveData = new MutableLiveData<>();

    public MonthlyActivitiesViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getCurrentMonthVisitLiveData() {
        if (currentMonthVisitLiveData.getValue() == null){
            String count = repository.getCurrentMonthVisit();
            currentMonthVisitLiveData.setValue(count);
        }
        return currentMonthVisitLiveData;
    }

    public LiveData<String> getCurrentMonthChildrenLiveData() {
        if (currentMonthChildrenLiveData.getValue() == null){
            String count = repository.getCurrentMonthChildVisits();
            currentMonthChildrenLiveData.setValue(count);
        }
        return currentMonthChildrenLiveData;
    }

    public LiveData<String> getCurrentMonthAdolescentLiveData() {
        if (currentMonthAdolescentLiveData.getValue() == null){
            String count = repository.getCurrentMonthAdolescentVisit();
            currentMonthAdolescentLiveData.setValue(count);
        }
        return currentMonthAdolescentLiveData;
    }

    public LiveData<String> getCurrentMonthANCLiveData() {
        if (currentMonthANCLiveData.getValue() == null){
            String count = repository.getCurrentMonthANCVisits();
            currentMonthANCLiveData.setValue(count);
        }
        return currentMonthANCLiveData;
    }

    public LiveData<String> getCurrentMonthPNCLiveData() {
        if (currentMonthPNCLiveData.getValue() == null) {
            String count = repository.getCurrentMonthPNCVisits();
            currentMonthPNCLiveData.setValue(count);
        }
        return currentMonthPNCLiveData;
    }

    public LiveData<String> getCurrentMonthOthersLiveData() {
        if (currentMonthOthersLiveData.getValue() == null) {
            String count = repository.getCurrentMonthOtherMemberVisits();
            currentMonthOthersLiveData.setValue(count);
        }
        return currentMonthOthersLiveData;
    }

    public LiveData<String> getCurrentMonthFacilityReferralsLiveData() {
        if (currentMonthFacilityReferralsLiveData.getValue() == null) {
            String count = repository.getReferralsToFacilityThisMonth();
            currentMonthFacilityReferralsLiveData.setValue(count);
        }
        return currentMonthFacilityReferralsLiveData;
    }

    public LiveData<String> getCurrentMonthCompletedFacilityReferralsLiveData() {
        if (currentMonthCompletedFacilityReferralsLiveData.getValue() == null) {
            String count = repository.getCompletedReferralsToFacilityThisMonth();
            currentMonthCompletedFacilityReferralsLiveData.setValue(count);
        }
        return currentMonthCompletedFacilityReferralsLiveData;
    }

    public LiveData<String> getLastMonthVisitLiveData() {
        if (lastMonthVisitLiveData.getValue() == null) {
            String count = repository.getTotalVisitsConductedLastMonth();
            lastMonthVisitLiveData.setValue(count);
        }
        return lastMonthVisitLiveData;
    }

    public LiveData<String> getLastMonthFacilityReferralsLiveData() {
        if (lastMonthFacilityReferralsLiveData.getValue() == null) {
            String count = repository.getReferralsToFacilityLastMonth();
            lastMonthFacilityReferralsLiveData.setValue(count);
        }
        return lastMonthFacilityReferralsLiveData;
    }

    public LiveData<String> getLastMonthCompletedReferralsLiveData() {
        if (lastMonthCompletedReferralsLiveData.getValue() == null) {
            String count = repository.getCompletedReferralsToFacilityLastMonth();
            lastMonthCompletedReferralsLiveData.setValue(count);
        }
        return lastMonthCompletedReferralsLiveData;
    }

}
