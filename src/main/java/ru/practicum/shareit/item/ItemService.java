package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.type.NotFoundException;
import ru.practicum.shareit.exception.type.WrongRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {
    public final ItemStorage itemStorage;
    public final UserStorage userStorage;

    public ItemDto createItem(long userId, ItemDto itemDto) {
        User user = findUser(userId);
        Item item = ItemMapper.toItem(itemDto, user, null);
        itemStorage.createItem(item);
        return ItemMapper.toItemDto(item);
    }

    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        findUser(userId);
        Item findItem = findItem(itemId);
        if (findItem.getOwner().getId() != userId) {
            throw new WrongRequestException("User ID: " + userId + " <> item owner with ID: " + findItem.getOwner().getId());
        }

        if (itemDto.getName() != null) findItem.setName(itemDto.getName());
        if (itemDto.getDescription() != null) findItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) findItem.setAvailable(itemDto.getAvailable());

        itemStorage.updateItem(findItem);
        return ItemMapper.toItemDto(findItem);
    }

    public ItemDto findItemById(long userId, long itemId) {
        Item findItem = findItem(itemId);
        if (findItem.getOwner().getId() != userId) {
            throw new WrongRequestException("User ID: " + userId + " <> item owner with ID: " + findItem.getOwner().getId());
        }
        return ItemMapper.toItemDto(findItem);
    }

    public List<ItemDto> findItemsByUserId(long userId) {
        return itemStorage.findItemsByUserId(userId).stream().map(ItemMapper::toItemDto).toList();
    }

    public List<ItemDto> findByRequest(String textToFind, Long userId) {
        return itemStorage.findByRequest(textToFind, userId).stream().map(ItemMapper::toItemDto).toList();
    }

    private User findUser(long userId) {
        Optional<User> user = userStorage.findUserById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User with ID: " + userId + " not found");
        }
        return user.get();
    }

    private Item findItem(long itemId) {
        Optional<Item> findItem = itemStorage.findItemById(itemId);
        if (findItem.isEmpty()) {
            throw new NotFoundException("Item with ID: " + itemId + " not found");
        }
        return findItem.get();
    }
}
