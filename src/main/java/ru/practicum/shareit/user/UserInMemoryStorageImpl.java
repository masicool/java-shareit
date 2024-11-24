package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserInMemoryStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 0L;

    @Override
    public void createUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
    }

    @Override
    public void updateUser(User user) {
        users.replace(user.getId(), user);
    }

    @Override
    public void deleteUser(long userId) {
        users.remove(userId);
    }

    @Override
    public Optional<User> findUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    private long getNextId() {
        return ++id;
    }
}
