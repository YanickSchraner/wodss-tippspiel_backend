package ch.fhnw.wodss.tippspiel.service;

import ch.fhnw.wodss.tippspiel.domain.Bet;
import ch.fhnw.wodss.tippspiel.domain.Role;
import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.dto.*;
import ch.fhnw.wodss.tippspiel.exception.IllegalActionException;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.persistance.RoleRepository;
import ch.fhnw.wodss.tippspiel.persistance.UserRepository;
import ch.fhnw.wodss.tippspiel.security.Argon2PasswordEncoder;
import ch.fhnw.wodss.tippspiel.util.GMail;
import ch.fhnw.wodss.tippspiel.util.RandomString;
import com.google.api.services.gmail.Gmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.naming.ServiceUnavailableException;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
public class UserService {

    private final UserRepository repository;
    private final BetService betService;
    private final BetGroupService betGroupService;
    private final Argon2PasswordEncoder argon2PasswordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository repository, BetGroupService betGroupService, BetService betService, Argon2PasswordEncoder argon2PasswordEncoder, RoleRepository roleRepository) {
        this.repository = repository;
        this.betGroupService = betGroupService;
        this.betService = betService;
        this.argon2PasswordEncoder = argon2PasswordEncoder;
        this.roleRepository = roleRepository;
    }

    private List<UserRankingDTO> createAllUsersForRankingDTOList(List<User> users) {
        List<UserRankingDTO> dtos = new ArrayList<>();
        for (User user : users) {
            UserRankingDTO dto = new UserRankingDTO();
            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setScore(user.getBets().stream().mapToInt(Bet::getScore).sum());
            dtos.add(dto);
        }
        return dtos;
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
        List<User> users = repository.findAll();
        List<UserRankingDTO> userRankingDTOs = createAllUsersForRankingDTOList(users);
        userRankingDTOs.sort(Comparator.comparingInt(UserRankingDTO::getScore));
        return userRankingDTOs;
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
        if (restUserDTO.getPassword() == null) throw new IllegalActionException("Please provide a password");
        if (!this.isValidEmail(restUserDTO.getEmail()))
            throw new IllegalActionException("Please provide a valid email");
        User user = new User();
        user.setName(restUserDTO.getName());
        user.setEmail(restUserDTO.getEmail());
        user.setPassword(argon2PasswordEncoder.encode(restUserDTO.getPassword()));
        user.setReminders(restUserDTO.isReminders());
        user.setDailyResults(restUserDTO.isDailyResults());
        user.setBets(new ArrayList<>());
        user.setBetGroups(new ArrayList<>());
        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.findById(1L).orElse(new Role("ROLE_USER"));
        roles.add(role);
        user.setRoles(roles);
        user = repository.save(user);
        return convertUserToUserDTO(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(Long id, User user) {
        if (!id.equals(user.getId())) throw new IllegalActionException("You can't delete another user!");
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new IllegalActionException("Operation failed.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserDTO updateUser(User user, RestUserDTO restUserDTO) {
        Optional<User> userToUpdate = repository.findById(user.getId());
        if (userToUpdate.isPresent()) {
            userToUpdate.get().setName(restUserDTO.getName());
            if (!isValidEmail(restUserDTO.getEmail()))
                throw new IllegalArgumentException("Please provida a valid email");
            userToUpdate.get().setEmail(restUserDTO.getEmail());
            userToUpdate.get().setDailyResults(restUserDTO.isDailyResults());
            userToUpdate.get().setReminders(restUserDTO.isReminders());
            if (restUserDTO.getNewPassword() != null) {
                boolean correctPW = argon2PasswordEncoder.matches(userToUpdate.get().getPassword(), argon2PasswordEncoder.encode(restUserDTO.getPassword()));
                if (!correctPW) throw new IllegalActionException("Operation failed.");
                if (restUserDTO.getPassword().length() < 10) throw new IllegalActionException("Operation failed.");
                userToUpdate.get().setPassword(argon2PasswordEncoder.encode(restUserDTO.getNewPassword()));
            }
            user = repository.save(userToUpdate.get());
            return convertUserToUserDTO(user);
        } else {
            throw new IllegalActionException("Operation failed.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void resetPassword(User user) {
        RandomString randomString = new RandomString(12, new SecureRandom(), RandomString.alphanum);
        String newPassword = randomString.nextString();
        StringBuilder stringBuilder = new StringBuilder()
                .append("Du hast ein neues Passwort angefordert. \n Dein neues Passwort lautet: ")
                .append(newPassword)
                .append("\nDieses Passwort ist absofort gültig.");
        try {
            User userToUpdate = repository.findById(user.getId()).orElseThrow(() -> new IllegalArgumentException("Unable to find user to reset password"));
            MimeMessage mimeMessage = GMail.createEmail(user.getEmail(), "tippspiel.wm18@gmail.com", "WM 2018 Tippspiel - Passwortreset", stringBuilder.toString());
            Gmail service = GMail.getAuthorizedService().orElseThrow(() -> new ServiceUnavailableException("GMail service not available"));
            GMail.sendMessage(service, "me", mimeMessage);
            userToUpdate.setPassword(argon2PasswordEncoder.encode(newPassword));
            repository.save(userToUpdate);

        } catch (MessagingException | ServiceUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    private UserDTO convertUserToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        List<BetDTO> betDTOs = user.getBets().isEmpty() ? new ArrayList<>() : user.getBets().stream().map(betService::convertBetToBetDTO).collect(Collectors.toList());
        List<BetGroupDTO> betGroupDTOs = user.getBetGroups().isEmpty() ? new ArrayList<>() : user.getBetGroups().stream().map(betGroupService::convertBetGroupToBetGroupDTO).collect(Collectors.toList());
        userDTO.setBets(betDTOs);
        userDTO.setBetGroups(betGroupDTOs);
        userDTO.setName(user.getName());
        userDTO.setPassword(user.getPassword());
        userDTO.setEmail(user.getEmail());
        userDTO.setReminders(user.isReminders());
        userDTO.setDailyResults(user.isDailyResults());
        userDTO.setRole(user.getRoles());
        return userDTO;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

}
