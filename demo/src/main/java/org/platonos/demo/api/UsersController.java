package org.platonos.demo.api;

import io.swagger.annotations.Api;
import org.platonos.demo.data.entity.User;
import org.platonos.demo.mapper.UserMapper;
import org.platonos.demo.service.UsersService;
import org.some.api.ApiUtils;
import org.some.api.UsersApi;
import org.some.models.UserDto;

import org.some.models.UserResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Api
@RestController
public class UsersController implements UsersApi {

    private final UsersService usersService;

    private final UserMapper mapper = new UserMapper();

    public UsersController(final UsersService usersService) {
        this.usersService = usersService;
    }

    @Override
    public ResponseEntity<Void> create(final UserDto user,
                                       final HttpServletRequest request) {
        return ResponseEntity.created(ApiUtils.createLocation(request, usersService.create(user))).build();
    }

    @Override
    public ResponseEntity<UserResponseDto> get(final int userId, final HttpServletRequest request) {
        final User user = usersService.findById(userId);

        if (user == null) {
            return ResponseEntity.notFound().build();
        } else {
            final UserResponseDto dto = mapper.toUserResponseDto(user);
            return ResponseEntity.ok(dto);
        }
    }
}
