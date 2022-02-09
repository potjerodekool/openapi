package org.platonos.demo.api;

import org.platonos.demo.api.model.AddressPatchDto;
import org.platonos.demo.api.model.AddressRequestDto;
import org.platonos.demo.api.model.AddressResponseDto;
import org.platonos.demo.api.model.AddressesResponseDto;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AddressesDelegateImpl implements UsersAddressesDelegate {

    @Override
    public AddressesResponseDto getUserAddresses(int userId, HttpServletRequest request) {
        return null;
    }

    @Override
    public int createAddressForUser(int userId, AddressRequestDto model, HttpServletRequest request) {
        return 0;
    }

    @Override
    public AddressResponseDto getAddressOfUserById(int userId, int addressId, HttpServletRequest request) {
        return null;
    }

    @Override
    public void patchAddressOfUser(int userId, int addressId, AddressPatchDto model, HttpServletRequest request) {

    }
}
