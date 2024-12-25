package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Builder
public class BookingDto {
    private Long id;

    @Future(message = "Date start should not be in the past")
    @NotNull(message = "Date start should not be null")
    private LocalDateTime start; // дата и время начала бронирования

    @Future(message = "Date end should not be in the past")
    @NotNull(message = "Date end should not be null")
    private LocalDateTime end; // дата и время конца бронирования

    private Item item; // вещь, которую пользователь бронирует

    private User booker; // пользователь, который осуществляет бронирование

    private BookingStatus status; // статус бронирования
}
