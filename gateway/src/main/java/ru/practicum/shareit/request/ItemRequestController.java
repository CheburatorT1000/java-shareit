package ru.practicum.shareit.request;

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

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> save(@RequestHeader(SHARER_USER_ID) long userId,
                                       @Validated({Create.class}) @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.save(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllById(@RequestHeader(SHARER_USER_ID) long userId) {
        return itemRequestClient.findAllByRequestorId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllByParams(@RequestHeader(SHARER_USER_ID) long userId,
                                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                                  @RequestParam(required = false, defaultValue = "10") Integer size) {
        Pageable pageable = PageableMaker.makePageable(from, size, Sort.by(Sort.Direction.ASC, "created"));
        return itemRequestClient.findAllByParams(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@RequestHeader(SHARER_USER_ID) long userId,
                                           @PathVariable long requestId) {
        return itemRequestClient.findById(userId, requestId);
    }
}
