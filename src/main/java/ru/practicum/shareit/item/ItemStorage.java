package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    void createItem(Item item);

    void updateItem(Item item);

    void deleteItem(long userId);

    Optional<Item> findItemById(long id);

    List<Item> findItemsByUserId(long userId);

    List<Item> findByRequest(String textToFind, Long userId);
}
