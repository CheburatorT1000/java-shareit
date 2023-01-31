package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

public interface UserService {

    UserDto save(UserDto userDto);

    UserDto update(long id, UserDto userDto);

    UserDto findById(long id);

    void delete(long id);

    List<UserDto> findAll();
}
