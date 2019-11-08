package org.smartregister.addo.presenter;

import org.smartregister.addo.R;
import org.smartregister.addo.contract.RegisterFragmentContract;
import org.smartregister.addo.util.ChildDBConstants;
import org.smartregister.family.contract.FamilyRegisterFragmentContract;
import org.smartregister.family.presenter.BaseFamilyRegisterFragmentPresenter;
import org.smartregister.family.util.DBConstants;

public class FamilyRegisterFragmentPresenter
        extends BaseFamilyRegisterFragmentPresenter
        implements RegisterFragmentContract.Presenter {

    public FamilyRegisterFragmentPresenter(FamilyRegisterFragmentContract.View view,
                                           FamilyRegisterFragmentContract.Model model,
                                           String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public void processViewConfigurations() {
        super.processViewConfigurations();
        if (config.getSearchBarText() != null && getView() != null) {
            getView().updateSearchBarHint(getView().getContext().getString(R.string.search_hint));
        }
    }

    @Override
    public String getDefaultSortQuery() {
        return DBConstants.KEY.LAST_INTERACTED_WITH + " DESC ";
    }

    @Override
    public String getDueFilterCondition() {
        return getMainCondition() + " AND " + ChildDBConstants.childDueFilter();
    }

    @Override
    public String getMainCondition() {
        return String.format(" %s is null ", "date_removed");
    }
}
