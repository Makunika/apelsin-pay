package ru.pshiblo.info.business.services;

import ru.pshiblo.info.business.domain.Company;
import ru.pshiblo.security.model.AuthUser;

public interface CompanyApiService {
    boolean checkApiKey(Company company, String apiKey);
    void regenerateApiKey(Company company, AuthUser user);
    String getApiKey(Company company, AuthUser user);
}
