package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemDtoMapper itemDtoMapper;

    public ItemDto create(long userId, ItemDto itemDto) {
        Item item = itemDtoMapper.fromItemDtoToItem(itemDto);

        if (!userRepository.checkExistId(userId))
            throw new NotFoundException("Пользователь с таким id отсутствует!");
        itemRepository.create(item, userId);
        return itemDtoMapper.toItemDto(item);
    }

    public ItemDto getById(long id) {
        if (!itemRepository.checkItemIdExist(id))
            throw new NotFoundException("Инструмент с таким id отсутствует!");
        return itemDtoMapper.toItemDto(itemRepository.getById(id));
    }

    public List<ItemDto> getAll(long userId) {
        List<Item> items = itemRepository.getAll(userId);

        return items.stream()
                .map(itemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto update(long userId, ItemDto itemDto, long itemId) {
        if (!userRepository.checkExistId(userId))
            throw new NotFoundException("Пользователь с таким id отсутствует!");
        if (!itemRepository.checkItemIdExist(itemId))
            throw new NotFoundException("Инструмент с таким id отсутствует!");
        Item itemToUpdate = itemRepository.getById(itemId);

        if (itemToUpdate.getOwner() != 0 && itemToUpdate.getOwner() != userId)
            throw new NotFoundException("Нет подходящего инструмента для этого пользователя");
        if (itemDto.getName() != null)
            itemToUpdate.setName(itemDto.getName());
        if (itemDto.getDescription() != null)
            itemToUpdate.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null)
            itemToUpdate.setAvailable(itemDto.getAvailable());
        if (itemDto.getRequest() != null)
            itemToUpdate.setRequest(itemDto.getRequest());
        return itemDtoMapper.toItemDto(itemToUpdate);
    }

    public List<ItemDto> getSearchResults(String text) {
        List<Item> items = itemRepository.getSearchResults(text);

        return items.stream()
                .map(itemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
