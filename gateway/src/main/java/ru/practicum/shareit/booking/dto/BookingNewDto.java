package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.valid.DateTimeValidAnnotation;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@DateTimeValidAnnotation
public class BookingNewDto {
    private long itemId;

    @FutureOrPresent(message = "Date 'start' is past")
    private LocalDateTime start;

    @Future(message = "Date 'end' is past")
    private LocalDateTime end;
}
