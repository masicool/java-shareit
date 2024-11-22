package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class Item {
    private Long id; // уникальный идентификатор вещи

    @NotBlank(message = "Field 'Name' is empty")
    private String name; // краткое название

    @NotBlank(message = "Field 'Description' is empty")
    private String description; // развёрнутое описание

    @NotNull(message = "Field 'available' is null")
    private Boolean available; // статус о том, доступна или нет вещь для аренды

    private User owner; // владелец вещи
    private ItemRequest request; // соответствующий запроса на вещь
}
