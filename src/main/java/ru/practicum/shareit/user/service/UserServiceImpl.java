package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    @Override
    public UserDto save(UserDto userDto) {
        User user = UserDtoMapper.fromUserDtoToUser(userDto);
        return UserDtoMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto findById(long id) {
        return UserDtoMapper.toUserDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден!")));
    }

    @Override
    public List<UserDto> findAll() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(UserDtoMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(long id, UserDto userDto) {
        User userToUpdate = UserDtoMapper.fromUserDtoToUser(findById(id));

        if (userDto.getName() != null)
            userToUpdate.setName(userDto.getName());

        if (userDto.getEmail() != null) {
            userToUpdate.setEmail(userDto.getEmail());
        }

        return UserDtoMapper.toUserDto(userRepository.save(userToUpdate));
    }

    @Override
    public void delete(long id) {
        userRepository.deleteById(id);
    }

}