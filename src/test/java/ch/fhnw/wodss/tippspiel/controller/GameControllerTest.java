package ch.fhnw.wodss.tippspiel.controller;

import ch.fhnw.wodss.tippspiel.TestUtil;
import ch.fhnw.wodss.tippspiel.builder.*;
import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.dto.*;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.service.GameService;
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
public class GameControllerTest {

    @Value("${security.cors.allowedOrigins}")
    private String corsAllowedOrigins;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private GameService gameServiceMock;

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
        Mockito.reset(gameServiceMock);
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findAll_AllGameFound_ShouldReturnFound() throws Exception {
        GameDTO gameDTO = new GameDTOBuilder()
                .withHomeTeamId(1L)
                .withHomeTeamName("Russland")
                .withAwayTeamId(2L)
                .withAwayTeamName("Saudi-Arabien")
                .withLocationName("Moskau")
                .withPhaseName("Gruppenphase")
                .withDate("2018-06-14")
                .withTime("18:00:00")
                .build();
        GameDTO gameDTO2 = new GameDTOBuilder()
                .withHomeTeamId(3L)
                .withHomeTeamName("Ägypten")
                .withAwayTeamId(4L)
                .withAwayTeamName("Uruguay")
                .withLocationName("Jekaterinburg")
                .withPhaseName("Gruppenphase")
                .withDate("2018-06-15")
                .withTime("17:00:00")
                .build();
        List<GameDTO> gameDTOS = new ArrayList<>();
        gameDTOS.add(gameDTO);
        gameDTOS.add(gameDTO2);
        when(gameServiceMock.getAllGames()).thenReturn(gameDTOS);
        mockMvc.perform(get("/games")
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].homeTeam_id", equalTo(1)))
                .andExpect(jsonPath("$.[0].awayTeam_id", equalTo(2)))
                .andExpect(jsonPath("$.[0].homeTeamName", equalTo("Russland")))
                .andExpect(jsonPath("$.[0].awayTeamName", equalTo("Saudi-Arabien")))
                .andExpect(jsonPath("$.[0].locationName", equalTo("Moskau")))
                .andExpect(jsonPath("$.[0].phaseName", equalTo("Gruppenphase")))
                .andExpect(jsonPath("$.[0].date", equalTo("2018-06-14")))
                .andExpect(jsonPath("$.[0].time", equalTo("18:00:00")))
                .andExpect(jsonPath("$.[1].homeTeam_id", equalTo(3)))
                .andExpect(jsonPath("$.[1].awayTeam_id", equalTo(4)))
                .andExpect(jsonPath("$.[1].homeTeamName", equalTo("Ägypten")))
                .andExpect(jsonPath("$.[1].awayTeamName", equalTo("Uruguay")))
                .andExpect(jsonPath("$.[1].locationName", equalTo("Jekaterinburg")))
                .andExpect(jsonPath("$.[1].phaseName", equalTo("Gruppenphase")))
                .andExpect(jsonPath("$.[1].date", equalTo("2018-06-15")))
                .andExpect(jsonPath("$.[1].time", equalTo("17:00:00")));
        Mockito.verify(gameServiceMock, times(1)).getAllGames();
    }

