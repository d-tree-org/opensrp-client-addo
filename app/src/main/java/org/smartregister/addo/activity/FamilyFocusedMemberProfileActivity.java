package org.smartregister.addo.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.addo.R;
import org.smartregister.addo.contract.FamilyFocusedMemberProfileContract;
import org.smartregister.addo.custom_views.FamilyMemberFloatingMenu;
import org.smartregister.addo.dao.AncDao;
import org.smartregister.addo.dao.FamilyDao;
import org.smartregister.addo.dao.PNCDao;
import org.smartregister.addo.presenter.FamilyFocusedMemberProfileActivityPresenter;
import org.smartregister.addo.util.ChildDBConstants;
import org.smartregister.addo.util.CoreConstants;
import org.smartregister.addo.util.JsonFormUtils;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.model.BaseFamilyProfileMemberModel;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.Utils;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.util.FormUtils;
import org.smartregister.view.activity.BaseProfileActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

public class FamilyFocusedMemberProfileActivity extends BaseProfileActivity implements FamilyFocusedMemberProfileContract.View {

    private TextView nameView;
    private TextView detailOneView;
    private TextView detailTwoView;
    private TextView detailThreeView;
    private CircleImageView imageView;
    private CustomFontTextView ctvScreeningMed, ctvCommodities, ctvDispense;

    private View familyHeadView;
    private View primaryCaregiverView;

    protected ViewPagerAdapter adapter;
    private String familyBaseEntityId;
    private String baseEntityId;
    private String familyHead;
    private String primaryCaregiver;
    private String villageTown;
    private String familyName;
    private String PhoneNumber;
    private CommonPersonObjectClient commonPersonObject;
    protected MemberObject memberObject;
    private FamilyMemberFloatingMenu familyFloatingMenu;

