package ch.fhnw.wodss.tippspiel.Services;

import ch.fhnw.wodss.tippspiel.DTOs.BetGroupDTO;
import ch.fhnw.wodss.tippspiel.DTOs.UserAllBetGroupDTO;
import ch.fhnw.wodss.tippspiel.Domain.BetGroup;
import ch.fhnw.wodss.tippspiel.Domain.User;
import ch.fhnw.wodss.tippspiel.Exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.Persistance.BetGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;


@Service
@Transactional
public class BetGroupService {

    private final BetGroupRepository repository;

    @Autowired
    public BetGroupService(BetGroupRepository repository) {
        this.repository = repository;
    }

    public List<UserAllBetGroupDTO> getAllUsersInBetGroup(Long id) {
        List<User> users = repository.getUserInBetGroup(id);
        // Todo Convert to DTO
        return null;
    }

    public List<BetGroupDTO> getAllBetGroups() {
        List<BetGroup> betGroups = repository.findAll();
        // ToDo Convert to DTO
        return null;
    }

    public BetGroup getBetGroupById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find BetGroup with id: " + id));
    }

    public BetGroup getBetGroupByName(String name) {
        return repository.getBetGroupByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find BetGroup with name: " + name));
    }

    public BetGroup addUser(Long betGroupId, User user) {
        return null;
    }

    public BetGroup createBetGroup(BetGroup betGroup) {
        return null;
    }

    public void deleteBetGroup(Long id) {

    }


}
