package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(long ownerId, Pageable paging);

    @Query("select i from Item i " +
            " where i.available = true " +
            "and (lower(i.name) like %?1% " +
            "or lower(i.description) like %?1%)")
    List<Item> searchByText(String text, Pageable pageable);

    List<Item> findAllByRequestIdIn(List<Long> itemRequest);

    List<Item> findAllByRequestId(long itemRequest);
}
