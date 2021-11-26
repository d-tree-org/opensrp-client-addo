package org.smartregister.addo.presenter;

import static org.apache.commons.lang3.StringUtils.trim;

import org.smartregister.addo.contract.AddoVillageClientsFragmentContract;
import org.smartregister.addo.util.AddoDBConstants;
import org.smartregister.addo.util.Constants;
import org.smartregister.addo.util.CoreConstants;
import org.smartregister.configurableviews.model.View;
import org.smartregister.family.contract.FamilyProfileActivityContract;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Kassim Sheghembe on 2021-11-24
 */
public class AddoVillageClientsFragmentPresenter implements AddoVillageClientsFragmentContract.Presenter {

    protected WeakReference<AddoVillageClientsFragmentContract.View> viewReference;
    protected AddoVillageClientsFragmentContract.Model model;

    protected Set<View> visibleColumns = new TreeSet();
    protected String viewConfigurationIdentifier;
    private String selectedVillage;

    public AddoVillageClientsFragmentPresenter(AddoVillageClientsFragmentContract.View viewReference, AddoVillageClientsFragmentContract.Model model, String viewConfigurationIdentifier) {
        this.viewReference = new WeakReference<>(viewReference);
        this.model = model;
        this.viewConfigurationIdentifier = viewConfigurationIdentifier;
    }


    @Override
    public void processViewConfigurations() {

    }

    @Override
    public void initializeQueries(String mainCondition) {
        String tableName = getMainTable();
        //mainCondition = trim(getMainCondition()).equals("") ? mainCondition : getMainCondition();
        String countSelect = this.model.countSelect(tableName, mainCondition);
        String mainSelect = this.model.mainSelect(tableName, mainCondition);

        if (getView() != null) {
            this.getView().initializeQueryParams(tableName, countSelect, mainSelect);
            this.getView().initializeAdapter(this.visibleColumns);
            this.getView().countExecute();
            this.getView().filterandSortInInitializeQueries();
            this.getView().setTotalPatients();
        }
    }

    @Override
    public void startSync() {

    }

    @Override
    public void searchGlobally(String uniqueId) {

    }

    @Override
    public String getMainCondition() {
        return " " + CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.DATE_REMOVED + " is null " +
                "AND " + CoreConstants.TABLE_NAME.ADOLESCENT + "." + AddoDBConstants.IS_CLOSED + " is 0 " +
                "AND (" + CoreConstants.TABLE_NAME.ADOLESCENT + "." + DBConstants.KEY.BASE_ENTITY_ID + " NOT IN (SELECT " +
                CoreConstants.TABLE_NAME.ANC_MEMBER + "." + DBConstants.KEY.BASE_ENTITY_ID + " FROM " +
                CoreConstants.TABLE_NAME.ANC_MEMBER + " WHERE " + CoreConstants.TABLE_NAME.ANC_MEMBER + "." +
                DBConstants.KEY.BASE_ENTITY_ID + " NOT IN (SELECT " + CoreConstants.TABLE_NAME.PNC_MEMBER + "." + DBConstants.KEY.BASE_ENTITY_ID +
                " FROM " + CoreConstants.TABLE_NAME.PNC_MEMBER + " WHERE " + CoreConstants.TABLE_NAME.PNC_MEMBER + "." + AddoDBConstants.IS_CLOSED +
                " is 1))) AND ec_family.village_town = '"+selectedVillage+"' ";
    }

    @Override
    public String getMainTable() {
        return CoreConstants.TABLE_NAME.ADOLESCENT;
    }

    @Override
    public void setSelectedVillage(String selectedVillage) {
        this.selectedVillage = selectedVillage;
    }

    protected AddoVillageClientsFragmentContract.View getView() {
        return this.viewReference != null ? (AddoVillageClientsFragmentContract.View) this.viewReference.get() : null;
    }
}
