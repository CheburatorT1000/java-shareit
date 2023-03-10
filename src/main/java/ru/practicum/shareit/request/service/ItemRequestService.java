package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto save(ItemRequestDto itemRequestDto, long userId);

    List<ItemRequestDto> findAllByRequestorId(long userId);

    List<ItemRequestDto> findAllByParams(long userId, int from, int size);

    ItemRequestDto findById(long userId, long requestId);
}
