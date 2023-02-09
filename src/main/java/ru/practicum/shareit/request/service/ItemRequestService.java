package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto save(ItemRequestDto itemRequestDto, long userId);

    List<ItemRequestDto> findAllById(long userId);

    List<ItemRequestDto> findAllByIdAndParams(long userId, int from, int size);
}