    @Test
    @WithMockUser(username = "test", roles = {"UNVERIFIED"})
    public void findAll_asRoleUnverified_accessDenied() throws Exception {
        mockMvc.perform(get("/games").headers(buildCORSHeaders()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findById_GameFound_ShouldReturnFound() throws Exception {
        GameDTO gameDTO = new GameDTOBuilder()
                .withHomeTeamId(1L)
                .withHomeTeamName("Russland")
                .withAwayTeamId(2L)
                .withAwayTeamName("Saudi-Arabien")
                .withLocationName("Moskau")
                .withPhaseName("Gruppenphase")
                .withDate("2018-06-14")
                .withTime("18:00:00")
                .build();
        when(gameServiceMock.getGameById(eq(1L))).thenReturn(gameDTO);
        mockMvc.perform(get("/games/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.homeTeam_id", equalTo(1)))
                .andExpect(jsonPath("$.awayTeam_id", equalTo(2)))
                .andExpect(jsonPath("$.homeTeamName", equalTo("Russland")))
                .andExpect(jsonPath("$.awayTeamName", equalTo("Saudi-Arabien")))
                .andExpect(jsonPath("$.locationName", equalTo("Moskau")))
                .andExpect(jsonPath("$.phaseName", equalTo("Gruppenphase")))
                .andExpect(jsonPath("$.date", equalTo("2018-06-14")))
                .andExpect(jsonPath("$.time", equalTo("18:00:00")));
        Mockito.verify(gameServiceMock, times(1)).getGameById(eq(1L));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findById_GameNotExisting_ShouldReturnNotFound() throws Exception {
        when(gameServiceMock.getGameById(eq(2L))).
                thenThrow(new ResourceNotFoundException("Could not find Game"));
        mockMvc.perform(get("/games/{id}", 2L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isNotFound());
        Mockito.verify(gameServiceMock, times(1)).getGameById(eq(2L));
    }

    @Test
    @WithMockUser(username = "test", roles = {"UNVERIFIED"})
    public void findById_asRoleUnverified_accessDenied() throws Exception {
        mockMvc.perform(get("/games/{id}", 1L).headers(buildCORSHeaders()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void create_GameCreated_ShouldReturnCreated() throws Exception {
        RestGameDTO restGameDTO = new RestGameDTOBuilder()
                .withHomeTeamId(1L)
                .withAwayTeamId(2L)
                .withLocationId(1L)
                .withPhaseId(1L)
                .withDate("2018-06-14")
                .withTime("18:00:00")
                .build();
        GameDTO gameDTO = new GameDTOBuilder()
                .withHomeTeamId(restGameDTO.getHomeTeamId())
                .withHomeTeamName("Russland")
                .withAwayTeamId(restGameDTO.getAwayTeamId())
                .withAwayTeamName("Saudi-Arabien")
                .withLocationName("Moskau")
                .withPhaseName("Gruppenphase")
                .withDate(restGameDTO.getDate())
                .withTime(restGameDTO.getTime())
                .build();
        when(gameServiceMock.addGame(eq(restGameDTO))).thenReturn(gameDTO);
        mockMvc.perform(post("/games")
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(restGameDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.homeTeam_id", equalTo(1)))
                .andExpect(jsonPath("$.awayTeam_id", equalTo(2)))
                .andExpect(jsonPath("$.homeTeamName", equalTo("Russland")))
                .andExpect(jsonPath("$.awayTeamName", equalTo("Saudi-Arabien")))
                .andExpect(jsonPath("$.locationName", equalTo("Moskau")))
                .andExpect(jsonPath("$.phaseName", equalTo("Gruppenphase")))
                .andExpect(jsonPath("$.date", equalTo("2018-06-14")))
                .andExpect(jsonPath("$.time", equalTo("18:00:00")));
        Mockito.verify(gameServiceMock, times(1)).addGame(eq(restGameDTO));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"UNVERIFIED"})
    public void create_asRoleUnverified_accessDenied() throws Exception {
        RestGameDTO restGameDTO = new RestGameDTOBuilder()
                .withHomeTeamId(1L)
                .withAwayTeamId(2L)
                .withLocationId(1L)
                .withPhaseId(1L)
                .withDate("2018-06-14")
                .withTime("18:00:00")
                .build();
        GameDTO gameDTO = new GameDTOBuilder()
                .withHomeTeamId(restGameDTO.getHomeTeamId())
                .withHomeTeamName("Russland")
                .withAwayTeamId(restGameDTO.getAwayTeamId())
                .withAwayTeamName("Saudi-Arabien")
                .withLocationName("Moskau")
                .withPhaseName("Gruppenphase")
                .withDate(restGameDTO.getDate())
                .withTime(restGameDTO.getTime())
                .build();
        when(gameServiceMock.addGame(eq(restGameDTO))).thenReturn(gameDTO);
        mockMvc.perform(post("/games")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(TestUtil.convertObjectToJsonBytes(restGameDTO))
                .headers(buildCORSHeaders()))
                .andExpect(status().isForbidden());
        Mockito.verify(gameServiceMock, times(0)).addGame(eq(restGameDTO));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void update_GameUpdated_ShouldReturnOk() throws Exception {
        RestGameDTO restGameDTO = new RestGameDTOBuilder()
                .withHomeTeamId(1L)
                .withAwayTeamId(2L)
                .withLocationId(1L)
                .withPhaseId(4L)
                .withDate("2018-06-14")
                .withTime("18:00:00")
                .build();
        GameDTO gameDTO = new GameDTOBuilder()
                .withHomeTeamId(restGameDTO.getHomeTeamId())
                .withHomeTeamName("Russland")
                .withAwayTeamId(restGameDTO.getAwayTeamId())
                .withAwayTeamName("Saudi-Arabien")
                .withLocationName("Rostow am Don")
                .withPhaseName("Gruppenphase")
                .withDate(restGameDTO.getDate())
                .withTime(restGameDTO.getTime())
                .build();
        when(gameServiceMock.updateGame(eq(1L), eq(restGameDTO))).thenReturn(gameDTO);
        mockMvc.perform(put("/games/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(restGameDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.homeTeam_id", equalTo(1)))
                .andExpect(jsonPath("$.awayTeam_id", equalTo(2)))
                .andExpect(jsonPath("$.homeTeamName", equalTo("Russland")))
                .andExpect(jsonPath("$.awayTeamName", equalTo("Saudi-Arabien")))
                .andExpect(jsonPath("$.locationName", equalTo("Rostow am Don")))
                .andExpect(jsonPath("$.phaseName", equalTo("Gruppenphase")))
                .andExpect(jsonPath("$.date", equalTo("2018-06-14")))
                .andExpect(jsonPath("$.time", equalTo("18:00:00")));
        Mockito.verify(gameServiceMock, times(1)).updateGame(eq(1L), eq(restGameDTO));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void update_GameNotFound_ShouldReturnNotFound() throws Exception {
        RestGameDTO restGameDTO = new RestGameDTOBuilder()
                .withHomeTeamId(1L)
                .withAwayTeamId(2L)
                .withLocationId(1L)
                .withPhaseId(1L)
                .withDate("2018-06-14")
                .withTime("18:00:00")
                .build();
        when(gameServiceMock.updateGame(eq(1L), eq(restGameDTO))).thenThrow(new ResourceNotFoundException("Game not found"));
        mockMvc.perform(put("/games/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(restGameDTO)))
                .andExpect(status().isNotFound());
        Mockito.verify(gameServiceMock, times(1)).updateGame(eq(1L), eq(restGameDTO));

    }

    @Test
    @WithMockUser(username = "testUser", roles = {"UNVERIFIED"})
    public void update_asRoleUnverified_accessDenied() throws Exception {
        RestGameDTO restGameDTO = new RestGameDTOBuilder()
                .withHomeTeamId(1L)
                .withAwayTeamId(2L)
                .withLocationId(1L)
                .withPhaseId(1L)
                .withDate("2018-06-14")
                .withTime("18:00:00")
                .build();
        GameDTO gameDTO = new GameDTOBuilder()
                .withHomeTeamId(restGameDTO.getHomeTeamId())
                .withHomeTeamName("Russland")
                .withAwayTeamId(restGameDTO.getAwayTeamId())
                .withAwayTeamName("Saudi-Arabien")
                .withLocationName("Moskau")
                .withPhaseName("Gruppenphase")
                .withDate(restGameDTO.getDate())
                .withTime(restGameDTO.getTime())
                .build();
        when(gameServiceMock.updateGame(eq(1L), eq(restGameDTO))).thenReturn(gameDTO);
        mockMvc.perform(put("/games/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(restGameDTO)))
                .andExpect(status().isForbidden());
        Mockito.verify(gameServiceMock, times(0)).updateGame(eq(1L), eq(restGameDTO));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete_GameDeleted_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/games/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isOk());
        Mockito.verify(gameServiceMock, times(1)).deleteGame(eq(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete_GameNotFound_ShouldReturnNotFound() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Could not find Game")).when(gameServiceMock).
                deleteGame(eq(1L));
        mockMvc.perform(delete("/games/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isNotFound());
        Mockito.verify(gameServiceMock, times(1)).deleteGame(eq(1L));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"UNVERIFIED"})
    public void delete_asRoleUnverified_accessDenied() throws Exception {
        mockMvc.perform(delete("/games/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isForbidden());
        Mockito.verify(gameServiceMock, times(0)).deleteGame(eq(1L));
    }

    private HttpHeaders buildCORSHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Requested-With", "JUNIT");
        httpHeaders.add("Origin", corsAllowedOrigins);
        return httpHeaders;
    }

}
