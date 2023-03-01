package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.PageableMaker;
import ru.practicum.shareit.utils.Update;

import java.util.Collections;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(SHARER_USER_ID) long userId,
                                         @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        return itemClient.createItem(userId, itemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@RequestHeader(SHARER_USER_ID) long userId,
                                          @PathVariable int id) {
        return itemClient.getItem(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(SHARER_USER_ID) long userId,
                                         @RequestParam(required = false, defaultValue = "0") Integer from,
                                         @RequestParam(required = false, defaultValue = "10") Integer size) {
        Pageable pageable = PageableMaker.makePageable(from, size, Sort.by(Sort.Direction.ASC, "id"));
        return itemClient.getAll(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(SHARER_USER_ID) long userId,
                                         @Validated({Update.class}) @RequestBody ItemDto itemDto,
                                         @PathVariable long itemId) {
        return itemClient.update(userId, itemDto, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getSearchResults(@RequestParam String text,
                                                   @RequestParam(required = false, defaultValue = "0") Integer from,
                                                   @RequestParam(required = false, defaultValue = "10") Integer size) {
        if (text == null || text.isEmpty())
            return ResponseEntity.ok(Collections.emptyList());
        PageableMaker.makePageable(from, size, Sort.by(Sort.Direction.ASC, "id"));
        return itemClient.getSearchResults(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestHeader(SHARER_USER_ID) long userId,
                                              @PathVariable long itemId,
                                              @Validated({Create.class}) @RequestBody CommentDto commentDto) {
        return itemClient.postComment(userId, itemId, commentDto);
    }
}
