package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
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
                                    @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader(USER_ID_HEADER) long ownerId,
                                    @PathVariable long bookingId,
                                    @RequestParam boolean approved) {
        return bookingService.updateBooking(ownerId, bookingId, approved);
    }
}
