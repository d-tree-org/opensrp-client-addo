package org.smartregister.addo.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.customviews.RadioButton;

import org.smartregister.addo.R;
import org.smartregister.addo.model.AdvancedSearchFragmentModel;
import org.smartregister.addo.presenter.AdvancedSearchFragmentPresenter;
import org.smartregister.addo.util.Constants;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;
import java.util.Map;

public class AdvancedSearchFragment extends BaseRegisterFragment {

    protected AdvancedSearchTextWatcher advancedSearchTextwatcher = new AdvancedSearchTextWatcher();
    protected HashMap<String, String> searchFormData = new HashMap<>();
    protected Map<String, View> advancedFormSearchableFields = new HashMap<>();
    private View listViewLayout;
    private View advancedSearchForm;
    private ImageButton backButton;
    private Button searchButton;
    private Button advancedSearchToolbarSearchButton;
    private RadioButton outsideInside;
    private RadioButton myCatchment;
    private TextView searchCriteria;
    private TextView matchingResults;
    private MaterialEditText firstName;
    private MaterialEditText lastName;

    public AdvancedSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_advanced_search, container, false);

        View view = inflater.inflate(R.layout.fragment_advanced_search, container, false);
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

        listViewLayout = view.findViewById(R.id.advanced_search_list);
        listViewLayout.setVisibility(View.GONE);

        advancedSearchForm = view.findViewById(R.id.advanced_search_form);
        backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(registerActionHandler);

        searchCriteria = view.findViewById(R.id.search_criteria);
        matchingResults = view.findViewById(R.id.matching_results);
        advancedSearchToolbarSearchButton = view.findViewById(R.id.search);
        searchButton = view.findViewById(R.id.advanced_form_search_btn);
        outsideInside = view.findViewById(R.id.out_and_inside);
        myCatchment = view.findViewById(R.id.my_catchment);


        populateFormViews(view);

        populateSearchableFields(view);

        resetForm();

    }

    protected void populateFormViews(View view) {

        setUpSearchButtons();

        setUpMyCatchmentControls(view, outsideInside, myCatchment, R.id.out_and_inside_layout);

        setUpMyCatchmentControls(view, myCatchment, outsideInside, R.id.my_catchment_layout);

        setUpMyCatchmentControls(view, myCatchment, outsideInside, R.id.my_catchment_layout);
    }


    public void populateSearchableFields(View view) {

        firstName = view.findViewById(R.id.first_name);
        firstName.addTextChangedListener(advancedSearchTextwatcher);

        lastName = view.findViewById(R.id.last_name);
        lastName.addTextChangedListener(advancedSearchTextwatcher);


        advancedFormSearchableFields.put(Constants.DB.FIRST_NAME, firstName);
        advancedFormSearchableFields.put(Constants.DB.LAST_NAME, lastName);
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

    public void updateSearchCriteria(String searchCriteriaString) {
        if (searchCriteria != null) {
            searchCriteria.setText(Html.fromHtml(searchCriteriaString));
            searchCriteria.setVisibility(View.VISIBLE);
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

    private void setUpMyCatchmentControls(View view, final RadioButton myCatchment,
                                          final RadioButton outsideInside, int p) {
        myCatchment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!Utils.isConnectedToNetwork(getActivity())) {
                    myCatchment.setChecked(true);
                    outsideInside.setChecked(false);
                } else {
                    outsideInside.setChecked(!isChecked);
                }
            }
        });

        View myCatchmentLayout = view.findViewById(p);
        myCatchmentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCatchment.toggle();
            }
        });
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
    protected void onViewClicked(View view) {
        /*if (view.getId() == R.id.search) {
            search();
        } else if (view.getId() == R.id.advanced_form_search_btn) {
            search();
        } else if (view.getId() == R.id.back_button) {
            switchViews(false);
        } else if ((view.getId() == R.id.patient_column || view.getId() == R.id.child_profile_info_layout) && view.getTag() != null) {

            RegisterClickables registerClickables = new RegisterClickables();
            if (view.getTag(org.smartregister.child.R.id.record_action) != null) {

                registerClickables.setRecordWeight(Constants.RECORD_ACTION.GROWTH.equals(view.getTag(org.smartregister.child.R.id.record_action)));
                registerClickables.setRecordAll(Constants.RECORD_ACTION.VACCINATION.equals(view.getTag(org.smartregister.child.R.id.record_action)));
                registerClickables.setNextAppointmentDate(view.getTag(R.id.next_appointment_date) != null ? String.valueOf(view.getTag(R.id.next_appointment_date)) : "");

            }
            BaseChildImmunizationActivity.launchActivity(getActivity(), (CommonPersonObjectClient) view.getTag(), registerClickables);

        } else if (view.getId() == R.id.move_to_catchment && view.getTag(R.id.move_to_catchment_ids) != null && view.getTag(R.id.move_to_catchment_ids) instanceof List) {

            @SuppressWarnings("unchecked") List<String> ids = (List<String>) view.getTag(R.id.move_to_catchment_ids);
            moveToMyCatchmentArea(ids);
        }*/
    }

    @Override
    public void showNotFoundPopup(String opensrpID) {
        //Todo implement this
    }
}
