package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto save(ItemRequestDto itemRequestDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не существует!"));
        ItemRequest itemRequest = ItemRequestMapper.INSTANCE.fromDto(itemRequestDto);

        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        return ItemRequestMapper.INSTANCE.toDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> findAllById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не существует!"));

        List<ItemRequest> allItemRequests = requestRepository.findAllByRequestor(user);

        return allItemRequests.stream()
                .map(ItemRequestMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> findAllByIdAndParams(long userId, int from, int size) {
        Sort sortByDateTime = Sort.by(Sort.Direction.ASC, String.valueOf(LocalDateTime.now()));
        Pageable page = PageRequest.of(from, size, sortByDateTime);
        return
    }
}