package org.platonos.demo.service;

import org.platonos.demo.data.entity.User;
import org.some.models.UserDto;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

    public int create(UserDto user) {
        return 2132;
    }

    public User findById(int userId) {
        return null;
    }
}
