package ch.fhnw.wodss.tippspiel.service;

import ch.fhnw.wodss.tippspiel.domain.Bet;
import ch.fhnw.wodss.tippspiel.dto.UserRankingDTO;
import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.exception.IllegalActionException;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.persistance.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
public class UserService {

    private final UserRepository repository;

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
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<UserRankingDTO> getAllUsersForRanking() {
        Sort sort = new Sort(Sort.Direction.ASC, "score");
        List<User> users = repository.findAll(sort);
        return createAllUsersForRankingDTOList(users);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public User getUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can't find a user with id: " + id));
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public User getUserByName(String name) {
        return repository.findUserByNameEquals(name)
                .orElseThrow(() -> new ResourceNotFoundException("Can't find a user with name: " + name));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public User addUser(User user) {
        if (repository.findUserByNameEquals(user.getName()).isPresent()) {
            throw new IllegalActionException("User with name: " + user.getName() + " already exists");
        }
        if (repository.findUserByEmailEquals(user.getEmail()).isPresent()) {
            throw new IllegalActionException("User with email: " + user.getEmail() + " already exists");
        }
        return repository.save(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Can't find the given user to delete");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public User changeEmail(Long id, User user) {
        Optional<User> userToUpdate = repository.findById(id);
        if (userToUpdate.isPresent()) {
            userToUpdate.get().setEmail(user.getEmail());
            return repository.save(userToUpdate.get());
        } else {
            throw new ResourceNotFoundException("Can't find the given user to change the email address.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void changePassword(Long id, String oldPassword, String newPassword) {
        // Todo how to do this with spring security and argon2?
        Optional<User> userToUpdate = repository.findById(id);
        if (userToUpdate.isPresent()) {
            userToUpdate.get().setPassword(newPassword);
            repository.save(userToUpdate.get());
        } else {
            throw new ResourceNotFoundException("Can't find the given user to change the password.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void resetPassword(Long id) {
        // Todo with email integration
    }


}
