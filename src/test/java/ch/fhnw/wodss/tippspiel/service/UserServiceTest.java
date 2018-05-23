package ch.fhnw.wodss.tippspiel.service;

import ch.fhnw.wodss.tippspiel.builder.BetBuilder;
import ch.fhnw.wodss.tippspiel.builder.RestUserDTOBuilder;
import ch.fhnw.wodss.tippspiel.builder.UserBuilder;
import ch.fhnw.wodss.tippspiel.domain.Bet;
import ch.fhnw.wodss.tippspiel.domain.Role;
import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.dto.RestUserDTO;
import ch.fhnw.wodss.tippspiel.dto.UserDTO;
import ch.fhnw.wodss.tippspiel.dto.UserRankingDTO;
import ch.fhnw.wodss.tippspiel.exception.IllegalActionException;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.persistance.RoleRepository;
import ch.fhnw.wodss.tippspiel.persistance.UserRepository;
import ch.fhnw.wodss.tippspiel.security.Argon2PasswordEncoder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@WebMvcTest(UserService.class)
public class UserServiceTest {

    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepositoryMock;

    @MockBean
    BetService betServiceMock;

    @MockBean
    BetGroupService betGroupServiceMock;

    @MockBean
    Argon2PasswordEncoder argon2PasswordEncoderMock;

    @MockBean
    RoleRepository roleRepositoryMock;

    @Before
    public void setup() {
        Mockito.reset(userRepositoryMock, betServiceMock, betGroupServiceMock, argon2PasswordEncoderMock, roleRepositoryMock);
    }


    @Test
    public void getAllUsers_ok() {
        List<User> users = new ArrayList<>();
        User user1 = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail.com")
                .withPassword("password")
                .withReminders(true)
                .withDailyResults(true)
                .withRole("USER")
                .build();
        User user2 = new UserBuilder()
                .withId(2L)
                .withName("Tom")
                .withEmail("tom.ohme@gmail.com")
                .withPassword("password")
                .withReminders(true)
                .withDailyResults(true)
                .withRole("USER")
                .build();
        users.add(user1);
        users.add(user2);

        when(userRepositoryMock.findAll()).thenReturn(users);

        List<UserDTO> result = userService.getAllUsers();
        Assert.assertEquals(users.size(), result.size());
        Assert.assertEquals(user1.getName(), result.get(0).getName());
        Assert.assertEquals(user1.getEmail(), result.get(0).getEmail());
        Assert.assertEquals((long) user1.getId(), result.get(0).getId());
        Assert.assertEquals(user1.getBetGroups(), result.get(0).getBetGroups());
        Assert.assertEquals(user1.getBets(), result.get(0).getBets());
        Assert.assertEquals(user1.getPassword(), result.get(0).getPassword());
        Assert.assertEquals(user1.isDailyResults(), result.get(0).getDailyResults());
        Assert.assertEquals(user1.isReminders(), result.get(0).getReminders());
        Assert.assertEquals(user1.getRoles(), result.get(0).getRole());
        Assert.assertEquals(user2.getName(), result.get(1).getName());
        Assert.assertEquals(user2.getEmail(), result.get(1).getEmail());
        Assert.assertEquals((long) user2.getId(), result.get(1).getId());
        Assert.assertEquals(user2.getBetGroups(), result.get(1).getBetGroups());
        Assert.assertEquals(user2.getBets(), result.get(1).getBets());
        Assert.assertEquals(user2.getPassword(), result.get(1).getPassword());
        Assert.assertEquals(user2.isDailyResults(), result.get(1).getDailyResults());
        Assert.assertEquals(user2.isReminders(), result.get(1).getReminders());
        Assert.assertEquals(user2.getRoles(), result.get(1).getRole());

        verify(userRepositoryMock, times(1)).findAll();

    }

