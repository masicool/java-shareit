package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequest {
    Long id; // уникальный идентификатор запроса
    String description; // текст запроса, содержащий описание требуемой вещи
    User requestor; // пользователь, создавший запрос
    Long created; // дата и время создания запроса
}
