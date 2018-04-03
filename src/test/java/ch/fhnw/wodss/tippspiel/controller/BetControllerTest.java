package ch.fhnw.wodss.tippspiel.controller;

import ch.fhnw.wodss.tippspiel.TestUtil;
import ch.fhnw.wodss.tippspiel.builder.BetBuilder;
import ch.fhnw.wodss.tippspiel.builder.GameBuilder;
import ch.fhnw.wodss.tippspiel.builder.UserBuilder;
import ch.fhnw.wodss.tippspiel.domain.Bet;
import ch.fhnw.wodss.tippspiel.domain.Game;
import ch.fhnw.wodss.tippspiel.domain.User;
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
        ArrayList<User> users = new ArrayList<>();
        users.add(new UserBuilder().withName("Yanick").withRole("USER").withId(1L).build());
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
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .build();
        Game game = new GameBuilder()
                .withId(1L)
                .withAwayTeamGoals(0)
                .withHomeTeamGoals(1)
                .build();
        Bet bet = new BetBuilder()
                .withId(1L)
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(1)
                .withScore(10)
                .withGame(game)
                .withUser(user)
                .build();
        when(betServiceMock.getBetById(1L)).thenReturn(bet);

        mockMvc.perform(get("/bets/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.homeTeamGoals", equalTo(0)))
                .andExpect(jsonPath("$.awayTeamGoals", equalTo(1)))
                .andExpect(jsonPath("$.score", equalTo(10)))
                .andExpect(jsonPath("$.game.id", equalTo(1)))
                .andExpect(jsonPath("$.game.awayTeamGoals", equalTo(0)))
                .andExpect(jsonPath("$.game.homeTeamGoals", equalTo(1)))
                .andExpect(jsonPath("$.user.id", equalTo(1)))
                .andExpect(jsonPath("$.user.name", equalTo("Yanick")));
        Mockito.verify(betServiceMock, times(1)).getBetById(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findById_BetNotExisting_ShouldReturnNotFound() throws Exception {
        when(betServiceMock.getBetById(2L)).thenThrow(new ResourceNotFoundException("Could not find Bet"));
        mockMvc.perform(get("/bets/{id}", 2L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
        )
                .andExpect(status().isNotFound());
        Mockito.verify(betServiceMock, times(0)).getBetById(1L);
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
        Bet bet = new BetBuilder()
                .withId(1L)
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(1)
                .withScore(10)
                .withGame(new Game())
                .withUser(new User())
                .build();
        when(betServiceMock.addBet(bet)).thenReturn(bet);
        mockMvc.perform(post("/bets")
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bet)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.homeTeamGoals", equalTo(0)))
                .andExpect(jsonPath("$.awayTeamGoals", equalTo(1)))
                .andExpect(jsonPath("$.score", equalTo(10)));
        Mockito.verify(betServiceMock, times(1)).addBet(bet);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void create_InvalidBetFormat_ShouldReturnBadRequest() throws Exception {
        Bet bet = new BetBuilder()
                .withId(1L)
                .withGame(new Game())
                .withUser(new User())
                .build();
        when(betServiceMock.addBet(bet)).thenReturn(bet);
        mockMvc.perform(post("/bets")
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bet)))
                .andExpect(status().isBadRequest());
        Mockito.verify(betServiceMock, times(0)).addBet(bet);
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"UNVERIFIED"})
    public void create_asRoleUnverified_accessDenied() throws Exception {
        Bet bet = new BetBuilder()
                .withId(1L)
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(1)
                .withScore(10)
                .withGame(new Game())
                .withUser(new User())
                .build();
        when(betServiceMock.addBet(bet)).thenReturn(bet);
        mockMvc.perform(post("/bets")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(TestUtil.convertObjectToJsonBytes(bet))
                .headers(buildCORSHeaders()))
                .andExpect(status().isForbidden());
        Mockito.verify(betServiceMock, times(0)).addBet(bet);
    }

    @Test
    @WithMockUser(roles ="USER")
    public void update_BetUpdated_ShouldReturnOk() throws Exception{
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .build();
        Game game = new GameBuilder()
                .withId(1L)
                .withAwayTeamGoals(0)
                .withHomeTeamGoals(1)
                .build();
        Bet bet = new BetBuilder()
                .withId(1L)
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(1)
                .withScore(10)
                .withGame(game)
                .withUser(user)
                .build();
        when(betServiceMock.updateBet(1L, bet)).thenReturn(bet);
        mockMvc.perform(put("/bets/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.homeTeamGoals", equalTo(0)))
                .andExpect(jsonPath("$.awayTeamGoals", equalTo(1)))
                .andExpect(jsonPath("$.score", equalTo(10)))
                .andExpect(jsonPath("$.game.id", equalTo(1)))
                .andExpect(jsonPath("$.game.awayTeamGoals", equalTo(0)))
                .andExpect(jsonPath("$.game.homeTeamGoals", equalTo(1)))
                .andExpect(jsonPath("$.user.id", equalTo(1)))
                .andExpect(jsonPath("$.user.name", equalTo("Yanick")));
        Mockito.verify(betServiceMock, times(1)).updateBet(1L, bet);
    }

    @Test
    @WithMockUser(roles ="USER")
    public void update_InvalidBetFormat_ShouldReturnBadRequest() throws Exception{
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .build();
        Game game = new GameBuilder()
                .withId(1L)
                .withAwayTeamGoals(0)
                .withHomeTeamGoals(1)
                .build();
        Bet bet = new BetBuilder() // invalid because no result in the bet object
                .withId(1L)
                .withScore(10)
                .withGame(game)
                .withUser(user)
                .build();
        when(betServiceMock.updateBet(1L, bet)).thenReturn(bet);
        mockMvc.perform(put("/bets/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bet)))
                .andExpect(status().isBadRequest());
        Mockito.verify(betServiceMock, times(0)).updateBet(1L, bet);
    }

    @Test
    @WithMockUser(roles ="USER")
    public void update_BetNotFound_ShouldReturnNotFound() throws Exception{
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .build();
        Game game = new GameBuilder()
                .withId(1L)
                .withAwayTeamGoals(0)
                .withHomeTeamGoals(1)
                .build();
        Bet bet = new BetBuilder()
                .withId(1L)
                .withScore(10)
                .withHomeTeamGoals(1)
                .withAwayTeamGoals(0)
                .withGame(game)
                .withUser(user)
                .build();
        when(betServiceMock.updateBet(1L, bet)).thenThrow(new ResourceNotFoundException("Bet not found"));
        mockMvc.perform(put("/bets/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bet)))
                .andExpect(status().isNotFound());
        Mockito.verify(betServiceMock, times(1)).updateBet(1L, bet);
    }

    @Test
    @WithMockUser(username = "testUser", roles ={"UNVERIFIED"})
    public void update_asRoleUnverified_accessDenied() throws Exception{
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .build();
        Game game = new GameBuilder()
                .withId(1L)
                .withAwayTeamGoals(0)
                .withHomeTeamGoals(1)
                .build();
        Bet bet = new BetBuilder()
                .withId(1L)
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(1)
                .withScore(10)
                .withGame(game)
                .withUser(user)
                .build();
        when(betServiceMock.updateBet(1L, bet)).thenReturn(bet);
        mockMvc.perform(put("/bets/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bet)))
                .andExpect(status().isForbidden());
        Mockito.verify(betServiceMock, times(0)).updateBet(1L, bet);
    }

    @Test
    @WithMockUser(roles ="USER")
    public void delete_BetDeleted_ShouldReturnOk() throws Exception{
        mockMvc.perform(delete("/bets/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isOk());
        Mockito.verify(betServiceMock, times(1)).deleteBet(1L);
    }

    @Test
    @WithMockUser(roles ="USER")
    public void delete_BetNotFound_ShouldReturnNotFound() throws Exception{
        Mockito.doThrow(new ResourceNotFoundException("Could not find Bet")).when(betServiceMock).deleteBet(1L);
        mockMvc.perform(delete("/bets/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isNotFound());
        Mockito.verify(betServiceMock, times(1)).deleteBet(1L);
    }

    @Test
    @WithMockUser(username = "testUser", roles ={"UNVERIFIED"})
    public void delete_asRoleUnverified_accessDenied() throws Exception{
        mockMvc.perform(delete("/bets/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isForbidden());
        Mockito.verify(betServiceMock, times(0)).deleteBet(1L);
    }



    private HttpHeaders buildCORSHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Requested-With", "JUNIT");
        httpHeaders.add("Origin", corsAllowedOrigins);
        return httpHeaders;
    }
}