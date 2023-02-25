package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utils.PageableMaker;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    @Transactional
    @Override
    public ItemDto save(long userId, ItemDto itemDto) {
        Item item = ItemMapper.INSTANCE.fromDto(itemDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id отсутствует!"));

        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос не существует!"));
            item.setRequest(itemRequest);
        }

        item.setOwner(user);

        return ItemMapper.INSTANCE.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDtoResponse getById(long id, long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Инструмент с таким id отсутствует!"));

        List<Booking> bookings = bookingRepository.findBookingByItem_Id(id);
        List<Comment> comments = commentRepository.findAllByItem_Id(id);

        ItemDtoResponse itemDtoResponse = addBookingsAndComments(item, bookings, comments);

        if (item.getOwner().getId() != userId) {
            itemDtoResponse.setLastBooking(null);
            itemDtoResponse.setNextBooking(null);
        }

        return itemDtoResponse;
    }

    public List<ItemDtoResponse> getAll(long ownerId, int from, int size) {
        Pageable paging = PageableMaker.makePageable(from, size, Sort.by(Sort.Direction.ASC, "id"));
        List<Item> items = itemRepository.findAllByOwnerId(ownerId, paging);
        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(ownerId);
        List<Comment> comments = commentRepository.findAllByItem_IdIn(items.stream()
                .map(Item::getId)
                .collect(Collectors.toList()));

        return items.stream()
                .map(item -> addBookingsAndComments(item, bookings, comments))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ItemDto update(long userId, ItemDto itemDto, long itemId) {
        Item itemToUpdate = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Инструмент с таким id отсутствует!"));

        if (itemToUpdate.getOwner() != null && itemToUpdate.getOwner().getId() != userId)
            throw new NotFoundException("Нет подходящего инструмента для этого пользователя");

        if (itemDto.getName() != null)
            itemToUpdate.setName(itemDto.getName());
        if (itemDto.getDescription() != null)
            itemToUpdate.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null)
            itemToUpdate.setAvailable(itemDto.getAvailable());

        return ItemMapper.INSTANCE.toDto(itemRepository.save(itemToUpdate));
    }

    @Override
    public List<ItemDto> getSearchResults(String text, int from, int size) {
        Pageable pageable = PageableMaker.makePageable(from, size, Sort.by(Sort.Direction.ASC, "id"));
        List<Item> items = itemRepository.searchByText(text.toLowerCase(), pageable);

        return items.stream()
                .map(ItemMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto postComment(long userId, long itemId, CommentDto commentDto) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id отсутствует!"));
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Инструмент с таким id отсутствует!"));
        final Booking booking = bookingRepository.findFirstByItem_IdAndBooker_Id(itemId, userId)
                .orElseThrow(() -> new NotFoundException("Бронирование отсутствует!"));
        if (!booking.getStatus().equals(BookingStatus.APPROVED) || booking.getEnd().isAfter(LocalDateTime.now()))
            throw new ValidationException("Нет прав оставлять комментарий!");

        Comment comment = CommentMapper.INSTANCE.fromDto(commentDto, user, item, LocalDateTime.now());
        return CommentMapper.INSTANCE.toDto(commentRepository.save(comment));
    }

    private static ItemDtoResponse addBookingsAndComments(Item item, List<Booking> bookings, List<Comment> comments) {

        Optional<Booking> nextBooking = bookings.stream()
                .filter(booking -> booking.getItem().getId().equals(item.getId()))
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getEnd));

        Optional<Booking> lastBooking = bookings.stream()
                .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                .filter(booking -> booking.getItem().getId().equals(item.getId()))
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getEnd));

        List<CommentDto> itemComments = comments.stream()
                .filter(comment -> comment.getItem().getId().equals(item.getId()))
                .map(CommentMapper.INSTANCE::toDto)
                .collect(Collectors.toList());

        BookingDtoShort nextBookingShort = BookingMapper.INSTANCE.toDtoShort(nextBooking.orElse(null));
        BookingDtoShort lastBookingShort = BookingMapper.INSTANCE.toDtoShort(lastBooking.orElse(null));

        return ItemMapper.INSTANCE.toDtoResponse(item, nextBookingShort, lastBookingShort, itemComments);
    }
}
