package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingByBookerIdOrderByIdDesc(long userId, Pageable pageable);

    List<Booking> findBookingByBookerIdAndStartIsBeforeAndEndIsAfter(long userId,
                                                                     LocalDateTime dateTime1,
                                                                     LocalDateTime dateTime2,
                                                                     Pageable pageable);

    List<Booking> findBookingByBookerIdAndEndIsBeforeAndStatusIs(long userId,
                                                                 LocalDateTime current,
                                                                 BookingStatus status,
                                                                 Pageable pageable);

    List<Booking> findBookingByBookerIdAndStartIsAfterOrderByStartDesc(long userId,
                                                                       LocalDateTime current,
                                                                       Pageable pageable);

    List<Booking> findBookingByBookerIdAndStatus(long userId,
                                                 BookingStatus status,
                                                 Pageable pageable);

    List<Booking> findBookingByItemOwnerIdOrderByIdDesc(long userId,
                                                        Pageable pageable);

    List<Booking> findBookingByItemOwnerIdAndStartIsBeforeAndEndIsAfter(long userId,
                                                                        LocalDateTime dateTime1,
                                                                        LocalDateTime dateTime2,
                                                                        Pageable pageable);

    List<Booking> findBookingByItemOwnerIdAndEndIsBeforeAndStatusIs(long userId,
                                                                    LocalDateTime current,
                                                                    BookingStatus status,
                                                                    Pageable pageable);

    List<Booking> findBookingByItemOwnerIdAndStartIsAfterOrderByStartDesc(long userId,
                                                                          LocalDateTime current,
                                                                          Pageable pageable);

    List<Booking> findBookingByItemOwnerIdAndStatus(long userId,
                                                    BookingStatus status,
                                                    Pageable pageable);

    List<Booking> findBookingByItem_Id(long userId);

    List<Booking> findAllByItemOwnerId(long userId);

    Optional<Booking> findFirstByItem_IdAndBooker_Id(long itemId, long userId);

}
