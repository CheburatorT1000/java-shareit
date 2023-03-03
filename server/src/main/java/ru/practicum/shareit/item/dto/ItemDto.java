package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.user.UserDto;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private UserDto owner;
    private Long requestId;
}
