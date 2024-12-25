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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class BookingServiceIntegrationTest {
    private final EntityManager em;
    private final BookingService bookingService;

    private User user;
    private BookingDto bookingDto;
    private BookingNewDto bookingNewDto;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .name("user1")
                .email("email1@mail.ru")
                .build();

        em.persist(user);
        em.flush();

        Item item = Item.builder()
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
    void updateBookingTest() {
        long bookingId = bookingService.createBooking(user.getId(), bookingNewDto).getId();
        bookingDto.setStatus(BookingStatus.APPROVED);
        bookingDto.setId(bookingId);

        bookingService.updateBooking(user.getId(), bookingId, true);

        TypedQuery<Booking> query = em.createQuery("select b from Booking b where b.id = :id", Booking.class);
        BookingDto foundBookingDto = BookingMapper.toBookingDto(query.setParameter("id", bookingId).getSingleResult());

        assertThat(foundBookingDto, equalTo(bookingDto));
    }

    @Test
    void findBookingsByBookerIdAndStateTest() {
        bookingService.createBooking(user.getId(), bookingNewDto);

        List<BookingDto> bookingDtos = bookingService.findBookingsByBookerIdAndState(user.getId(), BookingState.ALL);

        TypedQuery<Booking> query = em.createQuery("from Booking b where b.booker.id = :id", Booking.class);
        List<BookingDto> foundBookingDtos = query.setParameter("id", user.getId())
                .getResultList().stream().map(BookingMapper::toBookingDto).toList();

        assertThat(bookingDtos.size(), equalTo(1));
        assertThat(bookingDtos.getFirst().getId(), notNullValue());
        assertThat(bookingDtos, equalTo(foundBookingDtos));
    }

    @Test
    void findBookingsByOwnerIdAndSateTest() {
        bookingService.createBooking(user.getId(), bookingNewDto);

        List<BookingDto> bookingDtos = bookingService.findBookingsByOwnerIdAndSate(user.getId(), BookingState.ALL);

        TypedQuery<Booking> query = em.createQuery("from Booking b where b.item.owner.id = :id", Booking.class);
        List<BookingDto> foundBookingDtos = query.setParameter("id", user.getId())
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
