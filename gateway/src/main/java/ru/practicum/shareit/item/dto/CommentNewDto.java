package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentNewDto {
    @NotBlank(message = "Field 'text' is empty")
    private String text;
}
