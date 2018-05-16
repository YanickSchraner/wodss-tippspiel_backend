package ch.fhnw.wodss.tippspiel.controller;

import ch.fhnw.wodss.tippspiel.TestUtil;
import ch.fhnw.wodss.tippspiel.builder.TournamentGroupBuilder;
import ch.fhnw.wodss.tippspiel.builder.UserBuilder;
import ch.fhnw.wodss.tippspiel.domain.TournamentGroup;
import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.service.TournamentGroupService;
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
public class TournamentGroupControllerTest {

    @Value("${security.cors.allowedOrigins}")
    private String corsAllowedOrigins;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private TournamentGroupService tournamentGroupServiceMock;

    @MockBean
    private UserService userService;

    @Before
    public void mockUserService() {
        ArrayList<User> users = new ArrayList<>();
        users.add(new UserBuilder().withName("Tom").withRole("USER").withId(1L).build());
        when(userService.getAllUsers()).thenReturn(users);
    }

    @Before
    public void setUp() {
        Mockito.reset(tournamentGroupServiceMock);
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findAll_AllTournamentGroupFound_ShouldReturnFound() throws Exception {
        ArrayList<TournamentGroup> tournamentGroups = new ArrayList<>();
        tournamentGroups.add(new TournamentGroupBuilder().withName("GroupA").withId(1L).build());
        tournamentGroups.add(new TournamentGroupBuilder().withName("GroupB").withId(2L).build());
        when(tournamentGroupServiceMock.getAllTournamentGroups()).thenReturn(tournamentGroups);
        mockMvc.perform(get("/tournamentGroups")
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", equalTo(1)))
                .andExpect(jsonPath("$.[0].name", equalTo("GroupA")))
                .andExpect(jsonPath("$.[1].id", equalTo(2)))
                .andExpect(jsonPath("$.[1].name", equalTo("GroupB")));
        Mockito.verify(tournamentGroupServiceMock, times(1)).getAllTournamentGroups();
    }

    @Test
    @WithMockUser(username = "test", roles = {"UNVERIFIED"})
    public void findAll_asRoleUnverified_accessDenied() throws Exception {
        mockMvc.perform(get("/tournamentGroups").headers(buildCORSHeaders()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findById_TournamentGroupFound_ShouldReturnFound() throws Exception {
        TournamentGroup tournamentGroup = new TournamentGroupBuilder()
                .withId(1L)
                .withName("GroupA")
                .build();
        when(tournamentGroupServiceMock.getTournamentGroupById(eq(1L))).thenReturn(tournamentGroup);
        mockMvc.perform(get("/tournamentGroups/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("GroupA")));
        Mockito.verify(tournamentGroupServiceMock, times(1)).getTournamentGroupById(eq(1L));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findById_TournamentGroupNotExisting_ShouldReturnNotFound() throws Exception {
        when(tournamentGroupServiceMock.getTournamentGroupById(eq(2L))).
                thenThrow(new ResourceNotFoundException("Could not find TournamentGroup"));
        mockMvc.perform(get("/tournamentGroups/{id}", 2L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isNotFound());
        Mockito.verify(tournamentGroupServiceMock, times(1)).getTournamentGroupById(eq(2L));
    }

    @Test
    @WithMockUser(username = "test", roles = {"UNVERIFIED"})
    public void findById_asRoleUnverified_accessDenied() throws Exception {
        mockMvc.perform(get("/tournamentGroups/{id}", 1L).headers(buildCORSHeaders()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findByName_TournamentGroupFound_ShouldReturnFound() throws Exception {
        TournamentGroup tournamentGroup = new TournamentGroupBuilder()
                .withId(1L)
                .withName("GroupA")
                .build();
        when(tournamentGroupServiceMock.getTournamentGroupByName(eq("GroupA"))).thenReturn(tournamentGroup);
        mockMvc.perform(get("/tournamentGroups/name/{name}", "GroupA")
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("GroupA")));
        Mockito.verify(tournamentGroupServiceMock, times(1)).getTournamentGroupByName(eq("GroupA"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findByName_TournamentGroupNotExisting_ShouldReturnFound() throws Exception {
        when(tournamentGroupServiceMock.getTournamentGroupByName(eq("GroupA"))).
                thenThrow(new ResourceNotFoundException("Could not find TournamentGroup"));
        mockMvc.perform(get("/tournamentGroups/name/{name}", "GroupA")
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isNotFound());
        Mockito.verify(tournamentGroupServiceMock, times(1)).getTournamentGroupByName(eq("GroupA"));
    }

    @Test
    @WithMockUser(username = "test", roles = {"UNVERIFIED"})
    public void findByName_asRoleUnverified_accessDenied() throws Exception {
        mockMvc.perform(get("/tournamentGroups/name/{name}", 1L).headers(buildCORSHeaders()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void create_TournamentGroupCreated_ShouldReturnCreated() throws Exception {
        TournamentGroup tournamentGroup = new TournamentGroupBuilder()
                .withId(1L)
                .withName("GroupA")
                .build();
        when(tournamentGroupServiceMock.addTournamentGroup(eq(tournamentGroup))).thenReturn(tournamentGroup);
        mockMvc.perform(post("/tournamentGroups")
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tournamentGroup)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("GroupA")));
        Mockito.verify(tournamentGroupServiceMock, times(1)).addTournamentGroup(eq(tournamentGroup));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void create_InvalidTournamentGroupFormat_ShouldReturnBadRequest() throws Exception {
        TournamentGroup tournamentGroup = new TournamentGroupBuilder()
                .withId(1L)
                .build();
        when(tournamentGroupServiceMock.addTournamentGroup(eq(tournamentGroup))).thenReturn(tournamentGroup);
        mockMvc.perform(post("/tournamentGroups")
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tournamentGroup)))
                .andExpect(status().isBadRequest());
        Mockito.verify(tournamentGroupServiceMock, times(0)).addTournamentGroup(eq(tournamentGroup));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"UNVERIFIED"})
    public void create_asRoleUnverified_accessDenied() throws Exception {
        TournamentGroup tournamentGroup = new TournamentGroupBuilder()
                .withId(1L)
                .withName("GroupA")
                .build();
        when(tournamentGroupServiceMock.addTournamentGroup(eq(tournamentGroup))).thenReturn(tournamentGroup);
        mockMvc.perform(post("/tournamentGroups")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(TestUtil.convertObjectToJsonBytes(tournamentGroup))
                .headers(buildCORSHeaders()))
                .andExpect(status().isForbidden());
        Mockito.verify(tournamentGroupServiceMock, times(0)).addTournamentGroup(eq(tournamentGroup));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void update_TournamentGroupUpdated_ShouldReturnOk() throws Exception {
        TournamentGroup tournamentGroup = new TournamentGroupBuilder()
                .withId(1L)
                .withName("GroupB")
                .build();
        when(tournamentGroupServiceMock.updateTournamentGroup(eq(1L), eq(tournamentGroup))).thenReturn(tournamentGroup);
        mockMvc.perform(put("/tournamentGroups/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tournamentGroup)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("GroupB")));
        Mockito.verify(tournamentGroupServiceMock, times(1)).updateTournamentGroup(eq(1L), eq(tournamentGroup));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void update_InvalidTournamentGroupFormat_ShouldReturnBadRequest() throws Exception {
        TournamentGroup tournamentGroup = new TournamentGroupBuilder()
                .withId(1L)
                .build();
        when(tournamentGroupServiceMock.updateTournamentGroup(eq(1L), eq(tournamentGroup))).thenReturn(tournamentGroup);
        mockMvc.perform(put("/tournamentGroups/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tournamentGroup)))
                .andExpect(status().isBadRequest());
        Mockito.verify(tournamentGroupServiceMock, times(0)).updateTournamentGroup(eq(1L), eq(tournamentGroup));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void update_TournamentGroupNotFound_ShouldReturnNotFound() throws Exception {
        TournamentGroup tournamentGroup = new TournamentGroupBuilder()
                .withId(1L)
                .withName("GroupA")
                .build();
        when(tournamentGroupServiceMock.updateTournamentGroup(eq(1L), eq(tournamentGroup))).
                thenThrow(new ResourceNotFoundException("Tournament group not found"));
        mockMvc.perform(put("/tournamentGroups/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tournamentGroup)))
                .andExpect(status().isNotFound());
        Mockito.verify(tournamentGroupServiceMock, times(1)).updateTournamentGroup(eq(1L), eq(tournamentGroup));

    }

    @Test
    @WithMockUser(username = "testUser", roles = {"UNVERIFIED"})
    public void update_asRoleUnverified_accessDenied() throws Exception {
        TournamentGroup tournamentGroup = new TournamentGroupBuilder()
                .withId(1L)
                .withName("GroupA")
                .build();
        when(tournamentGroupServiceMock.updateTournamentGroup(eq(1L), eq(tournamentGroup))).thenReturn(tournamentGroup);
        mockMvc.perform(put("/tournamentGroups/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tournamentGroup)))
                .andExpect(status().isForbidden());
        Mockito.verify(tournamentGroupServiceMock, times(0)).updateTournamentGroup(eq(1L), eq(tournamentGroup));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete_TournamentGroupDeleted_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/tournamentGroups/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isOk());
        Mockito.verify(tournamentGroupServiceMock, times(1)).deleteTournamentGroup(eq(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void delete_TournamentGroupNotFound_ShouldReturnNotFound() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Could not find TournamentGroup")).when(tournamentGroupServiceMock).
                deleteTournamentGroup(eq(1L));
        mockMvc.perform(delete("/tournamentGroups/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isNotFound());
        Mockito.verify(tournamentGroupServiceMock, times(1)).deleteTournamentGroup(eq(1L));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"UNVERIFIED"})
    public void delete_asRoleUnverified_accessDenied() throws Exception {
        mockMvc.perform(delete("/tournamentGroups/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isForbidden());
        Mockito.verify(tournamentGroupServiceMock, times(0)).deleteTournamentGroup(eq(1L));
    }

    private HttpHeaders buildCORSHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Requested-With", "JUNIT");
        httpHeaders.add("Origin", corsAllowedOrigins);
        return httpHeaders;
    }

}
