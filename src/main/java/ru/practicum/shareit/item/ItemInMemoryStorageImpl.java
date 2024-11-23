package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class ItemInMemoryStorageImpl implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>(); // хранение вещей
    private final Map<Long, Set<Item>> userItems = new HashMap<>(); // хранение списка тех же вещей по юзерам
    private long id = 0L;

    @Override
    public void createItem(Item item) {
        item.setId(getNextId());
        userItems.computeIfAbsent(item.getOwner().getId(), k -> new HashSet<>()).add(item);
        items.put(item.getId(), item);
    }

    @Override
    public void updateItem(Item item) {
        userItems.get(item.getOwner().getId()).add(item);
        items.replace(item.getId(), item);
    }

    @Override
    public Optional<Item> findItemById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findItemsByUserId(long userId) {
        return userItems.getOrDefault(userId, Set.of()).stream().toList();
    }

    @Override
    public List<Item> findByRequest(String textToFind) {
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(textToFind.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(textToFind.toLowerCase())) &&
                        item.getAvailable())
                .toList();
    }

    private long getNextId() {
        return ++id;
    }
}
