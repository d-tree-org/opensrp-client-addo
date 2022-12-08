package org.smartregister.addo.provider;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Rules;
import org.smartregister.addo.R;
import org.smartregister.addo.application.AddoApplication;
import org.smartregister.addo.dao.AncDao;
import org.smartregister.addo.dao.PNCDao;
import org.smartregister.addo.util.ChildDBConstants;
import org.smartregister.addo.util.Constants;
import org.smartregister.addo.util.CoreConstants;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;
import org.smartregister.family.provider.FamilyMemberRegisterProvider;
import org.smartregister.family.util.DBConstants;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.addo.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.customcontrols.FontVariant;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

import static org.smartregister.family.util.Utils.getName;

public class AddoMemberRegisterProvider extends FamilyMemberRegisterProvider {
    private Context context;
    private View.OnClickListener onClickListener;
    private ImageRenderHelper imageRenderHelper;

    public AddoMemberRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener, String familyHead, String primaryCaregiver) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener, familyHead, primaryCaregiver);
        this.onClickListener = onClickListener;
        this.context = context;
        this.imageRenderHelper = new ImageRenderHelper(context);
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        super.getView(cursor, client, viewHolder);

        // Update UI cutoffs
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.profile.getLayoutParams();
        layoutParams.width = context.getResources().getDimensionPixelSize(R.dimen.member_profile_pic_width);
        layoutParams.height = context.getResources().getDimensionPixelSize(R.dimen.member_profile_pic_width);
        viewHolder.profile.setLayoutParams(layoutParams);
        viewHolder.patientNameAge.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.member_profile_list_title_size));

        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;

        viewHolder.statusLayout.setVisibility(View.GONE);
        viewHolder.status.setVisibility(View.GONE);

        String entityType = Utils.getValue(pc.getColumnmaps(), ChildDBConstants.KEY.ENTITY_TYPE, false);
        if (Constants.TABLE_NAME.CHILD.equals(entityType)) {
            Utils.startAsyncTask(new UpdateAsyncTask(viewHolder, pc), null);
        }
        populatePatientColumn(pc, client, viewHolder);

    }

    private void populatePatientColumn(CommonPersonObjectClient pc, SmartRegisterClient client, final RegisterViewHolder viewHolder) {

        String firstName = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
        String middleName = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true);
        String lastName = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.LAST_NAME, true);
        String baseEntityId = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, false);
        String patientName = getName(firstName, middleName, lastName);

        String entityType = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.ENTITY_TYPE, false);

        String dob = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DOB, false);
        String dobString = org.smartregister.family.util.Utils.getDuration(dob);
        dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;

        String dod = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DOD, false);
        if (StringUtils.isNotBlank(dod)) {

            dobString = org.smartregister.family.util.Utils.getDuration(dod, dob);
            dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;

            patientName = patientName + ", " + org.smartregister.family.util.Utils.getTranslatedDate(dobString, context) + " " + context.getString(R.string.deceased_brackets);
            viewHolder.patientNameAge.setFontVariant(FontVariant.REGULAR);
            viewHolder.patientNameAge.setTextColor(Color.GRAY);
            viewHolder.patientNameAge.setTypeface(viewHolder.patientNameAge.getTypeface(), Typeface.ITALIC);

            // Replace person avatar
            new ReplaceAvatarTask(viewHolder, pc).execute(baseEntityId);
            viewHolder.nextArrow.setVisibility(View.GONE);
        } else {
            patientName = patientName + ", " + org.smartregister.family.util.Utils.getTranslatedDate(dobString, context);
            viewHolder.patientNameAge.setFontVariant(FontVariant.REGULAR);
            viewHolder.patientNameAge.setTextColor(Color.BLACK);
            viewHolder.patientNameAge.setTypeface(viewHolder.patientNameAge.getTypeface(), Typeface.NORMAL);
            new ReplaceAvatarTask(viewHolder, pc).execute(baseEntityId);
            viewHolder.nextArrow.setVisibility(View.VISIBLE);
        }

        fillValue(viewHolder.patientNameAge, patientName);

        String gender_key = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.GENDER, true);
        String gender = "";
        if (gender_key.equalsIgnoreCase("Male")) {
            gender = context.getString(R.string.male);
        } else if (gender_key.equalsIgnoreCase("Female")) {
            gender = context.getString(R.string.female);
        }
        fillValue(viewHolder.gender, gender);

        viewHolder.nextArrowColumn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.nextArrow.performClick();
            }
        });

        viewHolder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.patientColumn.performClick();
            }
        });

        viewHolder.registerColumns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.patientColumn.performClick();
            }
        });
        if (StringUtils.isBlank(dod)) {
            View patient = viewHolder.patientColumn;
            attachPatientOnclickListener(patient, client);

            View nextArrow = viewHolder.nextArrow;
            attachNextArrowOnclickListener(nextArrow, client);
        }

    }

    private void attachPatientOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(R.id.VIEW_ID, BaseFamilyProfileMemberFragment.CLICK_VIEW_NORMAL);
    }

    private void attachNextArrowOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(R.id.VIEW_ID, BaseFamilyProfileMemberFragment.CLICK_VIEW_NEXT_ARROW);
    }

    private void setMemberProfileImageResourceIdentifier(Constants.FamilyMemberType memberType, CommonPersonObjectClient commonPersonObject, RegisterViewHolder viewHolder) {
        if (Constants.FamilyMemberType.Other.equals(memberType)) { // Non ANC/PNC family member
            String entityType = Utils.getValue(commonPersonObject.getColumnmaps(), ChildDBConstants.KEY.ENTITY_TYPE, false);
            if (CoreConstants.TABLE_NAME.CHILD.equals(entityType)) {
                setMemberProfileAvatar(org.smartregister.family.util.Utils.getMemberProfileImageResourceIDentifier(entityType), commonPersonObject, viewHolder);
            } else {
                setMemberProfileAvatar(Utils.getMemberImageResourceIdentifier(), commonPersonObject, viewHolder);
            }
        } else {
            if (Constants.FamilyMemberType.ANC.equals(memberType)) {
                setMemberProfileAvatar(Utils.getAnCWomanImageResourceIdentifier(), commonPersonObject, viewHolder);
            } else if (Constants.FamilyMemberType.PNC.equals(memberType)) {
                setMemberProfileAvatar(Utils.getPnCWomanImageResourceIdentifier(), commonPersonObject, viewHolder);
            } else {
                setMemberProfileAvatar(Utils.getMemberImageResourceIdentifier(), commonPersonObject, viewHolder);
            }
        }
    }

    private void setMemberProfileAvatar(int imageResourceIdentifier, CommonPersonObjectClient commonPersonObject, RegisterViewHolder registerViewHolder) {
        String dod = org.smartregister.family.util.Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.DOD, false);
        if (StringUtils.isNotBlank(dod)) {
            registerViewHolder.profile.setImageResource(imageResourceIdentifier);
        } else {
            imageRenderHelper.refreshProfileImage(commonPersonObject.getCaseId(), registerViewHolder.profile, imageResourceIdentifier);
        }
    }

    private Map<String, String> getChildDetails(String baseEntityId) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.selectInitiateMainTable(CommonFtsObject.searchTableName(Constants.TABLE_NAME.CHILD), new String[]{CommonFtsObject.idColumn, ChildDBConstants.KEY.LAST_HOME_VISIT, ChildDBConstants.KEY.VISIT_NOT_DONE, ChildDBConstants.KEY.DATE_CREATED});
        String query = queryBUilder.mainCondition(String.format(" %s is null AND %s = '%s' AND %s ",
                DBConstants.KEY.DATE_REMOVED,
                CommonFtsObject.idColumn,
                baseEntityId,
                ChildDBConstants.childAgeLimitFilter()));

        query = query.replace(CommonFtsObject.searchTableName(Constants.TABLE_NAME.CHILD) + ".id as _id ,", "");

        CommonRepository commonRepository = Utils.context().commonrepository(Constants.TABLE_NAME.CHILD);
        List<Map<String, String>> res = new ArrayList<>();

        Cursor cursor = null;
        try {
            cursor = commonRepository.queryTable(query);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                int columncount = cursor.getColumnCount();
                Map<String, String> columns = new HashMap<>();
                for (int i = 0; i < columncount; i++) {
                    columns.put(cursor.getColumnName(i), cursor.getType(i) == Cursor.FIELD_TYPE_NULL ? null : String.valueOf(cursor.getString(i)));
                }
                res.add(columns);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Timber.e(e, e.toString());
        } finally {
            if (cursor != null)
                cursor.close();
        }

        if (res.isEmpty()) {
            return null;
        }
        return res.get(0);
    }

    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    private class ReplaceAvatarTask extends AsyncTask<String, Void, Constants.FamilyMemberType> {

        private WeakReference<RegisterViewHolder> viewHolderWeakReference;
        private CommonPersonObjectClient commonPersonObject;

        private ReplaceAvatarTask(RegisterViewHolder registerViewHolder, CommonPersonObjectClient commonPersonObjectClient) {
            viewHolderWeakReference = new WeakReference<>(registerViewHolder);
            commonPersonObject = commonPersonObjectClient;
        }

        @Override
        protected Constants.FamilyMemberType doInBackground(String... strings) {
            String baseEntityId = strings[0];
            if (PNCDao.isPNCMember(baseEntityId)) {
                return Constants.FamilyMemberType.PNC;
            } else if (AncDao.isANCMember(baseEntityId)) {
                return Constants.FamilyMemberType.ANC;
            } else {
                return Constants.FamilyMemberType.Other;
            }
        }

        @Override
        protected void onPostExecute(Constants.FamilyMemberType memberType) {
            setMemberProfileImageResourceIdentifier(memberType, commonPersonObject, viewHolderWeakReference.get());
        }
    }

    private class UpdateAsyncTask extends AsyncTask<Void, Void, Void> {
        private final RegisterViewHolder viewHolder;
        private final CommonPersonObjectClient pc;

        private final Rules rules;

        private Map<String, String> map;
        //private ChildVisit childVisit;

        private UpdateAsyncTask(RegisterViewHolder viewHolder, CommonPersonObjectClient pc) {
            this.viewHolder = viewHolder;
            this.pc = pc;
            this.rules = AddoApplication.getInstance().getRulesEngineHelper().rules(Constants.RULE_FILE.HOME_VISIT);
        }

        @Override
        protected Void doInBackground(Void... params) {
            map = getChildDetails(pc.getCaseId());
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            // Update status column

        }
    }
}