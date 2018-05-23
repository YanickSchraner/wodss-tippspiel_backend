package ch.fhnw.wodss.tippspiel.controller;

import ch.fhnw.wodss.tippspiel.TestUtil;
import ch.fhnw.wodss.tippspiel.builder.*;
import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.dto.*;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

    @Value("${security.cors.allowedOrigins}")
    private String corsAllowedOrigins;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private UserService userServiceMock;

    @Before
    public void mockUserService() {
        BetDTO betDTO = new BetDTOBuilder()
                .withId(1L)
                .withBettedAwayTeamGoals(0)
                .withBettedHomeTeamGoals(1)
                .withScore(10)
                .withGameId(1L)
                .withUserId(1L)
                .withUserName("Tom")
                .withActualAwayTeamGoals(0)
                .withActualHomeTeamGoals(1)
                .withHomeTeamId(1L)
                .withAwayTeamId(1L)
                .withLocation("Moskau")
                .withPhase("Final")
                .build();
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        BetGroupDTO betGroupDTO = new BetGroupDTOBuilder()
                .withId(1L)
                .withName("FHNW")
                .withScore(0)
                .withUserIds(ids)
                .build();
        UserDTO userDTO = new UserDTOBuilder()
                .withId(1L)
                .withName("Tom")
                .withRole("ROLE_USER")
                .withPassword("test123")
                .withEmail("tom.ohme@gmx.ch")
                .withBet(betDTO)
                .withBetGroup(betGroupDTO)
                .withReminders(true)
                .withDailyResults(true)
                .build();
        List<UserDTO> userDTOS = new ArrayList<>();
        userDTOS.add(userDTO);
        ArrayList<User> users = new ArrayList<>();
        users.add(new UserBuilder().withName("Tom").withRole("USER").withId(1L).build());
        when(userServiceMock.getAllUsers()).thenReturn(userDTOS);
    }

    @Before
    public void setUp() {
        Mockito.reset(userServiceMock);
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findAll_AllUsersFound_ShouldReturnFound() throws Exception {
        BetDTO betDTO = new BetDTOBuilder()
                .withId(1L)
                .withBettedAwayTeamGoals(0)
                .withBettedHomeTeamGoals(1)
                .withScore(10)
                .withGameId(1L)
                .withUserId(1L)
                .withUserName("Tom")
                .withActualAwayTeamGoals(0)
                .withActualHomeTeamGoals(1)
                .withHomeTeamId(1L)
                .withAwayTeamId(1L)
                .withLocation("Moskau")
                .withPhase("Final")
                .build();
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        BetGroupDTO betGroupDTO = new BetGroupDTOBuilder()
                .withId(1L)
                .withName("FHNW")
                .withScore(0)
                .withUserIds(ids)
                .build();
        UserDTO userDTO = new UserDTOBuilder()
                .withId(1L)
                .withName("Tom")
                .withRole("ROLE_USER")
                .withPassword("test123")
                .withEmail("tom.ohme@gmx.ch")
                .withBet(betDTO)
                .withBetGroup(betGroupDTO)
                .withReminders(true)
                .withDailyResults(true)
                .build();
        UserDTO userDTO2 = new UserDTOBuilder()
                .withId(2L)
                .withName("Tom2")
                .withRole("ROLE_USER")
                .withPassword("test1234")
                .withEmail("tom2.ohme@gmx.ch")
                .withBet(betDTO)
                .withBetGroup(betGroupDTO)
                .withReminders(true)
                .withDailyResults(true)
                .build();
        List<UserDTO> userDTOS = new ArrayList<>();
        userDTOS.add(userDTO);
        userDTOS.add(userDTO2);
        when(userServiceMock.getAllUsers()).thenReturn(userDTOS);
        mockMvc.perform(get("/users")
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", equalTo(1)))
                .andExpect(jsonPath("$.[0].name", equalTo("Tom")))
                .andExpect(jsonPath("$.[0].password", equalTo("test123")))
                .andExpect(jsonPath("$.[0].email", equalTo("tom.ohme@gmx.ch")))
                .andExpect(jsonPath("$.[1].id", equalTo(2)))
                .andExpect(jsonPath("$.[1].name", equalTo("Tom2")))
                .andExpect(jsonPath("$.[1].password", equalTo("test1234")))
                .andExpect(jsonPath("$.[1].email", equalTo("tom2.ohme@gmx.ch")));
        Mockito.verify(userServiceMock, times(1)).getAllUsers();
    }

    @Test
    @WithMockUser(username = "test", roles = {"UNVERIFIED"})
    public void findAll_asRoleUnverified_accessDenied() throws Exception {
        mockMvc.perform(get("/users").headers(buildCORSHeaders()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findById_UserFound_ShouldReturnFound() throws Exception {
        BetDTO betDTO = new BetDTOBuilder()
                .withId(1L)
                .withBettedAwayTeamGoals(0)
                .withBettedHomeTeamGoals(1)
                .withScore(10)
                .withGameId(1L)
                .withUserId(1L)
                .withUserName("Tom")
                .withActualAwayTeamGoals(0)
                .withActualHomeTeamGoals(1)
                .withHomeTeamId(1L)
                .withAwayTeamId(1L)
                .withLocation("Moskau")
                .withPhase("Final")
                .build();
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        BetGroupDTO betGroupDTO = new BetGroupDTOBuilder()
                .withId(1L)
                .withName("FHNW")
                .withScore(0)
                .withUserIds(ids)
                .build();
        UserDTO userDTO = new UserDTOBuilder()
                .withId(1L)
                .withName("Tom")
                .withRole("ROLE_USER")
                .withPassword("test123")
                .withEmail("tom.ohme@gmx.ch")
                .withBet(betDTO)
                .withBetGroup(betGroupDTO)
                .withReminders(true)
                .withDailyResults(true)
                .build();
        when(userServiceMock.getUserById(eq(1L))).thenReturn(userDTO);
        mockMvc.perform(get("/users/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Tom")))
                .andExpect(jsonPath("$.password", equalTo("test123")))
                .andExpect(jsonPath("$.email", equalTo("tom.ohme@gmx.ch")));
        Mockito.verify(userServiceMock, times(1)).getUserById(eq(1L));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findById_UserNotExisting_ShouldReturnNotFound() throws Exception {
        when(userServiceMock.getUserById(eq(2L))).
                thenThrow(new ResourceNotFoundException("Could not find User"));
        mockMvc.perform(get("/users/{id}", 2L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isNotFound());
        Mockito.verify(userServiceMock, times(1)).getUserById(eq(2L));
    }

    @Test
    @WithMockUser(username = "test", roles = {"UNVERIFIED"})
    public void findById_asRoleUnverified_accessDenied() throws Exception {
        mockMvc.perform(get("/users/{id}", 1L).headers(buildCORSHeaders()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findByName_UserFound_ShouldReturnFound() throws Exception {
        BetDTO betDTO = new BetDTOBuilder()
                .withId(1L)
                .withBettedAwayTeamGoals(0)
                .withBettedHomeTeamGoals(1)
                .withScore(10)
                .withGameId(1L)
                .withUserId(1L)
                .withUserName("Tom")
                .withActualAwayTeamGoals(0)
                .withActualHomeTeamGoals(1)
                .withHomeTeamId(1L)
                .withAwayTeamId(1L)
                .withLocation("Moskau")
                .withPhase("Final")
                .build();
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        BetGroupDTO betGroupDTO = new BetGroupDTOBuilder()
                .withId(1L)
                .withName("FHNW")
                .withScore(0)
                .withUserIds(ids)
                .build();
        UserDTO userDTO = new UserDTOBuilder()
                .withId(1L)
                .withName("Tom")
                .withRole("ROLE_USER")
                .withPassword("test123")
                .withEmail("tom.ohme@gmx.ch")
                .withBet(betDTO)
                .withBetGroup(betGroupDTO)
                .withReminders(true)
                .withDailyResults(true)
                .build();
        when(userServiceMock.getUserByName(eq("Tom"))).thenReturn(userDTO);
        mockMvc.perform(get("/users/name/{name}", "Tom")
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Tom")))
                .andExpect(jsonPath("$.password", equalTo("test123")))
                .andExpect(jsonPath("$.email", equalTo("tom.ohme@gmx.ch")));
        Mockito.verify(userServiceMock, times(1)).getUserByName(eq("Tom"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findByName_UserNotExisting_ShouldReturnFound() throws Exception {
        when(userServiceMock.getUserByName(eq("Tom"))).
                thenThrow(new ResourceNotFoundException("Could not find User"));
        mockMvc.perform(get("/users/name/{name}", "Tom")
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isNotFound());
        Mockito.verify(userServiceMock, times(1)).getUserByName(eq("Tom"));
    }

    @Test
    @WithMockUser(username = "test", roles = {"UNVERIFIED"})
    public void findByName_asRoleUnverified_accessDenied() throws Exception {
        mockMvc.perform(get("/users/name/{name}", 1L).headers(buildCORSHeaders()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void create_UserCreated_ShouldReturnCreated() throws Exception {
        RestUserDTO restUserDTO = new RestUserDTOBuilder()
                .withName("Tom")
                .withEmail("tom.ohme@gmx.ch")
                .withPassword("test123")
                .withReminders(true)
                .withDailyResults(true)
                .build();
        BetDTO betDTO = new BetDTOBuilder()
                .withId(1L)
                .withBettedAwayTeamGoals(0)
                .withBettedHomeTeamGoals(1)
                .withScore(10)
                .withGameId(1L)
                .withUserId(1L)
                .withUserName("Tom")
                .withActualAwayTeamGoals(0)
                .withActualHomeTeamGoals(1)
                .withHomeTeamId(1L)
                .withAwayTeamId(1L)
                .withLocation("Moskau")
                .withPhase("Final")
                .build();
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        BetGroupDTO betGroupDTO = new BetGroupDTOBuilder()
                .withId(1L)
                .withName("FHNW")
                .withScore(0)
                .withUserIds(ids)
                .build();
        UserDTO userDTO = new UserDTOBuilder()
                .withId(1L)
                .withName(restUserDTO.getName())
                .withRole("ROLE_USER")
                .withPassword(restUserDTO.getPassword())
                .withEmail(restUserDTO.getEmail())
                .withBet(betDTO)
                .withBetGroup(betGroupDTO)
                .withReminders(true)
                .withDailyResults(true)
                .build();
        when(userServiceMock.addUser(eq(restUserDTO))).thenReturn(userDTO);
        mockMvc.perform(post("/users")
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(restUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Tom")))
                .andExpect(jsonPath("$.password", equalTo("test123")))
                .andExpect(jsonPath("$.email", equalTo("tom.ohme@gmx.ch")));
        Mockito.verify(userServiceMock, times(1)).addUser(eq(restUserDTO));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void create_InvalidUserFormat_ShouldReturnBadRequest() throws Exception {
        RestUserDTO restUserDTO = new RestUserDTOBuilder()
                .withEmail("tom.ohme@gmx.ch")
                .withPassword("test123")
                .withReminders(true)
                .withDailyResults(true)
                .build();
        BetDTO betDTO = new BetDTOBuilder()
                .withId(1L)
                .withBettedAwayTeamGoals(0)
                .withBettedHomeTeamGoals(1)
                .withScore(10)
                .withGameId(1L)
                .withUserId(1L)
                .withUserName("Tom")
                .withActualAwayTeamGoals(0)
                .withActualHomeTeamGoals(1)
                .withHomeTeamId(1L)
                .withAwayTeamId(1L)
                .withLocation("Moskau")
                .withPhase("Final")
                .build();
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        BetGroupDTO betGroupDTO = new BetGroupDTOBuilder()
                .withId(1L)
                .withName("FHNW")
                .withScore(0)
                .withUserIds(ids)
                .build();
        UserDTO userDTO = new UserDTOBuilder()
                .withId(1L)
                .withName(restUserDTO.getName())
                .withRole("ROLE_USER")
                .withPassword(restUserDTO.getPassword())
                .withEmail(restUserDTO.getEmail())
                .withBet(betDTO)
                .withBetGroup(betGroupDTO)
                .withReminders(true)
                .withDailyResults(true)
                .build();
        when(userServiceMock.addUser(eq(restUserDTO))).thenReturn(userDTO);
        mockMvc.perform(post("/users")
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(restUserDTO)))
                .andExpect(status().isBadRequest());
        Mockito.verify(userServiceMock, times(0)).addUser(eq(restUserDTO));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"UNVERIFIED"})
    public void create_asRoleUnverified_accessDenied() throws Exception {
        RestUserDTO restUserDTO = new RestUserDTOBuilder()
                .withName("Tom")
                .withEmail("tom.ohme@gmx.ch")
                .withPassword("test123")
                .withReminders(true)
                .withDailyResults(true)
                .build();
        BetDTO betDTO = new BetDTOBuilder()
                .withId(1L)
                .withBettedAwayTeamGoals(0)
                .withBettedHomeTeamGoals(1)
                .withScore(10)
                .withGameId(1L)
                .withUserId(1L)
                .withUserName("Tom")
                .withActualAwayTeamGoals(0)
                .withActualHomeTeamGoals(1)
                .withHomeTeamId(1L)
                .withAwayTeamId(1L)
                .withLocation("Moskau")
                .withPhase("Final")
                .build();
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        BetGroupDTO betGroupDTO = new BetGroupDTOBuilder()
                .withId(1L)
                .withName("FHNW")
                .withScore(0)
                .withUserIds(ids)
                .build();
        UserDTO userDTO = new UserDTOBuilder()
                .withId(1L)
                .withName(restUserDTO.getName())
                .withRole("ROLE_USER")
                .withPassword(restUserDTO.getPassword())
                .withEmail(restUserDTO.getEmail())
                .withBet(betDTO)
                .withBetGroup(betGroupDTO)
                .withReminders(true)
                .withDailyResults(true)
                .build();
        when(userServiceMock.addUser(eq(restUserDTO))).thenReturn(userDTO);
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(TestUtil.convertObjectToJsonBytes(restUserDTO))
                .headers(buildCORSHeaders()))
                .andExpect(status().isForbidden());
        Mockito.verify(userServiceMock, times(0)).addUser(eq(restUserDTO));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void update_UserUpdated_ShouldReturnOk() throws Exception {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Tom")
                .withEmail("tom.ohme@gmx.ch")
                .withPassword("test123")
                .withRole("ROLE_USER")
                .build();
        RestUserDTO restUserDTO = new RestUserDTOBuilder()
                .withName("Tom")
                .withEmail("tom2.ohme@gmx.ch")
                .withPassword("test123")
                .withReminders(true)
                .withDailyResults(true)
                .build();
        BetDTO betDTO = new BetDTOBuilder()
                .withId(1L)
                .withBettedAwayTeamGoals(0)
                .withBettedHomeTeamGoals(1)
                .withScore(10)
                .withGameId(1L)
                .withUserId(1L)
                .withUserName("Tom")
                .withActualAwayTeamGoals(0)
                .withActualHomeTeamGoals(1)
                .withHomeTeamId(1L)
                .withAwayTeamId(1L)
                .withLocation("Moskau")
                .withPhase("Final")
                .build();
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        BetGroupDTO betGroupDTO = new BetGroupDTOBuilder()
                .withId(1L)
                .withName("FHNW")
                .withScore(0)
                .withUserIds(ids)
                .build();
        UserDTO userDTO = new UserDTOBuilder()
                .withId(1L)
                .withName(restUserDTO.getName())
                .withRole("ROLE_USER")
                .withPassword(restUserDTO.getPassword())
                .withEmail(restUserDTO.getEmail())
                .withBet(betDTO)
                .withBetGroup(betGroupDTO)
                .withReminders(true)
                .withDailyResults(true)
                .build();
        when(userServiceMock.updateUser(eq(user), eq(restUserDTO))).thenReturn(userDTO);
        mockMvc.perform(put("/users/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(restUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Tom")))
                .andExpect(jsonPath("$.password", equalTo("test123")))
                .andExpect(jsonPath("$.email", equalTo("tom2.ohme@gmx.ch")));
        Mockito.verify(userServiceMock, times(1)).updateUser(eq(user), eq(restUserDTO));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void update_InvalidUserFormat_ShouldReturnBadRequest() throws Exception {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Tom")
                .withEmail("tom.ohme@gmx.ch")
                .withPassword("test123")
                .withRole("ROLE_USER")
                .build();
        RestUserDTO restUserDTO = new RestUserDTOBuilder()
                .withEmail("tom2.ohme@gmx.ch")
                .withPassword("test123")
                .withReminders(true)
                .withDailyResults(true)
                .build();
        BetDTO betDTO = new BetDTOBuilder()
                .withId(1L)
                .withBettedAwayTeamGoals(0)
                .withBettedHomeTeamGoals(1)
                .withScore(10)
                .withGameId(1L)
                .withUserId(1L)
                .withUserName("Tom")
                .withActualAwayTeamGoals(0)
                .withActualHomeTeamGoals(1)
                .withHomeTeamId(1L)
                .withAwayTeamId(1L)
                .withLocation("Moskau")
                .withPhase("Final")
                .build();
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        BetGroupDTO betGroupDTO = new BetGroupDTOBuilder()
                .withId(1L)
                .withName("FHNW")
                .withScore(0)
                .withUserIds(ids)
                .build();
        UserDTO userDTO = new UserDTOBuilder()
                .withId(1L)
                .withName(restUserDTO.getName())
                .withRole("ROLE_USER")
                .withPassword(restUserDTO.getPassword())
                .withEmail(restUserDTO.getEmail())
                .withBet(betDTO)
                .withBetGroup(betGroupDTO)
                .withReminders(true)
                .withDailyResults(true)
                .build();
        when(userServiceMock.updateUser(eq(user), eq(restUserDTO))).thenReturn(userDTO);
        mockMvc.perform(put("/users/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(restUserDTO)))
                .andExpect(status().isBadRequest());
        Mockito.verify(userServiceMock, times(0)).updateUser(eq(user), eq(restUserDTO));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void update_UserNotFound_ShouldReturnNotFound() throws Exception {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Tom")
                .withEmail("tom.ohme@gmx.ch")
                .withPassword("test123")
                .withRole("ROLE_USER")
                .build();
        RestUserDTO restUserDTO = new RestUserDTOBuilder()
                .withName("Tom")
                .withEmail("tom2.ohme@gmx.ch")
                .withPassword("test123")
                .withReminders(true)
                .withDailyResults(true)
                .build();
        when(userServiceMock.updateUser(eq(user), eq(restUserDTO))).thenThrow(new ResourceNotFoundException("User not found"));
        mockMvc.perform(put("/users/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(restUserDTO)))
                .andExpect(status().isNotFound());
        Mockito.verify(userServiceMock, times(1)).updateUser(eq(user), eq(restUserDTO));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"UNVERIFIED"})
    public void update_asRoleUnverified_accessDenied() throws Exception {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Tom")
                .withEmail("tom.ohme@gmx.ch")
                .withPassword("test123")
                .withRole("ROLE_USER")
                .build();
        RestUserDTO restUserDTO = new RestUserDTOBuilder()
                .withName("Tom")
                .withEmail("tom2.ohme@gmx.ch")
                .withPassword("test123")
                .withReminders(true)
                .withDailyResults(true)
                .build();
        BetDTO betDTO = new BetDTOBuilder()
                .withId(1L)
                .withBettedAwayTeamGoals(0)
                .withBettedHomeTeamGoals(1)
                .withScore(10)
                .withGameId(1L)
                .withUserId(1L)
                .withUserName("Tom")
                .withActualAwayTeamGoals(0)
                .withActualHomeTeamGoals(1)
                .withHomeTeamId(1L)
                .withAwayTeamId(1L)
                .withLocation("Moskau")
                .withPhase("Final")
                .build();
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        BetGroupDTO betGroupDTO = new BetGroupDTOBuilder()
                .withId(1L)
                .withName("FHNW")
                .withScore(0)
                .withUserIds(ids)
                .build();
        UserDTO userDTO = new UserDTOBuilder()
                .withId(1L)
                .withName(restUserDTO.getName())
                .withRole("ROLE_USER")
                .withPassword(restUserDTO.getPassword())
                .withEmail(restUserDTO.getEmail())
                .withBet(betDTO)
                .withBetGroup(betGroupDTO)
                .withReminders(true)
                .withDailyResults(true)
                .build();
        when(userServiceMock.updateUser(eq(user), eq(restUserDTO))).thenReturn(userDTO);
        mockMvc.perform(put("/users/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
                .andExpect(status().isForbidden());
        Mockito.verify(userServiceMock, times(0)).updateUser(eq(user), eq(restUserDTO));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete_UserDeleted_ShouldReturnOk() throws Exception {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Tom")
                .withEmail("tom.ohme@gmx.ch")
                .withPassword("test123")
                .withRole("ROLE_USER")
                .build();
        mockMvc.perform(delete("/users/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isOk());
        Mockito.verify(userServiceMock, times(1)).deleteUser(1L, user);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete_UserNotFound_ShouldReturnNotFound() throws Exception {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Tom")
                .withEmail("tom.ohme@gmx.ch")
                .withPassword("test123")
                .withRole("ROLE_USER")
                .build();
        Mockito.doThrow(new ResourceNotFoundException("Could not find User")).when(userServiceMock).
                deleteUser(eq(1L), eq(user));
        mockMvc.perform(delete("/users/{id}", 2L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isNotFound());
        Mockito.verify(userServiceMock, times(1)).deleteUser(eq(1L), eq(user));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"UNVERIFIED"})
    public void delete_asRoleUnverified_accessDenied() throws Exception {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Tom")
                .withEmail("tom.ohme@gmx.ch")
                .withPassword("test123")
                .withRole("ROLE_USER")
                .build();
        mockMvc.perform(delete("/users/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isForbidden());
        Mockito.verify(userServiceMock, times(0)).deleteUser(eq(1L), eq(user));
    }

    private HttpHeaders buildCORSHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Requested-With", "JUNIT");
        httpHeaders.add("Origin", corsAllowedOrigins);
        return httpHeaders;
    }

}
