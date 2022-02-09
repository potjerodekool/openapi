package org.platonos.demo.api;

import org.platonos.demo.api.model.CompanyRequestDto;
import org.platonos.demo.api.model.CompanyResponseDto;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
public class CompanyDelegateImpl implements CompaniesDelegate {


    @Override
    public CompanyResponseDto getCompanies(int coreCompanyId, HttpServletRequest request) {
        return null;
    }

    @Override
    public int createCompany(CompanyRequestDto model, HttpServletRequest request) {
        return 0;
    }
}
