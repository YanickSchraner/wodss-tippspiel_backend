package ch.fhnw.wodss.tippspiel.service;

import ch.fhnw.wodss.tippspiel.domain.Bet;
import ch.fhnw.wodss.tippspiel.domain.BetGroup;
import ch.fhnw.wodss.tippspiel.dto.*;
import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.exception.IllegalActionException;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.persistance.UserRepository;
import ch.fhnw.wodss.tippspiel.security.Argon2PasswordEncoder;
import com.sun.org.apache.xpath.internal.Arg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
public class UserService {

    private final UserRepository repository;
    private final BetService betService;
    private final BetGroupService betGroupService;
    private final Argon2PasswordEncoder argon2PasswordEncoder;

    private List<UserRankingDTO> createAllUsersForRankingDTOList(List<User> users) {
        List<UserRankingDTO> dtos = new ArrayList<>();
        for (User user : users) {
            UserRankingDTO dto = new UserRankingDTO();
            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setScore(user.getBets().stream().mapToInt(Bet::getScore).sum());
        }
        return dtos;
    }

    @Autowired
    public UserService(UserRepository repository, BetGroupService betGroupService, BetService betService, Argon2PasswordEncoder argon2PasswordEncoder) {
        this.repository = repository;
        this.betGroupService = betGroupService;
        this.betService = betService;
        this.argon2PasswordEncoder = argon2PasswordEncoder;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<UserDTO> getAllUsers() {
        List<User> users = repository.findAll();
        List<UserDTO> userDTOS = new ArrayList<>();
        for (User user : users) {
            userDTOS.add(convertUserToUserDTO(user));
        }
        return userDTOS;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<UserRankingDTO> getAllUsersForRanking() {
        Sort sort = new Sort(Sort.Direction.ASC, "score");
        List<User> users = repository.findAll(sort);
        return createAllUsersForRankingDTOList(users);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public UserDTO getUserById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can't find a user with id: " + id));
        return convertUserToUserDTO(user);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public UserDTO getUserByName(String name) {
        User user = repository.findUserByNameEquals(name)
                .orElseThrow(() -> new ResourceNotFoundException("Can't find a user with name: " + name));
        return convertUserToUserDTO(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserDTO addUser(RestUserDTO restUserDTO) {
        if (repository.findUserByNameEquals(restUserDTO.getName()).isPresent()) {
            throw new IllegalActionException("User with name: " + restUserDTO.getName() + " already exists");
        }
        if (repository.findUserByEmailEquals(restUserDTO.getEmail()).isPresent()) {
            throw new IllegalActionException("User with email: " + restUserDTO.getEmail() + " already exists");
        }
        User user = new User();
        user.setName(restUserDTO.getName());
        user.setEmail(restUserDTO.getEmail());
        user.setPassword(restUserDTO.getPassword());
        user.setReminders(restUserDTO.isReminders());
        user.setDailyResults(restUserDTO.isDailyResults());
        user = repository.save(user);
        return convertUserToUserDTO(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(Long id, User user) {
        if (!id.equals(user.getId())) throw new IllegalActionException("You can't delete another user!");
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Can't find the given user to delete");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserDTO changeEmail(User user, RestUserDTO restUserDTO) {
        Optional<User> userToUpdate = repository.findById(user.getId());
        if (userToUpdate.isPresent()) {
            userToUpdate.get().setEmail(restUserDTO.getEmail());
            user = repository.save(userToUpdate.get());
            return convertUserToUserDTO(user);
        } else {
            throw new ResourceNotFoundException("Can't find the given user to change the email address.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void changePassword(User user, String oldPassword, String newPassword) {
        Optional<User> userToUpdate = repository.findById(user.getId());
        if (userToUpdate.isPresent()) {
            boolean correctPW = argon2PasswordEncoder.matches(userToUpdate.get().getPassword(), argon2PasswordEncoder.encode(oldPassword));
            if (!correctPW) throw new IllegalActionException("You entered a wrong password!");
            userToUpdate.get().setPassword(argon2PasswordEncoder.encode(newPassword));
            repository.save(userToUpdate.get());
        } else {
            throw new ResourceNotFoundException("Can't find the given user to change the password.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void resetPassword(User user) {
        // Todo with email integration
    }

    private UserDTO convertUserToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        List<BetDTO> betDTOs = user.getBets().stream().map(betService::convertBetToBetDTO).collect(Collectors.toList());
        List<BetGroupDTO> betGroupDTOs = user.getBetGroups().stream().map(betGroupService::convertBetGroupToBetGroupDTO).collect(Collectors.toList());
        userDTO.setBets(betDTOs);
        userDTO.setBetGroups(betGroupDTOs);
        userDTO.setName(user.getName());
        userDTO.setPassword(user.getPassword());
        userDTO.setEmail(user.getEmail());
        userDTO.setReminders(user.isReminders());
        userDTO.setDailyResults(user.isDailyResults());
        userDTO.setRole(user.getRoles());
        return new UserDTO();
    }

}
