package org.smartregister.addo.provider;

import static org.smartregister.util.Utils.getName;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Rules;
import org.smartregister.addo.R;
import org.smartregister.chw.referral.util.DBConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewProvider;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

import static org.smartregister.addo.fragment.AddoVillageClientsFragment.CLICK_VIEW_DOSAGE_STATUS;
import static org.smartregister.addo.fragment.AddoVillageClientsFragment.CLICK_VIEW_NORMAL;

/**
 * Created by Kassim Sheghembe on 2021-11-24
 */

public class AddoVillageClientsProvider implements RecyclerViewProvider<AddoVillageClientsProvider.RegisterViewHolder> {

    protected static CommonPersonObjectClient client;
    private final LayoutInflater inflater;
    protected View.OnClickListener onClickListener;
    private View.OnClickListener paginationClickListener;
    private Context context;
    private Set<org.smartregister.configurableviews.model.View> visibleColumns;

    public AddoVillageClientsProvider(Context context, View.OnClickListener paginationClickListener, View.OnClickListener onClickListener,
                                      Set visibleColumns) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.onClickListener = onClickListener;
        this.paginationClickListener = paginationClickListener;
        this.context = context;
        this.visibleColumns = visibleColumns;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, RegisterViewHolder registerViewHolder) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;
        if (visibleColumns.isEmpty()) {
            populatePatientColumn(pc, registerViewHolder);
            //populateLastColumn(pc, registerViewHolder);
        }
    }

    private void populatePatientColumn(CommonPersonObjectClient pc, final AddoVillageClientsProvider.RegisterViewHolder viewHolder) {
        try {
            String fname = getName(
                    Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true),
                    Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true));

            String dobString = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DOB, false);
            String age = org.smartregister.family.util.Utils.getTranslatedDate(org.smartregister.family.util.Utils.getDuration(dobString), context);

            String patientName = getName(fname, Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.LAST_NAME, true));
            viewHolder.patientName.setText(patientName);
            viewHolder.textViewAge.setText("Age: " + age);

            setAddressAndGender(pc, viewHolder);

            // add patient listener
            viewHolder.patientColumn.setOnClickListener(onClickListener);
            viewHolder.patientColumn.setTag(pc);
            //viewHolder.patientColumn.setTag(R.id.VIEW_ID, CLICK_VIEW_NORMAL);
            viewHolder.registerColumns.setOnClickListener(onClickListener);
            viewHolder.registerColumns.setOnClickListener(v -> viewHolder.patientColumn.performClick());

            // add due listener
            viewHolder.dueButton.setOnClickListener(onClickListener);
            viewHolder.dueButton.setTag(pc);
            //viewHolder.dueButton.setTag(R.id.VIEW_ID, CLICK_VIEW_DOSAGE_STATUS);
            viewHolder.dueWrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.dueButton.performClick();
                }
            });

            // indicate if adolescent has referral
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void populateLastColumn(CommonPersonObjectClient pc, AddoVillageClientsProvider.RegisterViewHolder viewHolder) {
        Utils.startAsyncTask(new UpdateAsyncTaskAdolescent(context, viewHolder, pc, onClickListener), null);
    }

    public void setAddressAndGender(CommonPersonObjectClient pc, RegisterViewHolder viewHolder) {
        String address = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.VILLAGE_TOWN, true);
        String gender_key = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), org.smartregister.family.util.DBConstants.KEY.GENDER, true);
        String gender = "";
        if (gender_key.equalsIgnoreCase("Male")) {
            gender = context.getString(org.smartregister.addo.R.string.male);
        } else if (gender_key.equalsIgnoreCase("Female")) {
            gender = context.getString(R.string.female);
        }
        fillValue(viewHolder.textViewAddressAndGender, address + " \u00B7 " + gender);
    }

    protected static void fillValue(TextView v, String value) {
        if (v != null) {
            v.setText(value);
        }
    }

    @Override
    public void getFooterView(RecyclerView.ViewHolder viewHolder, int currentPageCount, int totalPageCount, boolean hasNext, boolean hasPrevious) {
        AddoVillageClientsProvider.FooterViewHolder footerViewHolder = (AddoVillageClientsProvider.FooterViewHolder) viewHolder;
        footerViewHolder.pageInfoView.setText(MessageFormat.format(context.getString(org.smartregister.R.string.str_page_info), currentPageCount, totalPageCount));

        footerViewHolder.nextPageView.setVisibility(hasNext ? View.VISIBLE : View.INVISIBLE);
        footerViewHolder.previousPageView.setVisibility(hasPrevious ? View.VISIBLE : View.INVISIBLE);

        footerViewHolder.nextPageView.setOnClickListener(paginationClickListener);
        footerViewHolder.previousPageView.setOnClickListener(paginationClickListener);

    }

    @Override
    public SmartRegisterClients updateClients(FilterOption filterOption, ServiceModeOption serviceModeOption, FilterOption filterOption1, SortOption sortOption) {
        return null;
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {

    }

    @Override
    public OnClickFormLauncher newFormLauncher(String s, String s1, String s2) {
        return null;
    }

    @Override
    public LayoutInflater inflater() {
        return inflater;
    }

    @Override
    public RegisterViewHolder createViewHolder(ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.adapter_adolescent_register_list_row, viewGroup, false);
        return new RegisterViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder createFooterHolder(ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.smart_register_pagination, viewGroup, false);
        return new FooterViewHolder(view);
    }

    @Override
    public boolean isFooterViewHolder(RecyclerView.ViewHolder viewHolder) {
        return viewHolder instanceof AddoVillageClientsProvider.FooterViewHolder;
    }

    private void setVisitButtonDueStatus(Context context, Button dueButton) {
/*        dueButton.setTextColor(context.getResources().getColor(org.smartregister.chw.core.R.color.alert_in_progress_blue));
        dueButton.setText(context.getString(org.smartregister.chw.core.R.string.record_home_visit));
        dueButton.setBackgroundResource(org.smartregister.chw.core.R.drawable.blue_btn_selector);
        dueButton.setOnClickListener(onClickListener);*/
    }

    private void setVisitButtonOverdueStatus(Context context, Button dueButton, String lastVisitDays) {
/*        dueButton.setTextColor(context.getResources().getColor(org.smartregister.chw.core.R.color.white));
        if (TextUtils.isEmpty(lastVisitDays)) {
            dueButton.setText(context.getString(org.smartregister.chw.core.R.string.record_visit));
        } else {
            dueButton.setText(context.getString(R.string.over_due_visit, lastVisitDays));
        }
        dueButton.setBackgroundResource(org.smartregister.chw.core.R.drawable.overdue_red_btn_selector);
        dueButton.setOnClickListener(onClickListener);*/
    }

    private void setVisitLessTwentyFourView(Context context, Button dueButton) {
        setVisitAboveTwentyFourView(context, dueButton);
    }

    private void setVisitAboveTwentyFourView(Context context, Button dueButton) {
/*        dueButton.setTextColor(context.getResources().getColor(org.smartregister.chw.core.R.color.alert_complete_green));
        dueButton.setText(context.getString(org.smartregister.chw.core.R.string.visit_done));
        dueButton.setBackgroundColor(context.getResources().getColor(org.smartregister.chw.core.R.color.transparent));
        dueButton.setOnClickListener(null);*/
    }

    private void setVisitNotDone(Context context, Button dueButton) {
/*        dueButton.setTextColor(context.getResources().getColor(org.smartregister.chw.core.R.color.progress_orange));
        dueButton.setText(context.getString(org.smartregister.chw.core.R.string.visit_not_done));
        dueButton.setBackgroundColor(context.getResources().getColor(org.smartregister.chw.core.R.color.transparent));
        dueButton.setOnClickListener(null);*/
    }

    public class RegisterViewHolder extends RecyclerView.ViewHolder {
        public TextView patientName;
        public TextView textViewAge;
        public TextView textViewAddressAndGender;
        public TextView textViewHasReferral;
        public View patientColumn;
        public View registerColumns;
        public View dueWrapper;
        public Button dueButton;

        public RegisterViewHolder(View itemView) {
            super(itemView);

            patientName = itemView.findViewById(R.id.textview_adolescent_name_age);
            textViewAge = itemView.findViewById(R.id.text_adolescent_age);
            textViewAddressAndGender = itemView.findViewById(R.id.text_view_address_gender);
            textViewHasReferral = itemView.findViewById(R.id.has_referral);
            patientColumn = itemView.findViewById(R.id.adolescent_column);
            registerColumns = itemView.findViewById(R.id.register_columns);
            dueWrapper = itemView.findViewById(R.id.due_button_wrapper);
            dueButton = itemView.findViewById(R.id.due_button);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        public TextView pageInfoView;
        public Button nextPageView;
        public Button previousPageView;

        public FooterViewHolder(View view) {
            super(view);

            nextPageView = view.findViewById(org.smartregister.R.id.btn_next_page);
            previousPageView = view.findViewById(org.smartregister.R.id.btn_previous_page);
            pageInfoView = view.findViewById(org.smartregister.R.id.txt_page_info);
        }
    }

    private class UpdateAsyncTaskAdolescent extends AsyncTask<Void, Void, Void> {

        private final AddoVillageClientsProvider.RegisterViewHolder viewHolder;
        private final CommonPersonObjectClient pc;
        private final Context context;
        private View.OnClickListener onClickListener;
        //private final Rules rules;
        //private AdolescentVisit adolescentVisit;
        private SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        public UpdateAsyncTaskAdolescent( Context context, RegisterViewHolder viewHolder, CommonPersonObjectClient pc, View.OnClickListener onClickListener) {
            this.viewHolder = viewHolder;
            this.pc = pc;
            this.context = context;
            this.onClickListener = onClickListener;
           // this.rules = CoreChwApplication.getInstance().getRulesEngineHelper().rules("adolescent-home-visit-rules.yml");
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String baseEntityID = org.smartregister.util.Utils.getValue(pc.getColumnmaps(), org.smartregister.chw.anc.util.DBConstants.KEY.BASE_ENTITY_ID, false);

            /*Map<String, VisitSummary> map = VisitDao.getVisitSummary(baseEntityID);

            if (map != null) {
                VisitSummary notDoneSummary = map.get(Constants.ADOLESCENT_HOME_VISIT_NOT_DONE);
                VisitSummary lastVisitSummary = map.get(Constants.ADOLESCENT_HOME_VISIT_DONE);

                long lastVisit = 0;
                long visitNot = 0;
                long dateCreated = 0;
                try {
                    String createVal = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), ChwDBConstants.DATE_CREATED, false);
                    if (StringUtils.isNotBlank(createVal))
                        dateCreated = ISO8601DATEFORMAT.parse(createVal).getTime();

                } catch (Exception e) {
                    Timber.e(e);
                }
                if (lastVisitSummary != null)
                    lastVisit = lastVisitSummary.getVisitDate().getTime();

                if (notDoneSummary != null)
                    visitNot = notDoneSummary.getVisitDate().getTime();

                String dobString = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), org.smartregister.family.util.DBConstants.KEY.DOB, false);
                adolescentVisit = AdolescentUtils.getAdolescentVisitStatus(context, rules, dobString, lastVisit, visitNot, dateCreated);
            }*/

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //updateDueColumn(context, viewHolder, adolescentVisit);
        }
    }
}
