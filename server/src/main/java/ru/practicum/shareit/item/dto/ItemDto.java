package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id; // id вещи
    private String name; // краткое название
    private String description; // развёрнутое описание
    private Boolean available; // статус о том, доступна или нет вещь для аренды
    private Long requestId; // id соответствующего запроса на вещь
    private LocalDateTime lastBooking; // дата последнего бронирования
    private LocalDateTime nextBooking; // дата следующего бронирования
    private List<CommentDto> comments;
}
