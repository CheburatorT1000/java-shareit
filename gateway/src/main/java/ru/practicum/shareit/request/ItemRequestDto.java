package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.utils.Create;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {

    private Long id;
    @NotNull(groups = {Create.class})
    private String description;

    private UserDto requestor;

    private LocalDateTime created;

    private List<ItemDto> items;
}