package ch.fhnw.wodss.tippspiel.persistance;

import ch.fhnw.wodss.tippspiel.domain.Bet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BetRepository extends JpaRepository<Bet, Long> {

    boolean existsBetsByGame_Id(Long gameId);

    boolean existsBetByUser_IdAndGame_Id(Long userId, Long gameId);

    @Query("SELECT ALL FROM Bet b WHERE b.idUser = :betUser")
    List<Bet> getBetsForUser(@Param("betUser") Long userId);
}
