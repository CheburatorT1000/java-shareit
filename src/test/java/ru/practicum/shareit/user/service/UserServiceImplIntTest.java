package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntTest {

    private final EntityManager entityManager;
    private final UserService userService;

    @Test
    void update() {
        long userId = 1L;
        UserDto userDto = UserDto.builder()
                .id(userId)
                .name("userName")
                .email("user@mail.com")
                .build();
        userService.save(userDto);

        UserDto userDtoUpdate = UserDto.builder()
                .id(userId)
                .email("user@mail.com")
                .build();
        userService.update(userId, userDtoUpdate);


        TypedQuery<User> query =
                entityManager.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", userDto.getId())
                .getSingleResult();
        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), notNullValue());
        assertThat(user.getEmail(), equalTo(userDtoUpdate.getEmail()));
    }
}