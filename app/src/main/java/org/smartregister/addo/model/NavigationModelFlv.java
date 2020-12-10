package org.smartregister.addo.model;

import org.smartregister.addo.R;
import org.smartregister.addo.util.Constants;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class NavigationModelFlv implements NavigationModel.Flavor {

    public static List<NavigationOption> navigationOptions = new ArrayList<>();

    @Override
    public List<NavigationOption> getNavigationItems() {

        if (navigationOptions.size() == 0 ) {

            NavigationOption opt1 = new NavigationOption(R.mipmap.ic_home, 0, R.string.menu_addo_home, Constants.DrawerMenu.ALL_FAMILIES, 0);
            NavigationOption opt2 = new NavigationOption(R.drawable.ic_report, 0, R.string.menu_addo_report, Constants.DrawerMenu.CHILD_CLIENTS, 0);
            NavigationOption opt3 = new NavigationOption(R.mipmap.ic_chart, R.mipmap.ic_chart_active, R.string.monthly_activity, Constants.DrawerMenu.MONTHLY_ACTIVITY, 0);

            navigationOptions.addAll(asList(opt1, opt2, opt3));
        }

        return navigationOptions;
    }
}