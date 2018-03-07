package ch.fhnw.wodss.tippspiel.Persistance;

import ch.fhnw.wodss.tippspiel.Domain.BetGroup;
import ch.fhnw.wodss.tippspiel.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BetGroupRepository extends JpaRepository<BetGroup, Long> {

    BetGroup getBetGroupByName(String name);

    @Query("SELECT b.User from BetGroup b where b.id = :betGroup")
    List<User> getUserInBetGroup(@Param("betGroup") long betGroupId);

}
