package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus());
    }

    public static List<BookingDto> toBookingDto(Iterable<Booking> bookings) {
        List<BookingDto> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(toBookingDto(booking));
        }
        return dtos;
    }

    public static Booking toBooking(BookingNewDto bookingNewDto, Item item, User booker, BookingStatus status) {
        Booking booking = new Booking();
        booking.setStart(bookingNewDto.getStart());
        booking.setEnd(bookingNewDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        return booking;
    }
}
