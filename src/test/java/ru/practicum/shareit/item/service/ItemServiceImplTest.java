package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
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
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {


    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;
    @Captor
    private ArgumentCaptor<Comment> commentArgumentCaptor;
    @InjectMocks
    private ItemServiceImpl itemService;
    ItemDto itemDto;
    UserDto userDto;
    CommentDto commentDto;
    ItemRequest itemRequest;
    Item item;
    User user;
    Comment comment;
    Booking booking1;
    Booking booking2;

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
        booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
        booking2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void save_whenUserFound_thenSaveWithOwner() {
        long userId = 1L;
        User testUser = User.builder()
                .name("TestName")
                .build();
        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(testUser));


        itemService.save(userId, itemDto);


        Mockito.verify(itemRepository).save(itemArgumentCaptor.capture());
        Item actualItem = itemArgumentCaptor.getValue();
        assertThat(actualItem.getOwner().getName(), equalTo("TestName"));
    }

    @Test
    void save_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.save(userId, itemDto));
        assertThat(exception.getMessage(), equalTo("Пользователь с таким id отсутствует!"));
    }

    @Test
    void save_whenRequestFound_thenSaveWithRequest() {
        long userId = 1L;
        User testUser = User.builder()
                .name("TestName")
                .build();
        itemDto.setRequestId(1L);
        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(testUser));
        Mockito.when(requestRepository.findById(itemDto.getRequestId()))
                .thenReturn(Optional.of(itemRequest));


        itemService.save(userId, itemDto);


        Mockito.verify(itemRepository).save(itemArgumentCaptor.capture());
        Item actualItem = itemArgumentCaptor.getValue();
        assertThat(actualItem.getRequest(), equalTo(itemRequest));
    }

    @Test
    void save_whenRequestNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        User testUser = User.builder()
                .name("TestName")
                .build();
        itemDto.setRequestId(1L);
        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(testUser));
        Mockito.when(requestRepository.findById(itemDto.getRequestId()))
                .thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.save(userId, itemDto));
        assertThat(exception.getMessage(), equalTo("Запрос не существует!"));
    }

    @Test
    void getById_whenItemFound_thenReturnItemDtoResponse() {
        long userId = 1L;
        long itemId = 1L;
        User user = UserMapper.INSTANCE.fromDto(userDto);
        itemDto.setOwner(userDto);
        Item item = ItemMapper.INSTANCE.fromDto(itemDto);
        Mockito.when(itemRepository.findById(userId))
                .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findBookingByItem_Id(userId))
                .thenReturn(Arrays.asList(booking1, booking2));
        Mockito.when(commentRepository.findAllByItem_Id(userId))
                .thenReturn(Collections.singletonList(comment));

        ItemDtoResponse itemDtoResponse = itemService.getById(itemId, userId);
        assertThat(itemDtoResponse.getNextBooking().getId(), equalTo(1L));
        assertThat(itemDtoResponse.getLastBooking(), nullValue());
    }

    @Test
    void getById_whenItemNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        long itemId = 1L;
        User user = UserMapper.INSTANCE.fromDto(userDto);
        itemDto.setOwner(userDto);
        Item item = ItemMapper.INSTANCE.fromDto(itemDto);
        Mockito.when(itemRepository.findById(userId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.getById(itemId, userId));
        assertThat(exception.getMessage(), equalTo("Инструмент с таким id отсутствует!"));
    }

    @Test
    void getAll_whenInvoked_thenReturnListOfOneItemDtoResponse() {
        long ownerId = 1L;
        int from = 0;
        int size = 5;
        Mockito.when(itemRepository.findAllByOwnerId(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(Collections.singletonList(item));
        Mockito.when(bookingRepository.findAllByItemOwnerId(ownerId))
                .thenReturn(Collections.singletonList(booking1));
        Mockito.when(commentRepository.findAllByItem_IdIn(Mockito.any()))
                .thenReturn(Collections.singletonList(comment));

        List<ItemDtoResponse> returnedList = itemService.getAll(ownerId, from, size);

        assertThat(returnedList, hasSize(1));
        assertThat(returnedList.get(0).getId(), is(1L));
    }

    @Test
    void update_whenItemDtoWithFields_thenUpdateFields() {
        long userId = 1L;
        long itemId = 1L;
        itemDto.setName("BeforeUpdate");
        itemDto.setDescription("BeforeUpdate");
        itemDto.setOwner(userDto);
        ItemDto itemWithUpdates = ItemDto.builder()
                .name("UpdatedName")
                .description("UpdatedDescription")
                .owner(userDto)
                .build();
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));


        itemService.update(userId, itemWithUpdates, itemId);


        Mockito.verify(itemRepository).save(itemArgumentCaptor.capture());
        Item updatedItem = itemArgumentCaptor.getValue();
        assertThat(updatedItem.getName(), equalTo("UpdatedName"));
        assertThat(updatedItem.getDescription(), equalTo("UpdatedDescription"));
    }

    @Test
    void update_whenItemNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        long itemId = 1L;
        itemDto.setName("BeforeUpdate");
        itemDto.setDescription("BeforeUpdate");
        itemDto.setOwner(userDto);
        ItemDto itemWithUpdates = ItemDto.builder()
                .name("UpdatedName")
                .description("UpdatedDescription")
                .owner(userDto)
                .build();
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.update(userId, itemWithUpdates, itemId));
        assertThat(exception.getMessage(), equalTo("Инструмент с таким id отсутствует!"));
    }

    @Test
    void update_whenItemDtoWithWrongOwner_thenNotFoundExceptionThrown() {
        long userId = 10L;
        long itemId = 1L;
        item.setName("BeforeUpdate");
        item.setDescription("BeforeUpdate");
        item.setOwner(user);
        ItemDto itemWithUpdates = ItemDto.builder()
                .name("UpdatedName")
                .description("UpdatedDescription")
                .owner(userDto)
                .build();
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));


        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.update(userId, itemWithUpdates, itemId));
        assertThat(exception.getMessage(), equalTo("Нет подходящего инструмента для этого пользователя"));
    }

    @Test
    void getSearchResults_whenInvoked_thenReturnListWithOneItem() {
        String text = "aSd";
        int from = 0;
        int size = 5;
        Mockito.when(itemRepository.searchByText(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(Collections.singletonList(item));

        List<ItemDto> searchResults = itemService.getSearchResults(text, from, size);
        assertThat(searchResults, hasSize(1));
    }

    @Test
    void postComment_whenInvoked_thenSaveComment() {
        long userId = 1L;
        long itemId = 1L;
        comment.setText("testText");
        commentDto = CommentMapper.INSTANCE.toDto(comment);
        booking2.setStatus(BookingStatus.APPROVED);
        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findFirstByItem_IdAndBooker_Id(itemId, userId))
                .thenReturn(Optional.of(booking2));

        itemService.postComment(userId, itemId, commentDto);

        Mockito.verify(commentRepository).save(commentArgumentCaptor.capture());
        Comment testComment = commentArgumentCaptor.getValue();
        assertThat(testComment.getText(), equalTo("testText"));
    }

    @Test
    void postComment_whenStatusWrong_thenValidationExceptionThrown() {
        long userId = 1L;
        long itemId = 1L;
        commentDto.setText("testText");
        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findFirstByItem_IdAndBooker_Id(itemId, userId))
                .thenReturn(Optional.of(booking2));


        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.postComment(userId, itemId, commentDto));
        assertThat(exception.getMessage(), equalTo("Нет прав оставлять комментарий!"));
    }

    @Test
    void postComment_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        long itemId = 1L;
        commentDto.setText("testText");
        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.postComment(userId, itemId, commentDto));
        assertThat(exception.getMessage(), equalTo("Пользователь с таким id отсутствует!"));
    }

    @Test
    void postComment_whenItemNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        long itemId = 1L;
        commentDto.setText("testText");
        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.postComment(userId, itemId, commentDto));
        assertThat(exception.getMessage(), equalTo("Инструмент с таким id отсутствует!"));
    }

    @Test
    void postComment_whenBookingNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        long itemId = 1L;
        commentDto.setText("testText");
        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findFirstByItem_IdAndBooker_Id(itemId, userId))
                .thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.postComment(userId, itemId, commentDto));
        assertThat(exception.getMessage(), equalTo("Бронирование отсутствует!"));
    }
}