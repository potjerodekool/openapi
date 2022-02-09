package org.platonos.demo.mapper;

import org.platonos.demo.api.model.UserRequestDto;
import org.platonos.demo.api.model.UserResponseDto;
import org.platonos.demo.data.entity.User;

public class UserMapper {

    public User from(final UserRequestDto dto) {
        final User user = new User();
        user.setName(dto.getName());
        user.setBirthDate(dto.getBirthDate());
        return user;
    }

    public UserResponseDto toUserResponseDto(final User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setBirthDate(user.getBirthDate());
        return dto;
    }
}
