package org.smartregister.addo.sync.helper;

import org.smartregister.CoreLibrary;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.TaskRepository;
import org.smartregister.sync.helper.TaskServiceHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddoTaskServiceHelper extends TaskServiceHelper {

    protected static AddoTaskServiceHelper instance;

    public AddoTaskServiceHelper(TaskRepository taskRepository) {
        super(taskRepository);
    }

    public static AddoTaskServiceHelper getInstance() {
        if (instance == null) {
            instance = new AddoTaskServiceHelper(CoreLibrary.getInstance().context().getTaskRepository());
        }
        return instance;
    }

    @Override
    protected List<String> getLocationIds() {
        return LocationHelper.getInstance().locationsFromHierarchy(true, null);
    }

    @Override
    protected Set<String> getPlanDefinitionIds() {
        Set<String> res = new HashSet<>();
        res.add("5270285b-5a3b-4647-b772-c0b3c52e2b71");
        return res;
    }
}
