package com.signalroot.service;

import com.signalroot.entity.Organization;
import java.util.Map;

public interface TenantAlertServiceInterface {
    void processPagerDutyAlert(Map<String, Object> payload, Organization organization);
    void processCloudWatchAlert(Map<String, Object> payload, Organization organization);
}
