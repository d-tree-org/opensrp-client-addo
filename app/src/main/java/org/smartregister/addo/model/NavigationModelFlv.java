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

            NavigationOption opt1 = new NavigationOption(R.mipmap.ic_family, 0, R.string.menu_all_families, Constants.DrawerMenu.ALL_FAMILIES, 0);
            NavigationOption opt2 = new NavigationOption(R.mipmap.ic_child, 0, R.string.menu_child, Constants.DrawerMenu.CHILD_CLIENTS, 0);

            navigationOptions.addAll(asList(opt1, opt2));
        }

        return navigationOptions;
    }
}