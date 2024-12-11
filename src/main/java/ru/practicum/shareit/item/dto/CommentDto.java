package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
public class CommentDto {
    private Long id; // уникальный идентификатор комментария
    private String text; // текст комментария
    private String authorName; // имя автора комментария
    private LocalDateTime created; // дата создания комментария
}
