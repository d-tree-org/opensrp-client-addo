package org.smartregister.addo.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.smartregister.addo.R;
import org.smartregister.addo.adapter.FamilyMemberAdapter;
import org.smartregister.addo.contract.AdvancedSearchContract;
import org.smartregister.addo.domain.Entity;
import org.smartregister.addo.model.AdvancedSearchFragmentModel;
import org.smartregister.addo.presenter.AdvancedSearchFragmentPresenter;
import org.smartregister.addo.util.Constants;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AdvancedSearchFragment extends BaseRegisterFragment implements AdvancedSearchContract.View {

    protected AdvancedSearchTextWatcher advancedSearchTextwatcher = new AdvancedSearchTextWatcher();
    protected HashMap<String, String> searchFormData = new HashMap<>();
    protected Map<String, View> advancedFormSearchableFields = new HashMap<>();
    private View listViewLayout;
    private View advancedSearchForm;
    private ImageButton backButton;
    private Button searchButton;
    private Button advancedSearchToolbarSearchButton;
    private TextView searchCriteria;
    private TextView matchingResults;
    private MaterialEditText searchName;
    private MaterialEditText firstName;
    private MaterialEditText lastName;
    private boolean isLocal = false;
    private boolean listMode = false;

    public static AdvancedSearchFragment newInstance(boolean isLocal) {
        Bundle args = new Bundle();
        args.putBoolean("isLocal", isLocal);
        AdvancedSearchFragment f = new AdvancedSearchFragment();
        f.setArguments(args);
        return f;
    }

    public AdvancedSearchFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_advanced_search, container, false);

        View view = inflater.inflate(R.layout.fragment_advanced_search, container, false);

        assert getArguments() != null;
        isLocal = getArguments().getBoolean("isLocal", false);

        if(!isLocal) {
            view.findViewById(R.id.search_name_ll).setVisibility(View.GONE);
            view.findViewById(R.id.first_name_ll).setVisibility(View.VISIBLE);
            view.findViewById(R.id.last_name_ll).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.search_name_ll).setVisibility(View.VISIBLE);
            view.findViewById(R.id.first_name_ll).setVisibility(View.GONE);
            view.findViewById(R.id.last_name_ll).setVisibility(View.GONE);
        }

        rootView = view;//handle to the root

        setupViews(view);
        onResumption();
        return view;
    }

    @Override
    protected void initializePresenter() {

        if (getActivity() == null) {
            return;
        }

        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new AdvancedSearchFragmentPresenter(this, new AdvancedSearchFragmentModel(), viewConfigurationIdentifier);

    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {

    }

    @Override
    protected String getMainCondition() {
        return ((AdvancedSearchFragmentPresenter)presenter).getMainCondition();
    }

    @Override
    protected String getDefaultSortQuery() {
        return ((AdvancedSearchFragmentPresenter)presenter).getDefaultSortQuery();
    }


    @Override
    public void setupViews(View view) {
        super.setupViews(view);

        if (titleLabelView != null) {
            if(isLocal) {
                titleLabelView.setText(getString(R.string.search));
            } else {
                titleLabelView.setText(getString(R.string.global_search));
            }
        }

        listViewLayout = view.findViewById(R.id.advanced_search_list);
        listViewLayout.setVisibility(View.GONE);

        advancedSearchForm = view.findViewById(R.id.advanced_search_form);
        backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(registerActionHandler);

        searchCriteria = view.findViewById(R.id.search_criteria);
        matchingResults = view.findViewById(R.id.matching_results);
        advancedSearchToolbarSearchButton = view.findViewById(R.id.search);
        searchButton = view.findViewById(R.id.advanced_form_search_btn);

        setUpSearchButtons();

        populateSearchableFields(view);

        resetForm();

    }

    public void populateSearchableFields(View view) {

        searchName = view.findViewById(R.id.search_name);
        searchName.addTextChangedListener(advancedSearchTextwatcher);

        firstName = view.findViewById(R.id.first_name);
        firstName.addTextChangedListener(advancedSearchTextwatcher);

        lastName = view.findViewById(R.id.last_name);
        lastName.addTextChangedListener(advancedSearchTextwatcher);

        advancedFormSearchableFields.put(Constants.DB.FIRST_NAME, firstName);
        advancedFormSearchableFields.put(Constants.DB.LAST_NAME, lastName);
        advancedFormSearchableFields.put(Constants.DB.FIRST_NAME, searchName);
    }

    private void resetForm() {
        clearSearchCriteria();
        clearMatchingResults();
    }

    private void clearSearchCriteria() {
        if (searchCriteria != null) {
            searchCriteria.setVisibility(View.GONE);
            searchCriteria.setText("");
        }
    }

    private void clearMatchingResults() {
        if (matchingResults != null) {
            matchingResults.setVisibility(View.GONE);
            matchingResults.setText("");
        }
    }


    public void updateMatchingResults(int count) {
        if (matchingResults != null) {
            matchingResults.setText(String.format(getString(R.string.matching_results), String.valueOf(count)));
            matchingResults.setVisibility(View.VISIBLE);
        }
    }

    private void setUpSearchButtons() {
        advancedSearchToolbarSearchButton.setEnabled(false);
        advancedSearchToolbarSearchButton.setTextColor(getResources().getColor(R.color.contact_complete_grey_border));
        advancedSearchToolbarSearchButton.setOnClickListener(registerActionHandler);


        searchButton.setEnabled(false);
        searchButton.setTextColor(getResources().getColor(R.color.contact_complete_grey_border));
        searchButton.setOnClickListener(registerActionHandler);
    }

    private boolean anySearchableFieldHasValue() {

        for (Map.Entry<String, View> entry : advancedFormSearchableFields.entrySet()) {

            if (entry.getValue() instanceof TextView && !TextUtils.isEmpty(((TextView) entry.getValue()).getText())) {
                return true;
            }
        }
        return false;

    }

    private void checkTextFields() {
        if (anySearchableFieldHasValue()) {
            advancedSearchToolbarSearchButton.setEnabled(true);
            advancedSearchToolbarSearchButton.setTextColor(getResources().getColor(R.color.white));

            searchButton.setEnabled(true);
            searchButton.setTextColor(getResources().getColor(R.color.white));
        } else {
            advancedSearchToolbarSearchButton.setEnabled(false);
            advancedSearchToolbarSearchButton.setTextColor(getResources().getColor(R.color.contact_complete_grey_border));

            searchButton.setEnabled(false);
            searchButton.setTextColor(getResources().getColor(R.color.contact_complete_grey_border));
        }
    }

    private class AdvancedSearchTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //Todo later
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkTextFields();
        }

        @Override
        public void afterTextChanged(Editable s) {
            checkTextFields();
        }
    }

    @Override
    public void setUniqueID(String s) {
        if (getSearchView() != null) {
            getSearchView().setText(s);
        }
    }

    @Override
    protected void startRegistration() {
        ((BaseRegisterActivity) getActivity()).startRegistration();
    }

    @Override
    public void onViewClicked(View view) {
        if (view.getId() == R.id.search) {
            search();
        } else if (view.getId() == R.id.advanced_form_search_btn) {
            search();
        } else if (view.getId() == R.id.back_button) {
            switchViews(false);
        }
    }

    private void search() {
        showProgressView();

        Map<String, String> editMap = getSearchMap(isLocal);
        ((AdvancedSearchContract.Presenter) presenter).search(editMap, isLocal);
    }

    protected Map<String, String> getSearchMap(boolean isLocal) {

        Map<String, String> searchParams = new HashMap<>();

        String fn = "";
        if(isLocal) {
            fn = Objects.requireNonNull(searchName.getText()).toString().trim();
        } else {
            fn = firstName.getText().toString().trim();
        }
        String ln = lastName.getText().toString().trim();

        if (!TextUtils.isEmpty(fn)) {
            searchParams.put(Constants.DB.FIRST_NAME, fn);
        }

        if (!TextUtils.isEmpty(ln)) {
            searchParams.put(Constants.DB.LAST_NAME, ln);
        }

        return searchParams;
    }

    @Override
    public void showNotFoundPopup(String opensrpID) {
        //Todo implement this
    }

    public void showResults(List<Entity> members, boolean isLocal) {
        FamilyMemberAdapter adapter = new FamilyMemberAdapter(getView().getContext(), members, isLocal);
        ListView listView = rootView.findViewById(R.id.family_member_list);
        listView.setAdapter(adapter);
        updateMatchingResults(members.size());
        switchViews(true);
    }

    public void switchViews(boolean showList) {
        if (showList) {
            Utils.hideKeyboard(getActivity());

            advancedSearchForm.setVisibility(View.GONE);
            listViewLayout.setVisibility(View.VISIBLE);
            clientsView.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.VISIBLE);
            searchButton.setVisibility(View.GONE);
            advancedSearchToolbarSearchButton.setVisibility(View.GONE);

            if (titleLabelView != null) {
                titleLabelView.setText(getString(R.string.search_results));
            }

            // hide result count , should be dynamic
            if (matchingResults != null) {
                //matchingResults.setVisibility(View.GONE);
            }

            hideProgressView();
            listMode = true;
        } else {
            if(clientsView.getVisibility() == View.INVISIBLE) {
                ((BaseRegisterActivity) getActivity()).switchToFragment(0);
                return;
            }

            clearSearchCriteria();
            advancedSearchForm.setVisibility(View.VISIBLE);
            listViewLayout.setVisibility(View.GONE);
            clientsView.setVisibility(View.INVISIBLE);
            searchButton.setVisibility(View.VISIBLE);
            advancedSearchToolbarSearchButton.setVisibility(View.VISIBLE);

            if (titleLabelView != null) {
                if(isLocal) {
                    titleLabelView.setText(getString(R.string.search));
                } else {
                    titleLabelView.setText(getString(R.string.global_search));
                }
            }


            listMode = false;
        }
    }

    @Override
    public boolean onBackPressed() {
        goBack();
        return true;
    }

    @Override
    protected void goBack() {
        if (listMode) {
            switchViews(false);
        } else {
            ((BaseRegisterActivity) getActivity()).switchToFragment(0);
        }
    }
}
