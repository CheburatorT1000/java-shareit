package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final HashMap<Long, Item> items = new HashMap<>();
    private long id = 0;

    private long makeId() {
        return ++id;
    }

    @Override
    public boolean checkItemIdExist(long id) {
        return items.containsKey(id);
    }

    @Override
    public Item create(Item item, long userId) {
        item.setId(makeId());
        item.setOwner(userId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getById(long id) {
        return items.get(id);
    }

    @Override
    public List<Item> getAll(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getSearchResults(String text) {
        List<Item> itemList = new ArrayList<>();
        if (!text.isBlank()) {
            itemList = items.values().stream()
                    .filter(Item::isAvailable)
                    .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                            || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                    .collect(Collectors.toList());
        }
        return itemList;
    }
}
