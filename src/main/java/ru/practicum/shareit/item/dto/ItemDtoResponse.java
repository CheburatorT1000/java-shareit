package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Data
@Builder
public class ItemDtoResponse {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private Long request;

    private BookingDtoShort lastBooking;

    private BookingDtoShort nextBooking;

    private List<CommentDto> comments;
}