package ch.fhnw.wodss.tippspiel.service;

import ch.fhnw.wodss.tippspiel.builder.RestTournamentTeamDTOBuilder;
import ch.fhnw.wodss.tippspiel.builder.TournamentGroupBuilder;
import ch.fhnw.wodss.tippspiel.builder.TournamentTeamBuilder;
import ch.fhnw.wodss.tippspiel.domain.TournamentGroup;
import ch.fhnw.wodss.tippspiel.domain.TournamentTeam;
import ch.fhnw.wodss.tippspiel.dto.RestTournamentTeamDTO;
import ch.fhnw.wodss.tippspiel.dto.TournamentTeamDTO;
import ch.fhnw.wodss.tippspiel.exception.IllegalActionException;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.persistance.TournamentGroupRepository;
import ch.fhnw.wodss.tippspiel.persistance.TournamentTeamRepository;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@WebMvcTest(TournamentTeamService.class)
public class TournamentTeamServiceTest {

    @Autowired
    TournamentTeamService tournamentTeamService;

    @MockBean
    TournamentTeamRepository tournamentTeamRepositoryMock;

    @MockBean
    TournamentGroupRepository tournamentGroupRepositoryMock;

    @Before
    public void setup() {
        Mockito.reset(tournamentTeamRepositoryMock, tournamentGroupRepositoryMock);
    }

    @Test
    public void getAllTournamentTeams_ok() {
        List<TournamentTeam> teams = new ArrayList<>();
        TournamentGroup group = new TournamentGroupBuilder()
                .withId(1L)
                .withName("A")
                .build();
        TournamentTeam team1 = new TournamentTeamBuilder()
                .withId(1L)
                .withName("Schweiz")
                .withGroup(group)
                .build();
        TournamentTeam team2 = new TournamentTeamBuilder()
                .withId(1L)
                .withName("Frankreich")
                .withGroup(group)
                .build();
        teams.add(team1);
        teams.add(team2);

        when(tournamentTeamRepositoryMock.findAll()).thenReturn(teams);

        List<TournamentTeamDTO> result = tournamentTeamService.getAllTournamentTeams();
        assertEquals(teams.size(), result.size());
        assertEquals(team1.getName(), result.get(0).getName());
        assertEquals(team2.getName(), result.get(1).getName());
        assertEquals(team1.getGroup().getName(), result.get(0).getTournamentGroupName());
        assertEquals(team2.getGroup().getName(), result.get(1).getTournamentGroupName());

        verify(tournamentTeamRepositoryMock, times(1)).findAll();
    }

