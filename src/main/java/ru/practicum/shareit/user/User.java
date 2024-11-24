package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * TODO Sprint add-controllers.
 */
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    Long id; // уникальный идентификатор пользователя
    String name; // имя или логин пользователя
    String email; // адрес электронной почты пользователя
}
