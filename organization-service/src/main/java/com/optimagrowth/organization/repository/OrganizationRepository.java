package com.optimagrowth.organization.repository;

import com.optimagrowth.organization.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}
