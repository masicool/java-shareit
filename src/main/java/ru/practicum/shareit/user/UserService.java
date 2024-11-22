package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.type.DuplicateException;
import ru.practicum.shareit.exception.type.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public UserDto createUser(User user) {
        checkDuplicateEmail(user.getEmail());
        userStorage.createUser(user);
        return UserMapper.toUserDto(user);
    }

    public UserDto updateUser(long userId, User user) {
        userStorage.findUserById(userId);
        checkDuplicateEmail(user.getEmail());
        user.setId(userId);
        userStorage.updateUser(user);
        return UserMapper.toUserDto(user);
    }

    public void deleteUser(long userId) {
        userStorage.deleteUser(userId);
    }

    public UserDto findUserById(long id) {
        Optional<User> user = userStorage.findUserById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("User with ID: " + id + " not found");
        }
        return UserMapper.toUserDto(user.get());
    }

    private void checkDuplicateEmail(String email) {
        if (userStorage.findUserByEmail(email).isPresent()) {
            throw new DuplicateException("Email: " + email + " already exist");
        }
    }
}
