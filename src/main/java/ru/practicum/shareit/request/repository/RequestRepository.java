package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestorId(long requestorId);

    List<ItemRequest> findAllByRequestorIdNotIn(List<Long> userId, Pageable paging);
}