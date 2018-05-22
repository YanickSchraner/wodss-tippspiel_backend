package ch.fhnw.wodss.tippspiel.controller;

import ch.fhnw.wodss.tippspiel.TestUtil;
import ch.fhnw.wodss.tippspiel.builder.*;
import ch.fhnw.wodss.tippspiel.domain.Bet;
import ch.fhnw.wodss.tippspiel.domain.Game;
import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.dto.BetDTO;
import ch.fhnw.wodss.tippspiel.dto.RestBetDTO;
import ch.fhnw.wodss.tippspiel.dto.UserDTO;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.service.BetService;
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

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BetControllerTest {
    @Value("${security.cors.allowedOrigins}")
    private String corsAllowedOrigins;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private BetService betServiceMock;

    @MockBean
    private UserService userService;

    @Before
    public void mockUserService() {
        ArrayList<UserDTO> users = new ArrayList<>();
        users.add(new UserDTOBuilder().withName("Yanick").withRole("USER").withId(1L).build());
        when(userService.getAllUsers()).thenReturn(users);
    }

    @Before
    public void setUp() {
        Mockito.reset(betServiceMock);
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findById_BetFound_ShouldReturnFound() throws Exception {
        BetDTO bet = new BetDTOBuilder()
                .withId(1L)
                .withBettedAwayTeamGoals(0)
                .withBettedHomeTeamGoals(1)
                .withScore(10)
                .withGameId(1L)
                .withUserId(1L)
                .withUserName("Yanick")
                .withActualAwayTeamGoals(0)
                .withActualHomeTeamGoals(1)
                .withHomeTeamId(1L)
                .withAwayTeamId(1L)
                .withLocation("Moskau")
                .withPhase("Final")
                .build();
        when(betServiceMock.getBetById(eq(1L), any())).thenReturn(bet);

        mockMvc.perform(get("/bets/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.homeTeamId", equalTo(1)))
                .andExpect(jsonPath("$.awayTeamId", equalTo(1)))
                .andExpect(jsonPath("$.bettedHomeTeamGoals", equalTo(1)))
                .andExpect(jsonPath("$.bettedAwayTeamGoals", equalTo(0)))
                .andExpect(jsonPath("$.actualHomeTeamGoals", equalTo(1)))
                .andExpect(jsonPath("$.actualAwayTeamGoals", equalTo(0)))
                .andExpect(jsonPath("$.score", equalTo(10)))
                .andExpect(jsonPath("$.game_id", equalTo(1)))
                .andExpect(jsonPath("$.user_id", equalTo(1)))
                .andExpect(jsonPath("$.username", equalTo("Yanick")))
                .andExpect(jsonPath("$.location", equalTo("Moskau")))
                .andExpect(jsonPath("$.phase", equalTo("Final")));
        Mockito.verify(betServiceMock, times(1)).getBetById(eq(1L), any());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findById_BetNotExisting_ShouldReturnNotFound() throws Exception {
        when(betServiceMock.getBetById(eq(2L), any())).thenThrow(new ResourceNotFoundException("Could not find Bet"));
        mockMvc.perform(get("/bets/{id}", 2L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isNotFound());
        Mockito.verify(betServiceMock, times(1)).getBetById(eq(2L), any());
    }

    @Test
    @WithMockUser(username = "test", roles = {"UNVERIFIED"})
    public void findById_asRoleUnverified_accessDenied() throws Exception {
        mockMvc.perform(get("/bets/{id}", 1L).headers(buildCORSHeaders()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void create_BetCreated_ShouldReturnCreated() throws Exception {
        RestBetDTO restBetDTO = new RestBetDTOBuilder()
                .withGameId(1L)
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(1)
                .build();
        BetDTO betDTO = new BetDTOBuilder()
                .withId(1L)
                .withBettedAwayTeamGoals(0)
                .withBettedHomeTeamGoals(1)
                .withScore(10)
                .withGameId(1L)
                .withUserId(1L)
                .withUserName("Yanick")
                .withActualAwayTeamGoals(0)
                .withActualHomeTeamGoals(1)
                .withHomeTeamId(1L)
                .withAwayTeamId(1L)
                .withLocation("Moskau")
                .withPhase("Final")
                .build();
        when(betServiceMock.addBet(eq(restBetDTO), any())).thenReturn(betDTO);
        mockMvc.perform(post("/bets")
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(betDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.game_id", equalTo(1)))
                .andExpect(jsonPath("$.bettedHomeTeamGoals", equalTo(0)))
                .andExpect(jsonPath("$.bettedAwayTeamGoals", equalTo(1)));
        Mockito.verify(betServiceMock, times(1)).addBet(eq(restBetDTO) ,any());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void create_InvalidBetFormat_ShouldReturnBadRequest() throws Exception {
        RestBetDTO restBet = new RestBetDTOBuilder()
                .withGameId(1L)
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(1)
                .build();
        BetDTO bet = new BetDTOBuilder()
                .withId(1L)
                .withBettedAwayTeamGoals(0)
                .withBettedHomeTeamGoals(1)
                .withScore(10)
                .withGameId(1L)
                .withUserId(1L)
                .withUserName("Yanick")
                .withActualAwayTeamGoals(0)
                .withActualHomeTeamGoals(1)
                .withHomeTeamId(1L)
                .withAwayTeamId(1L)
                .withLocation("Moskau")
                .withPhase("Final")
                .build();
        when(betServiceMock.addBet(eq(restBet), any())).thenReturn(bet);
        mockMvc.perform(post("/bets")
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bet)))
                .andExpect(status().isBadRequest());
        Mockito.verify(betServiceMock, times(0)).addBet(eq(restBet), any());
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"UNVERIFIED"})
    public void create_asRoleUnverified_accessDenied() throws Exception {
        RestBetDTO restBet = new RestBetDTOBuilder()
                .withGameId(1L)
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(1)
                .build();
        BetDTO bet = new BetDTOBuilder()
                .withId(1L)
                .withBettedAwayTeamGoals(0)
                .withBettedHomeTeamGoals(1)
                .withScore(10)
                .withGameId(1L)
                .withUserId(1L)
                .withUserName("Yanick")
                .withActualAwayTeamGoals(0)
                .withActualHomeTeamGoals(1)
                .withHomeTeamId(1L)
                .withAwayTeamId(1L)
                .withLocation("Moskau")
                .withPhase("Final")
                .build();
        when(betServiceMock.addBet(eq(restBet), any())).thenReturn(bet);
        mockMvc.perform(post("/bets")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(TestUtil.convertObjectToJsonBytes(bet))
                .headers(buildCORSHeaders()))
                .andExpect(status().isForbidden());
        Mockito.verify(betServiceMock, times(0)).addBet(eq(restBet), any());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void update_BetUpdated_ShouldReturnOk() throws Exception {
        RestBetDTO restBet = new RestBetDTOBuilder()
                .withGameId(1L)
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(1)
                .build();
        BetDTO bet = new BetDTOBuilder()
                .withId(1L)
                .withBettedAwayTeamGoals(0)
                .withBettedHomeTeamGoals(1)
                .withScore(10)
                .withGameId(1L)
                .withUserId(1L)
                .withUserName("Yanick")
                .withActualAwayTeamGoals(0)
                .withActualHomeTeamGoals(1)
                .withHomeTeamId(1L)
                .withAwayTeamId(1L)
                .withLocation("Moskau")
                .withPhase("Final")
                .build();
        when(betServiceMock.updateBet(eq(1L), eq(restBet), any())).thenReturn(bet);
        mockMvc.perform(put("/bets/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.homeTeamId", equalTo(1)))
                .andExpect(jsonPath("$.awayTeamId", equalTo(1)))
                .andExpect(jsonPath("$.bettedHomeTeamGoals", equalTo(0)))
                .andExpect(jsonPath("$.bettedAwayTeamGoals", equalTo(1)))
                .andExpect(jsonPath("$.actualHomeTeamGoals", equalTo(0)))
                .andExpect(jsonPath("$.actualAwayTeamGoals", equalTo(1)))
                .andExpect(jsonPath("$.score", equalTo(10)))
                .andExpect(jsonPath("$.game_id", equalTo(1)))
                .andExpect(jsonPath("$.user_id", equalTo(1)))
                .andExpect(jsonPath("$.username", equalTo("Yanick")))
                .andExpect(jsonPath("$.location", equalTo("Moskau")))
                .andExpect(jsonPath("$.phase", equalTo("Final")));
        Mockito.verify(betServiceMock, times(1)).updateBet(eq(1L), eq(restBet), any());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void update_InvalidBetFormat_ShouldReturnBadRequest() throws Exception {
        RestBetDTO restBet = new RestBetDTOBuilder()
                .withGameId(1L)
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(1)
                .build();
        BetDTO bet = new BetDTOBuilder()
                .withId(1L)
                .withBettedAwayTeamGoals(0)
                .withBettedHomeTeamGoals(1)
                .withScore(10)
                .withGameId(1L)
                .withUserId(1L)
                .withUserName("Yanick")
                .withActualAwayTeamGoals(0)
                .withActualHomeTeamGoals(1)
                .withHomeTeamId(1L)
                .withAwayTeamId(1L)
                .withLocation("Moskau")
                .withPhase("Final")
                .build();
        when(betServiceMock.updateBet(eq(1L), eq(restBet), any())).thenReturn(bet);
        mockMvc.perform(put("/bets/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bet)))
                .andExpect(status().isBadRequest());
        Mockito.verify(betServiceMock, times(0)).updateBet(eq(1L), eq(restBet), any());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void update_BetNotFound_ShouldReturnNotFound() throws Exception {
        RestBetDTO restBet = new RestBetDTOBuilder()
                .withGameId(1L)
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(1)
                .build();
        BetDTO bet = new BetDTOBuilder()
                .withId(1L)
                .withBettedAwayTeamGoals(0)
                .withBettedHomeTeamGoals(1)
                .withScore(10)
                .withGameId(1L)
                .withUserId(1L)
                .withUserName("Yanick")
                .withActualAwayTeamGoals(0)
                .withActualHomeTeamGoals(1)
                .withHomeTeamId(1L)
                .withAwayTeamId(1L)
                .withLocation("Moskau")
                .withPhase("Final")
                .build();
        when(betServiceMock.updateBet(eq(1L), eq(restBet), any())).thenThrow(new ResourceNotFoundException("Bet not found"));
        mockMvc.perform(put("/bets/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bet)))
                .andExpect(status().isNotFound());
        Mockito.verify(betServiceMock, times(1)).updateBet(eq(1L), eq(restBet), any());
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"UNVERIFIED"})
    public void update_asRoleUnverified_accessDenied() throws Exception {
        RestBetDTO restBet = new RestBetDTOBuilder()
                .withGameId(1L)
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(1)
                .build();
        BetDTO bet = new BetDTOBuilder()
                .withId(1L)
                .withBettedAwayTeamGoals(0)
                .withBettedHomeTeamGoals(1)
                .withScore(10)
                .withGameId(1L)
                .withUserId(1L)
                .withUserName("Yanick")
                .withActualAwayTeamGoals(0)
                .withActualHomeTeamGoals(1)
                .withHomeTeamId(1L)
                .withAwayTeamId(1L)
                .withLocation("Moskau")
                .withPhase("Final")
                .build();
        when(betServiceMock.updateBet(eq(1L), eq(restBet), any())).thenReturn(bet);
        mockMvc.perform(put("/bets/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bet)))
                .andExpect(status().isForbidden());
        Mockito.verify(betServiceMock, times(0)).updateBet(eq(1L), eq(restBet), any());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void delete_BetDeleted_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/bets/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isOk());
        Mockito.verify(betServiceMock, times(1)).deleteBet(eq(1L), any());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void delete_BetNotFound_ShouldReturnNotFound() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Could not find Bet")).when(betServiceMock).deleteBet(eq(1L), any());
        mockMvc.perform(delete("/bets/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isNotFound());
        Mockito.verify(betServiceMock, times(1)).deleteBet(eq(1L), any());
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"UNVERIFIED"})
    public void delete_asRoleUnverified_accessDenied() throws Exception {
        mockMvc.perform(delete("/bets/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isForbidden());
        Mockito.verify(betServiceMock, times(0)).deleteBet(eq(1L), any());
    }


    private HttpHeaders buildCORSHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Requested-With", "JUNIT");
        httpHeaders.add("Origin", corsAllowedOrigins);
        return httpHeaders;
    }
}