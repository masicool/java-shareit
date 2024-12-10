package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Setter
@Getter
public class ItemDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id; // id вещи

    @NotBlank(message = "Field 'Name' is empty")
    private String name; // краткое название

    @NotBlank(message = "Field 'Description' is empty")
    private String description; // развёрнутое описание

    @NotNull(message = "Field 'available' is null")
    private Boolean available; // статус о том, доступна или нет вещь для аренды

    private Long request; // id соответствующего запроса на вещь

    private LocalDateTime lastBooking; // дата последнего бронирования

    private LocalDateTime nextBooking; // дата следующего бронирования

    private List<CommentDto> comments;
}
