package com.signalroot.repository;

import com.signalroot.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    
    Optional<Organization> findByOrganizationKey(String organizationKey);
    
    Optional<Organization> findByWebhookSecret(String webhookSecret);
    
    Optional<Organization> findBySlackWorkspaceId(String slackWorkspaceId);
    
    boolean existsByOrganizationKey(String organizationKey);
    
    boolean existsByWebhookSecret(String webhookSecret);
    
    boolean existsBySlackWorkspaceId(String slackWorkspaceId);
}
