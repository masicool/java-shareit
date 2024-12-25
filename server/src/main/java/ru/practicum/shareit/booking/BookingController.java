package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@RequestHeader(USER_ID_HEADER) long userId,
                                      @PathVariable long bookingId) {
        return bookingService.findBookingByIdAndBookerIdOrOwnerId(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findBookingsByBookerIdAndState(@RequestHeader(USER_ID_HEADER) long bookerId,
                                                           @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.findBookingsByBookerIdAndState(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> findBookingsByOwnerIdAndState(@RequestHeader(USER_ID_HEADER) long ownerId,
                                                          @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.findBookingsByOwnerIdAndSate(ownerId, state);
    }

    @PostMapping
    public BookingDto createBooking(@RequestHeader(USER_ID_HEADER) long bookerId,
                                    @RequestBody BookingNewDto bookingNewDto) {
        return bookingService.createBooking(bookerId, bookingNewDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader(USER_ID_HEADER) long ownerId,
                                    @PathVariable long bookingId,
                                    @RequestParam boolean approved) {
        return bookingService.updateBooking(ownerId, bookingId, approved);
    }
}
