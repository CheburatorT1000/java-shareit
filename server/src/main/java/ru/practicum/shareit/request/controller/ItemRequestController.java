package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.item.controller.ItemController.SHARER_USER_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto save(@RequestHeader(SHARER_USER_ID) long userId,
                               @RequestBody ItemRequestDto itemRequestDto) {
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
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "created"));
        return itemRequestService.findAllByParams(userId, pageable);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@RequestHeader(SHARER_USER_ID) long userId,
                                   @PathVariable long requestId) {
        return itemRequestService.findById(userId, requestId);
    }
}
