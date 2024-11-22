package ru.practicum.shareit.user;

import java.util.Optional;

public interface UserStorage {
    void createUser(User user);

    void updateUser(User user);

    void deleteUser(long userId);

    Optional<User> findUserById(long id);

    Optional<User> findUserByEmail(String email);
}
