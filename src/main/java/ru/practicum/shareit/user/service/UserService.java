package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;

    public UserDto create(UserDto userDto) {

        if(userRepository.checkExistEmail(userDto.getEmail()))
            throw new ValidationException("Пользователь с таким email уже существует!");

        User user = userRepository.create(userDtoMapper.fromUserDtoToUser(userDto));

        return userDtoMapper.toUserDto(user);
    }

    public UserDto update(long id, UserDto userDto) {

        if(!userRepository.checkExistId(id))
            throw new NotFoundException("Пользователь с таким id отсутствует!");

        if(userRepository.checkExistEmail(userDto.getEmail()))
            throw new ValidationException("Пользователь с таким email уже существует!");

        User userToUpdate = userRepository.read(id);

        if(userDto.getName() != null)
            userToUpdate.setName(userDto.getName());

        if(userDto.getEmail() != null)
            userToUpdate.setEmail(userDto.getEmail());

        return userDtoMapper.toUserDto(userToUpdate);
    }

    public UserDto getById(long id) {
        return userDtoMapper.toUserDto(userRepository.read(id));
    }

    public void delete(long id) {
        userRepository.delete(id);
    }

    public List<UserDto> getAll() {
        List<User> users = userRepository.getAll();

        return users.stream()
                .map(userDtoMapper::toUserDto)
                .collect(Collectors.toList());
    }
}