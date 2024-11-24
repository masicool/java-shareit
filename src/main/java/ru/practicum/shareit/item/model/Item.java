package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    Long id; // уникальный идентификатор вещи
    String name; // краткое название
    String description; // развёрнутое описание
    Boolean available; // статус о том, доступна или нет вещь для аренды
    User owner; // владелец вещи
    ItemRequest request; // соответствующий запроса на вещь
}
