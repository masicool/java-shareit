package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // поиск бронирований, у которых есть пересечение времени бронирования
    @Query("select b from Booking as b " +
            "where b.item.id = :itemId and b.status in (:statuses) and :startDate <= b.end and :endDate >= b.start")
    List<Booking> findAllWithIntersectionDates(long itemId, Set<BookingStatus> statuses, LocalDateTime startDate,
                                               LocalDateTime endDate);

    List<Booking> findAllByItemIdAndBookerId(long itemId, long bookerId);

    // запросные методы для пользователя
    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findAllByBookerIdAndStatusAndEndIsAfterOrderByStartDesc(long bookerId, BookingStatus status, LocalDateTime dt);

    List<Booking> findAllByBookerIdAndStatusAndEndIsBeforeOrderByStartDesc(long bookerId, BookingStatus status, LocalDateTime dt);

    List<Booking> findAllByBookerIdAndStatusAndStartIsAfterOrderByStartDesc(long bookerId, BookingStatus status, LocalDateTime dt);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, BookingStatus status);

    // запросные методы для владельца вещи
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId);

    List<Booking> findAllByItemOwnerIdAndStatusAndEndIsAfterOrderByStartDesc(long ownerId, BookingStatus status, LocalDateTime dt);

    List<Booking> findAllByItemOwnerIdAndStatusAndEndIsBeforeOrderByStartDesc(long ownerId, BookingStatus status, LocalDateTime dt);

    List<Booking> findAllByItemOwnerIdAndStatusAndStartIsAfterOrderByStartDesc(long ownerId, BookingStatus status, LocalDateTime dt);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long ownerId, BookingStatus status);
}
