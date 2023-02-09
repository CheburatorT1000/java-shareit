package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.Create;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ItemRequestDto {

    private int id;
    @NotNull(groups = {Create.class})
    private String description;

    private User requestor;

    private LocalDateTime created;
}
