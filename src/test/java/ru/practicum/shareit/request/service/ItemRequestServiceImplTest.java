package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Captor
    ArgumentCaptor<ItemRequest> itemRequestArgumentCaptor;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    ItemDto itemDto;
    UserDto userDto;
    CommentDto commentDto;
    ItemRequest itemRequest;
    ItemRequestDto itemRequestDto;
    Item item;
    User user;
    Comment comment;
    Booking booking;
    BookingDto bookingDto;

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
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("request description")
                .build();
        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("itemRequest description")
                .created(LocalDateTime.now().plusDays(1))
                .build();
    }

    @Test
    void save_whenInvoked_thenSaveItemRequest() {
        long userId = 1L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        itemRequestService.save(itemRequestDto, userId);

        verify(requestRepository).save(itemRequestArgumentCaptor.capture());
        ItemRequest value = itemRequestArgumentCaptor.getValue();
        assertThat(value.getRequestor(), equalTo(user));
    }

    @Test
    void save_whenItemNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.save(itemRequestDto, userId));
        assertThat(exception.getMessage(), equalTo("Пользователь не существует!"));
    }

    @Test
    void findAllByRequestorId_whenInvoked_thenGetListOfRequestDto() {
        long userId = 1L;
        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(10L)
                .requestor(user)
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(20L)
                .requestor(user)
                .build();
        User user3 = User.builder()
                .id(3L)
                .name("userName")
                .email("name@mail.com")
                .build();
        Item item1 = Item.builder()
                .id(2L)
                .owner(user3)
                .request(itemRequest1)
                .build();
        Item item2 = Item.builder()
                .id(3L)
                .owner(user3)
                .request(itemRequest2)
                .build();
        item.setOwner(user3);
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestorId(userId))
                .thenReturn(Arrays.asList(itemRequest1,itemRequest2));
        when(itemRepository.findAllByRequestIdIn(anyList()))
                .thenReturn(Arrays.asList(item1, item2));


        List<ItemRequestDto> allByRequestorId = itemRequestService.findAllByRequestorId(userId);
        assertThat(allByRequestorId, hasSize(2));
        assertThat(allByRequestorId.get(0).getId(), equalTo(10L));
        assertThat(allByRequestorId.get(1).getId(), equalTo(20L));
    }

    @Test
    void findAllByRequestorId_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.findAllByRequestorId(userId));
        assertThat(exception.getMessage(), equalTo("Пользователь не существует!"));
    }

    @Test
    void findAllByParams_whenInvoked_thenGetListOfRequestDto() {
        long userId = 1L;
        int from = 0;
        int size = 5;
        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(10L)
                .requestor(user)
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(20L)
                .requestor(user)
                .build();
        User user3 = User.builder()
                .id(3L)
                .name("userName")
                .email("name@mail.com")
                .build();
        Item item1 = Item.builder()
                .id(2L)
                .owner(user3)
                .request(itemRequest1)
                .build();
        Item item2 = Item.builder()
                .id(3L)
                .owner(user3)
                .request(itemRequest2)
                .build();
        item.setOwner(user3);
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestorIdNotIn(anyList(), any(Pageable.class)))
                .thenReturn(Collections.emptyList());
        when(itemRepository.findAllByRequestIdIn(anyList()))
                .thenReturn(Arrays.asList(item1, item2));


        List<ItemRequestDto> allByParams = itemRequestService.findAllByParams(userId, from, size);
        assertThat(allByParams, hasSize(0));
    }

    @Test
    void findAllByParams_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        int from = 0;
        int size = 5;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.findAllByParams(userId, from, size));
        assertThat(exception.getMessage(), equalTo("Пользователь не существует!"));
    }

    @Test
    void findById() {
        long userId = 1L;
        long requestId = 1L;
        when(requestRepository.findById(requestId))
                .thenReturn(Optional.of(itemRequest));
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findAllByRequestId(requestId))
                .thenReturn(Collections.singletonList(item));

        ItemRequestDto result = itemRequestService.findById(userId, requestId);
        assertThat(result.getItems(), hasSize(1));
    }

    @Test
    void findById_whenRequestNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        long requestId = 1L;
        when(requestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.findById(userId, requestId));
        assertThat(exception.getMessage(), equalTo("Запрос не существует!"));
    }

    @Test
    void findById_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        long requestId = 1L;
        when(requestRepository.findById(requestId))
                .thenReturn(Optional.of(itemRequest));
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.findById(userId, requestId));
        assertThat(exception.getMessage(), equalTo("Пользователь не существует!"));
    }
}