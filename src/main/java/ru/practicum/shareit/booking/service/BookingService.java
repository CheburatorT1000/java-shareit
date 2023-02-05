package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

import java.util.List;

public interface BookingService {
    BookingDto save(BookingDtoCreate bookingDtoCreate, long userId);

    BookingDto bookingApprove(long bookingId, long userId, boolean approved);

    BookingDto findById(long bookingId, long userId);

    List<BookingDto> findAllByParam(long userId, String state);

    List<BookingDto> findAllByOwner(long userId, String state);
}
