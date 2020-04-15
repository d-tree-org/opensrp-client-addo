package org.smartregister.addo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;
import org.smartregister.addo.R;
import org.smartregister.addo.contract.FamilyOtherMemberProfileExtendedContract;
import org.smartregister.addo.custom_views.FamilyMemberFloatingMenu;
import org.smartregister.addo.dataloader.AncMemberDataLoader;
import org.smartregister.addo.dataloader.FamilyMemberDataLoader;
import org.smartregister.addo.form_data.NativeFormsDataBinder;
import org.smartregister.addo.fragment.FamilyOtherMemberProfileFragment;
import org.smartregister.addo.listeners.FloatingMenuListener;
import org.smartregister.addo.listeners.OnClickFloatingMenu;
import org.smartregister.addo.presenter.FamilyOtherMemberActivityPresenter;
import org.smartregister.addo.util.CoreConstants;
import org.smartregister.addo.util.CoreJsonFormUtils;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.activity.BaseFamilyOtherMemberProfileActivity;
import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.fragment.BaseFamilyOtherMemberProfileFragment;
import org.smartregister.family.model.BaseFamilyOtherMemberProfileActivityModel;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.util.FormUtils;
import org.smartregister.view.fragment.BaseRegisterFragment;

import timber.log.Timber;

public class FamilyOtherMemberProfileActivity extends BaseFamilyOtherMemberProfileActivity implements FamilyOtherMemberProfileExtendedContract.View {

    private String familyBaseEntityId;
    private String baseEntityId;
    private String familyHead;
    private String primaryCaregiver;
    private String villageTown;
    private String familyName;
    private String PhoneNumber;
    private CommonPersonObjectClient commonPersonObject;
    private FamilyMemberFloatingMenu familyFloatingMenu;
    private TextView textViewFamilyHas, textViewDangersignScreening;
    private RelativeLayout layoutFamilyHasRow;
    protected MemberObject memberObject;
    private FormUtils formUtils;

    private OnClickFloatingMenu onClickFloatingMenu;
    //private FamilyOtherMemberProfileActivityFlv flavor = new FamilyOtherMemberProfileActivityFlv();


    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_family_other_member_profile_addo);

        Toolbar toolbar = findViewById(R.id.addo_other_family_member_toolbar);
        this.setSupportActionBar(toolbar);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable backArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
            backArrow.setColorFilter(getResources().getColor(R.color.addo_primary), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(backArrow);
            actionBar.setTitle("");
        }

        this.appBarLayout = findViewById(R.id.toolbar_appbarlayout_addo_non_focused);

        this.imageRenderHelper = new ImageRenderHelper(this);

        initializePresenter();

        setupViews();
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
        presenter = new FamilyOtherMemberActivityPresenter(this, new BaseFamilyOtherMemberProfileActivityModel(), null, familyBaseEntityId, baseEntityId, familyHead, primaryCaregiver, villageTown, familyName);

        //TODO: Include flavor to implement
        //onClickFloatingMenu = flavor.getOnClickFloatingMenu(this, familyBaseEntityId);

    }

    @Override
    protected void setupViews() {
        super.setupViews();

        textViewDangersignScreening = findViewById(R.id.textview_ds_screening);

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(String.format(getString(R.string.return_to_family_name), presenter().getFamilyName()));

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorHeight(0);

        findViewById(R.id.viewpager).setVisibility(View.GONE);

        // add floating menu
        familyFloatingMenu = new FamilyMemberFloatingMenu(this);
        LinearLayout.LayoutParams linearLayoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        familyFloatingMenu.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        addContentView(familyFloatingMenu, linearLayoutParams);

        familyFloatingMenu.setClickListener(onClickFloatingMenu);
        //textViewFamilyHas = findViewById(R.id.textview_family_has);
        //layoutFamilyHasRow = findViewById(R.id.family_has_row);

        textViewDangersignScreening.setOnClickListener(this);

        //layoutFamilyHasRow.setOnClickListener(this);

    }

    @Override
    public Context getContext() {
        return this;
    }


    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        BaseFamilyOtherMemberProfileFragment profileOtherMemberFragment = FamilyOtherMemberProfileFragment.newInstance(this.getIntent().getExtras());
        adapter.addFragment(profileOtherMemberFragment, "");

        viewPager.setAdapter(adapter);

        return viewPager;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuItem addMember = menu.findItem(R.id.add_member);
        if (addMember != null) {
            addMember.setVisible(false);
        }

        getMenuInflater().inflate(R.menu.other_member_menu, menu);

        //TODO : Flavour
        /*if (flavor.showMalariaConfirmationMenu()) {
            menu.findItem(R.id.action_malaria_registration).setVisible(false);
        } else {
            menu.findItem(R.id.action_malaria_registration).setVisible(false);
        }

        if (flavor.isWra(commonPersonObject)) {
            menu.findItem(R.id.action_anc_registration).setVisible(true);
        } else {
            menu.findItem(R.id.action_anc_registration).setVisible(false);
        }
        */

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_anc_registration:
//                AncRegisterActivity.startAncRegistrationActivity(FamilyOtherMemberProfileActivity.this, baseEntityId, PhoneNumber,
//                        org.smartregister.chw.util.Constants.JSON_FORM.getAncRegistration(), null, familyBaseEntityId);
                return true;
            case R.id.action_malaria_registration:
