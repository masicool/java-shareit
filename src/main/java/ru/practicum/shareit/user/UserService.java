package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.type.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Transactional
    public UserDto updateUser(long userId, UserDto userDto) {
        User foundUser = findUser(userId);

        if (userDto.getName() != null) foundUser.setName(userDto.getName());
        if (userDto.getEmail() != null) foundUser.setEmail(userDto.getEmail());

        userRepository.save(foundUser);
        return UserMapper.toUserDto(foundUser);
    }

    @Transactional
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

    public UserDto findUserById(long userId) {
        User user = findUser(userId);
        return UserMapper.toUserDto(user);
    }

    private User findUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID: " + userId + " not found"));
    }
}
