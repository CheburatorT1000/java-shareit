package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemService itemService;
    public static final String SHARER_USER_ID = "X-Sharer-User-Id";
    @InjectMocks
    private ItemController itemController;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private UserDto userDto;
    private ItemDto itemDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
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
    }

    @SneakyThrows
    @Test
    void create_whenItemDtoWithValidFields_thenReturnItemDto() {
        ItemDto itemToSave = ItemDto.builder()
                .name("item name")
                .description("item description")
                .available(false)
                .build();

        when(itemService.save(anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemToSave))
                        .header(SHARER_USER_ID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @SneakyThrows
    @Test
    void create_whenItemDtoWithNotValidFields_thenReturnStatusBadRequest() {
        ItemDto itemDto1 = ItemDto.builder().build();

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto1))
                        .header(SHARER_USER_ID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getById_whenInvoked_thenGetItemDtoResponse() {
        long itemId = 1L;
        long userId = 1L;
        ItemDtoResponse itemDtoResponse = ItemDtoResponse.builder()
                .id(itemId)
                .name("item name")
                .description("item description")
                .available(false)
                .owner(UserMapper.INSTANCE.fromDto(userDto))
                .build();
        when(itemService.getById(userId, itemId))
                .thenReturn(itemDtoResponse);

        mvc.perform(get("/items/{itemId}", itemId)
                        .header(SHARER_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @SneakyThrows
    @Test
    void getAll_whenInvoked_thenGetEmptyList() {
        long userId = 1L;
        when(itemService.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/items")
                        .header(SHARER_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @SneakyThrows
    @Test
    void update_whenDtoHasFields_thenReturnItemDtoUpdated() {
        long userId = 1L;
        long itemId = 1L;
        ItemDto itemUpdate = ItemDto.builder()
                .name("item name")
                .description("item description")
                .available(false)
                .build();
        when(itemService.update(anyLong(), any(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header(SHARER_USER_ID, userId)
                        .content(mapper.writeValueAsString(itemUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @SneakyThrows
    @Test
    void getSearchResults_whenTextNotEmpty_thenGetListWithItemDto() {
        when(itemService.getSearchResults(anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(itemDto));

        mvc.perform(get("/items/search")
                        .param("text", "asasd")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @SneakyThrows
    @Test
    void getSearchResults_whenTextIsEmpty_thenGetEmptyList() {
        mvc.perform(get("/items/search")
                        .param("text", "")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @SneakyThrows
    @Test
    void postComment_whenInvoked_thenStatusIsOk() {
        long itemId = 1L;
        when(itemService.postComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(mapper.writeValueAsString(commentDto))
                        .header(SHARER_USER_ID, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }
}