package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.type.NotFoundException;
import ru.practicum.shareit.exception.type.WrongRequestException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class BookingServiceIntegrationTest {
    private final EntityManager em;
    private final BookingService bookingService;

    private User user;
    private BookingDto bookingDto;
    private BookingNewDto bookingNewDto;
    private Item item;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .name("user1")
                .email("email1@mail.ru")
                .build();

        em.persist(user);
        em.flush();

        item = Item.builder()
                .name("name1")
                .description("description1")
                .available(true)
                .owner(user)
                .comments(List.of())
                .build();

        em.persist(item);
        em.flush();

        bookingNewDto = BookingNewDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(item.getId())
                .build();

        bookingDto = BookingDto.builder()
                .start(bookingNewDto.getStart())
                .end(bookingNewDto.getEnd())
                .status(BookingStatus.WAITING)
                .item(item)
                .booker(user)
                .build();
    }

    @Test
    void createBookingTest() {
        BookingDto createdBookingDto = bookingService.createBooking(user.getId(), bookingNewDto);
        bookingDto.setId(createdBookingDto.getId());

        TypedQuery<Booking> query = em.createQuery("from Booking", Booking.class);
        List<BookingDto> bookingDtos = query.getResultList().stream().map(BookingMapper::toBookingDto).toList();

        assertThat(bookingDtos.size(), equalTo(1));
        assertThat(bookingDtos.getFirst().getId(), notNullValue());
        assertThat(bookingDtos, hasItem(bookingDto));
    }

    @Test
    void createBookingWithItemIsNotExistOrNullTest() {
        bookingNewDto.setItemId(Long.MAX_VALUE);
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(user.getId(), bookingNewDto));

        bookingNewDto.setItemId(null);
        assertThrows(NullPointerException.class, () -> bookingService.createBooking(user.getId(), bookingNewDto));
    }

    @Test
    void createBookingWithItemIsNotAvailableTest() {
        item.setAvailable(false);
        assertThrows(WrongRequestException.class, () -> bookingService.createBooking(user.getId(), bookingNewDto));
    }

    @Test
    void createBookingWithItemIsIntersectionDatesTest() {
        long bookingId = bookingService.createBooking(user.getId(), bookingNewDto).getId();
        bookingService.updateBooking(user.getId(), bookingId, true);
        assertThrows(WrongRequestException.class, () -> bookingService.createBooking(user.getId(), bookingNewDto));
    }

    @Test
    void updateBookingWhenApprovedTest() {
        long bookingId = bookingService.createBooking(user.getId(), bookingNewDto).getId();

        bookingService.updateBooking(user.getId(), bookingId, true);

        TypedQuery<Booking> query = em.createQuery("select b from Booking b where b.id = :id", Booking.class);
        BookingDto foundBookingDto = BookingMapper.toBookingDto(query.setParameter("id", bookingId).getSingleResult());

        bookingDto.setStatus(BookingStatus.APPROVED);
        bookingDto.setId(bookingId);
        assertThat(foundBookingDto, equalTo(bookingDto));
    }

    @Test
    void updateBookingWhenRejectedTest() {
        long bookingId = bookingService.createBooking(user.getId(), bookingNewDto).getId();

        bookingService.updateBooking(user.getId(), bookingId, false);

        TypedQuery<Booking> query = em.createQuery("select b from Booking b where b.id = :id", Booking.class);
        BookingDto foundBookingDto = BookingMapper.toBookingDto(query.setParameter("id", bookingId).getSingleResult());

        bookingDto.setStatus(BookingStatus.REJECTED);
        bookingDto.setId(bookingId);
        assertThat(foundBookingDto, equalTo(bookingDto));
    }

    @Test
    void updateBookingWithWrongOwnerTest() {
        User user2 = User.builder()
                .name("user2")
                .email("email2@mail.ru")
                .build();

        em.persist(user2);
        em.flush();

        long bookingId = bookingService.createBooking(user.getId(), bookingNewDto).getId();

        assertThrows(WrongRequestException.class, () -> bookingService.updateBooking(user2.getId(), bookingId, true));
    }

    @Test
    void updateBookingWithWrongBookingStatusTest() {
        long bookingId = bookingService.createBooking(user.getId(), bookingNewDto).getId();
        bookingService.updateBooking(user.getId(), bookingId, true);

        assertThrows(WrongRequestException.class, () -> bookingService.updateBooking(user.getId(), bookingId, true));
    }

    @Test
    void findBookingsByBookerIdAndStateTest() {
        bookingService.createBooking(user.getId(), bookingNewDto);

        // Booking state - ALL
        List<BookingDto> bookingDtos = bookingService.findBookingsByBookerIdAndState(user.getId(), BookingState.ALL);
        TypedQuery<Booking> query = em.createQuery("from Booking b where b.booker.id = :id", Booking.class);
        List<BookingDto> foundBookingDtos = query.setParameter("id", user.getId())
                .getResultList().stream().map(BookingMapper::toBookingDto).toList();
        assertThat(bookingDtos.size(), equalTo(1));
        assertThat(bookingDtos.getFirst().getId(), notNullValue());
        assertThat(bookingDtos, equalTo(foundBookingDtos));

        // Booking state - CURRENT
        bookingDtos = bookingService.findBookingsByBookerIdAndState(user.getId(), BookingState.CURRENT);
        query = em.createQuery("from Booking b where b.booker.id = :id", Booking.class);
        foundBookingDtos = query.setParameter("id", user.getId())
                .getResultList().stream().map(BookingMapper::toBookingDto).toList();
        assertThat(bookingDtos.size(), equalTo(0));

        // Booking state - PAST
        bookingDtos = bookingService.findBookingsByBookerIdAndState(user.getId(), BookingState.PAST);
        query = em.createQuery("from Booking b where b.booker.id = :id", Booking.class);
        foundBookingDtos = query.setParameter("id", user.getId())
                .getResultList().stream().map(BookingMapper::toBookingDto).toList();
        assertThat(bookingDtos.size(), equalTo(0));

        // Booking state - FUTURE
        bookingDtos = bookingService.findBookingsByBookerIdAndState(user.getId(), BookingState.FUTURE);
        query = em.createQuery("from Booking b where b.booker.id = :id", Booking.class);
        foundBookingDtos = query.setParameter("id", user.getId())
                .getResultList().stream().map(BookingMapper::toBookingDto).toList();
        assertThat(bookingDtos.size(), equalTo(0));

        // Booking state - REJECTED
        bookingDtos = bookingService.findBookingsByBookerIdAndState(user.getId(), BookingState.REJECTED);
        query = em.createQuery("from Booking b where b.booker.id = :id", Booking.class);
        foundBookingDtos = query.setParameter("id", user.getId())
                .getResultList().stream().map(BookingMapper::toBookingDto).toList();
        assertThat(bookingDtos.size(), equalTo(0));

        // Booking state - WAITING
        bookingDtos = bookingService.findBookingsByBookerIdAndState(user.getId(), BookingState.WAITING);
        query = em.createQuery("from Booking b where b.booker.id = :id", Booking.class);
        foundBookingDtos = query.setParameter("id", user.getId())
                .getResultList().stream().map(BookingMapper::toBookingDto).toList();
        assertThat(bookingDtos.size(), equalTo(1));
        assertThat(bookingDtos.getFirst().getId(), notNullValue());
        assertThat(bookingDtos, equalTo(foundBookingDtos));
    }

    @Test
    void findBookingsByOwnerIdAndSateTest() {
        bookingService.createBooking(user.getId(), bookingNewDto);

        // Booking state - ALL
        List<BookingDto> bookingDtos = bookingService.findBookingsByOwnerIdAndSate(user.getId(), BookingState.ALL);
        TypedQuery<Booking> query = em.createQuery("from Booking b where b.item.owner.id = :id", Booking.class);
        List<BookingDto> foundBookingDtos = query.setParameter("id", user.getId())
                .getResultList().stream().map(BookingMapper::toBookingDto).toList();
        assertThat(bookingDtos.size(), equalTo(1));
        assertThat(bookingDtos.getFirst().getId(), notNullValue());
        assertThat(bookingDtos, equalTo(foundBookingDtos));

        // Booking state - PAST
        bookingDtos = bookingService.findBookingsByOwnerIdAndSate(user.getId(), BookingState.PAST);
        query = em.createQuery("from Booking b where b.item.owner.id = :id", Booking.class);
        foundBookingDtos = query.setParameter("id", user.getId())
                .getResultList().stream().map(BookingMapper::toBookingDto).toList();
        assertThat(bookingDtos.size(), equalTo(0));

        // Booking state - CURRENT
        bookingDtos = bookingService.findBookingsByOwnerIdAndSate(user.getId(), BookingState.CURRENT);
        query = em.createQuery("from Booking b where b.item.owner.id = :id", Booking.class);
        foundBookingDtos = query.setParameter("id", user.getId())
                .getResultList().stream().map(BookingMapper::toBookingDto).toList();
        assertThat(bookingDtos.size(), equalTo(0));

        // Booking state - FUTURE
        bookingDtos = bookingService.findBookingsByOwnerIdAndSate(user.getId(), BookingState.FUTURE);
        query = em.createQuery("from Booking b where b.item.owner.id = :id", Booking.class);
        foundBookingDtos = query.setParameter("id", user.getId())
                .getResultList().stream().map(BookingMapper::toBookingDto).toList();
        assertThat(bookingDtos.size(), equalTo(0));

        // Booking state - REJECTED
        bookingDtos = bookingService.findBookingsByOwnerIdAndSate(user.getId(), BookingState.REJECTED);
        query = em.createQuery("from Booking b where b.item.owner.id = :id", Booking.class);
        foundBookingDtos = query.setParameter("id", user.getId())
                .getResultList().stream().map(BookingMapper::toBookingDto).toList();
        assertThat(bookingDtos.size(), equalTo(0));

        // Booking state - WAITING
        bookingDtos = bookingService.findBookingsByOwnerIdAndSate(user.getId(), BookingState.WAITING);
        query = em.createQuery("from Booking b where b.item.owner.id = :id", Booking.class);
        foundBookingDtos = query.setParameter("id", user.getId())
                .getResultList().stream().map(BookingMapper::toBookingDto).toList();
        assertThat(bookingDtos.size(), equalTo(1));
        assertThat(bookingDtos.getFirst().getId(), notNullValue());
        assertThat(bookingDtos, equalTo(foundBookingDtos));
    }

    @Test
    void findBookingByIdAndBookerIdOrOwnerIdTest() {
        long bookingId = bookingService.createBooking(user.getId(), bookingNewDto).getId();

        bookingDto = bookingService.findBookingByIdAndBookerIdOrOwnerId(bookingId, user.getId());

        TypedQuery<Booking> query = em.createQuery("from Booking b where b.id = :id " +
                "and b.item.owner.id = :id or b.booker.id = :id", Booking.class);
        BookingDto foundBookingDto = BookingMapper.toBookingDto(query.setParameter("id", user.getId())
                .getSingleResult());

        assertThat(bookingDto, equalTo(foundBookingDto));
    }
}
