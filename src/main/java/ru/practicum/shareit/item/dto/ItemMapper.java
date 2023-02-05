package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper
public interface ItemMapper {

    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(target = "owner", ignore = true)
    ItemDto toDto(Item item);

    Item fromDto(ItemDto itemDto);

    @Mapping(target = "id", source = "item.id")
    ItemDtoResponse toDtoResponse(Item item, BookingDtoShort nextBooking, BookingDtoShort lastBooking, List<CommentDto> comments);
}
