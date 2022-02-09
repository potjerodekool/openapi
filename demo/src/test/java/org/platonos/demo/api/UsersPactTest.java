package org.platonos.demo.api;

import au.com.dius.pact.provider.junitsupport.Provider;
import org.junit.jupiter.api.BeforeEach;
import org.platonos.demo.api.model.UserResponseDto;
import org.platonos.demo.api.model.UsersResponseDto;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Provider("usersService")
public class UsersPactTest extends AbstractPactTest {

    @MockBean
    private UsersDelegate usersDelegate;

    @BeforeEach
    void beforeEach() {
        final UserResponseDto userDto = new UserResponseDto();
        userDto.setId(1);
        userDto.setName("test");
        userDto.setBirthDate(LocalDate.of(1970, 1, 1));

        when(usersDelegate.createUser(any(), any()))
                .thenReturn(1);

        when(usersDelegate.getUsers(any()))
                .thenReturn(
                        new UsersResponseDto()
                                .total(1)
                                .records(List.of(userDto))
                );
/*
        when(usersDelegate.getUserById(eq(1), any()))
                .thenReturn(Optional.of(userDto));
        */
    }

}
