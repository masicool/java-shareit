package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {
    private Long id; // уникальный идентификатор пользователя

    @NotBlank(message = "Field 'name' is empty")
    private String name; // имя или логин пользователя

    @NotBlank(message = "Field 'email' is empty")
    @Email(message = "Wrong email format")
    private String email; // адрес электронной почты пользователя
}
