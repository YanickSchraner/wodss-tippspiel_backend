package ch.fhnw.wodss.tippspiel.controller;

import ch.fhnw.wodss.tippspiel.builder.*;
import ch.fhnw.wodss.tippspiel.domain.*;
import ch.fhnw.wodss.tippspiel.dto.*;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.service.BetGroupService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BetGroupControllerTest {

    //TODO check tests

    @Value("${security.cors.allowedOrigins}")
    private String corsAllowedOrigins;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private BetGroupService betGroupServiceMock;

    @MockBean
    private UserService userService;

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
        when(userService.getAllUsers()).thenReturn(userDTOS);
    }

    @Before
    public void setUp() {
        Mockito.reset(betGroupServiceMock);
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findAll_AllBetGroupFound_ShouldReturnFound() throws Exception {
        List<Long> userIds = new ArrayList<>();
        userIds.add(1L);
        BetGroupDTO betGroupDTO = new BetGroupDTOBuilder()
                .withId(1L)
                .withName("FHNW")
                .withScore(0)
                .withUserIds(userIds)
                .build();
        BetGroupDTO betGroupDTO2 = new BetGroupDTOBuilder()
                .withId(2L)
                .withName("FHNW2")
                .withScore(0)
                .withUserIds(userIds)
                .build();
        List<BetGroupDTO> betGroupDTOS = new ArrayList<>();
        betGroupDTOS.add(betGroupDTO);
        betGroupDTOS.add(betGroupDTO2);
        when(betGroupServiceMock.getAllBetGroups()).thenReturn(betGroupDTOS);
        mockMvc.perform(get("/betgroups")
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", equalTo(1)))
                .andExpect(jsonPath("$.[0].name", equalTo("FHNW")))
                .andExpect(jsonPath("$.[0].score", equalTo(0)))
                .andExpect(jsonPath("$.[1].id", equalTo(2)))
                .andExpect(jsonPath("$.[1].name", equalTo("FHNW2")))
                .andExpect(jsonPath("$.[1].score", equalTo(0)))
        ;
        Mockito.verify(betGroupServiceMock, times(1)).getAllBetGroups();
    }

    @Test
    @WithMockUser(username = "test", roles = {"UNVERIFIED"})
    public void findAll_asRoleUnverified_accessDenied() throws Exception {
        mockMvc.perform(get("/betgroups").headers(buildCORSHeaders()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findById_BetGroupFound_ShouldReturnFound() throws Exception {
        List<Long> userIds = new ArrayList<>();
        userIds.add(1L);
        BetGroupDTO betGroupDTO = new BetGroupDTOBuilder()
                .withId(1L)
                .withName("FHNW")
                .withScore(0)
                .withUserIds(userIds)
                .build();
        when(betGroupServiceMock.getBetGroupById(eq(1L))).thenReturn(betGroupDTO);
        mockMvc.perform(get("/betgroups/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("FHNW")))
                .andExpect(jsonPath("$.score", equalTo(0)));
        Mockito.verify(betGroupServiceMock, times(1)).getBetGroupById(eq(1L));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findById_BetGroupNotExisting_ShouldReturnNotFound() throws Exception {
        when(betGroupServiceMock.getBetGroupById(eq(2L))).
                thenThrow(new ResourceNotFoundException("Could not find BetGroup"));
        mockMvc.perform(get("/betgroups/{id}", 2L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isNotFound());
        Mockito.verify(betGroupServiceMock, times(1)).getBetGroupById(eq(2L));
    }

    @Test
    @WithMockUser(username = "test", roles = {"UNVERIFIED"})
    public void findById_asRoleUnverified_accessDenied() throws Exception {
        mockMvc.perform(get("/betgroups/{id}", 1L).headers(buildCORSHeaders()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findByName_BetGroupFound_ShouldReturnFound() throws Exception {
        List<Long> userIds = new ArrayList<>();
        userIds.add(1L);
        BetGroupDTO betGroupDTO = new BetGroupDTOBuilder()
                .withId(1L)
                .withName("FHNW")
                .withScore(0)
                .withUserIds(userIds)
                .build();
        when(betGroupServiceMock.getBetGroupByName(eq("FHNW"))).thenReturn(betGroupDTO);
        mockMvc.perform(get("/betgroups/name/{name}", "FHNW")
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("FHNW")))
                .andExpect(jsonPath("$.score", equalTo(0)));
        Mockito.verify(betGroupServiceMock, times(1)).getBetGroupByName(eq("FHNW"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findByName_BetGroupNotExisting_ShouldReturnFound() throws Exception {
        when(betGroupServiceMock.getBetGroupByName(eq("FHNW"))).
                thenThrow(new ResourceNotFoundException("Could not find BetGroup"));
        mockMvc.perform(get("/betgroups/name/{name}", "FHNW")
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isNotFound());
        Mockito.verify(betGroupServiceMock, times(1)).getBetGroupByName(eq("FHNW"));
    }

    @Test
    @WithMockUser(username = "test", roles = {"UNVERIFIED"})
    public void findByName_asRoleUnverified_accessDenied() throws Exception {
        mockMvc.perform(get("/betgroups/name/{name}", 1L).headers(buildCORSHeaders()))
                .andExpect(status().isForbidden());
    }
    /*

    @Test
    @WithMockUser(roles = "USER")
    public void addUser_UserAddedToBetGroup_ShouldReturnAdded() throws Exception {
        List<Long> userIds = new ArrayList<>();
        userIds.add(1L);
        BetGroupDTO betGroupDTO = new BetGroupDTOBuilder()
                .withId(1L)
                .withName("FHNW")
                .withScore(0)
                .withUserIds(userIds)
                .build();
        User user = new UserBuilder()
                .withId(2L)
                .withName("Tom2")
                .withRole("ROLE_USER")
                .withPassword("test123")
                .withEmail("tom2.ohme@gmx.ch")
                .withReminders(true)
                .withDailyResults(true)
                .build();
        when(betGroupServiceMock.addUser(1L, user, "test123")).thenReturn(betGroupDTO);
        mockMvc.perform(get("/betgroups/{id}/addUser", 2L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("FHNW")))
                .andExpect(jsonPath("$.score", equalTo(0)));
        Mockito.verify(betGroupServiceMock, times(1)).addUser(eq(1L), user, "test123");
    }
    */

    private HttpHeaders buildCORSHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Requested-With", "JUNIT");
        httpHeaders.add("Origin", corsAllowedOrigins);
        return httpHeaders;
    }

}
