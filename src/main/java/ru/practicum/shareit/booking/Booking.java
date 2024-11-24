package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-bookings.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
    Long id; // уникальный идентификатор бронирования
    Long start; // дата и время начала бронирования
    Long end; // дата и время конца бронирования
    Item item; // вещь, которую пользователь бронирует
    User booker; // пользователь, который осуществляет бронирование
    BookingStatus status; // статус бронирования
}
