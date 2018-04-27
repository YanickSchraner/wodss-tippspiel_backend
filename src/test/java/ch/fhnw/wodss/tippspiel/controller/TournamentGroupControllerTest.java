package ch.fhnw.wodss.tippspiel.controller;

import ch.fhnw.wodss.tippspiel.TestUtil;
import ch.fhnw.wodss.tippspiel.builder.TournamentGroupBuilder;
import ch.fhnw.wodss.tippspiel.domain.TournamentGroup;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.service.TournamentGroupService;
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
    private TournamentGroupService tournamentGroupService;

    @Before
    public void mockUserService() {
        ArrayList<TournamentGroup> tournamentGroups = new ArrayList<>();
        tournamentGroups.add(new TournamentGroupBuilder().withName("GroupA").withId(1L).build());
        tournamentGroups.add(new TournamentGroupBuilder().withName("GroupB").withId(2L).build());
        when(tournamentGroupService.getAllTournamentGroups()).thenReturn(tournamentGroups);
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
        /*Mockito.verify(tournamentGroupServiceMock).getAllTournamentGroups(argThat(new ArgumentMatcher<TournamentGroup>() {
            @Override
            public boolean matches(TournamentGroup argument) {
                return false;
            }
        }), anyString());*/
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findById_TournamentGroupFound_ShouldReturnFound() throws Exception {
        TournamentGroup tournamentGroup = new TournamentGroupBuilder()
                .withId(1L)
                .withName("GroupA")
                .build();
        Mockito.verify(tournamentGroupServiceMock, times(1)).
                getTournamentGroupById(eq(tournamentGroup.getId()));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findById_TournamentGroupNotExisting_ShouldReturnNotFound() throws Exception {
        TournamentGroup tournamentGroup = new TournamentGroupBuilder()
                .withId(3L)
                .withName("GroupC")
                .build();
        when(tournamentGroupServiceMock.getTournamentGroupById(eq(tournamentGroup.getId()))).
                thenThrow(new ResourceNotFoundException("Could not find TournamentGroup"));
        mockMvc.perform(get("/tournamentGroups/{id}", tournamentGroup.getId())
                .headers(buildCORSHeaders())
                .header("Accept", "application/json"))
                .andExpect(status().isNotFound());
        Mockito.verify(tournamentGroupServiceMock, times(1)).
                getTournamentGroupById(eq(tournamentGroup.getId()));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findByName_TournamentGroupFound_ShouldReturnFound() throws Exception {
        TournamentGroup tournamentGroup = new TournamentGroupBuilder()
                .withId(1L)
                .withName("GroupA")
                .build();
        Mockito.verify(tournamentGroupServiceMock, times(1)).
                getTournamentGroupByName(eq(tournamentGroup.getName()));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findByName_TournamentGroupNotExisting_ShouldReturnFound() throws Exception {
        TournamentGroup tournamentGroup = new TournamentGroupBuilder()
                .withId(3L)
                .withName("GroupC")
                .build();
        Mockito.verify(tournamentGroupServiceMock, times(1)).
                getTournamentGroupByName(eq(tournamentGroup.getName()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void create_TournamentGroupCreated_ShouldReturnCreated() throws Exception {
        TournamentGroup tournamentGroup = new TournamentGroupBuilder()
                .withId(4L)
                .withName("GroupD")
                .build();
        when(tournamentGroupServiceMock.addTournamentGroup(eq(tournamentGroup))).thenReturn(tournamentGroup);
        mockMvc.perform(post("/tournamentGroups")
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tournamentGroup)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(tournamentGroup.getId())))
                .andExpect(jsonPath("$.name", equalTo(tournamentGroup.getName())));
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
    @WithMockUser(roles = "ADMIN")
    public void update_TournamentGroupUpdated_ShouldReturnOk() throws Exception {
        TournamentGroup tournamentGroup = new TournamentGroupBuilder()
                .withId(1L)
                .withName("GroupAA")
                .build();
        when(tournamentGroupServiceMock.updateTournamentGroup(eq(tournamentGroup.getId()), eq(tournamentGroup))).thenReturn(tournamentGroup);
        mockMvc.perform(put("/tournamentGroups/{id}", tournamentGroup.getId())
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tournamentGroup)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(tournamentGroup.getId())))
                .andExpect(jsonPath("$.name", equalTo(tournamentGroup.getName())));
        Mockito.verify(tournamentGroupServiceMock, times(1)).
                updateTournamentGroup(eq(tournamentGroup.getId()), eq(tournamentGroup));
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

    private HttpHeaders buildCORSHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Requested-With", "JUNIT");
        httpHeaders.add("Origin", corsAllowedOrigins);
        return httpHeaders;
    }

}
