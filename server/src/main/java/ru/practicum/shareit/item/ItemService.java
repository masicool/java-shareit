package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.type.NotFoundException;
import ru.practicum.shareit.exception.type.WrongRequestException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    public final ItemRepository itemRepository;
    public final UserRepository userRepository;
    public final CommentRepository commentRepository;
    public final BookingRepository bookingRepository;
    public final ItemRequestRepository itemRequestRepository;

    @Transactional
    public ItemDto createItem(long userId, ItemDto itemDto) {
        User user = findUser(userId);
        ItemRequest itemRequest;
        Item item;

        if (itemDto.getRequestId() == null) {
            item = ItemMapper.toItem(itemDto, user, null);
        } else {
            itemRequest = findItemRequest(itemDto.getRequestId());
            item = ItemMapper.toItem(itemDto, user, itemRequest);
        }

        itemRepository.save(item);
        return toItemDto(item);
    }

    @Transactional
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        findUser(userId);
        Item foundItem = findItem(itemId);
        if (foundItem.getOwner().getId() != userId) {
            throw new WrongRequestException("User ID: " + userId + " <> item owner with ID: " + foundItem.getOwner().getId());
        }

        if (itemDto.getName() != null) foundItem.setName(itemDto.getName());
        if (itemDto.getDescription() != null) foundItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) foundItem.setAvailable(itemDto.getAvailable());
        if (itemDto.getRequestId() != null) foundItem.setRequest(findItemRequest(itemDto.getRequestId()));

        itemRepository.save(foundItem);
        return toItemDto(foundItem);
    }

    public ItemDto findItemById(long itemId) {
        ItemDto foundItem = toItemDto(findItem(itemId));
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        foundItem.setComments(CommentMapper.toCommentDto(comments));
        return foundItem;
    }

    public List<ItemDto> findItemsByUserId(long userId) {
        // найдем все вещи пользователя
        List<ItemDto> itemDtos = toItemDto(itemRepository.findByOwnerId(userId));
        List<Long> itemIds = itemDtos.stream().map(ItemDto::getId).toList();

        // получим отзывы по всем вещам из списка id вещей
        Map<Long, List<Comment>> commentsMap = commentRepository.findAllByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(o -> o.getItem().getId()));

        List<ItemDto> itemsWithComments = new ArrayList<>();
        for (ItemDto itemDto : itemDtos) {
            List<Comment> comments = commentsMap.get(itemDto.getId());

            if (comments != null) itemDto.setComments(CommentMapper.toCommentDto(comments));

            itemsWithComments.add(itemDto);
        }

        return itemsWithComments;
    }

    public List<ItemDto> findByRequest(String textToFind) {
        return itemRepository.findByRequest(textToFind).stream().map(ItemMapper::toItemDto).toList();
    }

    @Transactional
    public CommentDto createComment(long authorId, long itemId, CommentNewDto commentNewDto) {
        User author = findUser(authorId);
        Item item = findItem(itemId);
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerId(itemId, authorId);

        for (Booking booking : bookings) {
            if (booking.getStatus() == BookingStatus.APPROVED && booking.getEnd().isBefore(LocalDateTime.now())) {
                Comment comment = CommentMapper.toNewComment(commentNewDto.getText(), item, author);
                commentRepository.save(comment);
                return CommentMapper.toCommentDto(comment);
            }
        }
        throw new WrongRequestException("User with ID: " + authorId + " cannot leave comments on the item with ID: " + itemId);
    }

    private User findUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID: " + userId + " not found"));

    }

    private Item findItem(long itemId) {
        return itemRepository.findById((itemId)).orElseThrow(() -> new NotFoundException("Item with ID: " + itemId + " not found"));
    }

    private ItemRequest findItemRequest(Long itemRequestId) {

        return itemRequestRepository.findById(itemRequestId).orElseThrow(() ->
                new NotFoundException("Item request with ID : " + itemRequestId + " not found"));
    }
}
