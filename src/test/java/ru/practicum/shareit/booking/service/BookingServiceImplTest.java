package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;
    private ItemDto itemDto;
    private UserDto userDto;
    private CommentDto commentDto;
    private ItemRequest itemRequest;
    private Item item;
    private User user;
    private Comment comment;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .name("userName")
                .email("name@mail.com")
                .build();
        itemDto = ItemDto.builder()
                .id(1L)
                .name("item name")
                .description("item description")
                .available(false)
                .build();
        user = UserMapper.INSTANCE.fromDto(userDto);
        item = ItemMapper.INSTANCE.fromDto(itemDto);
        commentDto = CommentDto.builder()
                .id(1L)
                .text("comment text")
                .authorName("Vasya")
                .build();
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("request description")
                .build();
        comment = Comment.builder()
                .id(1L)
                .text("asdasd")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
        bookingDto = BookingDto.builder()
                .id(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void save_whenInvoked_thenSaveBooking() {
        long userId = 1L;
        user.setId(2L);
        item.setAvailable(true);
        item.setOwner(user);
        BookingDtoCreate bookingDtoCreate = BookingDtoCreate.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(userId))
                .thenReturn(Optional.of(item));


        bookingService.save(bookingDtoCreate, userId);


        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();
        assertThat(savedBooking.getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(savedBooking.getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(savedBooking.getBooker(), equalTo(user));
        assertThat(savedBooking.getItem(), equalTo(item));
        assertThat(savedBooking.getStatus(), is(BookingStatus.WAITING));
    }

    @Test
    void save_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        user.setId(2L);
        item.setAvailable(true);
        item.setOwner(user);
        BookingDtoCreate bookingDtoCreate = BookingDtoCreate.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.save(bookingDtoCreate, userId));
        assertThat(exception.getMessage(), equalTo("Пользователь не существует!"));
    }

    @Test
    void save_whenItemNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        user.setId(2L);
        item.setAvailable(true);
        item.setOwner(user);
        BookingDtoCreate bookingDtoCreate = BookingDtoCreate.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(userId))
                .thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.save(bookingDtoCreate, userId));
        assertThat(exception.getMessage(), equalTo("Предмет отсутствует!"));
    }

    @Test
    void save_whenItemNotAvailable_thenValidationExceptionThrown() {
        long userId = 1L;
        user.setId(2L);
        item.setOwner(user);
        BookingDtoCreate bookingDtoCreate = BookingDtoCreate.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(userId))
                .thenReturn(Optional.of(item));


        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.save(bookingDtoCreate, userId));
        assertThat(exception.getMessage(), equalTo("Предмет не доступен для бронирования!"));
    }

    @Test
    void save_whenWrongTimeUsed_thenValidationExceptionThrown() {
        long userId = 1L;
        user.setId(2L);
        item.setAvailable(true);
        item.setOwner(user);
        BookingDtoCreate bookingDtoCreate = BookingDtoCreate.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(userId))
                .thenReturn(Optional.of(item));


        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.save(bookingDtoCreate, userId));
        assertThat(exception.getMessage(), equalTo("Неверно указано время старта бронирования!"));
    }

    @Test
    void save_whenItemOwnerAndBookerTheSame_thenNotFoundExceptionThrown() {
        long userId = 1L;
        user.setId(1L);
        item.setAvailable(true);
        item.setOwner(user);
        BookingDtoCreate bookingDtoCreate = BookingDtoCreate.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(userId))
                .thenReturn(Optional.of(item));


        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.save(bookingDtoCreate, userId));
        assertThat(exception.getMessage(), equalTo("Нет подходящих для бронирования предметов!"));
    }

    @Test
    void bookingApprove_whenInvoked_thenBookingStatusChanged() {
        long bookingId = 1L;
        long ownerId = 1L;
        boolean approved = true;
        item.setOwner(user);
        booking.setItem(item);
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));


        bookingService.bookingApprove(bookingId, ownerId, approved);


        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();
        assertThat(savedBooking.getBooker(), equalTo(user));
        assertThat(savedBooking.getItem(), equalTo(item));
        assertThat(savedBooking.getStatus(), is(BookingStatus.APPROVED));
    }

    @Test
    void bookingApprove_whenBookingNotFound_thenNotFoundExceptionThrown() {
        long bookingId = 1L;
        long ownerId = 1L;
        boolean approved = true;
        item.setOwner(user);
        booking.setItem(item);
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.bookingApprove(bookingId, ownerId, approved));
        assertThat(exception.getMessage(), equalTo("Бронирование отсутствует!"));
    }

    @Test
    void bookingApprove_whenItemOwner_thenNotFoundExceptionThrown() {
        long bookingId = 1L;
        long ownerId = 2L;
        boolean approved = true;
        item.setOwner(user);
        booking.setItem(item);
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));


        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.bookingApprove(bookingId, ownerId, approved));
        assertThat(exception.getMessage(), equalTo("Соответствие между бронированием и пользователем отсутствует!"));
    }

    @Test
    void bookingApprove_whenWrongStatus_thenNotFoundExceptionThrown() {
        long bookingId = 1L;
        long ownerId = 1L;
        boolean approved = true;
        booking.setStatus(BookingStatus.REJECTED);
        item.setOwner(user);
        booking.setItem(item);
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));


        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.bookingApprove(bookingId, ownerId, approved));
        assertThat(exception.getMessage(), equalTo("Статус для изменения не доступен!"));
    }

    @Test
    void findById_whenInvoked_thenReturnBookingDto() {
        long bookingId = 1L;
        long userId = 1L;
        User owner = User.builder()
                .id(3L)
                .build();
        item.setOwner(owner);
        booking.setItem(item);
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));


        BookingDto bookingReturned = bookingService.findById(bookingId, userId);

        assertThat(bookingReturned.getId(), equalTo(1L));
        assertThat(bookingReturned.getBooker(), equalTo(user));
    }

    @Test
    void findById_whenBookingNotFound_thenNotFoundExceptionThrown() {
        long bookingId = 1L;
        long userId = 1L;
        User owner = User.builder()
                .id(3L)
                .build();
        item.setOwner(owner);
        booking.setItem(item);
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.findById(bookingId, userId));
        assertThat(exception.getMessage(), equalTo("Бронирование отсутствует!"));
    }

    @Test
    void findById_whenWrongUser_thenNotFoundExceptionThrown() {
        long bookingId = 1L;
        long userId = 2L;
        User owner = User.builder()
                .id(3L)
                .build();
        item.setOwner(owner);
        booking.setItem(item);
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));


        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.findById(bookingId, userId));
        assertThat(exception.getMessage(), equalTo("Соответствие между бронированием и пользователем отсутствует!"));
    }

    @Test
    void findAllByParam_whenInvokedStateALL_thenReturnSingleBookingList() {
        long userId = 1L;
        String state = "ALL";
        int from = 0;
        int size = 4;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByBookerIdOrderByIdDesc(anyLong(), any(Pageable.class)))
                .thenReturn(Collections.singletonList(booking));


        List<BookingDto> returnedList = bookingService.findAllByParam(userId, state, from, size);
        assertThat(returnedList, hasSize(1));
    }

    @Test
    void findAllByParam_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        String state = "ALL";
        int from = 0;
        int size = 4;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.findAllByParam(userId, state, from, size));
        assertThat(exception.getMessage(), equalTo("Пользователь не существует!"));
    }

    @Test
    void findAllByParam_whenWrongStateUsed_thenValidationExceptionThrown() {
        long userId = 1L;
        String state = "WrongState";
        int from = 0;
        int size = 4;
        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));


        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.findAllByParam(userId, state, from, size));
        assertThat(exception.getMessage(), equalTo("Unknown state: " + state));
    }

    @Test
    void findAllByParam_whenInvokedStateCURRENT_thenReturnSingleBookingList() {
        long userId = 1L;
        String state = "current";
        int from = 0;
        int size = 4;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByBookerIdAndStartIsBeforeAndEndIsAfter(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(Collections.singletonList(booking));


        List<BookingDto> returnedList = bookingService.findAllByParam(userId, state, from, size);
        assertThat(returnedList, hasSize(1));
    }

    @Test
    void findAllByParam_whenInvokedStatePAST_thenReturnSingleBookingList() {
        long userId = 1L;
        String state = "past";
        int from = 0;
        int size = 4;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByBookerIdAndEndIsBeforeAndStatusIs(anyLong(),
                any(LocalDateTime.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(Collections.singletonList(booking));


        List<BookingDto> returnedList = bookingService.findAllByParam(userId, state, from, size);
        assertThat(returnedList, hasSize(1));
    }

    @Test
    void findAllByParam_whenInvokedStateFUTURE_thenReturnSingleBookingList() {
        long userId = 1L;
        String state = "future";
        int from = 0;
        int size = 4;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(Collections.singletonList(booking));


        List<BookingDto> returnedList = bookingService.findAllByParam(userId, state, from, size);
        assertThat(returnedList, hasSize(1));
    }

    @Test
    void findAllByParam_whenInvokedStateWAITING_thenReturnSingleBookingList() {
        long userId = 1L;
        String state = "waiting";
        int from = 0;
        int size = 4;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByBookerIdAndStatus(anyLong(),
                any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(Collections.singletonList(booking));


        List<BookingDto> returnedList = bookingService.findAllByParam(userId, state, from, size);
        assertThat(returnedList, hasSize(1));
    }

    @Test
    void findAllByParam_whenInvokedStateREJECTED_thenReturnSingleBookingList() {
        long userId = 1L;
        String state = "rejected";
        int from = 0;
        int size = 4;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByBookerIdAndStatus(anyLong(),
                any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(Collections.singletonList(booking));


        List<BookingDto> returnedList = bookingService.findAllByParam(userId, state, from, size);
        assertThat(returnedList, hasSize(1));
    }

    @Test
    void findAllByOwner_whenInvokedStateALL_thenReturnSingleBookingList() {
        long userId = 1L;
        String state = "ALL";
        int from = 0;
        int size = 4;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByItemOwnerIdOrderByIdDesc(anyLong(), any(Pageable.class)))
                .thenReturn(Collections.singletonList(booking));


        List<BookingDto> returnedList = bookingService.findAllByOwner(userId, state, from, size);
        assertThat(returnedList, hasSize(1));
    }

    @Test
    void findAllByOwner_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        String state = "ALL";
        int from = 0;
        int size = 4;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.findAllByOwner(userId, state, from, size));
        assertThat(exception.getMessage(), equalTo("Пользователь не существует!"));
    }

    @Test
    void findAllByOwner_whenWrongStateUsed_thenValidationExceptionThrown() {
        long userId = 1L;
        String state = "WrongState";
        int from = 0;
        int size = 4;
        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));


        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.findAllByOwner(userId, state, from, size));
        assertThat(exception.getMessage(), equalTo("Unknown state: " + state));
    }

    @Test
    void findAllByOwner_whenInvokedStateCURRENT_thenReturnSingleBookingList() {
        long userId = 1L;
        String state = "current";
        int from = 0;
        int size = 4;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByItemOwnerIdAndStartIsBeforeAndEndIsAfter(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(Collections.singletonList(booking));


        List<BookingDto> returnedList = bookingService.findAllByOwner(userId, state, from, size);
        assertThat(returnedList, hasSize(1));
    }

    @Test
    void findAllByOwner_whenInvokedStatePAST_thenReturnSingleBookingList() {
        long userId = 1L;
        String state = "past";
        int from = 0;
        int size = 4;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByItemOwnerIdAndEndIsBeforeAndStatusIs(anyLong(),
                any(LocalDateTime.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(Collections.singletonList(booking));


        List<BookingDto> returnedList = bookingService.findAllByOwner(userId, state, from, size);
        assertThat(returnedList, hasSize(1));
    }

    @Test
    void findAllByOwner_whenInvokedStateFUTURE_thenReturnSingleBookingList() {
        long userId = 1L;
        String state = "future";
        int from = 0;
        int size = 4;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(Collections.singletonList(booking));


        List<BookingDto> returnedList = bookingService.findAllByOwner(userId, state, from, size);
        assertThat(returnedList, hasSize(1));
    }

    @Test
    void findAllByOwner_whenInvokedStateWAITING_thenReturnSingleBookingList() {
        long userId = 1L;
        String state = "waiting";
        int from = 0;
        int size = 4;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByItemOwnerIdAndStatus(anyLong(),
                any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(Collections.singletonList(booking));


        List<BookingDto> returnedList = bookingService.findAllByOwner(userId, state, from, size);
        assertThat(returnedList, hasSize(1));
    }

    @Test
    void findAllByOwner_whenInvokedStateREJECTED_thenReturnSingleBookingList() {
        long userId = 1L;
        String state = "rejected";
        int from = 0;
        int size = 4;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByItemOwnerIdAndStatus(anyLong(),
                any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(Collections.singletonList(booking));


        List<BookingDto> returnedList = bookingService.findAllByOwner(userId, state, from, size);
        assertThat(returnedList, hasSize(1));
    }
}