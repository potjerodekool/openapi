package org.platonos.demo.mapper;

import org.platonos.demo.data.entity.User;
import org.some.models.UserDto;
import org.some.models.UserResponseDto;

public class UserMapper {

    public User from(final UserDto dto) {
        final User user = new User();
        user.setName(dto.getName());
        user.setBirthDate(dto.getBirthDate());
        return user;
    }

    public UserResponseDto toUserResponseDto(final User user) {
        return new UserResponseDto();
    }
}
