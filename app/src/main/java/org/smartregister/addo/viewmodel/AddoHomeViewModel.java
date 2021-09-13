package org.smartregister.addo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.smartregister.addo.repository.AddoWeeklySummaryRepository;

/**
 * Created by Kassim Sheghembe on 2021-08-18
 */
public class AddoHomeViewModel extends ViewModel {

    private final MutableLiveData<String> selectedVillage = new MutableLiveData<String>();

    private MutableLiveData<String> numRefferalsWeek;
    private MutableLiveData<String> numClosedRefferalsWeek;
    private MutableLiveData<String> numAddoVisits;

    private final AddoWeeklySummaryRepository weeklySummaryRepository = new AddoWeeklySummaryRepository();

    public AddoHomeViewModel() {}

    public void setSelectedVillage(String village) {
        selectedVillage.setValue(village);
    }

    public LiveData<String> getSelectedVillage() {
        return selectedVillage;
    }

    public LiveData<String> getNumRefferalsWeek() {
        if (numRefferalsWeek == null) {
            numRefferalsWeek = new MutableLiveData<String>();
            weeklySummaryRepository.getReferralCounts(result -> numRefferalsWeek.setValue(result));
        }

        return numRefferalsWeek;
    }

    public LiveData<String> getNumClosedRefferalsWeek() {
        if (numClosedRefferalsWeek == null) {
            numClosedRefferalsWeek = new MutableLiveData<String>();
            weeklySummaryRepository.getClosedRefferalCount(result -> numClosedRefferalsWeek.setValue(result));
        }

        return numClosedRefferalsWeek;
    }

    public LiveData<String> getNumAddoVisits() {
        if (numAddoVisits == null) {
            numAddoVisits  = new MutableLiveData<String>();
            weeklySummaryRepository.getAddoWeeklyVisit(result -> numAddoVisits.setValue(result));
        }
        return numAddoVisits;
    }

}
