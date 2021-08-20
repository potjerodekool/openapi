package org.platonos.demo.api;

import org.platonos.demo.api.model.CompanyDto;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class CompanyDelegateImpl implements CompaniesDelegate {

    @Override
    public Object createCompany(CompanyDto body, HttpServletRequest httpServletRequest) {
        return null;
    }

    @Override
    public CompanyDto get(int coreCompanyId, HttpServletRequest httpServletRequest) {
        return null;
    }
}
