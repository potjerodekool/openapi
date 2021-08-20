package org.platonos.demo.api;

import org.platonos.demo.api.model.AddressDto;
import org.platonos.demo.api.model.AddressPatchDto;
import org.platonos.demo.api.model.AddressResponseDto;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Component
public class AddressesDelegateImpl implements AddressesDelegate {

    @Override
    public int createAddressForUser(int userId, AddressDto body, HttpServletRequest httpServletRequest) {
        return 1;
    }

    @Override
    public List<AddressResponseDto> getUserAddresses(int userId, HttpServletRequest httpServletRequest) {
        return List.of();
    }

    @Override
    public Optional<AddressResponseDto> getAddressOfUserById(int userId, int addressId, HttpServletRequest httpServletRequest) {
        return Optional.empty();
    }

    @Override
    public void patchAddressOfUser(int userId, int addressId, AddressPatchDto body, HttpServletRequest httpServletRequest) {

    }
}
