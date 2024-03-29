package com.optimagrowth.organization.service;

import com.optimagrowth.organization.model.Organization;
import com.optimagrowth.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository repository;

    public Organization findById(Long organizationId) {
        Optional<Organization> opt = repository.findById(organizationId);
        log.info("organization service findById: {}", organizationId);
        return opt.orElse(null);
    }

    public Organization create(Organization organization) {
        organization = repository.save(organization);
        return organization;
    }

    public void update(Organization organization) {
        repository.save(organization);
    }

    public void delete(Organization organization) {
        repository.deleteById(organization.getId());
    }
}
