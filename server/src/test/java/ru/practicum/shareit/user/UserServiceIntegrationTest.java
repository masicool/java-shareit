package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class UserServiceIntegrationTest {

    private final EntityManager em;
    private final UserService userService;

    @Test
    void createUserTest() {
        // добавим пользователя 1
        UserDto userDto1 = makeUserDto("user1", "email1@mail.ru");
        userDto1.setId(userService.createUser(userDto1).getId());
        TypedQuery<User> query = em.createQuery("from User", User.class);
        List<User> users = query.getResultList();
        assertThat(users.size(), equalTo(1));
        assertThat(users.getFirst().getId(), notNullValue());
        assertThat(users.getFirst().getName(), equalTo(userDto1.getName()));
        assertThat(users.getFirst().getEmail(), equalTo(userDto1.getEmail()));

        // добавим еще 2 пользователя
        UserDto userDto2 = makeUserDto("user2", "email2@mail.ru");
        userDto2.setId(userService.createUser(userDto2).getId());
        UserDto userDto3 = makeUserDto("user3", "email3@mail.ru");
        userDto3.setId(userService.createUser(userDto3).getId());
        query = em.createQuery("from User", User.class);
        users = query.getResultList();

        assertThat(users.size(), equalTo(3));
        assertThat(users.get(0).getId(), notNullValue());
        assertThat(users.get(1).getId(), notNullValue());
        assertThat(users.get(2).getId(), notNullValue());
        List<UserDto> userDtos = users.stream().map(UserMapper::toUserDto).toList();
        assertThat(userDtos, hasItem(userDto1));
        assertThat(userDtos, hasItem(userDto2));
        assertThat(userDtos, hasItem(userDto3));
    }

    @Test
    void updateUserTest() {
        UserDto userDto = makeUserDto("user1", "email1@mail.ru");
        userDto.setId(userService.createUser(userDto).getId());

        TypedQuery<User> query = em.createQuery("from User u where u.id = :id", User.class);

        User user = query
                .setParameter("id", userDto.getId())
                .getSingleResult();

        // меняем имя и email
        userDto.setName("user1_updated");
        userDto.setEmail("email1_updated@mail.ru");
        userService.updateUser(user.getId(), userDto);
        query = em.createQuery("select u from User u where u.id = :id", User.class);
        UserDto updatedUser = UserMapper.toUserDto(query.setParameter("id", userDto.getId()).getSingleResult());
        assertThat(UserMapper.toUserDto(user), equalTo(updatedUser));

        // имя и email = null, ничего не должно измениться
        userDto.setName(null);
        userDto.setEmail(null);
        userService.updateUser(user.getId(), userDto);
        query = em.createQuery("select u from User u where u.id = :id", User.class);
        updatedUser = UserMapper.toUserDto(query.setParameter("id", userDto.getId()).getSingleResult());
        assertThat(updatedUser.getName(), is(notNullValue()));
        assertThat(updatedUser.getEmail(), is(notNullValue()));
    }

    @Test
    void findAllUsersTest() {
        List<UserDto> sourceUsers = List.of(
                makeUserDto("user1", "email1@mail.ru"),
                makeUserDto("user2", "email2@mail.ru"),
                makeUserDto("user3", "email3@mail.ru")
        );

        for (UserDto userDto : sourceUsers) {
            User entity = UserMapper.toUser(userDto);
            em.persist(entity);
            userDto.setId(entity.getId());
        }

        em.flush();

        List<UserDto> targetUsers = userService.findAllUsers();

        assertThat(targetUsers, hasSize(sourceUsers.size()));

        for (UserDto sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem(sourceUser));
        }
    }

    @Test
    void deleteUserTest() {
        UserDto userDto = makeUserDto("user1", "email1@mail.ru");
        userDto.setId(userService.createUser(userDto).getId());

        userService.deleteUser(userDto.getId());

        TypedQuery<User> query = em.createQuery("from User u where u.id = :id", User.class);
        List<User> users = query.setParameter("id", userDto.getId()).getResultList();
        assertThat(users.size(), equalTo(0));
    }

    @Test
    void findUserByIdTest() {
        UserDto userDto = makeUserDto("user1", "email1@mail.ru");
        long userId = userService.createUser(userDto).getId();

        userDto = userService.findUserById(userId);

        TypedQuery<User> query = em.createQuery("from User u where u.id = :id", User.class);

        User foundUser = query
                .setParameter("id", userDto.getId())
                .getSingleResult();

        assertThat(UserMapper.toUserDto(foundUser), equalTo(userDto));
    }

    private UserDto makeUserDto(String name, String email) {
        return UserDto.builder().name(name).email(email).build();
    }
}