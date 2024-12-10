package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.type.NotFoundException;
import ru.practicum.shareit.exception.type.WrongRequestException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookingService {
    public final ItemRepository itemRepository;
    public final UserRepository userRepository;
    public final BookingRepository bookingRepository;

    public BookingDto createBooking(long bookerId, BookingDto bookingDto) {
        User booker = getUser(bookerId);
        Item item = getItem(bookingDto.getItemId());

        if (bookingDto.getStart().equals(bookingDto.getEnd()))
            throw new WrongRequestException("Date end equals date start");

        if (!item.getAvailable())
            throw new WrongRequestException("Item with ID: " + item.getId() + " is not available");

        List<Booking> bookings = bookingRepository.findAllWithIntersectionDates(item.getId(),
                Set.of(BookingStatus.WAITING, BookingStatus.APPROVED), bookingDto.getStart(), bookingDto.getEnd());

        if (!bookings.isEmpty()) throw new WrongRequestException("Interval intersects with the existing ones");

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker, BookingStatus.WAITING);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public BookingDto updateBooking(long ownerId, long bookingId, boolean approved) {
        Booking booking = getBooking(bookingId);
        Item item = getItem(booking.getItem().getId());

        if (item.getOwner().getId() != ownerId)
            throw new WrongRequestException("Item does not belong to the owner with ID:" + ownerId);

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public List<BookingDto> findBookingsByBookerIdAndState(long userId, BookingState state) {
        getUser(userId);
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findAllByBookerIdAndStatusAndEndIsAfterOrderByStartDesc(userId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case PAST -> bookingRepository.findAllByBookerIdAndStatusAndEndIsBeforeOrderByStartDesc(userId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllByBookerIdAndStatusAndStartIsAfterOrderByStartDesc(userId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case WAITING -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        };

        return BookingMapper.toBookingDto(bookings);
    }

    public List<BookingDto> findBookingsByOwnerIdAndSate(long ownerId, BookingState state) {
        getUser(ownerId);
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
            case CURRENT -> bookingRepository.findAllByItemOwnerIdAndStatusAndEndIsAfterOrderByStartDesc(ownerId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case PAST -> bookingRepository.findAllByItemOwnerIdAndStatusAndEndIsBeforeOrderByStartDesc(ownerId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllByItemOwnerIdAndStatusAndStartIsAfterOrderByStartDesc(ownerId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case WAITING ->
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
        };

        return BookingMapper.toBookingDto(bookings);
    }

    public BookingDto findBookingByIdAndBookerIdOrOwnerId(long bookingId, long userId) {
        Booking booking = getBooking(bookingId);
        long bookerId = booking.getBooker().getId();
        long ownerId = booking.getItem().getOwner().getId();

        if (bookerId != userId && ownerId != userId)
            throw new NotFoundException("Booking with ID: " + bookingId + " for user with ID: " + userId + " not found");

        return BookingMapper.toBookingDto(booking);
    }

    private Booking getBooking(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with ID: " + bookingId + " not found"));

    }

    private Item getItem(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item with" +
                " ID: " + itemId + " not found"));
    }

    private User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID: " + userId + " not found"));
    }
}
