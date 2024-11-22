package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class ItemInMemoryStorageImpl implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long id = 0L;

    @Override
    public void createItem(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
    }

    @Override
    public void updateItem(Item item) {
        items.replace(item.getId(), item);
    }

    @Override
    public void deleteItem(long userId) {

    }

    @Override
    public Optional<Item> findItemById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findItemsByUserId(long userId) {
        return items.values().stream().filter(item -> item.getOwner().getId() == userId).toList();
    }

    @Override
    public List<Item> findByRequest(String textToFind, Long userId) {
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(textToFind.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(textToFind.toLowerCase())) &&
                        (userId == null || Objects.equals(item.getOwner().getId(), userId)) &&
                        item.getAvailable())
                .toList();
    }

    private long getNextId() {
        return ++id;
    }
}
