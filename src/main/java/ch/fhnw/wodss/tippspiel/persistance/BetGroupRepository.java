package ch.fhnw.wodss.tippspiel.persistance;

import ch.fhnw.wodss.tippspiel.domain.BetGroup;
import ch.fhnw.wodss.tippspiel.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BetGroupRepository extends JpaRepository<BetGroup, Long> {

    Optional<BetGroup> findBetGroupByNameEquals(String name);

    @Query("SELECT b.User from BetGroup b where b.id = :betGroup")
    List<User> getUserInBetGroup(@Param("betGroup") Long betGroupId);

    boolean existsBetGroupByMembersIsWithin(Long userId);

    @Query("SELECT count(user) FROM BetGroup betGroup WHERE :id in (betGroup.members)")
    boolean hasMembers(@Param("id") Long id);

}
