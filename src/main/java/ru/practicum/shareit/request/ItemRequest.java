package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    private Long id; // уникальный идентификатор запроса
    private String description; // текст запроса, содержащий описание требуемой вещи
    private User requestor; // пользователь, создавший запрос
    private Long created; // дата и время создания запроса
}
