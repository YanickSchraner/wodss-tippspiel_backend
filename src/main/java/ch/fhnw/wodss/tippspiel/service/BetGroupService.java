package ch.fhnw.wodss.tippspiel.service;

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
import java.util.Optional;
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
    public BetGroupDTO addUser(Long betGroupId, String password, User user) {
        userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Could not find given user."));
        BetGroup betGroup = betGroupRepository.findById(betGroupId)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find bet group with id: " + betGroupId));
        boolean containsUser = betGroupRepository.existsBetGroupsByMembersContaining(user);
        if (!containsUser) {
            if (betGroup.getPassword() != null) {
                if (password == null || !argon2PasswordEncoder.matches(password, betGroup.getPassword())) {
                    throw new ResourceNotAllowedException("Wrong password for this bet group!");
                }
            }
            List<User> users = betGroup.getMembers();
            users.add(user);
            betGroup.setMembers(users);
            betGroup.setId(betGroupId);
            betGroup = betGroupRepository.saveAndFlush(betGroup);
            user = userRepository.findById(user.getId()).get();
            List<BetGroup> betGroups = user.getBetGroups();
            betGroups.add(betGroup);
            user.setBetGroups(betGroups);
            userRepository.save(user);
            return convertBetGroupToBetGroupDTO(betGroup);
        } else {
            throw new IllegalActionException("User with name: " + user.getName() + " is already part of the given bet group.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeUserFromBetGroup(Long betGroupId, User user) {
        userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Could not find given user."));
        BetGroup betGroup = betGroupRepository.findById(betGroupId)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find bet group with id: " + betGroupId));
        boolean containsUser = betGroupRepository.existsBetGroupsByMembersContaining(user);
        if (containsUser) {
            List<User> users = betGroup.getMembers();
            users.remove(user);
            betGroup.setMembers(users);
            betGroupRepository.saveAndFlush(betGroup);
            user = userRepository.findById(user.getId()).get();
            List<BetGroup> betGroups = user.getBetGroups();
            betGroups.remove(betGroup);
            user.setBetGroups(betGroups);
            userRepository.save(user);
            if (betGroup.getMembers().isEmpty()) {
                deleteBetGroup(betGroupId);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public BetGroupDTO createBetGroup(RestBetGroupDTO restBetGroupDTO, User user) {
        if (betGroupRepository.findBetGroupByNameEquals(restBetGroupDTO.getName()).isPresent()) {
            throw new IllegalActionException("A bet group with name: " + restBetGroupDTO.getName() + " already exists.");
        }
        BetGroup betGroup = new BetGroup();
        betGroup.setName(restBetGroupDTO.getName());
        if (restBetGroupDTO.getPassword() != null) {
            betGroup.setPassword(argon2PasswordEncoder.encode(restBetGroupDTO.getPassword()));
        }
        List<User> members = new ArrayList<>();
        members.add(user);
        betGroup.setMembers(members);
        betGroup = betGroupRepository.save(betGroup);
        List<BetGroup> betGroups = new ArrayList<>();
        betGroups.add(betGroup);
        user.setBetGroups(betGroups);
        userRepository.save(user);
        return convertBetGroupToBetGroupDTO(betGroup);
    }

    private void deleteBetGroup(Long id) {
        Optional<BetGroup> betGroup = betGroupRepository.findById(id);
        if (!betGroup.isPresent()) throw new ResourceNotFoundException("Can't find a bet group with id: " + id);
        if (!betGroup.get().getMembers().isEmpty()) {
            throw new IllegalActionException("Can't delete a bet group with bet group members");
        }
        betGroupRepository.deleteById(id);
    }

    private List<UserAllBetGroupDTO> createAllUsersInBetGroupDTOList(List<User> users) {
        List<UserAllBetGroupDTO> dtos = new ArrayList<>();
        for (User user : users) {
            UserAllBetGroupDTO dto = new UserAllBetGroupDTO();
            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setScore(user.getBets().stream().mapToInt(bet -> bet.getScore() == null ? 0 : bet.getScore()).sum());
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

    protected BetGroupDTO convertBetGroupToBetGroupDTO(BetGroup betGroup) {
        BetGroupDTO betGroupDTO = new BetGroupDTO();
        betGroupDTO.setId(betGroup.getId());
        betGroupDTO.setName(betGroup.getName());
        int score = betGroup.getScore() == null ? 0 : betGroup.getScore();
        betGroupDTO.setScore(score);
        List<Long> userIds = betGroup.getMembers() == null ? new ArrayList<>() : betGroup.getMembers().stream().map(User::getId).collect(Collectors.toList());
        betGroupDTO.setUserIds(userIds);
        return betGroupDTO;
    }

}
