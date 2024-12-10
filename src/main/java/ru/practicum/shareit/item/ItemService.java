package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.type.NotFoundException;
import ru.practicum.shareit.exception.type.WrongRequestException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    public final ItemRepository itemRepository;
    public final UserRepository userRepository;
    public final CommentRepository commentRepository;
    public final BookingRepository bookingRepository;

    public ItemDto createItem(long userId, ItemDto itemDto) {
        User user = findUser(userId);
        Item item = ItemMapper.toItem(itemDto, user, null);
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        findUser(userId);
        Item foundItem = findItem(itemId);
        if (foundItem.getOwner().getId() != userId) {
            throw new WrongRequestException("User ID: " + userId + " <> item owner with ID: " + foundItem.getOwner().getId());
        }

        if (itemDto.getName() != null) foundItem.setName(itemDto.getName());
        if (itemDto.getDescription() != null) foundItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) foundItem.setAvailable(itemDto.getAvailable());

        itemRepository.save(foundItem);
        return ItemMapper.toItemDto(foundItem);
    }

    public ItemDto findItemById(long itemId) {
        Item findItem = findItem(itemId);
        findItem.setComments(commentRepository.findAllByItemId(itemId));
        return ItemMapper.toItemDto(findItem);
    }

    public List<ItemDto> findItemsByUserId(long userId) {
        List<Item> items = itemRepository.findByOwnerId(userId);
        List<Long> itemIds = items.stream().map(Item::getId).toList();
        Map<Long, List<Comment>> commentsMap = commentRepository.findAllByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(o -> o.getItem().getId()));

        for (Item item : items) {
            item.setComments(commentsMap.getOrDefault(item.getId(), List.of()));
        }

        return items.stream().map(ItemMapper::toItemDto).toList();
    }

    public List<ItemDto> findByRequest(String textToFind) {
        if (textToFind.isBlank()) return List.of();
        return itemRepository.findByRequest(textToFind).stream().map(ItemMapper::toItemDto).toList();

    }

    public CommentDto createComment(long authorId, long itemId, String text) {
        User author = findUser(authorId);
        Item item = findItem(itemId);
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerId(itemId, authorId);
        for (Booking booking : bookings) {
            if (booking.getStatus() == BookingStatus.APPROVED && booking.getEnd().isBefore(LocalDateTime.now())) {
                Comment comment = new Comment();
                comment.setText(text.trim());
                comment.setItem(item);
                comment.setAuthor(author);
                comment.setCreated(LocalDateTime.now());
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
}
