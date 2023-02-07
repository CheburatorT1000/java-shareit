package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.enums.BookingStatus.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingDto save(BookingDtoCreate bookingDtoCreate, long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не существует!"));
        final Item item = itemRepository.findById(bookingDtoCreate.getItemId())
                .orElseThrow(() -> new NotFoundException("Предмет отсутствует!"));

        if (!item.getAvailable())
            throw new ValidationException("Предмет не доступен для бронирования!");

        if (bookingDtoCreate.getEnd().isBefore(bookingDtoCreate.getStart()))
            throw new ValidationException("Неверно указано время старта бронирования!");

        if (item.getOwner().getId() == userId)
            throw new NotFoundException("Нет подходящих для бронирования предметов!");

        final Booking booking = Booking.builder()
                .start(bookingDtoCreate.getStart())
                .end(bookingDtoCreate.getEnd())
                .item(item)
                .booker(user)
                .status(WAITING)
                .build();

        return BookingMapper.INSTANCE.toDtoResponse(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingDto bookingApprove(long bookingId, long ownerId, boolean approved) {
        final Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование отсутствует!"));
        final Item item = booking.getItem();

        if (item.getOwner().getId() != ownerId)
            throw new NotFoundException("Соответствие между бронированием и пользователем отсутствует!");
        if (!booking.getStatus().equals(WAITING))
            throw new ValidationException("Статус для изменения не доступен!");

        if (approved)
            booking.setStatus(APPROVED);
        else
            booking.setStatus(REJECTED);

        return BookingMapper.INSTANCE.toDtoResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findById(long bookingId, long userId) {
        final Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование отсутствует!"));

        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId)
            return BookingMapper.INSTANCE.toDtoResponse(booking);
        else
            throw new NotFoundException("Соответствие между бронированием и пользователем отсутствует!");
    }

    @Override
    public List<BookingDto> findAllByParam(long userId, String state) {
        List<Booking> bookingList = new ArrayList<>();
        BookingStatus status;
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не существует!"));

        try {
            status = BookingStatus.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + state);
        }

        switch (status) {
            case ALL:
                bookingList = bookingRepository.findBookingByBookerIdOrderByIdDesc(userId);
                break;
            case CURRENT:
                bookingList = bookingRepository
                        .findBookingByBookerIdAndStartIsBeforeAndEndIsAfter(userId,
                                LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookingList = bookingRepository.findBookingByBookerIdAndEndIsBeforeAndStatusIs(userId,
                        LocalDateTime.now(), APPROVED);
                break;
            case FUTURE:
                bookingList = bookingRepository.findBookingByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookingList = bookingRepository.findBookingByBookerIdAndStatus(userId, WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository.findBookingByBookerIdAndStatus(userId, REJECTED);
                break;
        }
        return bookingList.stream()
                .map(BookingMapper.INSTANCE::toDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllByOwner(long userId, String state) {
        List<Booking> bookingList = new ArrayList<>();
        BookingStatus status;
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не существует!"));

        try {
            status = BookingStatus.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + state);
        }

        switch (status)  {
            case ALL:
                bookingList = bookingRepository.findBookingByItemOwnerIdOrderByIdDesc(userId);
                break;
            case CURRENT:
                bookingList = bookingRepository
                        .findBookingByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId,
                                LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookingList = bookingRepository.findBookingByItemOwnerIdAndEndIsBeforeAndStatusIs(userId,
                        LocalDateTime.now(), APPROVED);
                break;
            case FUTURE:
                bookingList = bookingRepository.findBookingByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookingList = bookingRepository.findBookingByItemOwnerIdAndStatus(userId, WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository.findBookingByItemOwnerIdAndStatus(userId, REJECTED);
                break;
        }
        return bookingList.stream()
                .map(BookingMapper.INSTANCE::toDtoResponse)
                .collect(Collectors.toList());
    }

}
