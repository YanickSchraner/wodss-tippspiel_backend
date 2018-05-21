package ch.fhnw.wodss.tippspiel.service;

import ch.fhnw.wodss.tippspiel.domain.Bet;
import ch.fhnw.wodss.tippspiel.domain.BetGroup;
import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.dto.BetGroupDTO;
import ch.fhnw.wodss.tippspiel.dto.RestBetGroupDTO;
import ch.fhnw.wodss.tippspiel.dto.UserAllBetGroupDTO;
import ch.fhnw.wodss.tippspiel.exception.IllegalActionException;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotAllowedException;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.persistance.BetGroupRepository;
import ch.fhnw.wodss.tippspiel.persistance.UserRepository;
import ch.fhnw.wodss.tippspiel.security.Argon2PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
public class BetGroupService {

    private final BetGroupRepository betGroupRepository;
    private final UserRepository userRepository;
    private final Argon2PasswordEncoder argon2PasswordEncoder;

    @Autowired
    public BetGroupService(BetGroupRepository betGroupRepository, UserRepository userRepository, Argon2PasswordEncoder argon2PasswordEncoder) {
        this.betGroupRepository = betGroupRepository;
        this.userRepository = userRepository;
        this.argon2PasswordEncoder = argon2PasswordEncoder;
    }

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
            dtos.add(convertBetGroupToBetGroupDTO(betGroup));
        }
        return dtos;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<UserAllBetGroupDTO> getAllUsersInBetGroup(Long id) {
        List<User> users = betGroupRepository.getUserInBetGroup(id);
        return createAllUsersInBetGroupDTOList(users);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<BetGroupDTO> getAllBetGroups() {
        List<BetGroup> betGroups = betGroupRepository.findAll();
        return createAllBetGroupDTOList(betGroups);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public BetGroupDTO getBetGroupById(Long id) {
        BetGroup betGroup = betGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can't find a bet group with id: " + id));
        return convertBetGroupToBetGroupDTO(betGroup);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public BetGroupDTO getBetGroupByName(String name) {
        BetGroup betGroup = betGroupRepository.findBetGroupByNameEquals(name)
                .orElseThrow(() -> new ResourceNotFoundException("Can't find a bet group with name: " + name));
        return convertBetGroupToBetGroupDTO(betGroup);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public BetGroupDTO addUser(Long betGroupId, User user, String password) {
        userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Could not find given user."));
        BetGroup betGroup = betGroupRepository.findById(betGroupId)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find bet group with id: " + betGroupId));
        boolean containsUser = betGroupRepository.existsBetGroupsByMembersContaining(user.getId());
        if (!containsUser) {
            if (betGroup.getPassword() != null && password != null) {
                if (!argon2PasswordEncoder.matches(betGroup.getPassword(), password)) {
                    throw new ResourceNotAllowedException("Wrong password for this bet group!");
                }
            }
            List<User> users = betGroup.getMembers();
            users.add(user);
            betGroup.setMembers(users);
            betGroup.setId(betGroupId);
            betGroupRepository.save(betGroup);
            return convertBetGroupToBetGroupDTO(betGroup);
        } else {
            throw new IllegalActionException("User with name: " + user.getName() + " is already part of the given bet group.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeUserFromBetGroup(Long betGroupId, User user) {
        userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Could not find given user."));
        betGroupRepository.findById(betGroupId)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find bet group with id: " + betGroupId));
        boolean containsUser = betGroupRepository.existsBetGroupsByMembersContaining(user.getId());
        if (containsUser) {
            deleteBetGroup(betGroupId);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public BetGroupDTO createBetGroup(RestBetGroupDTO restBetGroupDTO) {
        if (betGroupRepository.findBetGroupByNameEquals(restBetGroupDTO.getName()).isPresent()) {
            throw new IllegalActionException("A bet group with name: " + restBetGroupDTO.getName() + " already exists.");
        }
        BetGroup betGroup = new BetGroup();
        betGroup.setName(restBetGroupDTO.getName());
        if (restBetGroupDTO.getName() != null) {
            betGroup.setPassword(argon2PasswordEncoder.encode(restBetGroupDTO.getPassword()));
        }
        betGroup = betGroupRepository.save(betGroup);
        return convertBetGroupToBetGroupDTO(betGroup);
    }

    @Transactional(propagation = Propagation.REQUIRED)
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

    protected BetGroupDTO convertBetGroupToBetGroupDTO(BetGroup betGroup) {
        BetGroupDTO betGroupDTO = new BetGroupDTO();
        betGroupDTO.setId(betGroup.getId());
        betGroupDTO.setName(betGroup.getName());
        betGroupDTO.setScore(betGroup.getScore());
        betGroupDTO.setUserIds(betGroup.getMembers().stream().map(User::getId).collect(Collectors.toList()));
        return new BetGroupDTO();
    }

}
