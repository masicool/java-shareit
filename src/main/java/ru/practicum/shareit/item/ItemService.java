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
        Item foundItem = findItem(itemId);
        if (foundItem.getOwner().getId() != userId) {
            throw new WrongRequestException("User ID: " + userId + " <> item owner with ID: " + foundItem.getOwner().getId());
        }

        if (itemDto.getName() != null) foundItem.setName(itemDto.getName());
        if (itemDto.getDescription() != null) foundItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) foundItem.setAvailable(itemDto.getAvailable());

        itemStorage.updateItem(foundItem);
        return ItemMapper.toItemDto(foundItem);
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

    public List<ItemDto> findByRequest(String textToFind) {
        if (textToFind.isBlank()) return List.of();
        return itemStorage.findByRequest(textToFind).stream().map(ItemMapper::toItemDto).toList();
    }

    private User findUser(long userId) {
        return userStorage.findUserById(userId).orElseThrow(() -> new NotFoundException("User with ID: " + userId + " not found"));

    }

    private Item findItem(long itemId) {
        return itemStorage.findItemById((itemId)).orElseThrow(() -> new NotFoundException("Item with ID: " + itemId + " not found"));
    }
}
