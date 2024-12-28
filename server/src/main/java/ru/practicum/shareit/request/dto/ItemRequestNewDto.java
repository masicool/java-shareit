package ru.practicum.shareit.request.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestNewDto {
    private String description; // текст запроса, содержащий описание требуемой вещи
}
