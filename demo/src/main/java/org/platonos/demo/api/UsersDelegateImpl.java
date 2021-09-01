package org.platonos.demo.api;

import lombok.RequiredArgsConstructor;
import org.platonos.demo.api.model.UserDto;
import org.platonos.demo.api.model.UserPatchDto;
import org.platonos.demo.api.model.UserResponseDto;
import org.platonos.demo.api.model.UsersResponseDto;
import org.platonos.demo.data.entity.User;
import org.platonos.demo.mapper.UserMapper;
import org.platonos.demo.service.UsersServiceImpl;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class UsersDelegateImpl implements UsersDelegate {

    private final UsersServiceImpl usersService;
    private final UserMapper mapper = new UserMapper();

    @Override
    public int createUser(final UserDto user,
                          final HttpServletRequest request) {
        return usersService.create(user);
    }

    @Override
    public Optional<UserResponseDto> getUserById(final int userId,
                                                 final HttpServletRequest request) {
        return usersService.findById(userId)
                .map(mapper::toUserResponseDto);
    }

    @Override
    public UsersResponseDto getUsers(final HttpServletRequest request) {
        final List<User> users = usersService.getUsers();
        final List<UserResponseDto> dtoList = users.stream()
                .map(mapper::toUserResponseDto)
                .collect(Collectors.toList());
        return new UsersResponseDto()
                .total(dtoList.size())
                .records(dtoList);
    }

    @Override
    public void replaceUserById(final int userId,
                                final UserDto userDto,
                                final HttpServletRequest httpServletRequest) {
        usersService.replaceUserById(userId, userDto);
    }

    @Override
    public void patchUserById(final int userId,
                              final UserPatchDto userpatchdto,
                              final HttpServletRequest request) {
        usersService.patchUserById(userId, userpatchdto);
    }

    @Override
    public void deleteUserById(final int userId,
                               final HttpServletRequest request) {
        usersService.deleteUserById(userId);
    }

}
