package org.smartregister.addo.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.addo.R;
import org.smartregister.addo.contract.AddoVillageClientsFragmentContract;
import org.smartregister.addo.model.AddoVillageClientsFragmentModel;
import org.smartregister.addo.model.FamilyProfileActivityModel;
import org.smartregister.addo.presenter.AddoHomeFragmentPresenter;
import org.smartregister.addo.presenter.AddoVillageClientsFragmentPresenter;
import org.smartregister.addo.presenter.ScanFingerPrintFragmentPresenter;
import org.smartregister.addo.provider.AddoMemberRegisterProvider;
import org.smartregister.addo.provider.AddoVillageClientsProvider;
import org.smartregister.addo.provider.FamilyActivityRegisterProvider;
import org.smartregister.addo.util.Constants;
import org.smartregister.addo.util.CoreConstants;
import org.smartregister.addo.util.QueryBuilder;
import org.smartregister.addo.viewmodel.AddoHomeViewModel;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.adapter.FamilyRecyclerViewCustomAdapter;
import org.smartregister.family.util.Utils;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

/**
 * Created by Kassim Sheghembe on 2021-11-24
 */
public class AddoVillageClientsFragment extends BaseRegisterFragment implements AddoVillageClientsFragmentContract.View {

    public static final String CLICK_VIEW_NORMAL = "click_view_normal";
    public static final String CLICK_VIEW_DOSAGE_STATUS = "click_view_dosage_status";

    private AddoHomeViewModel model;
    private android.view.View view;
    private static String villageSelected;

    @Nullable
    @Override
    public android.view.View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_village_client_list, container, false);

        this.rootView = view;

        setupViews(view);

        return view;
    }

    @Override
    public void setupViews(android.view.View view) {
        super.setupViews(view);
        Toolbar toolbar = view.findViewById(R.id.village_client_list_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);

        toolbar.setContentInsetStartWithNavigation(0);

        ImageButton backButton = view.findViewById(R.id.btn_back_to_villages);
        backButton.setOnClickListener(registerActionHandler);

        TextView tvBacktoVillage = view.findViewById(R.id.return_to_village_txt);
        tvBacktoVillage.setOnClickListener(registerActionHandler);

        model = new ViewModelProvider(requireActivity(), new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new AddoHomeViewModel();
            }
        }).get(AddoHomeViewModel.class);

        model.getSelectedVillage().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                joinTable = "";
                villageSelected = s;
                presenter().setSelectedVillage(s);
                mainCondition = getMainCondition() + " AND ec_family.village_town = '"+s+"' ";
                presenter().initializeQueries(mainCondition);
            }
        });

    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        AddoVillageClientsProvider addoVillageClientsProvider = new AddoVillageClientsProvider(getActivity(), paginationViewHandler, registerActionHandler, visibleColumns);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, addoVillageClientsProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    public AddoVillageClientsFragmentContract.Presenter presenter() {
        return (AddoVillageClientsFragmentContract.Presenter) presenter;
    }

    @Override
    protected void initializePresenter() {

        if (getActivity() == null) {
            return;
        }

        String viewConfigurationIdentifiers = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new AddoVillageClientsFragmentPresenter(this, new AddoVillageClientsFragmentModel()
                , viewConfigurationIdentifiers);

    }

    @Override
    public void setUniqueID(String qrCode) {

    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> advancedSearchFormData) {

    }

    @Override
    protected String getMainCondition() {
        return this.presenter().getMainCondition() + " AND ec_family.village_town = '"+villageSelected+"' ";
    }

    @Override
    protected String getDefaultSortQuery() {
        return null;
    }

    @Override
    protected void startRegistration() {

    }

    @Override
    protected void onViewClicked(android.view.View view) {

    }

    @Override
    public void showNotFoundPopup(String opensrpId) {

    }

    @Override
    protected void onResumption() {
        super.onResumption();
        Toolbar toolbar = view.findViewById(R.id.village_client_list_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetStartWithNavigation(0);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ID) {
            return new CursorLoader(getActivity()) {
                @Override
                public Cursor loadInBackground() {
                    // Count query
                    final String COUNT = "count_execute";
                    if (args != null && args.getBoolean(COUNT)) {
                        countExecute();
                    }
                    String query = defaultFilterAndSortQuery();
                    return commonRepository().rawCustomQueryForAdapter(query);
                }
            };
        }
        return super.onCreateLoader(id, args);
    }

    private String defaultFilterAndSortQuery() {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(mainSelect);

        String query = "";
        String customFilter = getFilterString();
        try {
            if (isValidFilterForFts(commonRepository())) {

                String myquery = QueryBuilder.getQuery(joinTables, mainCondition, tablename, customFilter, clientAdapter, Sortqueries);
                List<String> ids = commonRepository().findSearchIds(myquery);
                query = sqb.toStringFts(ids, tablename, CommonRepository.ID_COLUMN,
                        Sortqueries);
                query = sqb.Endquery(query);
            } else {
                sqb.addCondition(customFilter);
                query = sqb.orderbyCondition(Sortqueries);
                query = sqb.Endquery(sqb.addlimitandOffset(query, clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset()));

            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return query;
    }

    private String getFilterString() {
        StringBuilder customFilter = new StringBuilder();
        if (StringUtils.isNotBlank(filters)) {
            customFilter.append(MessageFormat.format(" and ( {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.FIRST_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.LAST_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.MIDDLE_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ) ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.UNIQUE_ID, filters));
        }

        return customFilter.toString();
    }

    @Override
    public void countExecute() {
        Cursor cursor = null;
        try {

            String query = "select count(*) from " + presenter().getMainTable() + " inner join " + CoreConstants.TABLE_NAME.FAMILY_MEMBER +
                    " on " + presenter().getMainTable() + "." + org.smartregister.chw.anc.util.DBConstants.KEY.BASE_ENTITY_ID + " = " +
                    CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.BASE_ENTITY_ID + " inner join " +
                    CoreConstants.TABLE_NAME.FAMILY + " on " + CoreConstants.TABLE_NAME.FAMILY + "." + DBConstants.KEY.BASE_ENTITY_ID + " = " +
                    CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.RELATIONAL_ID + " where " + presenter().getMainCondition();

            if (StringUtils.isNotBlank(filters))
                query = query + getFilterString();

            cursor = commonRepository().rawCustomQueryForAdapter(query);
            cursor.moveToFirst();
            clientAdapter.setTotalcount(cursor.getInt(0));
            Timber.v("total count here %d", clientAdapter.getTotalcount());

            clientAdapter.setCurrentlimit(20);
            clientAdapter.setCurrentoffset(0);


        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