    @Test
    public void getAllUsersForRanking_ok() {
        List<User> users = new ArrayList<>();
        List<Bet> bets = new ArrayList<>();
        User user1 = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail.com")
                .withPassword("password")
                .withReminders(true)
                .withDailyResults(true)
                .withRole("USER")
                .build();
        Bet bet1 = new BetBuilder()
                .withScore(3)
                .withUser(user1)
                .build();
        Bet bet2 = new BetBuilder()
                .withScore(7)
                .withUser(user1)
                .build();
        bets.add(bet1);
        bets.add(bet2);
        user1.setBets(bets);
        User user2 = new UserBuilder()
                .withId(2L)
                .withName("Tom")
                .withEmail("tom.ohme@gmail.com")
                .withPassword("password")
                .withReminders(true)
                .withDailyResults(true)
                .withRole("USER")
                .build();
        users.add(user1);
        users.add(user2);

        when(userRepositoryMock.findAll()).thenReturn(users);

        List<UserRankingDTO> result = userService.getAllUsersForRanking();
        Assert.assertEquals(users.size(), result.size());
        Assert.assertEquals(user2.getName(), result.get(0).getName());
        Assert.assertEquals(user2.getId(), result.get(0).getId());
        Assert.assertEquals((Integer) 0, result.get(0).getScore());
        Assert.assertEquals(user1.getName(), result.get(1).getName());
        Assert.assertEquals(user1.getId(), result.get(1).getId());
        Assert.assertEquals((Integer) 10, result.get(1).getScore());

        verify(userRepositoryMock, times(1)).findAll();

    }

