package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.PageableMaker;
import ru.practicum.shareit.utils.Update;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@RequestHeader(SHARER_USER_ID) long userId,
                          @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        return itemService.save(userId, itemDto);
    }

    @GetMapping("/{id}")
    public ItemDtoResponse getById(@RequestHeader(SHARER_USER_ID) long userId,
                                   @PathVariable int id) {
        return itemService.getById(id, userId);
    }

    @GetMapping
    public List<ItemDtoResponse> getAll(@RequestHeader(SHARER_USER_ID) long userId,
                                        @RequestParam(required = false, defaultValue = "0") int from,
                                        @RequestParam(required = false, defaultValue = "10") int size) {
        Pageable pageable = PageableMaker.makePageable(from, size, Sort.by(Sort.Direction.ASC, "id"));
        return itemService.getAll(userId, pageable);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(SHARER_USER_ID) long userId,
                          @Validated({Update.class}) @RequestBody ItemDto itemDto,
                          @PathVariable long itemId) {
        return itemService.update(userId, itemDto, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearchResults(@RequestParam String text,
                                          @RequestParam(required = false, defaultValue = "0") int from,
                                          @RequestParam(required = false, defaultValue = "10") int size) {
        if (text == null || text.isEmpty())
            return Collections.emptyList();
        Pageable pageable = PageableMaker.makePageable(from, size, Sort.by(Sort.Direction.ASC, "id"));
        return itemService.getSearchResults(text, pageable);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestHeader(SHARER_USER_ID) long userId,
                                  @PathVariable long itemId,
                                  @Validated({Create.class}) @RequestBody CommentDto commentDto) {
        return itemService.postComment(userId, itemId, commentDto);
    }
}