//                MalariaRegisterActivity.startMalariaRegistrationActivity(FamilyOtherMemberProfileActivity.this, baseEntityId);
                return true;
            case R.id.action_registration:
                startFormForEdit(R.string.edit_member_form_title);
                return true;
            case R.id.action_remove_member:
//                IndividualProfileRemoveActivity.startIndividualProfileActivity(FamilyOtherMemberProfileActivity.this, commonPersonObject, familyBaseEntityId, familyHead, primaryCaregiver);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public FamilyOtherMemberActivityPresenter presenter() {
        return (FamilyOtherMemberActivityPresenter) presenter;
    }

    public void startFormForEdit(Integer title_resource) {

        CommonRepository commonRepository = org.smartregister.addo.util.Utils.context().commonrepository(org.smartregister.addo.util.Utils.metadata().familyMemberRegister.tableName);

        final CommonPersonObject personObject = commonRepository.findByBaseEntityId(commonPersonObject.getCaseId());
        final CommonPersonObjectClient client =
                new CommonPersonObjectClient(personObject.getCaseId(), personObject.getDetails(), "");
        client.setColumnmaps(personObject.getColumnmaps());

        JSONObject form = org.smartregister.addo.util.JsonFormUtils.getAutoPopulatedJsonEditMemberFormString(
                (title_resource != null) ? getResources().getString(title_resource) : null,
                org.smartregister.addo.util.Constants.JSON_FORM.getFamilyMemberRegister(),
                this, client, org.smartregister.addo.util.Utils.metadata().familyMemberRegister.updateEventType, familyName, commonPersonObject.getCaseId().equalsIgnoreCase(primaryCaregiver));
        try {
            startFormActivity(form);
        } catch (Exception e) {
            Timber.e(e.getMessage());
        }
    }

    public void startFormActivity(JSONObject jsonForm) {
        startActivityForResult(CoreJsonFormUtils.getJsonIntent(this, jsonForm, Utils.metadata().familyMemberFormActivity),
                JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case org.smartregister.addo.util.Constants.ProfileActivityResults.CHANGE_COMPLETED:
                if (resultCode == Activity.RESULT_OK) {
                    Intent intent = new Intent(FamilyOtherMemberProfileActivity.this, FamilyProfileActivity.class);
                    intent.putExtras(getIntent().getExtras());
                    startActivity(intent);
                    finish();
                }
                break;
            case JsonFormUtils.REQUEST_CODE_GET_JSON:
                if (resultCode == RESULT_OK) {
                    try {
                        String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                        JSONObject form = new JSONObject(jsonString);
                        if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Utils.metadata().familyMemberRegister.updateEventType)) {
                            presenter().updateFamilyMember(jsonString);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        FloatingMenuListener.getInstance(this, presenter().getFamilyBaseEntityId());
    }

    public void refreshList() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            for (int i = 0; i < adapter.getCount(); i++) {
                refreshList(adapter.getItem(i));
            }
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    for (int i = 0; i < adapter.getCount(); i++) {
                        refreshList(adapter.getItem(i));
                    }
                }
            });
        }
    }

    private void refreshList(Fragment fragment) {
        if (fragment instanceof BaseRegisterFragment && fragment instanceof FamilyOtherMemberProfileFragment) {
            FamilyOtherMemberProfileFragment familyOtherMemberProfileFragment = ((FamilyOtherMemberProfileFragment) fragment);
            if (familyOtherMemberProfileFragment.presenter() != null) {
                familyOtherMemberProfileFragment.refreshListView();
            }
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.family_has_row:
                //TODO: Uncomment to implement viewing family due screen
                //openFamilyDueTab();
                break;

            case R.id.textview_ds_screening:
                // Technically here we can implement the logic to check whether they are ANC or PNC and handle the danger signs for them
                // This line checks whether the woman is already registered as ANC
/*                if (AncDao.isANCMember(baseEntityId)) {
                    startAncFormActivity(R.string.anc_home_visit_danger_signs, CoreConstants.JSON_FORM.ANC_HOME_VISIT.getDangerSigns());
                } else {
                    startAncFormActivity(R.string.anc_home_visit_danger_signs, CoreConstants.JSON_FORM.PNC_HOME_VISIT.getDangerSignsMother());
                }*/
                startRecordServiceProvided();

            default:
                super.onClick(view);
                break;
        }
    }

    private void startRecordServiceProvided() {
        startFormActivity(getFormUtils().getFormJson(CoreConstants.JSON_FORM.getAddoRecordServiceOther()));
    }

    public void startAncFormActivity(Integer title_resource, String formName) {
        try {
            JSONObject form = null;
            boolean isPrimaryCareGiver = memberObject.getPrimaryCareGiver().equals(memberObject.getBaseEntityId());
            String titleString = title_resource != null ? getResources().getString(title_resource) : null;

            if (formName.equals(CoreConstants.JSON_FORM.getAncRegistration())) {

                NativeFormsDataBinder binder = new NativeFormsDataBinder(this, memberObject.getBaseEntityId());
                binder.setDataLoader(new AncMemberDataLoader(titleString));
                form = binder.getPrePopulatedForm(formName);

            } else if (formName.equals(CoreConstants.JSON_FORM.getFamilyMemberRegister())) {

                String eventName = org.smartregister.addo.util.Utils.metadata().familyMemberRegister.updateEventType;

                NativeFormsDataBinder binder = new NativeFormsDataBinder(this, memberObject.getBaseEntityId());
                binder.setDataLoader(new FamilyMemberDataLoader(memberObject.getFamilyName(), isPrimaryCareGiver, titleString, eventName));

                form = binder.getPrePopulatedForm(CoreConstants.JSON_FORM.getFamilyMemberRegister());
            } else if (formName.equals(CoreConstants.JSON_FORM.ANC_HOME_VISIT.getDangerSigns())) {
                NativeFormsDataBinder binder = new NativeFormsDataBinder(this, memberObject.getBaseEntityId());
                binder.setDataLoader(new AncMemberDataLoader(titleString));
                form = binder.getPrePopulatedForm(formName);
            }

            else if (formName.equals(CoreConstants.JSON_FORM.PNC_HOME_VISIT.getDangerSignsMother())) {
                NativeFormsDataBinder binder = new NativeFormsDataBinder(this, memberObject.getBaseEntityId());
                binder.setDataLoader(new AncMemberDataLoader(titleString));
                form = binder.getPrePopulatedForm(formName);
            }
            startActivityForResult(org.smartregister.addo.util.JsonFormUtils.getAncPncStartFormIntent(form, this), JsonFormUtils.REQUEST_CODE_GET_JSON);
        } catch (Exception e) {
            Timber.e(e);
        }
    }


    private void openFamilyDueTab() {
        Intent intent = new Intent(this, FamilyProfileActivity.class);

        intent.putExtra(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, familyBaseEntityId);
        intent.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, familyHead);
        intent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, primaryCaregiver);
        intent.putExtra(Constants.INTENT_KEY.FAMILY_NAME, familyName);

        intent.putExtra(org.smartregister.addo.util.Constants.INTENT_KEY.SERVICE_DUE, true);
        startActivity(intent);
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

    /**
     * build implementation differences file
     */
    public interface Flavor {
        /**
         * Return implementation menu
         */
        OnClickFloatingMenu getOnClickFloatingMenu(final Activity activity, final String familyBaseEntityId);

        /**
         * calculate wra validity for each implementation
         *
         * @param commonPersonObject
         * @return
         */
        boolean isWra(CommonPersonObjectClient commonPersonObject);

        boolean showMalariaConfirmationMenu();

    }

}
