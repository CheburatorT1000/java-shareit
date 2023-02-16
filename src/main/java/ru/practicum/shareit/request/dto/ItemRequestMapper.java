package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper
public interface ItemRequestMapper {

    ItemRequestMapper INSTANCE = Mappers.getMapper(ItemRequestMapper.class);


    ItemRequestDto toDto(ItemRequest itemRequest);

    ItemRequest fromDto(ItemRequestDto itemRequestDto);

}
