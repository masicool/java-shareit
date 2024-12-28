package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ItemForResponseDto {
    private Long id; // id вещи
    private String name; // краткое название
    private Long ownerId;
}
