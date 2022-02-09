package org.platonos.demo.service;

import org.platonos.demo.api.model.UserPatchDto;
import org.platonos.demo.api.model.UserRequestDto;
import org.platonos.demo.data.entity.User;

import java.util.List;
import java.util.Optional;

interface UserService {

    int create(final UserRequestDto userDto);

    void replaceUserById(int userId, UserRequestDto userDto);

    Optional<User> findById(final int userId);

    List<User> getUsers();

    void deleteUserById(final int userId);

    void patchUserById(final int userId, final UserPatchDto userpatchdto);
}