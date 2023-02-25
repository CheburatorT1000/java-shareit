package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.utils.Create;

import java.util.List;

import static ru.practicum.shareit.item.controller.ItemController.SHARER_USER_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto save(@RequestHeader(SHARER_USER_ID) long userId,
                               @Validated({Create.class}) @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.save(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> findAllById(@RequestHeader(SHARER_USER_ID) long userId) {
        return itemRequestService.findAllByRequestorId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllByParams(@RequestHeader(SHARER_USER_ID) long userId,
                                                @RequestParam(required = false, defaultValue = "0") int from,
                                                @RequestParam(required = false, defaultValue = "10") int size) {
        return itemRequestService.findAllByParams(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@RequestHeader(SHARER_USER_ID) long userId,
                                   @PathVariable long requestId) {
        return itemRequestService.findById(userId, requestId);
    }
}