    @Test
    public void getTournamentTeamById_ok() {
        TournamentGroup group = new TournamentGroupBuilder()
                .withId(1L)
                .withName("A")
                .build();
        TournamentTeam team1 = new TournamentTeamBuilder()
                .withId(1L)
                .withName("Schweiz")
                .withGroup(group)
                .build();

        when(tournamentTeamRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(team1));

        TournamentTeamDTO result = tournamentTeamService.getTournamentTeamById(1L);
        assertEquals(team1.getName(), result.getName());
        assertEquals(team1.getGroup().getName(), result.getTournamentGroupName());

        verify(tournamentTeamRepositoryMock, times(1)).findById(1L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getTournamentTeamById_notFound() {
        when(tournamentTeamRepositoryMock.findById(1L)).thenReturn(Optional.empty());
        tournamentTeamService.getTournamentTeamById(1L);
        verify(tournamentTeamRepositoryMock, times(1)).findById(1L);
    }

    @Test
    public void getTournamentTeamByName_ok() {
        TournamentGroup group = new TournamentGroupBuilder()
                .withId(1L)
                .withName("A")
                .build();
        TournamentTeam team1 = new TournamentTeamBuilder()
                .withId(1L)
                .withName("Schweiz")
                .withGroup(group)
                .build();

        when(tournamentTeamRepositoryMock.findTournamentTeamByNameEquals("Schweiz")).thenReturn(Optional.ofNullable(team1));

        TournamentTeamDTO result = tournamentTeamService.getTournamentTeamByName("Schweiz");
        assertEquals(team1.getName(), result.getName());
        assertEquals(team1.getGroup().getName(), result.getTournamentGroupName());

        verify(tournamentTeamRepositoryMock, times(1)).findTournamentTeamByNameEquals("Schweiz");
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getTournamentTeamByName_notFound() {
        when(tournamentTeamRepositoryMock.findTournamentTeamByNameEquals("Schweiz")).thenReturn(Optional.empty());
        tournamentTeamService.getTournamentTeamByName("Schweiz");
        verify(tournamentTeamRepositoryMock, times(1)).findTournamentTeamByNameEquals("Schweiz");
    }

    @Test
    public void addTournamentTeam_ok() {
        TournamentGroup group = new TournamentGroupBuilder()
                .withId(1L)
                .withName("A")
                .build();
        RestTournamentTeamDTO restDTO = new RestTournamentTeamDTOBuilder()
                .withName("Schweiz")
                .withTournamentGroupId(1L)
                .build();
        when(tournamentTeamRepositoryMock.findTournamentTeamByNameEquals("Schweiz")).thenReturn(Optional.empty());
        when(tournamentGroupRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(group));

        TournamentTeamDTO result = tournamentTeamService.addTournamentTeam(restDTO);
        assertEquals(restDTO.getName(), result.getName());
        assertEquals(group.getName(), result.getTournamentGroupName());

        verify(tournamentTeamRepositoryMock, times(1)).findTournamentTeamByNameEquals("Schweiz");
        verify(tournamentGroupRepositoryMock, times(1)).findById(1L);
        verify(tournamentTeamRepositoryMock, times(1)).save(any(TournamentTeam.class));
    }

    @Test(expected = IllegalActionException.class)
    public void addTournamentTeam_alreadyExists() {
        TournamentGroup group = new TournamentGroupBuilder()
                .withId(1L)
                .withName("A")
                .build();
        TournamentTeam team1 = new TournamentTeamBuilder()
                .withId(1L)
                .withName("Schweiz")
                .withGroup(group)
                .build();
        RestTournamentTeamDTO restDTO = new RestTournamentTeamDTOBuilder()
                .withName("Schweiz")
                .withTournamentGroupId(1L)
                .build();
        when(tournamentTeamRepositoryMock.findTournamentTeamByNameEquals("Schweiz")).thenReturn(Optional.ofNullable(team1));

        tournamentTeamService.addTournamentTeam(restDTO);

        verify(tournamentTeamRepositoryMock, times(1)).findTournamentTeamByNameEquals("Schweiz");
        verify(tournamentGroupRepositoryMock, times(0)).findById(1L);
        verify(tournamentTeamRepositoryMock, times(0)).save(any(TournamentTeam.class));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void addTournamentTeam_groupNotFound() {
        TournamentGroup group = new TournamentGroupBuilder()
                .withId(1L)
                .withName("A")
                .build();
        TournamentTeam team1 = new TournamentTeamBuilder()
                .withId(1L)
                .withName("Schweiz")
                .withGroup(group)
                .build();
        RestTournamentTeamDTO restDTO = new RestTournamentTeamDTOBuilder()
                .withName("Schweiz")
                .withTournamentGroupId(1L)
                .build();
        when(tournamentTeamRepositoryMock.findTournamentTeamByNameEquals("Schweiz")).thenReturn(Optional.empty());
        when(tournamentGroupRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        tournamentTeamService.addTournamentTeam(restDTO);

        verify(tournamentTeamRepositoryMock, times(1)).findTournamentTeamByNameEquals("Schweiz");
        verify(tournamentGroupRepositoryMock, times(1)).findById(1L);
        verify(tournamentTeamRepositoryMock, times(0)).save(any(TournamentTeam.class));
    }

    @Test
    public void updateTournamentTeam_ok() {
        TournamentGroup group = new TournamentGroupBuilder()
                .withId(1L)
                .withName("A")
                .build();
        TournamentTeam team1 = new TournamentTeamBuilder()
                .withId(1L)
                .withName("Schweiz")
                .withGroup(group)
                .build();
        RestTournamentTeamDTO restDTO = new RestTournamentTeamDTOBuilder()
                .withName("Schweiz")
                .withTournamentGroupId(1L)
                .build();
        when(tournamentTeamRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(team1));
        when(tournamentGroupRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(group));

        TournamentTeamDTO result = tournamentTeamService.updateTournamentTeam(1L, restDTO);
        assertEquals(restDTO.getName(), result.getName());
        assertEquals(group.getName(), result.getTournamentGroupName());

        verify(tournamentTeamRepositoryMock, times(1)).findById(1L);
        verify(tournamentGroupRepositoryMock, times(1)).findById(1L);
        verify(tournamentTeamRepositoryMock, times(1)).save(any(TournamentTeam.class));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void updateTournamentTeam_notFound() {
        TournamentGroup group = new TournamentGroupBuilder()
                .withId(1L)
                .withName("A")
                .build();
        TournamentTeam team1 = new TournamentTeamBuilder()
                .withId(1L)
                .withName("Schweiz")
                .withGroup(group)
                .build();
        RestTournamentTeamDTO restDTO = new RestTournamentTeamDTOBuilder()
                .withName("Schweiz")
                .withTournamentGroupId(1L)
                .build();
        when(tournamentTeamRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        tournamentTeamService.updateTournamentTeam(1L, restDTO);

        verify(tournamentTeamRepositoryMock, times(1)).findById(1L);
        verify(tournamentGroupRepositoryMock, times(0)).findById(1L);
        verify(tournamentTeamRepositoryMock, times(0)).save(any(TournamentTeam.class));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void updateTournamentTeam_groupNotFound() {
        TournamentGroup group = new TournamentGroupBuilder()
                .withId(1L)
                .withName("A")
                .build();
        TournamentTeam team1 = new TournamentTeamBuilder()
                .withId(1L)
                .withName("Schweiz")
                .withGroup(group)
                .build();
        RestTournamentTeamDTO restDTO = new RestTournamentTeamDTOBuilder()
                .withName("Schweiz")
                .withTournamentGroupId(1L)
                .build();
        when(tournamentTeamRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(team1));
        when(tournamentGroupRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        tournamentTeamService.updateTournamentTeam(1L, restDTO);

        verify(tournamentTeamRepositoryMock, times(1)).findById(1L);
        verify(tournamentGroupRepositoryMock, times(1)).findById(1L);
        verify(tournamentTeamRepositoryMock, times(0)).save(any(TournamentTeam.class));
    }

    @Test
    public void deleteTournamentTeam_ok() {
        when(tournamentTeamRepositoryMock.hasGames(1L)).thenReturn(false);
        when(tournamentTeamRepositoryMock.existsById(1L)).thenReturn(true);

        tournamentTeamService.deleteTournamentTeam(1L);

        verify(tournamentTeamRepositoryMock, times(1)).hasGames(1L);
        verify(tournamentTeamRepositoryMock, times(1)).existsById(1L);
        verify(tournamentTeamRepositoryMock, times(1)).deleteById(1L);
    }

    @Test(expected = IllegalActionException.class)
    public void deleteTournamentTeam_openGames() {
        when(tournamentTeamRepositoryMock.hasGames(1L)).thenReturn(true);

        tournamentTeamService.deleteTournamentTeam(1L);

        verify(tournamentTeamRepositoryMock, times(1)).hasGames(1L);
        verify(tournamentGroupRepositoryMock, times(0)).existsById(1L);
        verify(tournamentTeamRepositoryMock, times(0)).deleteById(1L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void deleteTournamentTeam_notFound() {
        when(tournamentTeamRepositoryMock.hasGames(1L)).thenReturn(false);
        when(tournamentGroupRepositoryMock.existsById(1L)).thenReturn(false);

        tournamentTeamService.deleteTournamentTeam(1L);

        verify(tournamentTeamRepositoryMock, times(1)).hasGames(1L);
        verify(tournamentGroupRepositoryMock, times(1)).existsById(1L);
        verify(tournamentTeamRepositoryMock, times(0)).deleteById(1L);
    }
}
