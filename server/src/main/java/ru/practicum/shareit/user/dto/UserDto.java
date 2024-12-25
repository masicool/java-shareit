package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id; // уникальный идентификатор пользователя

    @NotBlank(message = "Field 'name' is empty")
    private String name; // имя или логин пользователя

    @NotBlank(message = "Field 'email' is empty")
    @Email(message = "Wrong email format")
    private String email; // адрес электронной почты пользователя
}
