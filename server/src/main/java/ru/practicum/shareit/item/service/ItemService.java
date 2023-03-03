package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.util.List;

public interface ItemService {
    ItemDto save(long userId, ItemDto itemDto);

    ItemDtoResponse getById(long id, long userId);

    List<ItemDtoResponse> getAll(long userId, Pageable pageable);

    ItemDto update(long userId, ItemDto itemDto, long itemId);

    List<ItemDto> getSearchResults(String text, Pageable pageable);

    CommentDto postComment(long userId, long itemId, CommentDto commentDto);
}
