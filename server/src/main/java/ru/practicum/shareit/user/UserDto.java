package ru.practicum.shareit.user;

import lombok.*;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private long id;
    @NotNull(groups = {Create.class})
    private String name;
    @NotNull(groups = {Create.class})
    @Email(groups = {Update.class, Create.class})
    private String email;
}