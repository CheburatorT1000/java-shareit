package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @GetMapping("/{id}")
    public UserDto read(@PathVariable long id) {
        return userService.findById(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.findAll();
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id,
                          @RequestBody UserDto userDto) {
        return userService.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        userService.delete(id);
    }
}
