package ch.fhnw.wodss.tippspiel.Services;

import ch.fhnw.wodss.tippspiel.DTOs.UserRankingDTO;
import ch.fhnw.wodss.tippspiel.Domain.User;
import ch.fhnw.wodss.tippspiel.Persistance.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> getAllUsers() {
        return null;
    }

    public List<UserRankingDTO> getAllUsersForRanking() {
        return null;
    }

    public User getUserById(Long id) {
        return null;
    }

    public User getUserByName(String name) {
        return null;
    }

    public User addUser(User user) {
        return null;
    }

    public void deleteUser(Long id) {

    }

    public User changeEmail(Long id, User user) {
        return null;
    }

    public void changePassword(Long id, String oldPassword, String newPassword) {

    }

    public void resetPassword(Long id) {

    }


}
