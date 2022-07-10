package com.optimagrowth.license.service;

import com.optimagrowth.license.model.License;
import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.repository.LicenseRepository;
import com.optimagrowth.license.service.client.OrganizationFeignClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
@RequiredArgsConstructor
public class LicenseService {
    private final MessageSource messages;
    private final LicenseRepository licenseRepository;
    private final OrganizationFeignClient organizationFeignClient;

    public License getLicense(Long licenseId, Long organizationId) {
        License license = new License();
        license.setLicenseId(licenseId);
        license.setOrganizationId(organizationId);
        license.setDescription("Software product");
        license.setProductName("Ostock");
        license.setLicenseType("full");
        Organization organization = organizationFeignClient.getOrganization(organizationId);
        if (organization != null) {
            license.setOrganizationName(organization.getName());
            license.setContactName(organization.getContactName());
            license.setContactEmail(organization.getContactEmail());
            license.setContactPhone(organization.getContactPhone());
        }
        return license;
    }

    @CircuitBreaker(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
    @RateLimiter(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
    @Retry(name = "retryLicenseService", fallbackMethod = "buildFallbackLicenseList")
    @Bulkhead(name = "bulkheadLicenseService", type= Bulkhead.Type.THREADPOOL, fallbackMethod = "buildFallbackLicenseList")
    public List<License> getLicensesByOrganization(Long organizationId) throws TimeoutException {
        randomlyRunLong();
        return licenseRepository.findAllByOrganizationId(organizationId);
    }

    private void randomlyRunLong() throws TimeoutException {
        Random random = new Random();
        int randomNum = random.nextInt(3) + 1;
        if (randomNum == 3) {
            sleep();
        }
    }

    private void sleep() throws TimeoutException {
        try {
            Thread.sleep(1);
            throw new TimeoutException();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    private List<License> buildFallbackLicenseList(Long organizationId, Throwable t){
        List<License> fallbackList = new ArrayList<>();
        License license = new License();
        license.setLicenseId(0L);
        license.setOrganizationId(organizationId);
        license.setProductName("Sorry no licensing information currently available");
        fallbackList.add(license);
        return fallbackList;
    }

    public String createLicense(License license, Long organizationId, Locale locale) {
        String responseMessage = null;
        if (!StringUtils.isEmpty(license)) {
            license.setOrganizationId(organizationId);
            responseMessage = String.format(messages.getMessage("license.create.message", null, locale), license.toString());
        }

        return responseMessage;
    }

    public String updateLicense(License license, Long organizationId) {
        String responseMessage = null;
        if (!StringUtils.isEmpty(license)) {
            license.setOrganizationId(organizationId);
            responseMessage = String.format(messages.getMessage("license.update.message", null, null), license.toString());
        }

        return responseMessage;
    }

    public String deleteLicense(Long licenseId, Long organizationId) {
        String responseMessage = null;
        responseMessage = String.format(messages.getMessage("license.delete.message", null, null), licenseId, organizationId);
        return responseMessage;
    }
}
