package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id; // уникальный идентификатор пользователя

    @NotBlank(message = "Field 'name' is empty")
    String name; // имя или логин пользователя

    @NotBlank(message = "Field 'email' is empty")
    @Email(message = "Wrong email format")
    String email; // адрес электронной почты пользователя
}
