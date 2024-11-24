package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.type.DuplicateException;
import ru.practicum.shareit.exception.type.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public UserDto createUser(UserDto userDto) {
        checkDuplicateEmail(userDto.getEmail());
        User user = UserMapper.toUser(userDto);
        userStorage.createUser(user);
        return UserMapper.toUserDto(user);
    }

    public UserDto updateUser(long userId, UserDto userDto) {
        User foundUser = userStorage.findUserById(userId).orElseThrow(() -> new NotFoundException("User with ID: " + userId + " not found"));
        checkDuplicateEmail(userDto.getEmail());
        userDto.setId(userId);

        if (userDto.getName() != null) foundUser.setName(userDto.getName());
        if (userDto.getEmail() != null) foundUser.setEmail(userDto.getEmail());

        userStorage.updateUser(foundUser);
        return UserMapper.toUserDto(foundUser);
    }

    public void deleteUser(long userId) {
        userStorage.deleteUser(userId);
    }

    public UserDto findUserById(long id) {
        User user = userStorage.findUserById(id).orElseThrow(() -> new NotFoundException("User with ID: " + id + " not found"));
        return UserMapper.toUserDto(user);
    }

    private void checkDuplicateEmail(String email) {
        if (userStorage.findUserByEmail(email).isPresent()) {
            throw new DuplicateException("Email: " + email + " already exist");
        }
    }
}
