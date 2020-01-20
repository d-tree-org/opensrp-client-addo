package org.smartregister.addo.domain;

import org.opensrp.api.domain.BaseEntity;

import java.util.Map;

public class Entity extends BaseEntity {
    private String baseEntityId;
    private Map<String, Object> relationships;
    private Map<String, Object> identifiers;

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public Map<String, Object> getRelationships() {
        return relationships;
    }

    public void setRelationships(Map<String, Object> relationships) {
        this.relationships = relationships;
    }

    public Map<String, Object> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(Map<String, Object> identifiers) {
        this.identifiers = identifiers;
    }
}
