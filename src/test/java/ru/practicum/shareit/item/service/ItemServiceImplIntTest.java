package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntTest {
    private final UserRepository userRepository;
    private final ItemService itemService;

    @Test
    void getSearchResults() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .name("vasya")
                .email("Vasya@mail.com")
                .build();
        userRepository.save(user);
        Item item = Item.builder()
                .id(1L)
                .name("otvertka")
                .description("otvertka description")
                .owner(user)
                .available(true)
                .build();
        ItemDto itemDto = ItemMapper.INSTANCE.toDto(item);
        itemService.save(userId, itemDto);
        String text = "vertk";
        int from = 0;
        int size = 5;


        List<ItemDto> searchResults = itemService.getSearchResults(text, from, size);


        assertThat(searchResults, hasSize(1));
        assertThat(searchResults.get(0).getName(), equalTo(itemDto.getName()));
        assertThat(searchResults.get(0).getOwner(), nullValue());
    }
}