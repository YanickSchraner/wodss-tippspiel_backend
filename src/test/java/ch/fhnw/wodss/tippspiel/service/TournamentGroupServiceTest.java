package ch.fhnw.wodss.tippspiel.service;

import ch.fhnw.wodss.tippspiel.builder.RestTournamentGroupDTOBuilder;
import ch.fhnw.wodss.tippspiel.builder.TournamentGroupBuilder;
import ch.fhnw.wodss.tippspiel.domain.TournamentGroup;
import ch.fhnw.wodss.tippspiel.dto.RestTournamentGroupDTO;
import ch.fhnw.wodss.tippspiel.dto.TournamentGroupDTO;
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
@WebMvcTest(TournamentGroupService.class)
public class TournamentGroupServiceTest {

    @Autowired
    TournamentGroupService tournamentGroupService;

    @MockBean
    TournamentTeamRepository tournamentTeamRepositoryMock;

    @MockBean
    TournamentGroupRepository tournamentGroupRepositoryMock;

    @Before
    public void setup() {
        Mockito.reset(tournamentTeamRepositoryMock, tournamentGroupRepositoryMock);
    }

    @Test
    public void getAllTournamentGroups_ok() {
        List<TournamentGroup> tournamentGroups = new ArrayList<>();
        TournamentGroup tournamentGroup1 = new TournamentGroupBuilder()
                .withId(1L)
                .withName("FHNW")
                .build();
        TournamentGroup tournamentGroup2 = new TournamentGroupBuilder()
                .withId(2L)
                .withName("noobs")
                .build();
        tournamentGroups.add(tournamentGroup1);
        tournamentGroups.add(tournamentGroup2);

        when(tournamentGroupRepositoryMock.findAll()).thenReturn(tournamentGroups);

        List<TournamentGroupDTO> result = tournamentGroupService.getAllTournamentGroups();
        assertEquals((long) tournamentGroup1.getId(), result.get(0).getId());
        assertEquals(tournamentGroup1.getName(), result.get(0).getName());
        assertEquals((long) tournamentGroup2.getId(), result.get(1).getId());
        assertEquals(tournamentGroup2.getName(), result.get(1).getName());
        assertEquals(tournamentGroups.size(), result.size());

        verify(tournamentGroupRepositoryMock, times(1)).findAll();
    }

