package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utils.PageableMaker;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


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
    public List<ItemRequestDto> findAllByRequestorId(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не существует!"));
        List<ItemRequest> allItemRequests = requestRepository.findAllByRequestorId(userId);

        List<Long> requestsIds = allItemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<Item> allItems = itemRepository.findAllByRequestIdIn(requestsIds);

        return getItemRequestDtosWithItems(allItemRequests, allItems);
    }

    @Override
    public List<ItemRequestDto> findAllByParams(long userId, int from, int size) {
        Pageable pageable = PageableMaker.makePageable(from, size, Sort.by(Sort.Direction.ASC, "created"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не существует!"));
        List<ItemRequest> allItemRequests = requestRepository.findAllByRequestorIdNotIn(Collections.singletonList(userId),
                pageable);
        List<Long> requestsIds = allItemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<Item> allItems = itemRepository.findAllByRequestIdIn(requestsIds);

        return getItemRequestDtosWithItems(allItemRequests, allItems);
    }

    @Override
    public ItemRequestDto findById(long userId, long requestId) {
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не существует!"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не существует!"));

        List<Item> allItems = itemRepository.findAllByRequestId(requestId);
        ItemRequestDto itemRequestDto = ItemRequestMapper.INSTANCE.toDto(itemRequest);

        itemRequestDto.setItems(allItems.stream()
                .map(ItemMapper.INSTANCE::toDto)
                .collect(Collectors.toList()));

        return itemRequestDto;
    }

    private static List<ItemRequestDto> getItemRequestDtosWithItems(List<ItemRequest> allItemRequests, List<Item> allItems) {
        List<ItemRequestDto> itemRequestDtos = allItemRequests.stream()
                .map(ItemRequestMapper.INSTANCE::toDto)
                .collect(Collectors.toList());

        itemRequestDtos.forEach(itemRequestDto ->
                itemRequestDto.setItems(allItems.stream()
                        .filter(item -> item.getRequest().getId().equals(itemRequestDto.getId()))
                        .map(ItemMapper.INSTANCE::toDto)
                        .collect(Collectors.toList())));
        return itemRequestDtos;
    }
}