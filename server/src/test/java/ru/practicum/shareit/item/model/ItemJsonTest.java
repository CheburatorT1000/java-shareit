package ru.practicum.shareit.item.model;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemJsonTest {

    @Autowired
    private JacksonTester<Item> jacksonTester;

    @SneakyThrows
    @Test
    void testSerialization() {
        User vasya = User.builder()
                .id(1L)
                .name("Vasya")
                .email("vasya@mail.ru")
                .build();
        User petr = User.builder()
                .id(2L)
                .name("Petr")
                .email("petr@mail.ru")
                .build();
        ItemRequest requestDescription = ItemRequest.builder()
                .id(1L)
                .description("request description")
                .requestor(petr)
                .created(LocalDateTime.of(2023, Month.JANUARY, 22, 10, 00))
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("Отвертка")
                .description("Крестовая отвертка")
                .available(true)
                .owner(vasya)
                .request(requestDescription)
                .build();

        JsonContent<Item> json = jacksonTester.write(item);
        assertThat(json).extractingJsonPathNumberValue("$.id")
                .isEqualTo(item.getId().intValue());
        assertThat(json).extractingJsonPathStringValue("$.name")
                .isEqualTo(item.getName());
        assertThat(json).extractingJsonPathStringValue("$.description")
                .isEqualTo(item.getDescription());
        assertThat(json).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(item.getAvailable());
        assertThat(json).extractingJsonPathNumberValue("$.owner.id")
                .isEqualTo(item.getOwner().getId().intValue());
        assertThat(json).extractingJsonPathStringValue("$.owner.name")
                .isEqualTo(item.getOwner().getName());
        assertThat(json).extractingJsonPathStringValue("$.owner.email")
                .isEqualTo(item.getOwner().getEmail());
        assertThat(json).extractingJsonPathNumberValue("$.request.id")
                .isEqualTo(item.getRequest().getId().intValue());
        assertThat(json).extractingJsonPathStringValue("$.request.description")
                .isEqualTo(item.getRequest().getDescription());
    }
}