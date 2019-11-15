package org.smartregister.addo.custom_views;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.smartregister.addo.R;
import org.smartregister.addo.listeners.OnClickFloatingMenu;

public class FamilyMemberFloatingMenu extends LinearLayout implements View.OnClickListener {
    private RelativeLayout activityMain;
    private FloatingActionButton fab;
    private LinearLayout menuBar;
    private Animation fabOpen, fabClose, rotateForward, rotateBack;
    private boolean isFabMenuOpen = false;
    private OnClickFloatingMenu onClickFloatingMenu;
    private Flavor flavor = new FamilyMemberFloatingMenuFlv();

    private View callLayout;
    private View referLayout;

    public FamilyMemberFloatingMenu(Context context) {
        super(context);
        initUi();
    }

    public View getCallLayout() {
        return callLayout;
    }

    public FamilyMemberFloatingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUi();
    }

    public FamilyMemberFloatingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUi();
    }

    public void reDraw(boolean has_phone) {
        flavor.reDraw(this, has_phone);
    }

    private void initUi() {
        inflate(getContext(), R.layout.view_individual_floating_menu, this);
        activityMain = findViewById(R.id.activity_main);
        menuBar = findViewById(R.id.menu_bar);
        fab = findViewById(R.id.fab);

        fabOpen = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_forward);
        rotateBack = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_back);

        flavor.prepareFab(this, fab);

        callLayout = findViewById(R.id.call_layout);
        callLayout.setOnClickListener(this);

        referLayout = findViewById(R.id.refer_to_facility_layout);
        referLayout.setOnClickListener(this);

        callLayout.setClickable(false);
        referLayout.setClickable(false);

        menuBar.setVisibility(GONE);
    }

    public void setClickListener(OnClickFloatingMenu onClickFloatingMenu) {
        this.onClickFloatingMenu = onClickFloatingMenu;
    }

    public void animateFAB() {
        if (menuBar.getVisibility() == GONE) {
            menuBar.setVisibility(VISIBLE);
        }

        if (isFabMenuOpen) {
            activityMain.setBackgroundResource(R.color.transparent);

            fab.startAnimation(rotateBack);
            fab.setImageResource(R.drawable.ic_edit_white);

            callLayout.startAnimation(fabClose);
            referLayout.startAnimation(fabClose);

            callLayout.setClickable(false);
            referLayout.setClickable(false);
            isFabMenuOpen = false;

        } else {
            activityMain.setBackgroundResource(R.color.black_tranparent_50);

            fab.startAnimation(rotateForward);
            fab.setImageResource(R.drawable.ic_input_add);

            callLayout.startAnimation(fabOpen);
            referLayout.startAnimation(fabOpen);

            callLayout.setClickable(true);
            referLayout.setClickable(true);

            isFabMenuOpen = true;
        }
    }

    @Override
    public void onClick(View v) {
        //TODO: Implement Family Member FAB click event
        //onClickFloatingMenu.onClickMenu(v.getId());
        flavor.fabInteraction(this);
    }


    interface Flavor {
        void reDraw(FamilyMemberFloatingMenu menu, boolean has_phone);

        void prepareFab(FamilyMemberFloatingMenu menu, FloatingActionButton fab);

        void fabInteraction(FamilyMemberFloatingMenu menu);
    }

    //TODO - Removed from 'ba' Flavor
    class FamilyMemberFloatingMenuFlv implements FamilyMemberFloatingMenu.Flavor {

        @Override
        public void reDraw(FamilyMemberFloatingMenu menu, boolean has_phone) {
            redrawWithOption(menu, has_phone);
        }

        private void redrawWithOption(FamilyMemberFloatingMenu menu, boolean has_phone) {
            TextView callTextView = menu.findViewById(R.id.CallTextView);
            TextView callTextViewHint = menu.findViewById(R.id.CallTextViewHint);

            if (has_phone) {

                callTextViewHint.setVisibility(GONE);
                menu.getCallLayout().setOnClickListener(menu);
                callTextView.setTypeface(null, Typeface.NORMAL);
                callTextView.setTextColor(menu.getResources().getColor(android.R.color.black));
                ((FloatingActionButton) menu.findViewById(R.id.callFab)).getDrawable().setAlpha(255);

            } else {

                callTextViewHint.setVisibility(VISIBLE);
                menu.getCallLayout().setOnClickListener(null);
                callTextView.setTypeface(null, Typeface.ITALIC);
                callTextView.setTextColor(menu.getResources().getColor(R.color.grey));
                ((FloatingActionButton) menu.findViewById(R.id.callFab)).getDrawable().setAlpha(122);

            }
        }

        @Override
        public void prepareFab(final FamilyMemberFloatingMenu menu, FloatingActionButton fab) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menu.animateFAB();
                }
            });

            fab.setImageResource(R.drawable.ic_edit_white);
        }

        @Override
        public void fabInteraction(FamilyMemberFloatingMenu menu) {
            menu.animateFAB();
        }
    }


}

