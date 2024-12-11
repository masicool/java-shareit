package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@AllArgsConstructor
@Data
public class BookingNewDto {
    @Future(message = "Date start should not be in the past")
    @NotNull(message = "Date start should not be null")
    private LocalDateTime start; // дата и время начала бронирования

    @Future(message = "Date end should not be in the past")
    @NotNull(message = "Date end should not be null")
    private LocalDateTime end; // дата и время конца бронирования

    @NotNull(message = "Item ID should not be null")
    private Long itemId; // id вещи
}
