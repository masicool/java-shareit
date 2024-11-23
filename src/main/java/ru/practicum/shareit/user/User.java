package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@AllArgsConstructor
@Data
public class User {
    private Long id; // уникальный идентификатор пользователя
    private String name; // имя или логин пользователя
    private String email; // адрес электронной почты пользователя
}
