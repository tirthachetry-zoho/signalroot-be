package com.signalroot.service;

import com.signalroot.entity.Organization;
import java.util.Map;

public interface TenantDeployServiceInterface {
    void processGitHubDeploy(Map<String, Object> payload, Organization organization);
    void processJenkinsDeploy(Map<String, Object> payload, Organization organization);
}
