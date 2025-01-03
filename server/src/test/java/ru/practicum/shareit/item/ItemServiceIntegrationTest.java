package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.type.NotFoundException;
import ru.practicum.shareit.exception.type.WrongRequestException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
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
public class ItemServiceIntegrationTest {
    private final EntityManager em;
    private final ItemService itemService;

    private User user;
    private ItemDto itemDto;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .name("user1")
                .email("email1@mail.ru")
                .build();

        em.persist(user);
        em.flush();

        itemDto = ItemDto.builder()
                .name("name1")
                .description("description1")
                .available(true)
                .comments(List.of())
                .build();
    }

    @Test
    void createItemTest() {
        ItemDto createdItemDto = itemService.createItem(user.getId(), itemDto);
        itemDto.setId(createdItemDto.getId());

        TypedQuery<Item> query = em.createQuery("from Item", Item.class);
        List<ItemDto> itemDtos = query.getResultList().stream().map(ItemMapper::toItemDto).toList();

        assertThat(itemDtos.size(), equalTo(1));
        assertThat(itemDtos.getFirst().getId(), notNullValue());
        assertThat(itemDtos, hasItem(itemDto));
    }

    @Test
    void createItemForItemRequestTest() {
        ItemRequest itemRequest = ItemRequest.builder()
                .created(LocalDateTime.now())
                .description("item request")
                .requestor(user)
                .build();

        em.persist(itemRequest);
        em.flush();

        itemDto.setRequestId(itemRequest.getId());
        ItemDto createdItemDto = itemService.createItem(user.getId(), itemDto);
        itemDto.setId(createdItemDto.getId());

        TypedQuery<Item> query = em.createQuery("from Item", Item.class);
        List<ItemDto> itemDtos = query.getResultList().stream().map(ItemMapper::toItemDto).toList();

        assertThat(itemDtos.size(), equalTo(1));
        assertThat(itemDtos.getFirst().getId(), notNullValue());
        assertThat(itemDtos, hasItem(itemDto));
    }

    @Test
    void createItemForInvalidItemRequestTest() {
        itemDto.setRequestId(Long.MAX_VALUE);
        assertThrows(NotFoundException.class, () -> itemService.createItem(user.getId(), itemDto));
    }

    @Test
    void updateItemTest() {
        ItemDto createdItemDto = itemService.createItem(user.getId(), itemDto);
        itemDto.setId(createdItemDto.getId());

        ItemDto newItemDto = ItemDto.builder().name("item1_updated").build();
        itemService.updateItem(user.getId(), createdItemDto.getId(), newItemDto);

        TypedQuery<Item> query = em.createQuery("from Item where id = :id", Item.class);
        ItemDto updatedItemDto = ItemMapper.toItemDto(query.setParameter("id", createdItemDto.getId()).getSingleResult());

        assertThat(updatedItemDto.getName(), equalTo(newItemDto.getName()));
        assertThat(updatedItemDto.getDescription(), equalTo(createdItemDto.getDescription()));
        assertThat(updatedItemDto.getAvailable(), equalTo(createdItemDto.getAvailable()));

        newItemDto.setDescription("description1_updated");
        itemDto = itemService.updateItem(user.getId(), createdItemDto.getId(), newItemDto);
        assertThat(itemDto.getDescription(), equalTo(newItemDto.getDescription()));

        newItemDto.setAvailable(false);
        itemDto = itemService.updateItem(user.getId(), createdItemDto.getId(), newItemDto);
        assertThat(itemDto.getAvailable(), equalTo(newItemDto.getAvailable()));
    }

    @Test
    void updateItemWrongOwnerTest() {
        User user2 = User.builder()
                .name("user2")
                .email("email2@mail.ru")
                .build();

        em.persist(user2);
        em.flush();

        ItemDto createdItemDto = itemService.createItem(user.getId(), itemDto);
        itemDto.setId(createdItemDto.getId());

        ItemDto newItemDto = ItemDto.builder().name("item1_updated").build();
        assertThrows(WrongRequestException.class, () -> itemService.updateItem(user2.getId(), createdItemDto.getId(), newItemDto));
    }

    @Test
    void findItemByIdTest() {
        long itemId = itemService.createItem(user.getId(), itemDto).getId();

        itemDto = itemService.findItemById(itemId);

        TypedQuery<Item> query = em.createQuery("from Item where id = :id", Item.class);
        ItemDto foundItemDto = ItemMapper.toItemDto(query.setParameter("id", itemDto.getId()).getSingleResult());

        assertThat(itemDto, equalTo(foundItemDto));
    }

    @Test
    void findItemsByUserIdTest() {
        itemService.createItem(user.getId(), itemDto);

        List<ItemDto> itemDtos = itemService.findItemsByUserId(user.getId());

        TypedQuery<Item> query = em.createQuery("from Item u where u.owner.id = :id", Item.class);
        List<ItemDto> foundItemDtos = ItemMapper.toItemDto(query.setParameter("id", user.getId()).getResultList());

        assertThat(itemDtos, equalTo(foundItemDtos));
    }

    @Test
    void findByRequestTest() {
        itemService.createItem(user.getId(), itemDto);

        List<ItemDto> itemDtos = itemService.findByRequest(itemDto.getName());

        TypedQuery<Item> query = em.createQuery("from Item i where i.available=true " +
                "and upper(i.name) like upper(concat('%', :text, '%')) " +
                "or upper(i.description) like upper(concat('%', :text, '%'))", Item.class);
        List<ItemDto> foundItemDto = ItemMapper.toItemDto(query.setParameter("text", itemDto.getName()).getResultList());

        assertThat(itemDtos, equalTo(foundItemDto));
    }

    @Test
    void createCommentTest() {
        Item item = Item.builder()
                .name("item1")
                .description("description1")
                .available(true)
                .owner(user)
                .build();

        em.persist(item);
        em.flush();

        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(2))
                .booker(user)
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();

        em.persist(booking);
        em.flush();

        CommentNewDto commentNewDto = CommentNewDto.builder()
                .text("comment1")
                .build();

        CommentDto commentDto = itemService.createComment(user.getId(), item.getId(), commentNewDto);

        TypedQuery<Comment> query = em.createQuery("from Comment ", Comment.class);
        List<CommentDto> commentDtos = query.getResultList().stream().map(CommentMapper::toCommentDto).toList();

        assertThat(commentDtos.size(), equalTo(1));
        assertThat(commentDtos.getFirst().getId(), notNullValue());
        assertThat(commentDtos, hasItem(commentDto));

        CommentNewDto commentNewDto2 = CommentNewDto.builder()
                .text("comment2")
                .build();

        CommentDto commentDto2 = itemService.createComment(user.getId(), item.getId(), commentNewDto2);

        query = em.createQuery("from Comment ", Comment.class);
        commentDtos = query.getResultList().stream().map(CommentMapper::toCommentDto).toList();

        assertThat(commentDtos.size(), equalTo(2));
        assertThat(commentDtos, hasItem(commentDto));
        assertThat(commentDtos, hasItem(commentDto2));
    }

    @Test
    void createCommentWithOtherUserTest() {
        Item item = Item.builder()
                .name("item1")
                .description("description1")
                .available(true)
                .owner(user)
                .build();

        em.persist(item);
        em.flush();

        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(2))
                .booker(user)
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();

        em.persist(booking);
        em.flush();

        User user2 = User.builder()
                .name("user2")
                .email("emai2@mail.ru")
                .build();

        em.persist(user2);
        em.flush();

        CommentNewDto commentNewDto = CommentNewDto.builder()
                .text("comment1")
                .build();

        assertThrows(WrongRequestException.class, () -> itemService.createComment(user2.getId(), item.getId(), commentNewDto));
    }
}
