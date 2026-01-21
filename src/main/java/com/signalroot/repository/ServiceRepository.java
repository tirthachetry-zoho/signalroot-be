package com.signalroot.repository;

import com.signalroot.entity.Service;
import com.signalroot.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID> {
    Optional<Service> findByName(String name);
    Optional<Service> findByNameAndOrganization(String name, Organization organization);
    boolean existsByName(String name);
    boolean existsByNameAndOrganization(String name, Organization organization);
}
