package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;
    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    void save_whenInvoked_thenSaveUser() {
        UserDto userDtoToSave = UserDto.builder()
                .name("name")
                .email("email@mail.com")
                .build();
        User returnedUser = User.builder()
                .name("returnedUserName")
                .email("email@mail.com")
                .build();
        when(userRepository.save(any()))
                .thenReturn(returnedUser);


        UserDto userDtoActual = userServiceImpl.save(userDtoToSave);


        assertEquals("returnedUserName", userDtoActual.getName());
        verify(userRepository, times(1))
                .save(UserMapper.INSTANCE.fromDto(userDtoToSave));
    }

    @Test
    void findById_whenUserFound_thenReturnUser() {
        long userId = 1L;
        User expectedUser = User.builder()
                .id(userId)
                .name("name")
                .email("email@mail.com")
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(expectedUser));


        User actualUser = UserMapper.INSTANCE.fromDto(userServiceImpl.findById(userId));


        assertEquals(expectedUser, actualUser);
    }

    @Test
    void findById_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userServiceImpl.findById(userId));
    }

    @Test
    void findAll_whenInvoked_thenGetEmptyList() {
        when(userRepository.findAll())
                .thenReturn(Collections.emptyList());


        List<UserDto> all = userServiceImpl.findAll();


        verify(userRepository, times(1))
                .findAll();
        assertTrue(all.isEmpty());
    }

    @Test
    void update_whenUserHasFields_thenUpdateOnlyAvailableFields() {
        long userId = 1L;
        User userBeforeUpdate = User.builder()
                .id(userId)
                .name("name")
                .email("email@mail.com")
                .build();
        UserDto userUpdate = UserDto.builder()
                .id(userId)
                .name("name2")
                .email("email@mail.com2")
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(userBeforeUpdate));


        userServiceImpl.update(userId, userUpdate);


        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();
        assertEquals(1L, capturedUser.getId());
        assertEquals("name2", capturedUser.getName());
        assertEquals("email@mail.com2", capturedUser.getEmail());
    }

    @Test
    void update_whenUserWithEmptyFields_thenUpdateNothing() {
        long userId = 1L;
        User userBeforeUpdate = User.builder()
                .id(userId)
                .name("name")
                .email("email@mail.com")
                .build();
        UserDto userUpdate = UserDto.builder()
                .id(userId)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(userBeforeUpdate));


        userServiceImpl.update(userId, userUpdate);


        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();
        assertEquals(1L, capturedUser.getId());
        assertEquals("name", capturedUser.getName());
        assertEquals("email@mail.com", capturedUser.getEmail());
    }

    @Test
    void delete() {
        long userId = 1L;


        userServiceImpl.delete(userId);


        verify(userRepository, times(1))
                .deleteById(userId);
    }
}