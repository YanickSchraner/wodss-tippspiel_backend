package ch.fhnw.wodss.tippspiel.controller;

import ch.fhnw.wodss.tippspiel.TestUtil;
import ch.fhnw.wodss.tippspiel.builder.*;
import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.dto.*;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.service.TournamentTeamService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TournamentTeamControllerTest {

    @Value("${security.cors.allowedOrigins}")
    private String corsAllowedOrigins;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private TournamentTeamService tournamentTeamServiceMock;

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
        Mockito.reset(tournamentTeamServiceMock);
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findAll_AllTournamentTeamFound_ShouldReturnFound() throws Exception {
        TournamentTeamDTO tournamentTeamDTO = new TournamentTeamDTOBuilder()
                .withName("Russland")
                .withTournamentGroupName("GruppeA")
                .build();
        TournamentTeamDTO tournamentTeamDTO2 = new TournamentTeamDTOBuilder()
                .withName("Brasilien")
                .withTournamentGroupName("GruppeE")
                .build();
        List<TournamentTeamDTO> tournamentGroups = new ArrayList<>();
        tournamentGroups.add(tournamentTeamDTO);
        tournamentGroups.add(tournamentTeamDTO2);
        when(tournamentTeamServiceMock.getAllTournamentTeams()).thenReturn(tournamentGroups);
        mockMvc.perform(get("/tournamentTeams")
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name", equalTo("Russland")))
                .andExpect(jsonPath("$.[0].tournamentGroupName", equalTo("GruppeA")))
                .andExpect(jsonPath("$.[1].name", equalTo("Brasilien")))
                .andExpect(jsonPath("$.[1].tournamentGroupName", equalTo("GruppeE")));
        Mockito.verify(tournamentTeamServiceMock, times(1)).getAllTournamentTeams();
    }

    @Test
    @WithMockUser(username = "test", roles = {"UNVERIFIED"})
    public void findAll_asRoleUnverified_accessDenied() throws Exception {
        mockMvc.perform(get("/tournamentTeams").headers(buildCORSHeaders()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findById_TournamentTeamFound_ShouldReturnFound() throws Exception {
        TournamentTeamDTO tournamentTeamDTO = new TournamentTeamDTOBuilder()
                .withName("Russland")
                .withTournamentGroupName("GruppeA")
                .build();
        when(tournamentTeamServiceMock.getTournamentTeamById(eq(1L))).thenReturn(tournamentTeamDTO);
        mockMvc.perform(get("/tournamentTeams/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("Russland")))
                .andExpect(jsonPath("$.tournamentGroupName", equalTo("GruppeA")));
        Mockito.verify(tournamentTeamServiceMock, times(1)).getTournamentTeamById(eq(1L));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findById_TournamentTeamNotExisting_ShouldReturnNotFound() throws Exception {
        when(tournamentTeamServiceMock.getTournamentTeamById(eq(2L))).
                thenThrow(new ResourceNotFoundException("Could not find TournamentTeam"));
        mockMvc.perform(get("/tournamentTeams/{id}", 2L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isNotFound());
        Mockito.verify(tournamentTeamServiceMock, times(1)).getTournamentTeamById(eq(2L));
    }

    @Test
    @WithMockUser(username = "test", roles = {"UNVERIFIED"})
    public void findById_asRoleUnverified_accessDenied() throws Exception {
        mockMvc.perform(get("/tournamentTeams/{id}", 1L).headers(buildCORSHeaders()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findByName_TournamentTeamFound_ShouldReturnFound() throws Exception {
        TournamentTeamDTO tournamentTeamDTO = new TournamentTeamDTOBuilder()
                .withName("Russland")
                .withTournamentGroupName("GruppeA")
                .build();
        when(tournamentTeamServiceMock.getTournamentTeamByName(eq("GruppeA"))).thenReturn(tournamentTeamDTO);
        mockMvc.perform(get("/tournamentTeams/name/{name}", "GruppeA")
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("Russland")))
                .andExpect(jsonPath("$.tournamentGroupName", equalTo("GruppeA")));
        Mockito.verify(tournamentTeamServiceMock, times(1)).getTournamentTeamByName(eq("GruppeA"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findByName_TournamentTeamNotExisting_ShouldReturnFound() throws Exception {
        when(tournamentTeamServiceMock.getTournamentTeamByName(eq("GruppeA"))).
                thenThrow(new ResourceNotFoundException("Could not find TournamentTeam"));
        mockMvc.perform(get("/tournamentTeams/name/{name}", "GruppeA")
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isNotFound());
        Mockito.verify(tournamentTeamServiceMock, times(1)).getTournamentTeamByName(eq("GruppeA"));
    }

    @Test
    @WithMockUser(username = "test", roles = {"UNVERIFIED"})
    public void findByName_asRoleUnverified_accessDenied() throws Exception {
        mockMvc.perform(get("/tournamentTeams/name/{name}", 1L).headers(buildCORSHeaders()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void create_TournamentTeamCreated_ShouldReturnCreated() throws Exception {
        RestTournamentTeamDTO restTournamentTeamDTO = new RestTournamentTeamDTOBuilder()
                .withName("Russland")
                .withTournamentGroupId(1L)
                .build();
        TournamentTeamDTO tournamentTeamDTO = new TournamentTeamDTOBuilder()
                .withName("Russland")
                .withTournamentGroupName("GruppeA")
                .build();
        when(tournamentTeamServiceMock.addTournamentTeam(eq(restTournamentTeamDTO))).thenReturn(tournamentTeamDTO);
        mockMvc.perform(post("/tournamentTeams")
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(restTournamentTeamDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", equalTo("Russland")))
                .andExpect(jsonPath("$.tournamentGroupName", equalTo("GruppeA")));
        Mockito.verify(tournamentTeamServiceMock, times(1)).addTournamentTeam(eq(restTournamentTeamDTO));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void create_InvalidTournamentTeamFormat_ShouldReturnBadRequest() throws Exception {
        RestTournamentTeamDTO restTournamentTeamDTO = new RestTournamentTeamDTOBuilder()
                .withName("Russland")
                .build();
        TournamentTeamDTO tournamentTeamDTO = new TournamentTeamDTOBuilder()
                .withName("Russland")
                .withTournamentGroupName("GruppeA")
                .build();
        when(tournamentTeamServiceMock.addTournamentTeam(eq(restTournamentTeamDTO))).thenReturn(tournamentTeamDTO);
        mockMvc.perform(post("/tournamentTeams")
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(restTournamentTeamDTO)))
                .andExpect(status().isBadRequest());
        Mockito.verify(tournamentTeamServiceMock, times(0)).addTournamentTeam(eq(restTournamentTeamDTO));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"UNVERIFIED"})
    public void create_asRoleUnverified_accessDenied() throws Exception {
        RestTournamentTeamDTO restTournamentTeamDTO = new RestTournamentTeamDTOBuilder()
                .withName("Russland")
                .withTournamentGroupId(1L)
                .build();
        TournamentTeamDTO tournamentTeamDTO = new TournamentTeamDTOBuilder()
                .withName("Russland")
                .withTournamentGroupName("GruppeA")
                .build();
        when(tournamentTeamServiceMock.addTournamentTeam(eq(restTournamentTeamDTO))).thenReturn(tournamentTeamDTO);
        mockMvc.perform(post("/tournamentTeams")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(TestUtil.convertObjectToJsonBytes(restTournamentTeamDTO))
                .headers(buildCORSHeaders()))
                .andExpect(status().isForbidden());
        Mockito.verify(tournamentTeamServiceMock, times(0)).addTournamentTeam(eq(restTournamentTeamDTO));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void update_TournamentTeamUpdated_ShouldReturnOk() throws Exception {
        RestTournamentTeamDTO restTournamentTeamDTO = new RestTournamentTeamDTOBuilder()
                .withName("Russland")
                .withTournamentGroupId(2L)
                .build();
        TournamentTeamDTO tournamentTeamDTO = new TournamentTeamDTOBuilder()
                .withName("Russland")
                .withTournamentGroupName("GruppeB")
                .build();
        when(tournamentTeamServiceMock.updateTournamentTeam(eq(1L), eq(restTournamentTeamDTO))).thenReturn(tournamentTeamDTO);
        mockMvc.perform(put("/tournamentTeams/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(restTournamentTeamDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("Russland")))
                .andExpect(jsonPath("$.tournamentGroupName", equalTo("GruppeB")));
        Mockito.verify(tournamentTeamServiceMock, times(1)).updateTournamentTeam(eq(1L), eq(restTournamentTeamDTO));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void update_InvalidTournamentTeamFormat_ShouldReturnBadRequest() throws Exception {
        RestTournamentTeamDTO restTournamentTeamDTO = new RestTournamentTeamDTOBuilder()
                .withTournamentGroupId(2L)
                .build();
        TournamentTeamDTO tournamentTeamDTO = new TournamentTeamDTOBuilder()
                .withName("Russland")
                .withTournamentGroupName("GruppeB")
                .build();
        when(tournamentTeamServiceMock.updateTournamentTeam(eq(1L), eq(restTournamentTeamDTO))).thenReturn(tournamentTeamDTO);
        mockMvc.perform(put("/tournamentTeams/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(restTournamentTeamDTO)))
                .andExpect(status().isBadRequest());
        Mockito.verify(tournamentTeamServiceMock, times(0)).updateTournamentTeam(eq(1L), eq(restTournamentTeamDTO));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void update_TournamentTeamNotFound_ShouldReturnNotFound() throws Exception {
        RestTournamentTeamDTO restTournamentTeamDTO = new RestTournamentTeamDTOBuilder()
                .withName("Russland")
                .withTournamentGroupId(1L)
                .build();
        when(tournamentTeamServiceMock.updateTournamentTeam(eq(1L), eq(restTournamentTeamDTO))).
                thenThrow(new ResourceNotFoundException("Tournament team not found"));
        mockMvc.perform(put("/tournamentTeams/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(restTournamentTeamDTO)))
                .andExpect(status().isNotFound());
        Mockito.verify(tournamentTeamServiceMock, times(1)).updateTournamentTeam(eq(1L), eq(restTournamentTeamDTO));

    }

    @Test
    @WithMockUser(username = "testUser", roles = {"UNVERIFIED"})
    public void update_asRoleUnverified_accessDenied() throws Exception {
        RestTournamentTeamDTO restTournamentTeamDTO = new RestTournamentTeamDTOBuilder()
                .withName("Russland")
                .withTournamentGroupId(1L)
                .build();
        TournamentTeamDTO tournamentTeamDTO = new TournamentTeamDTOBuilder()
                .withName("Russland")
                .withTournamentGroupName("GruppeA")
                .build();
        when(tournamentTeamServiceMock.updateTournamentTeam(eq(1L), eq(restTournamentTeamDTO))).thenReturn(tournamentTeamDTO);
        mockMvc.perform(put("/tournamentTeams/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(restTournamentTeamDTO)))
                .andExpect(status().isForbidden());
        Mockito.verify(tournamentTeamServiceMock, times(0)).updateTournamentTeam(eq(1L), eq(restTournamentTeamDTO));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete_TournamentTeamDeleted_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/tournamentTeams/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isOk());
        Mockito.verify(tournamentTeamServiceMock, times(1)).deleteTournamentTeam(eq(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete_TournamentTeamNotFound_ShouldReturnNotFound() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Could not find TournamentTeam")).when(tournamentTeamServiceMock).
                deleteTournamentTeam(eq(1L));
        mockMvc.perform(delete("/tournamentTeams/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isNotFound());
        Mockito.verify(tournamentTeamServiceMock, times(1)).deleteTournamentTeam(eq(1L));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"UNVERIFIED"})
    public void delete_asRoleUnverified_accessDenied() throws Exception {
        mockMvc.perform(delete("/tournamentTeams/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isForbidden());
        Mockito.verify(tournamentTeamServiceMock, times(0)).deleteTournamentTeam(eq(1L));
    }

    private HttpHeaders buildCORSHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Requested-With", "JUNIT");
        httpHeaders.add("Origin", corsAllowedOrigins);
        return httpHeaders;
    }

}
