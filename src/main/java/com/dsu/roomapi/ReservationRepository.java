package com.dsu.roomapi;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByRoomIdOrderByStartTime(Long roomId);

    boolean existsByRoomId(Long roomId);

    boolean existsByRoomIdAndStartTimeLessThanAndEndTimeGreaterThan(Long roomId, LocalDateTime end, LocalDateTime start);

    @Query("select r from Reservation r join fetch r.room where r.reservedBy = :reservedBy order by r.startTime")
    List<Reservation> findByReservedByWithRoom(String reservedBy);
}