    @Test
    public void getUserById_ok() {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail.com")
                .withPassword("password")
                .withReminders(true)
                .withDailyResults(false)
                .withRole("USER")
                .build();

        when(userRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(user));

        UserDTO userDTO = userService.getUserById(1L);
        Assert.assertEquals((long) user.getId(), userDTO.getId());
        Assert.assertEquals(user.getName(), userDTO.getName());
        Assert.assertEquals(user.getEmail(), userDTO.getEmail());
        Assert.assertEquals(user.getRoles(), userDTO.getRole());
        Assert.assertEquals(user.isDailyResults(), userDTO.getDailyResults());
        Assert.assertEquals(user.isReminders(), userDTO.getReminders());

        verify(userRepositoryMock, times(1)).findById(1L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getUserById_notFound() {
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.empty());
        UserDTO userDTO = userService.getUserById(1L);
        verify(userRepositoryMock, times(1)).findById(1L);
    }

    @Test
    public void getUserByName_ok() {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail.com")
                .withPassword("password")
                .withReminders(true)
                .withDailyResults(false)
                .withRole("USER")
                .build();

        when(userRepositoryMock.findUserByNameEquals("Yanick")).thenReturn(Optional.ofNullable(user));

        UserDTO userDTO = userService.getUserByName("Yanick");
        Assert.assertEquals((long) user.getId(), userDTO.getId());
        Assert.assertEquals(user.getName(), userDTO.getName());
        Assert.assertEquals(user.getEmail(), userDTO.getEmail());
        Assert.assertEquals(user.getRoles(), userDTO.getRole());
        Assert.assertEquals(user.isDailyResults(), userDTO.getDailyResults());
        Assert.assertEquals(user.isReminders(), userDTO.getReminders());

        verify(userRepositoryMock, times(1)).findUserByNameEquals("Yanick");
    }

    @Test
    public void addUser_ok() {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail.com")
                .withPassword("passwordHashed")
                .withReminders(true)
                .withDailyResults(false)
                .withRole("USER")
                .build();
        RestUserDTO restUserDTO = new RestUserDTOBuilder()
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail.com")
                .withPassword("password")
                .withDailyResults(false)
                .withReminders(false)
                .build();
        Role userRole = user.getRoles().iterator().next();
        when(userRepositoryMock.findUserByNameEquals("Yanick")).thenReturn(Optional.empty());
        when(userRepositoryMock.findUserByEmailEquals("yanick.schraner@gmail.com")).thenReturn(Optional.empty());
        when(argon2PasswordEncoderMock.encode("password")).thenReturn("passwordHashed");
        when(roleRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(userRole));
        when(userRepositoryMock.save(any(User.class))).thenReturn(user);

        UserDTO result = userService.addUser(restUserDTO);
        Assert.assertEquals((long) user.getId(), result.getId());
        Assert.assertEquals(user.getEmail(), result.getEmail());
        Assert.assertEquals(user.getName(), result.getName());
        Assert.assertEquals("passwordHashed", result.getPassword());
        Assert.assertEquals(user.isReminders(), result.getReminders());
        Assert.assertEquals(user.isDailyResults(), result.getDailyResults());
        Assert.assertEquals(user.getRoles(), result.getRole());
        Assert.assertEquals(user.getBets().size(), result.getBets().size());
        Assert.assertEquals(user.getBetGroups().size(), result.getBetGroups().size());

        verify(userRepositoryMock, times(1)).findUserByNameEquals("Yanick");
        verify(userRepositoryMock, times(1)).findUserByEmailEquals("yanick.schraner@gmail.com");
        verify(argon2PasswordEncoderMock, times(1)).encode("password");
        verify(roleRepositoryMock, times(1)).findById(1L);
        verify(userRepositoryMock, times(1)).save(any(User.class));

    }

    @Test(expected = IllegalActionException.class)
    public void addUser_alreadyExists() {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail.com")
                .withPassword("password")
                .withReminders(true)
                .withDailyResults(false)
                .withRole("USER")
                .build();
        RestUserDTO restUserDTO = new RestUserDTOBuilder()
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail.com")
                .withPassword("password")
                .withDailyResults(false)
                .withReminders(false)
                .build();
        when(userRepositoryMock.findUserByNameEquals("Yanick")).thenReturn(Optional.ofNullable(user));
        UserDTO result = userService.addUser(restUserDTO);
        verify(userRepositoryMock, times(1)).findUserByNameEquals("Yanick");
    }

    @Test(expected = IllegalActionException.class)
    public void addUser_emailAlreadyExists() {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail.com")
                .withPassword("password")
                .withReminders(true)
                .withDailyResults(false)
                .withRole("USER")
                .build();
        RestUserDTO restUserDTO = new RestUserDTOBuilder()
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail.com")
                .withPassword("password")
                .withDailyResults(false)
                .withReminders(false)
                .build();
        when(userRepositoryMock.findUserByNameEquals("Yanick")).thenReturn(Optional.empty());
        when(userRepositoryMock.findUserByEmailEquals("yanick.schraner@gmail.com")).thenReturn(Optional.ofNullable(user));

        UserDTO result = userService.addUser(restUserDTO);

        verify(userRepositoryMock, times(1)).findUserByNameEquals("Yanick");
        verify(userRepositoryMock, times(1)).findUserByEmailEquals("yanick.schraner@gmail.com");
    }

    @Test(expected = IllegalActionException.class)
    public void addUser_passwordRequired() {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail.com")
                .withReminders(true)
                .withDailyResults(false)
                .withRole("USER")
                .build();
        RestUserDTO restUserDTO = new RestUserDTOBuilder()
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail.com")
                .withDailyResults(false)
                .withReminders(false)
                .build();
        when(userRepositoryMock.findUserByNameEquals("Yanick")).thenReturn(Optional.empty());
        when(userRepositoryMock.findUserByEmailEquals("yanick.schraner@gmail.com")).thenReturn(Optional.empty());

        UserDTO result = userService.addUser(restUserDTO);

        verify(userRepositoryMock, times(1)).findUserByNameEquals("Yanick");
        verify(userRepositoryMock, times(1)).findUserByEmailEquals("yanick.schraner@gmail.com");
    }

    @Test(expected = IllegalActionException.class)
    public void addUser_invalidEmail() {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail.com")
                .withPassword("password")
                .withReminders(true)
                .withDailyResults(false)
                .withRole("USER")
                .build();
        RestUserDTO restUserDTO = new RestUserDTOBuilder()
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail")
                .withPassword("password")
                .withDailyResults(false)
                .withReminders(false)
                .build();
        Role userRole = user.getRoles().iterator().next();
        when(userRepositoryMock.findUserByNameEquals("Yanick")).thenReturn(Optional.empty());
        when(userRepositoryMock.findUserByEmailEquals("yanick.schraner@gmail.com")).thenReturn(Optional.empty());

        UserDTO result = userService.addUser(restUserDTO);

        verify(userRepositoryMock, times(1)).findUserByNameEquals("Yanick");
        verify(userRepositoryMock, times(1)).findUserByEmailEquals("yanick.schraner@gmail.com");
    }


    @Test(expected = ResourceNotFoundException.class)
    public void getUserByName_notFound() {
        when(userRepositoryMock.findUserByNameEquals("Fred")).thenReturn(Optional.empty());
        UserDTO userDTO = userService.getUserByName("Fred");
        verify(userRepositoryMock, times(1)).findUserByNameEquals("Fred");
    }

    @Test
    public void deleteUser_ok() {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail")
                .withPassword("password")
                .withReminders(true)
                .withDailyResults(false)
                .withRole("USER")
                .build();

        when(userRepositoryMock.existsById(1L)).thenReturn(true);
        userService.deleteUser(1L, user);
        verify(userRepositoryMock, times(1)).existsById(1L);
        verify(userRepositoryMock, times(1)).deleteById(1L);
    }

    @Test(expected = IllegalActionException.class)
    public void deleteUser_other() {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail")
                .withPassword("password")
                .withReminders(true)
                .withDailyResults(false)
                .withRole("USER")
                .build();
        userService.deleteUser(2L, user);
    }

    @Test(expected = IllegalActionException.class)
    public void deleteUser_dontExists() {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail")
                .withPassword("password")
                .withReminders(true)
                .withDailyResults(false)
                .withRole("USER")
                .build();

        when(userRepositoryMock.existsById(1L)).thenReturn(false);
        userService.deleteUser(1L, user);
        verify(userRepositoryMock, times(1)).existsById(1L);
    }

    @Test
    public void updateUser_ok() {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail.com")
                .withPassword("hash")
                .withReminders(true)
                .withDailyResults(false)
                .withRole("USER")
                .build();
        RestUserDTO restUserDTO = new RestUserDTOBuilder()
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail.com")
                .withPassword("passwordpassword")
                .withNewPassword("passwordpassword")
                .withDailyResults(false)
                .withReminders(false)
                .build();

        when(userRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(user));
        when(argon2PasswordEncoderMock.encode("passwordpassword")).thenReturn("hash");
        when(argon2PasswordEncoderMock.matches("hash", "hash")).thenReturn(true);
        when(userRepositoryMock.save(any(User.class))).thenReturn(user);

        UserDTO result = userService.updateUser(user, restUserDTO);
        Assert.assertEquals((long) user.getId(), result.getId());
        Assert.assertEquals(restUserDTO.isReminders(), result.getReminders());
        Assert.assertEquals(restUserDTO.isDailyResults(), result.getDailyResults());

        verify(userRepositoryMock, times(1)).findById(1L);
        verify(argon2PasswordEncoderMock, times(2)).encode("passwordpassword");
        verify(argon2PasswordEncoderMock, times(1)).matches("hash", "hash");
        verify(userRepositoryMock, times(1)).save(any(User.class));
    }

    @Test(expected = IllegalActionException.class)
    public void updateUser_notFound() {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail.com")
                .withPassword("hash")
                .withReminders(true)
                .withDailyResults(false)
                .withRole("USER")
                .build();
        RestUserDTO restUserDTO = new RestUserDTOBuilder()
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail.com")
                .withPassword("passwordpassword")
                .withDailyResults(false)
                .withReminders(false)
                .build();

        when(userRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        UserDTO result = userService.updateUser(user, restUserDTO);

        verify(userRepositoryMock, times(1)).findById(1L);
        verify(argon2PasswordEncoderMock, times(0)).encode("passwordpassword");
        verify(argon2PasswordEncoderMock, times(0)).matches("hash", "hash");
        verify(userRepositoryMock, times(0)).save(any(User.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateUser_invalidEmail() {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail.com")
                .withPassword("hash")
                .withReminders(true)
                .withDailyResults(false)
                .withRole("USER")
                .build();
        RestUserDTO restUserDTO = new RestUserDTOBuilder()
                .withName("Yanick")
                .withEmail("yanick.schraner@")
                .withPassword("passwordpassword")
                .withDailyResults(false)
                .withReminders(false)
                .build();

        when(userRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(user));

        UserDTO result = userService.updateUser(user, restUserDTO);

        verify(userRepositoryMock, times(1)).findById(1L);
        verify(argon2PasswordEncoderMock, times(0)).encode("passwordpassword");
        verify(argon2PasswordEncoderMock, times(0)).matches("hash", "hash");
        verify(userRepositoryMock, times(0)).save(any(User.class));
    }

    @Test(expected = IllegalActionException.class)
    public void updaetUser_incorrectPassword() {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail.com")
                .withPassword("hash")
                .withReminders(true)
                .withDailyResults(false)
                .withRole("USER")
                .build();
        RestUserDTO restUserDTO = new RestUserDTOBuilder()
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail.com")
                .withPassword("password2")
                .withNewPassword("passwordpassword")
                .withDailyResults(false)
                .withReminders(false)
                .build();

        when(userRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(user));
        when(argon2PasswordEncoderMock.encode("password2")).thenReturn("hash2");
        when(argon2PasswordEncoderMock.matches("hash", "hash2")).thenReturn(false);

        UserDTO result = userService.updateUser(user, restUserDTO);

        verify(userRepositoryMock, times(1)).findById(1L);
        verify(argon2PasswordEncoderMock, times(1)).encode("password");
        verify(argon2PasswordEncoderMock, times(1)).matches("hash", "hash");
        verify(userRepositoryMock, times(0)).save(any(User.class));
    }

    @Test(expected = IllegalActionException.class)
    public void updateUser_passwordToShort() {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail.com")
                .withPassword("hash")
                .withReminders(true)
                .withDailyResults(false)
                .withRole("USER")
                .build();
        RestUserDTO restUserDTO = new RestUserDTOBuilder()
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail.com")
                .withPassword("password")
                .withNewPassword("passwordpassword")
                .withDailyResults(false)
                .withReminders(false)
                .build();

        when(userRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(user));
        when(argon2PasswordEncoderMock.encode("password")).thenReturn("hash");
        when(argon2PasswordEncoderMock.matches("hash", "hash")).thenReturn(true);
        when(userRepositoryMock.save(any(User.class))).thenReturn(user);

        UserDTO result = userService.updateUser(user, restUserDTO);

        verify(userRepositoryMock, times(1)).findById(1L);
        verify(argon2PasswordEncoderMock, times(1)).encode("password");
        verify(argon2PasswordEncoderMock, times(1)).matches("hash", "hash");
        verify(userRepositoryMock, times(0)).save(any(User.class));
    }

    @Test
    public void resetPassword_ok(){
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .withEmail("yanick.schraner@gmail.com")
                .withPassword("hash")
                .withReminders(true)
                .withDailyResults(false)
                .withRole("USER")
                .build();
        when(userRepositoryMock.findUserByEmailEquals("yanick.schraner@gmmm.ch")).thenReturn(Optional.ofNullable(user));

        userService.resetPassword("yanick.schraner@gmmm.ch");

        verify(userRepositoryMock, times(1)).findUserByEmailEquals("yanick.schraner@gmmm.ch");
        verify(userRepositoryMock, times(1)).save(user);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void resetPassword_userNotFound(){
        when(userRepositoryMock.findUserByEmailEquals("yanick.schraner@gmmm.ch")).thenReturn(Optional.empty());
        userService.resetPassword("yanick.schraner@gmmm.ch");
    }

}
