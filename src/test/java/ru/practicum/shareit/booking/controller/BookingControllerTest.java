package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;
    public static final String SHARER_USER_ID = "X-Sharer-User-Id";
    @InjectMocks
    private BookingController bookingController;
    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    private MockMvc mvc;
    private UserDto userDto;
    private ItemDto itemDto;
    private CommentDto commentDto;
    private BookingDto bookingDto;
    private BookingDtoShort bookingDtoShort;
    private BookingDtoCreate bookingDtoCreate;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();
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
        commentDto = CommentDto.builder()
                .id(1L)
                .text("comment text")
                .authorName("Vasya")
                .build();
        bookingDto = BookingDto.builder()
                .id(1L)
                .build();
        bookingDtoShort = BookingDtoShort.builder()
                .id(1L)
                .build();
        bookingDtoCreate = BookingDtoCreate.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .itemId(1L)
                .build();
    }


    @SneakyThrows
    @Test
    void save() {
        long userId = 1L;
        when(bookingService.save(any(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoCreate))
                        .header(SHARER_USER_ID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void bookingApprove() {
    }

    @Test
    void findById() {
    }

    @Test
    void findByParam() {
    }

    @Test
    void findByOwner() {
    }
}