    @Test
    public void getTournamentGroupById_ok() {
        TournamentGroup tournamentGroup1 = new TournamentGroupBuilder()
                .withId(1L)
                .withName("FHNW")
                .build();
        when(tournamentGroupRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(tournamentGroup1));

        TournamentGroupDTO result = tournamentGroupService.getTournamentGroupById(1L);
        assertEquals((long) tournamentGroup1.getId(), result.getId());
        assertEquals(tournamentGroup1.getName(), result.getName());

        verify(tournamentGroupRepositoryMock, times(1)).findById(1L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getTournamentGroupById_notFound() {
        TournamentGroup tournamentGroup1 = new TournamentGroupBuilder()
                .withId(1L)
                .withName("FHNW")
                .build();
        when(tournamentGroupRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        tournamentGroupService.getTournamentGroupById(1L);

        verify(tournamentGroupRepositoryMock, times(1)).findById(1L);
    }

    @Test
    public void getTournamentGroupByName_ok() {
        TournamentGroup tournamentGroup1 = new TournamentGroupBuilder()
                .withId(1L)
                .withName("FHNW")
                .build();
        when(tournamentGroupRepositoryMock.findByNameEquals("FHNW")).thenReturn(Optional.ofNullable(tournamentGroup1));

        TournamentGroupDTO result = tournamentGroupService.getTournamentGroupByName("FHNW");
        assertEquals((long) tournamentGroup1.getId(), result.getId());
        assertEquals(tournamentGroup1.getName(), result.getName());

        verify(tournamentGroupRepositoryMock, times(1)).findByNameEquals("FHNW");
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getTournamentGroupByName_notFound() {
        TournamentGroup tournamentGroup1 = new TournamentGroupBuilder()
                .withId(1L)
                .withName("FHNW")
                .build();
        when(tournamentGroupRepositoryMock.findByNameEquals("FHNW")).thenReturn(Optional.empty());

        tournamentGroupService.getTournamentGroupByName("FHNW");

        verify(tournamentGroupRepositoryMock, times(1)).findByNameEquals("FHNW");
    }

    @Test
    public void addTournamentGroup_ok() {
        TournamentGroup tournamentGroup1 = new TournamentGroupBuilder()
                .withId(1L)
                .withName("FHNW")
                .build();
        RestTournamentGroupDTO restTournamentGroupDTO = new RestTournamentGroupDTOBuilder()
                .withName("FHNW")
                .build();
        when(tournamentGroupRepositoryMock.findByNameEquals("FHNW")).thenReturn(Optional.empty());
        when(tournamentGroupRepositoryMock.save(any())).thenReturn(tournamentGroup1);

        TournamentGroupDTO result = tournamentGroupService.addTournamentGroup(restTournamentGroupDTO);
        assertEquals((long) tournamentGroup1.getId(), result.getId());
        assertEquals(tournamentGroup1.getName(), result.getName());

        verify(tournamentGroupRepositoryMock, times(1)).findByNameEquals("FHNW");
        verify(tournamentGroupRepositoryMock, times(1)).save(any());
    }

    @Test(expected = IllegalActionException.class)
    public void addTournamentGroup_exists() {
        TournamentGroup tournamentGroup1 = new TournamentGroupBuilder()
                .withId(1L)
                .withName("FHNW")
                .build();
        RestTournamentGroupDTO restTournamentGroupDTO = new RestTournamentGroupDTOBuilder()
                .withName("FHNW")
                .build();
        when(tournamentGroupRepositoryMock.findByNameEquals("FHNW")).thenReturn(Optional.ofNullable(tournamentGroup1));

        tournamentGroupService.addTournamentGroup(restTournamentGroupDTO);

        verify(tournamentGroupRepositoryMock, times(1)).findByNameEquals("FHNW");
        verify(tournamentGroupRepositoryMock, times(0)).save(any());
    }

    @Test
    public void updateTournamentGroup_ok() {
        TournamentGroup tournamentGroup1 = new TournamentGroupBuilder()
                .withId(1L)
                .withName("FHNW")
                .build();
        RestTournamentGroupDTO restTournamentGroupDTO = new RestTournamentGroupDTOBuilder()
                .withName("FHNW")
                .build();
        when(tournamentGroupRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(tournamentGroup1));
        when(tournamentGroupRepositoryMock.save(any())).thenReturn(tournamentGroup1);

        TournamentGroupDTO result = tournamentGroupService.updateTournamentGroup(1L, restTournamentGroupDTO);
        assertEquals((long) tournamentGroup1.getId(), result.getId());
        assertEquals(tournamentGroup1.getName(), result.getName());

        verify(tournamentGroupRepositoryMock, times(1)).findById(1L);
        verify(tournamentGroupRepositoryMock, times(1)).save(any());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void updateTournamentGroup_notFound() {
        RestTournamentGroupDTO restTournamentGroupDTO = new RestTournamentGroupDTOBuilder()
                .withName("FHNW")
                .build();
        when(tournamentGroupRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        tournamentGroupService.updateTournamentGroup(1L, restTournamentGroupDTO);

        verify(tournamentGroupRepositoryMock, times(1)).findById(1L);
        verify(tournamentGroupRepositoryMock, times(0)).save(any());
    }

    @Test
    public void deleteTournamentGroup_ok() {
        when(tournamentTeamRepositoryMock.existsTournamentTeamsByGroup_Id(1L)).thenReturn(false);
        when(tournamentGroupRepositoryMock.existsById(1L)).thenReturn(true);

        tournamentGroupService.deleteTournamentGroup(1L);

        verify(tournamentTeamRepositoryMock, times(1)).existsTournamentTeamsByGroup_Id(1L);
        verify(tournamentGroupRepositoryMock, times(1)).existsById(1L);
        verify(tournamentGroupRepositoryMock, times(1)).deleteById(1L);
    }

    @Test(expected = IllegalActionException.class)
    public void deleteTournamentGroup_hasMembers() {
        when(tournamentTeamRepositoryMock.existsTournamentTeamsByGroup_Id(1L)).thenReturn(true);

        tournamentGroupService.deleteTournamentGroup(1L);

        verify(tournamentTeamRepositoryMock, times(1)).existsTournamentTeamsByGroup_Id(1L);
        verify(tournamentGroupRepositoryMock, times(0)).existsById(1L);
        verify(tournamentGroupRepositoryMock, times(0)).deleteById(1L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void deleteTournamentGroup_notFound() {
        when(tournamentTeamRepositoryMock.existsTournamentTeamsByGroup_Id(1L)).thenReturn(false);
        when(tournamentGroupRepositoryMock.existsById(1L)).thenReturn(false);

        tournamentGroupService.deleteTournamentGroup(1L);

        verify(tournamentTeamRepositoryMock, times(1)).existsTournamentTeamsByGroup_Id(1L);
        verify(tournamentGroupRepositoryMock, times(1)).existsById(1L);
        verify(tournamentGroupRepositoryMock, times(0)).deleteById(1L);
    }
}
