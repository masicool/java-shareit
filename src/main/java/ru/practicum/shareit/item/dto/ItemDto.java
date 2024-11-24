package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id; // id вещи

    @NotBlank(message = "Field 'Name' is empty")
    String name; // краткое название

    @NotBlank(message = "Field 'Description' is empty")
    String description; // развёрнутое описание

    @NotNull(message = "Field 'available' is null")
    Boolean available; // статус о том, доступна или нет вещь для аренды

    Long request; // id соответствующего запроса на вещь
}
