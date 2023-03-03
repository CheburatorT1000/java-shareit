package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.PageableMaker;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingClient bookingClient;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(SHARER_USER_ID) long userId,
                                                @Validated({Create.class}) @RequestBody BookingDtoCreate bookingDtoCreate) {
        return bookingClient.createBooking(bookingDtoCreate, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> bookingApprove(@RequestHeader(SHARER_USER_ID) long ownerId,
                                                 @PathVariable long bookingId,
                                                 @RequestParam boolean approved) {
        return bookingClient.bookingApprove(bookingId, ownerId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@RequestHeader(SHARER_USER_ID) long userId,
                                           @PathVariable long bookingId) {
        return bookingClient.getBooking(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findByParam(@RequestHeader(SHARER_USER_ID) long userId,
                                              @RequestParam(required = false, defaultValue = "ALL") String state,
                                              @RequestParam(required = false, defaultValue = "0") Integer from,
                                              @RequestParam(required = false, defaultValue = "10") Integer size) {
        Pageable pageable = PageableMaker.makePageable(from, size, Sort.by(Sort.Direction.DESC, "id"));
        BookingStatus status;
        try {
            status = BookingStatus.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + state);
        }
        log.info("{}, {}, {}, {}", userId, status, from, size);
        return bookingClient.findAllByParam(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findByOwner(@RequestHeader(SHARER_USER_ID) long userId,
                                              @RequestParam(required = false, defaultValue = "ALL") String state,
                                              @RequestParam(required = false, defaultValue = "0") Integer from,
                                              @RequestParam(required = false, defaultValue = "10") Integer size) {
        Pageable pageable = PageableMaker.makePageable(from, size, Sort.by(Sort.Direction.DESC, "id"));
        BookingStatus status;
        try {
            status = BookingStatus.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + state);
        }
        return bookingClient.findAllByOwner(userId, state, from, size);
    }
}
