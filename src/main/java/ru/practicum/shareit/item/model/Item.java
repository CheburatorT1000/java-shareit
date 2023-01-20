package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class Item {
    long id;
    String name;
    String description;
    boolean available;
    long owner;
    Long request;
}
