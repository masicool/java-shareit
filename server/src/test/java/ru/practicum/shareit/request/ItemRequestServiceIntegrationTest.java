package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestNewDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class ItemRequestServiceIntegrationTest {
    private final EntityManager em;
    private final ItemRequestService itemRequestService;

    private User user;
    private ItemRequestNewDto itemRequestNewDto;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .name("user1")
                .email("email1@mail.ru")
                .build();

        em.persist(user);
        em.flush();

        Item item = Item.builder()
                .name("item1")
                .description("description1")
                .available(true)
                .owner(user)
                .build();

        em.persist(item);
        em.flush();

        itemRequestNewDto = ItemRequestNewDto.builder()
                .description("request1")
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .description(itemRequestNewDto.getDescription())
//                .items(List.of(new ItemForResponseDto(item.getId(), item.getName(), user.getId())))
                .items(List.of())
                .build();
    }

    @Test
    void createRequestTest() {
        ItemRequestDto createdItemRequestDto = itemRequestService.createRequest(user.getId(), itemRequestNewDto);
        itemRequestDto.setId(createdItemRequestDto.getId());
        itemRequestDto.setCreated(createdItemRequestDto.getCreated());

        TypedQuery<ItemRequest> query = em.createQuery("from ItemRequest", ItemRequest.class);
        List<ItemRequestDto> itemRequestDtos = query.getResultList().stream().map(ItemRequestMapper::toItemRequestDto).toList();

        assertThat(itemRequestDtos.size(), equalTo(1));
        assertThat(itemRequestDtos.getFirst().getId(), notNullValue());
        assertThat(itemRequestDtos, hasItem(itemRequestDto));
    }

    @Test
    void findItemRequestsByUserIdTest() {
        itemRequestService.createRequest(user.getId(), itemRequestNewDto);

        List<ItemRequestDto> itemRequestDtos = itemRequestService.findItemRequestsByUserId(user.getId(), false);

        TypedQuery<ItemRequest> query = em.createQuery("from ItemRequest ir where ir.requestor.id = :id", ItemRequest.class);
        List<ItemRequestDto> foundItemRequestDtos = ItemRequestMapper
                .toItemRequestDto(query.setParameter("id", user.getId()).getResultList());

        assertThat(itemRequestDtos, equalTo(foundItemRequestDtos));
    }

    @Test
    void findItemRequestByIdTest() {
        long itemRequestId = itemRequestService.createRequest(user.getId(), itemRequestNewDto).getId();

        itemRequestDto = itemRequestService.findItemRequestById(itemRequestId);

        TypedQuery<ItemRequest> query = em.createQuery("from ItemRequest ir where ir.id = :id", ItemRequest.class);
        ItemRequestDto foundItemRequestDto = ItemRequestMapper
                .toItemRequestDto(query.setParameter("id", itemRequestId).getSingleResult());

        assertThat(itemRequestDto, equalTo(foundItemRequestDto));
    }
}
