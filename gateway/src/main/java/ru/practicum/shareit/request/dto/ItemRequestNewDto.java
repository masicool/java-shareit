package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ItemRequestNewDto {
    @NotBlank(message = "Field 'Description' is empty or null")
    private String description; // текст запроса, содержащий описание требуемой вещи
}
