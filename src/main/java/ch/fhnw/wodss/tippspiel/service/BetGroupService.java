package ch.fhnw.wodss.tippspiel.service;

import ch.fhnw.wodss.tippspiel.domain.Bet;
import ch.fhnw.wodss.tippspiel.dto.BetGroupDTO;
import ch.fhnw.wodss.tippspiel.dto.UserAllBetGroupDTO;
import ch.fhnw.wodss.tippspiel.domain.BetGroup;
import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.exception.IllegalActionException;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.persistance.BetGroupRepository;
import ch.fhnw.wodss.tippspiel.persistance.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class BetGroupService {

    private final BetGroupRepository betGroupRepository;
    private final UserRepository userRepository;

    private List<UserAllBetGroupDTO> createAllUsersInBetGroupDTOList(List<User> users) {
        List<UserAllBetGroupDTO> dtos = new ArrayList<>();
        for (User user : users) {
            UserAllBetGroupDTO dto = new UserAllBetGroupDTO();
            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setScore(user.getBets().stream().mapToInt(Bet::getScore).sum());
            dtos.add(dto);
        }
        return dtos;
    }

    private List<BetGroupDTO> createAllBetGroupDTOList(List<BetGroup> betGroups) {
        List<BetGroupDTO> dtos = new ArrayList<>();
        for (BetGroup betGroup : betGroups) {
            BetGroupDTO dto = new BetGroupDTO();
            dto.setId(betGroup.getId());
            dto.setName(betGroup.getName());
            dto.setScore(betGroup.getScore());
            dto.setMembers(betGroup.getMembers());
        }
        return dtos;
    }

    @Autowired
    public BetGroupService(BetGroupRepository betGroupRepository, UserRepository userRepository) {
        this.betGroupRepository = betGroupRepository;
        this.userRepository = userRepository;
    }

    public List<UserAllBetGroupDTO> getAllUsersInBetGroup(Long id) {
        List<User> users = betGroupRepository.getUserInBetGroup(id);
        return createAllUsersInBetGroupDTOList(users);
    }

    public List<BetGroupDTO> getAllBetGroups() {
        List<BetGroup> betGroups = betGroupRepository.findAll();
        return createAllBetGroupDTOList(betGroups);
    }

    public BetGroup getBetGroupById(Long id) {
        return betGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find bet group with id: " + id));
    }

    public BetGroup getBetGroupByName(String name) {
        return betGroupRepository.findBetGroupByNameEquals(name)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find bet group with name: " + name));
    }

    public BetGroup addUser(Long betGroupId, User user) {
        userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Could not find given user."));
        BetGroup betGroup = betGroupRepository.findById(betGroupId)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find bet group with id: " + betGroupId));
        boolean containsUser = betGroupRepository.existsBetGroupsByMembersContaining(user.getId());
        if (!containsUser) {
            List<User> users = betGroup.getMembers();
            users.add(user);
            betGroup.setMembers(users);
            betGroup.setId(betGroupId);
            return betGroupRepository.save(betGroup);
        } else {
            throw new IllegalActionException("User with name: " + user.getName() + " is already part of the given bet group.");
        }
    }

    // Todo
    public BetGroup removeUser(Long betGroupId, User user) {
        return null;
    }

    public BetGroup createBetGroup(BetGroup betGroup) {
        if (betGroupRepository.findBetGroupByNameEquals(betGroup.getName()).isPresent()) {
            throw new IllegalActionException("A bet group with name: " + betGroup.getName() + " already exists.");
        }
        return betGroupRepository.save(betGroup);
    }

    // Todo make private if we disable the deletion of a whole group
    public void deleteBetGroup(Long id) {
        if (betGroupRepository.hasMembers(id)) {
            throw new IllegalActionException("Can't delete a bet group with bet group members");
        }
        if (betGroupRepository.existsById(id)) {
            betGroupRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Can't find a bet group with id: " + id);
        }
    }


}
