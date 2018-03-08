package ch.fhnw.wodss.tippspiel.service;

import ch.fhnw.wodss.tippspiel.dto.UserRankingDTO;
import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.exception.IllegalActionException;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.persistance.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public List<UserRankingDTO> getAllUsersForRanking() {
        // Todo DTO
        return null;
    }

    public User getUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can't find a user with id: " + id));
    }

    public User getUserByName(String name) {
        return repository.findUserByNameEquals(name)
                .orElseThrow(() -> new ResourceNotFoundException("Can't find a user with name: " + name));
    }

    public User addUser(User user) {
        if (repository.findUserByNameEquals(user.getName()).isPresent()) {
            throw new IllegalActionException("User with name: " + user.getName() + " already exists");
        }
        if (repository.findUserByEmailEquals(user.getEmail()).isPresent()) {
            throw new IllegalActionException("User with email: " + user.getEmail() + " already exists");
        }
        return repository.save(user);
    }

    public void deleteUser(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Can't find the given user to delete");
        }
    }

    public User changeEmail(Long id, User user) {
        Optional<User> userToUpdate = repository.findById(id);
        if (userToUpdate.isPresent()) {
            userToUpdate.get().setEmail(user.getEmail());
            return repository.save(userToUpdate.get());
        } else {
            throw new ResourceNotFoundException("Can't find the given user to change the email address.");
        }
    }

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

    public void resetPassword(Long id) {
        // Todo with email integration
    }


}
