package org.platonos.demo.api;

import au.com.dius.pact.provider.junitsupport.Provider;
import org.junit.jupiter.api.BeforeEach;
import org.platonos.demo.api.model.AddressDto;
import org.platonos.demo.api.model.AddressResponseDto;
import org.platonos.demo.api.model.UserResponseDto;
import org.platonos.demo.api.model.UsersResponseDto;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

//@Provider("users/addressesService")
public class AddressPactTest extends AbstractPactTest {

    @MockBean
    private AddressesDelegate addressesDelegate;

    @BeforeEach
    void beforeEach() {
        AddressResponseDto addressResponseDto = new AddressResponseDto()
                .id(1)
                .streetName("test");

        when(addressesDelegate.createAddressForUser(
                eq(1),
                any(),
                any()
        )).thenReturn(1);

        when(addressesDelegate.getUserAddresses(
                eq(1),
                any()
        )).thenReturn(List.of(addressResponseDto));

        when(addressesDelegate.getAddressOfUserById(
                eq(1),
                eq(1),
                any()
        )).thenReturn(Optional.of(addressResponseDto));

    }

}
