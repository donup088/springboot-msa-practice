package com.optimagrowth.license.service;

import com.optimagrowth.license.model.License;
import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.service.client.OrganizationFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class LicenseService {
    private final MessageSource messages;
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
