package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.util.List;

public interface BookingService {
    BookingDto save(BookingDtoCreate bookingDtoCreate, long userId);

    BookingDto bookingApprove(long bookingId, long userId, boolean approved);

    BookingDto findById(long bookingId, long userId);

    List<BookingDto> findAllByParam(long userId, BookingStatus status, Pageable pageablec);

    List<BookingDto> findAllByOwner(long userId, BookingStatus status, Pageable pageable);
}
