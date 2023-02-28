package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void searchByText_whenInvoked_thenGetProperResult() {
        String searchString1 = "совковая";
        String searchString2 = "опата";
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "id"));
        User user = User.builder()
                .id(1L)
                .name("Vasya")
                .email("vasya@mail.ru")
                .build();
        Item item1 = Item.builder()
                .id(1L)
                .name("Лопата")
                .description("Лопата штыковая")
                .available(true)
                .owner(user)
                .build();
        Item item2 = Item.builder()
                .id(2L)
                .name("Лопата")
                .description("Лопата совковая")
                .available(true)
                .owner(user)
                .build();
        Item item3 = Item.builder()
                .id(3L)
                .name("Отвертка")
                .description("Плоская отвертка")
                .available(true)
                .owner(user)
                .build();

        userRepository.save(user);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);


        List<Item> resultList1 = itemRepository.searchByText(searchString1, pageable);
        List<Item> resultList2 = itemRepository.searchByText(searchString2, pageable);


        assertThat(resultList1, hasSize(1));
        assertThat(resultList1.get(0).getId(), equalTo(item2.getId()));
        assertThat(resultList1.get(0).getName(), equalTo(item2.getName()));
        assertThat(resultList1.get(0).getDescription(), equalTo(item2.getDescription()));
        assertThat(resultList1.get(0).getOwner(), equalTo(user));
        assertThat(resultList1.get(0), equalTo(item2));

        assertThat(resultList2, hasSize(2));
        assertThat(resultList2.get(0).getId(), equalTo(item1.getId()));
        assertThat(resultList2.get(0).getName(), equalTo(item1.getName()));
        assertThat(resultList2.get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(resultList2.get(1).getId(), equalTo(item2.getId()));
        assertThat(resultList2.get(1).getName(), equalTo(item2.getName()));
        assertThat(resultList2.get(1).getDescription(), equalTo(item2.getDescription()));
    }
}