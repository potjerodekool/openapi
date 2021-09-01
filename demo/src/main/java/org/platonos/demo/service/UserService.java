package org.platonos.demo.service;

import lombok.val;
import org.platonos.demo.api.model.UserDto;
import org.platonos.demo.api.model.UserPatchDto;
import org.platonos.demo.data.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

interface UserService {

    int create(final UserDto userDto);

    void replaceUserById(int userId, UserDto userDto);

    Optional<User> findById(final int userId);

    List<User> getUsers();

    void deleteUserById(final int userId);

    void patchUserById(final int userId, final UserPatchDto userpatchdto);
}