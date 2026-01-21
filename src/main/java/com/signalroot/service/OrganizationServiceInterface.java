package com.signalroot.service;

import com.signalroot.entity.Organization;
import java.util.List;
import java.util.UUID;

public interface OrganizationServiceInterface {
    Organization createOrganization(String name);
    Organization findByOrganizationKey(String organizationKey);
    Organization findById(UUID id);
    List<Organization> findAll();
    Organization updateOrganization(UUID id, String name);
    void deleteOrganization(UUID id);
    boolean existsByOrganizationKey(String organizationKey);
    Organization connectSlack(UUID organizationId, String accessToken, String workspaceId, String workspaceName);
    Organization disconnectSlack(UUID organizationId);
    Organization updateSlackChannel(UUID organizationId, String channel);
}
