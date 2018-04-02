package ch.fhnw.wodss.tippspiel.controller;

import ch.fhnw.wodss.tippspiel.TestUtil;
import ch.fhnw.wodss.tippspiel.builder.BetBuilder;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    public void mockUserService(){
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
    @WithMockUser(roles="USER")
    public void findById_BetFound_ShouldReturnFound() throws Exception {
        Bet bet = new BetBuilder()
                .withId(1L)
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(1)
                .withScore(10)
                .withGame(new Game())
                .withUser(new User())
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
                .andExpect(jsonPath("$.score", equalTo(10)));
        Mockito.verify(betServiceMock, times(1)).getBetById(1L);
    }

    @Test
    @WithMockUser(roles="USER")
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
    @WithMockUser(roles="USER")
    public void create_BetCreated_ShouldReturnCreted() throws Exception {
        Bet bet = new BetBuilder()
                .withId(1L)
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(1)
                .withScore(10)
                .withGame(new Game())
                .withUser(new User())
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
                .andExpect(jsonPath("$.score", equalTo(10)));
        Mockito.verify(betServiceMock, times(1)).getBetById(1L);
    }

    @Test
    @WithMockUser(roles="USER")
    public void create_InvalidBetFormat_ShouldReturnBadRequest() throws Exception {
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
    public void create_asRoleUnverified_accessDenied() throws Exception {
        Bet bet = new BetBuilder()
                .withId(1L)
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(1)
                .withScore(10)
                .withGame(new Game())
                .withUser(new User())
                .build();
        mockMvc.perform(post("/bets")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(TestUtil.convertObjectToJsonBytes(bet))
                .headers(buildCORSHeaders()))
                .andExpect(status().isForbidden());
    }



    private HttpHeaders buildCORSHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Requested-With", "JUNIT");
        httpHeaders.add("Origin", corsAllowedOrigins);
        return httpHeaders;
    }
}