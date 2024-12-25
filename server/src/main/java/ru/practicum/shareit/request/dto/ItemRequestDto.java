package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDto {
    private Long id; // уникальный идентификатор запроса

    @NotBlank(message = "Field 'Description' is empty or null")
    private String description; // текст запроса, содержащий описание требуемой вещи

    private LocalDateTime created; // дата и время создания запроса

    private List<ItemForResponseDto> items; // список ответов на запрос - список вещей
}