    private FormUtils formUtils;

    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_focused_member_profile);

        Toolbar toolbar = findViewById(R.id.addo_focused_family_member_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable backArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
            backArrow.setColorFilter(getResources().getColor(R.color.addo_primary), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(backArrow);
            actionBar.setTitle("");
        }
        this.appBarLayout = findViewById(R.id.toolbar_appbarlayout_addo_focused);

        this.imageRenderHelper = new ImageRenderHelper(this);

        initializePresenter();

        setupViews();
    }

    @Override
    protected void setupViews() {
        super.setupViews();
        TextView toolBarTitle = findViewById(R.id.toolbar_title_focused);
        toolBarTitle.setText(String.format(getString(R.string.return_to_family_name), presenter().getFamilyName()));

        detailOneView = findViewById(R.id.textview_detail_one_focused);
        detailTwoView = findViewById(R.id.textview_detail_two_focused);
        detailThreeView = findViewById(R.id.textview_detail_three_focused);

        familyHeadView = findViewById(R.id.family_head_focused);
        primaryCaregiverView = findViewById(R.id.primary_caregiver_focused);

        nameView = findViewById(R.id.textview_name_focused);

        imageView = findViewById(R.id.imageview_profile);
        imageView.setBorderWidth(2);

        ctvScreeningMed = findViewById(R.id.tv_focused_client_screening);
        ctvScreeningMed.setOnClickListener(this);
        ctvCommodities = findViewById(R.id.tv_focused_client_commodities);
        ctvCommodities.setOnClickListener(this);
        ctvDispense = findViewById(R.id.tv_focused_client_dispense);
        ctvDispense.setOnClickListener(this);
    }

    @Override
    protected void initializePresenter() {
        commonPersonObject = (CommonPersonObjectClient) getIntent().getSerializableExtra(org.smartregister.addo.util.Constants.INTENT_KEY.CHILD_COMMON_PERSON);
        familyBaseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
        baseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
        familyHead = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_HEAD);
        primaryCaregiver = getIntent().getStringExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER);
        villageTown = getIntent().getStringExtra(Constants.INTENT_KEY.VILLAGE_TOWN);
        familyName = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_NAME);
        PhoneNumber = commonPersonObject.getColumnmaps().get(org.smartregister.addo.util.Constants.JsonAssets.FAMILY_MEMBER.PHONE_NUMBER);
        memberObject = new MemberObject(commonPersonObject);
        presenter = new FamilyFocusedMemberProfileActivityPresenter(this, new BaseFamilyProfileMemberModel(), null, baseEntityId,
                familyBaseEntityId, familyHead, primaryCaregiver, familyName, villageTown);
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        return null;
    }

    @Override
    protected void fetchProfileData() {
        presenter().fetchProfileData();
    }

    @Override
    public FamilyFocusedMemberProfileContract.Presenter presenter() {
        return (FamilyFocusedMemberProfileContract.Presenter) presenter;
    }

    @Override
    public void setProfileImage(String baseEntityId, String entityType) {
        imageRenderHelper.refreshProfileImage(baseEntityId, imageView, Utils.getMemberProfileImageResourceIDentifier(entityType));
    }

    @Override
    public void setProfileName(String fullName) {
        nameView.setText(fullName);
    }

    @Override
    public void setProfileDetailOne(String detailOne) {
        detailOneView.setText(detailOne);
    }

    @Override
    public void setProfileDetailTwo(String detailTwo) {
        detailTwoView.setText(detailTwo);
    }

    @Override
    public void setProfileDetailThree(String detailThree) {
        detailThreeView.setText(detailThree);
    }

    @Override
    public void toggleFamilyHead(boolean show) {
        if (show) {
            familyHeadView.setVisibility(View.VISIBLE);
        } else {
            familyHeadView.setVisibility(View.GONE);
        }
    }

    @Override
    public void togglePrimaryCaregiver(boolean show) {
        if (show) {
            primaryCaregiverView.setVisibility(View.VISIBLE);
        } else {
            primaryCaregiverView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        presenter().refreshProfileView();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.tv_focused_client_screening:
                if (isChildClient()) {
                    startFormActivity(getFormUtils().getFormJson(CoreConstants.JSON_FORM.getChildAddoDangerSigns()));
                } else if (isAncClient()) {
                    startFormActivity(getFormUtils().getFormJson(CoreConstants.JSON_FORM.getAncAddoDangerSigns()));
                } else if (isPncClient()) {
                    startFormActivity(getFormUtils().getFormJson(CoreConstants.JSON_FORM.getPncAddoDangerSigns()));
                } else {
                    Toast.makeText(this, "You clicked a client that is not in the focused group screening", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.tv_focused_client_commodities:
                Toast.makeText(this, "Give your client the commodities they want", Toast.LENGTH_SHORT).show();
                break;

            case R.id.tv_focused_client_dispense:
                Toast.makeText(this, "Please dispense the medication now.", Toast.LENGTH_SHORT).show();
                break;

            default:
                super.onClick(view);
                break;

        }

    }

    private boolean isChildClient() {
        String entityType = Utils.getValue(commonPersonObject.getColumnmaps(), ChildDBConstants.KEY.ENTITY_TYPE, false);
        return entityType.equals(org.smartregister.addo.util.Constants.TABLE_NAME.CHILD);
    }

    private boolean isAncClient() {
        return AncDao.isANCMember(baseEntityId);
    }

    private boolean isPncClient() {
        return PNCDao.isPNCMember(baseEntityId);
    }

    public void startFormActivity(JSONObject jsonForm) {
        Form form = new Form();
        form.setName("Danger Sign Screening"); // Make it so that it is for Child, ANC or PNC
        form.setActionBarBackground(R.color.family_actionbar);
        form.setNavigationBackground(R.color.family_navigation);
        form.setHomeAsUpIndicator(R.mipmap.ic_cross_white);
        form.setSaveLabel("FINISH");
        form.setHideSaveLabel(true);
        form.setWizard(true);

        Intent intent = new Intent(this, ReferralWizardFormActivity.class);
        intent.putExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());
        intent.putExtra(Constants.WizardFormActivity.EnableOnCloseDialog, false);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        startActivityForResult(intent, org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    private FormUtils getFormUtils() {
        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(Utils.context().applicationContext());
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return formUtils;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON) {
            try {

                String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                JSONObject form = new JSONObject(jsonString);
                Map<String, String> formForSubmission = new HashMap<>();

                formForSubmission.put(form.optString(org.smartregister.chw.anc.util.Constants.ENCOUNTER_TYPE), jsonString);

                submitForm(formForSubmission);

                FamilyDao.completeTasksForEntity(baseEntityId);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void submitForm(Map<String, String> formForSubmission) {
        presenter().submitVisit(formForSubmission);
    }
}
