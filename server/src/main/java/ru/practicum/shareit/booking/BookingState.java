package ru.practicum.shareit.booking;

public enum BookingState {
    ALL, // все бронирования
    CURRENT, // текущие бронирования
    PAST, // завершенные бронирования
    FUTURE, // будущие бронирования
    WAITING, // ожидающие бронирования
    REJECTED // отклоненные бронирования
}
