package ch.fhnw.wodss.tippspiel.persistance;

import ch.fhnw.wodss.tippspiel.domain.Bet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BetRepository extends JpaRepository<Bet, Long> {

    boolean existsBetsByGame_Id(Long gameId);

    boolean existsBetByUser_IdAndGame_Id(Long userId, Long gameId);

    @Query("SELECT b FROM Bet b WHERE b.user = :betUser")
    List<Bet> getBetsForUser(@Param("betUser") Long userId);

    @Query("SELECT b FROM Bet b WHERE b.game.dateTime BETWEEN :start AND :end")
    List<Bet> getTodayBets(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(b.score) FROM Bet b WHERE b.user = :user AND  b.game.dateTime BETWEEN :start AND :end")
    int getTodayScore(@Param("user") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(b.score) FROM Bet b WHERE  b.user = :user")
    int getUserScore(@Param("user") Long userId);
}
