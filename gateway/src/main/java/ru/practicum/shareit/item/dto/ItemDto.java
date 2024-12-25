package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ItemDto {
    @NotBlank(message = "Field 'Name' is empty")
    private String name; // краткое название

    @NotBlank(message = "Field 'Description' is empty")
    private String description; // развёрнутое описание

    @NotNull(message = "Field 'available' is null")
    private Boolean available; // статус о том, доступна или нет вещь для аренды

    private Long requestId; // id соответствующего запроса на вещь
}
