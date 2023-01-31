package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@RequestHeader(SHARER_USER_ID) long userId,
                          @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable int id) {
        return itemService.getById(id);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader(SHARER_USER_ID) long userId) {
        return itemService.getAll(userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader(SHARER_USER_ID) long userId,
                          @Validated({Update.class}) @RequestBody ItemDto itemDto,
                          @PathVariable("id") long itemId) {
        return itemService.update(userId, itemDto, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearchResults(@RequestParam String text) {
        return itemService.getSearchResults(text);
    }
}
