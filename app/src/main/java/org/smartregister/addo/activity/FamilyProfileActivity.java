package org.smartregister.addo.activity;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.smartregister.addo.R;
import org.smartregister.addo.contract.FamilyProfileExtendedContract;
import org.smartregister.addo.custom_views.FamilyFloatingMenu;
import org.smartregister.addo.dao.AdolescentDao;
import org.smartregister.addo.dao.AncDao;
import org.smartregister.addo.dao.PNCDao;
import org.smartregister.addo.event.PermissionEvent;
import org.smartregister.addo.fragment.FamilyProfileActivityFragment;
import org.smartregister.addo.fragment.FamilyProfileMemberFragment;
import org.smartregister.addo.listeners.FloatingMenuListener;
import org.smartregister.addo.model.FamilyProfileModel;
import org.smartregister.addo.presenter.FamilyProfilePresenter;
import org.smartregister.addo.util.ChildDBConstants;
import org.smartregister.addo.util.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.activity.BaseFamilyProfileActivity;
import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.Utils;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.util.PermissionUtils;
import org.smartregister.view.fragment.BaseRegisterFragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class FamilyProfileActivity extends BaseFamilyProfileActivity implements FamilyProfileExtendedContract.View {

    private static final String TAG = FamilyProfileActivity.class.getCanonicalName();
    private String familyBaseEntityId;
    private String familyHead;
    private String primaryCaregiver;
    private String familyName;

    @Override
    protected void onCreation() {
        super.onCreation();
        setContentView(R.layout.activity_family_profile);

        Toolbar toolbar =  findViewById(R.id.family_toolbar);
        this.setSupportActionBar(toolbar);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }

        this.appBarLayout = this.findViewById(R.id.toolbar_appbarlayout);
        this.imageRenderHelper = new ImageRenderHelper(this);

        initializePresenter();
        setupViews();
    }

    @Override
    protected void initializePresenter() {
        familyBaseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
        familyHead = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_HEAD);
        primaryCaregiver = getIntent().getStringExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER);
        familyName = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_NAME);
        presenter = new FamilyProfilePresenter(this, new FamilyProfileModel(familyName), familyBaseEntityId, familyHead, primaryCaregiver, familyName);
    }

    @Override
    protected void setupViews() {
        super.setupViews();

        // Update profile border
        CircleImageView profileView = findViewById(R.id.imageview_profile);
        profileView.setBorderWidth(2);

    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        BaseFamilyProfileMemberFragment profileMemberFragment = FamilyProfileMemberFragment.newInstance(this.getIntent().getExtras());

        adapter.addFragment(profileMemberFragment, this.getString(R.string.family_members).toUpperCase());
        viewPager.setAdapter(adapter);

        if (getIntent().getBooleanExtra(org.smartregister.addo.util.Constants.INTENT_KEY.SERVICE_DUE, true) || getIntent().getBooleanExtra(Constants.INTENT_KEY.GO_TO_DUE_PAGE, false)) {
            viewPager.setCurrentItem(0);
        }

        return viewPager;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuItem addMember = menu.findItem(R.id.add_member);
        if (addMember != null) {
            addMember.setVisible(false);
        }

        //Menu items for the family members profile
        //getMenuInflater().inflate(R.menu.addo_family_profile_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_family_details:
            case R.id.action_item1_placeholder:
            case R.id.action_item2_placeholder:

                // TODO Add Menu Item here

                break;
            case android.R.id.home:
                //onBackPressed();
                Intent intent = NavUtils.getParentActivityIntent(this);
                assert intent != null;
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(this, intent);
                return true;
            default:
                super.onOptionsItemSelected(item);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionUtils.PHONE_STATE_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                boolean granted = (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                if (granted) {
                    PermissionEvent event = new PermissionEvent(requestCode, granted);
                    EventBus.getDefault().post(event);
                } else {
                    Toast.makeText(this, getText(R.string.allow_calls_denied), Toast.LENGTH_LONG).show();
                }
            }
            break;
            default:
                break;
        }
    }

    public String getFamilyBaseEntityId() {
        return familyBaseEntityId;
    }

    @Override
    public void startChildForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            Log.v("SIMPRINT_SDK","HNPPMemberJsonFormActivity >>requestCode:"+requestCode+":resultCode:"+resultCode+":intent:"+data);

            switch (requestCode) {
                case org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON:
                    break;
                case org.smartregister.addo.util.Constants.ProfileActivityResults.CHANGE_COMPLETED:
                    break;
                case org.smartregister.addo.util.Constants.SIMPRINTS_IDENTIFICATION.IDENTIFY_RESULT_CODE:
                    Boolean check = data.getBooleanExtra("biometricsComplete" , false);
            }
        }
    }

    @Override
    public void refreshMemberList(FetchStatus fetchStatus) {
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
        if (fragment != null && fragment instanceof BaseRegisterFragment) {
            if (fragment instanceof FamilyProfileMemberFragment) {
                FamilyProfileMemberFragment familyProfileMemberFragment = ((FamilyProfileMemberFragment) fragment);
                if (familyProfileMemberFragment.presenter() != null) {
                    familyProfileMemberFragment.refreshListView();
                }
            }else if (fragment instanceof FamilyProfileActivityFragment) {
                FamilyProfileActivityFragment familyProfileActivityFragment = ((FamilyProfileActivityFragment) fragment);
                if (familyProfileActivityFragment.presenter() != null) {
                    familyProfileActivityFragment.refreshListView();
                }
            }
        }
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        FloatingMenuListener.getInstance(this, presenter().familyBaseEntityId());
    }

    public void setPrimaryCaregiver(String caregiver) {
        if (StringUtils.isNotBlank(caregiver)) {
            this.primaryCaregiver = caregiver;
            getIntent().putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, caregiver);
        }
    }

    public String getPrimaryCaregiver() {
        return primaryCaregiver;
    }

    public String getFamilyHead(){
        return this.familyHead;
    }

    public void setFamilyHead(String head) {
        if (StringUtils.isNotBlank(head)) {
            this.familyHead = head;
            getIntent().putExtra(Constants.INTENT_KEY.FAMILY_HEAD, head);
        }
    }

    @Override
    public FamilyProfileExtendedContract.Presenter presenter() {
        return (FamilyProfilePresenter) presenter;
    }

    public void goToProfileActivity(View view, Bundle fragmentArguments) {
        if (view.getTag() instanceof CommonPersonObjectClient) {
            CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) view.getTag();
            String entityType = Utils.getValue(commonPersonObjectClient.getColumnmaps(), ChildDBConstants.KEY.ENTITY_TYPE, false);

            if (!(CoreConstants.TABLE_NAME.CHILD.equals(entityType) || isAncMember(commonPersonObjectClient.entityId()) || isPncMember(commonPersonObjectClient.entityId()) || isAdolescent(commonPersonObjectClient.entityId()))) {
                goToOtherMemberProfileActivity(commonPersonObjectClient, fragmentArguments);
            } else {
                goToFocusMemberProfileActivity(commonPersonObjectClient, fragmentArguments);
            }
        }
    }

    private void goToFocusMemberProfileActivity(CommonPersonObjectClient patient, Bundle fragmentArguments) {
        Intent intent = new Intent(this, FamilyFocusedMemberProfileActivity.class);
        if (fragmentArguments != null) {
            intent.putExtras(fragmentArguments);
        }
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        intent.putExtra(org.smartregister.addo.util.Constants.INTENT_KEY.CHILD_COMMON_PERSON, patient);
        intent.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, getFamilyHead());
        intent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, getPrimaryCaregiver());
        startActivity(intent);
    }

    public void goToOtherMemberProfileActivity(CommonPersonObjectClient patient, Bundle fragmentArguments) {
        Intent intent = new Intent(this, FamilyOtherMemberProfileActivity.class);
        if (fragmentArguments != null) {
            intent.putExtras(fragmentArguments);
        }
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        intent.putExtra(org.smartregister.addo.util.Constants.INTENT_KEY.CHILD_COMMON_PERSON, patient);
        intent.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, getFamilyHead());
        intent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, getPrimaryCaregiver());
        startActivity(intent);
    }

    private boolean isPncMember(String entityId) {
        return PNCDao.isPNCMember(entityId);
    }

    private boolean isAncMember(String entityId) {
        return AncDao.isANCMember(entityId);
    }

    private boolean isAdolescent(String entityId) { return AdolescentDao.isAdolescentMember(entityId); }
}
