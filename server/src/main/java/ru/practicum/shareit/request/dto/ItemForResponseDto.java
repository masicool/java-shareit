package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ItemForResponseDto {
    @NotNull(message = "Field 'id' is empty")
    private Long id; // id вещи

    @NotBlank(message = "Field 'Name' is empty")
    private String name; // краткое название

    @NotNull(message = "Field ' ownerId' is empty")
    private Long ownerId;
}
