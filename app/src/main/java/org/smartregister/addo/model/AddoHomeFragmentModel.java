package org.smartregister.addo.model;

import android.util.Log;

import org.smartregister.CoreLibrary;
import org.smartregister.addo.BuildConfig;
import org.smartregister.addo.contract.AddoHomeFragmentContract;
import org.smartregister.addo.fragment.AddoHomeFragment;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.domain.jsonmapping.Location;
import org.smartregister.domain.jsonmapping.util.LocationTree;
import org.smartregister.domain.jsonmapping.util.TreeNode;
import org.smartregister.family.util.ConfigHelper;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AddoHomeFragmentModel implements AddoHomeFragmentContract.Model {

    public static AddoHomeFragmentModel instance;

    public static AddoHomeFragmentModel getInstance() {
        if (instance == null) {
            instance = new AddoHomeFragmentModel();
        }

        return instance;
    }

    @Override
    public RegisterConfiguration defaultRegisterConfiguration() {
        return ConfigHelper.defaultRegisterConfiguration(org.smartregister.family.util.Utils.context().applicationContext());
    }

    @Override
    public ViewConfiguration getViewConfiguration(String viewConfigurationIdentifier) {
        return ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().getViewConfiguration(viewConfigurationIdentifier);
    }

    @Override
    public Set<View> getRegisterActiveColumns(String viewConfigurationIdentifier) {
        return ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().getRegisterActiveColumns(viewConfigurationIdentifier);
    }

    @Override
    public List<String> getAddoUserAllowedLocation() {
        return getAddoLocations();
    }

    private ArrayList<String> getAddoLocations() {

        ArrayList<String> locations = new ArrayList<>();

        LinkedHashMap<String, TreeNode<String, Location>> locatioHierarchy = getTreeNodeWithUserAssignedLocationId();

        // For testing purpose, in case the list is null
        //LinkedHashMap<String, TreeNode<String, Location>> locatioHierarchy = new LinkedHashMap<>();

        if (!Utils.isEmptyMap(locatioHierarchy)) {
            for (Map.Entry<String, TreeNode<String, Location>> entry : locatioHierarchy.entrySet()) {
                List<String> loca = getListOfTaggedLocationFromTheNode(entry.getValue());
                locations.addAll(loca);
            }
        }

        return locations;
    }

    private LinkedHashMap<String, TreeNode<String, Location>> getTreeNodeWithUserAssignedLocationId() {

        String userName = CoreLibrary.getInstance().context().allSharedPreferences().fetchRegisteredANM();
        String userGivenLocationId = CoreLibrary.getInstance().context().allSharedPreferences().fetchUserLocalityId(userName);

        String locationData = CoreLibrary.getInstance().context().anmLocationController().get();
        LocationTree locationTree = (LocationTree) AssetHandler.jsonStringToJava(locationData, LocationTree.class);
        LinkedHashMap<String, TreeNode<String, Location>> lochie = (locationTree != null) ? locationTree.getLocationsHierarchy() : null;

        LinkedHashMap<String, TreeNode<String, Location>> userLocationTree = new LinkedHashMap<>();

        for (Map.Entry<String, TreeNode<String, Location>> entry : lochie.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(userGivenLocationId)) {
                userLocationTree.put(userGivenLocationId, entry.getValue());
            } else {
                userLocationTree.put(userGivenLocationId, entry.getValue().findChild(userGivenLocationId));
            }
        }

        return userLocationTree;
    }


    private ArrayList<String> getListOfTaggedLocationFromTheNode(TreeNode<String, Location> rawLocationData) {

        ArrayList<String> allowedLocationLevel = new ArrayList<>(Arrays.asList(BuildConfig.ALLOWED_LOCATION_LEVELS));
        ArrayList<String> locationList = new ArrayList<>();

        try {
            if (rawLocationData == null) {
                return null;
            }
            Location node = rawLocationData.getNode();
            if (node == null) {
                return null;
            }
            String value = node.getName();
            Set<String> levels = node.getTags();

            if (!Utils.isEmptyCollection(levels)) {
                for (String level : levels) {
                    if (allowedLocationLevel.contains(level)) {
                        locationList.add(value);
                    }
                }
            }

            LinkedHashMap<String, TreeNode<String, Location>> childMap = childMap(rawLocationData);
            if (!Utils.isEmptyMap(childMap)) {
                for (Map.Entry<String, TreeNode<String, Location>> childEntry : childMap.entrySet()) {
                    ArrayList<String> childLocations = getListOfTaggedLocationFromTheNode(childEntry.getValue());
                    if (!Utils.isEmptyCollection(childLocations)) {
                        locationList.addAll(childLocations);
                    }
                }
            }

        } catch (Exception e) {
            Log.e(AddoHomeFragment.class.getCanonicalName(), Log.getStackTraceString(e));
        }

        return locationList;

    }

    private LinkedHashMap<String, TreeNode<String, Location>> childMap
            (TreeNode<String, Location> treeNode) {
        if (treeNode.getChildren() != null) {
            return treeNode.getChildren();
        }
        return null;
    }
}
