package org.smartregister.addo.model;

public class ChildModel {
    private String childFullName;
    private String dateOfBirth;
    private String firstName;
    private String baseEntityId;

    public ChildModel(String childFullName, String dateOfBirth, String firstName, String baseEntityId) {
        this.childFullName = childFullName;
        this.dateOfBirth = dateOfBirth;
        this.firstName = firstName;
        this.baseEntityId = baseEntityId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getChildFullName() {
        return childFullName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }
}
