package org.smartregister.addo.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import org.smartregister.addo.R;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.provider.FamilyRegisterProvider;
import org.smartregister.family.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.Set;

public class VerificationFamilyProvider extends FamilyRegisterProvider {

    private final CommonRepository commonRepository;
    private Set<org.smartregister.configurableviews.model.View> visibleColumns;
    boolean familyHeadFirstNameEnabled = Utils.getBooleanProperty("family.head.first.name.enabled");
    private Context context;
    private android.view.View.OnClickListener onClickListener;
    private android.view.View.OnClickListener paginationClickListener;

    public VerificationFamilyProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener);
        this.visibleColumns = visibleColumns;
        this.context = context;
        this.commonRepository = commonRepository;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient)client;
        if (this.visibleColumns.isEmpty()) {
            if (this.familyHeadFirstNameEnabled) {
                String familyHeadId = (String)pc.getColumnmaps().get("family_head");
                CommonPersonObject familyHeadObject = Utils.context().commonrepository(Utils.metadata().familyMemberRegister.tableName).findByBaseEntityId(familyHeadId);
                String familyHeadName = "";
                if (familyHeadObject != null && familyHeadObject.getColumnmaps() != null) {
                    familyHeadName = (String)familyHeadObject.getColumnmaps().get("first_name");
                }

                pc.getColumnmaps().put("family_head_name", familyHeadName);
            }

            this.populatePatientColumn(pc, client, viewHolder);
            this.populateLastColumn(pc, viewHolder);
        }
    }

    private void populatePatientColumn(CommonPersonObjectClient pc, SmartRegisterClient client, final RegisterViewHolder viewHolder) {
        String firstName = Utils.getValue(pc.getColumnmaps(), "first_name", true);
        String famName;
        String villageTown;
        if (this.familyHeadFirstNameEnabled) {
            villageTown = Utils.getValue(pc.getColumnmaps(), "family_head_name", true);
            famName = this.context.getString(org.smartregister.family.R.string.family_profile_title_with_firstname, new Object[]{villageTown, firstName});
        } else {
            famName = this.context.getString(org.smartregister.family.R.string.family_profile_title, new Object[]{firstName});
        }

        fillValue(viewHolder.patientName, famName);
        villageTown = Utils.getValue(pc.getColumnmaps(), "village_town", true);
        fillValue(viewHolder.villageTown, villageTown);
        android.view.View patient = viewHolder.patientColumn;
        this.attachPatientOnclickListener(patient, client);
        android.view.View dueButton = viewHolder.dueButton;
        this.attachDosageOnclickListener(dueButton, client);
        viewHolder.registerColumns.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(android.view.View v) {
                viewHolder.patientColumn.performClick();
            }
        });
        viewHolder.dueWrapper.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(android.view.View v) {
                viewHolder.dueButton.performClick();
            }
        });
    }

    private void populateLastColumn(CommonPersonObjectClient pc, RegisterViewHolder viewHolder) {
        if (this.commonRepository != null) {
            CommonPersonObject commonPersonObject = this.commonRepository.findByBaseEntityId(pc.entityId());
            if (commonPersonObject != null) {
                viewHolder.dueButton.setVisibility(View.VISIBLE);
                viewHolder.dueButton.setText("Not there");
                viewHolder.dueButton.setAllCaps(true);
            } else {
                viewHolder.dueButton.setVisibility(View.GONE);
            }
        }

    }

    private void attachPatientOnclickListener(android.view.View view, SmartRegisterClient client) {
        view.setOnClickListener(this.onClickListener);
        view.setTag(client);
        view.setTag(R.id.VIEW_ID, "click_view_normal");
    }

    private void attachDosageOnclickListener(android.view.View view, SmartRegisterClient client) {
        view.setOnClickListener(this.onClickListener);
        view.setTag(client);
        view.setTag(R.id.VIEW_ID, "click_view_dosage_status");
    }
}
