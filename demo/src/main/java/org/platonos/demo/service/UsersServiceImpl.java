package org.platonos.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.platonos.demo.api.model.UserPatchDto;
import org.platonos.demo.api.model.UserRequestDto;
import org.platonos.demo.data.UserRepository;
import org.platonos.demo.data.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UsersServiceImpl implements UserService {

    private final UserRepository userRepository;

    public int create(final UserRequestDto userDto) {
        return userRepository.save(toUser(userDto)).getId();
    }

    public void replaceUserById(int userId, UserRequestDto userDto) {
        val user = toUser(userDto);
        user.setId(userId);
        userRepository.save(user);
    }

    private User toUser(final UserRequestDto userDto) {
        val user = new User();
        user.setName(userDto.getName());
        user.setBirthDate(userDto.getBirthDate());
        return user;
    }

    public Optional<User> findById(final int userId) {
        return userRepository.findById(userId);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public void deleteUserById(final int userId) {
        userRepository.deleteById(userId);
    }

    @Transactional
    public void patchUserById(final int userId, final UserPatchDto userpatchdto) {
        val userOptional = userRepository.findById(userId);

        userOptional.ifPresent(user -> {
            if (userpatchdto.getName().isPresent()) {
                user.setName(userpatchdto.getName().get());
            }

            if (userpatchdto.getBirthDate().isPresent()) {
                user.setBirthDate(userpatchdto.getBirthDate().get());
            }
        }
       );
    }

}
