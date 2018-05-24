package ch.fhnw.wodss.tippspiel.service;

import ch.fhnw.wodss.tippspiel.builder.*;
import ch.fhnw.wodss.tippspiel.domain.*;
import ch.fhnw.wodss.tippspiel.dto.GameDTO;
import ch.fhnw.wodss.tippspiel.dto.RestGameDTO;
import ch.fhnw.wodss.tippspiel.exception.IllegalActionException;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.persistance.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(GameService.class)
public class GameServiceTest {

    @Autowired
    GameService gameService;

    @MockBean
    GameRepository gameRepositoryMock;

    @MockBean
    BetRepository betRepositoryMock;

    @MockBean
    TournamentTeamRepository tournamentTeamRepositoryMock;

    @MockBean
    LocationRepository locationRepositoryMock;

    @MockBean
    PhaseRepository phaseRepositoryMock;

    @Before
    public void setup() {
        Mockito.reset(gameRepositoryMock, betRepositoryMock, tournamentTeamRepositoryMock, locationRepositoryMock, phaseRepositoryMock);
    }

    @Test
    public void getAllGames_ok() {
        TournamentGroup tournamentGroup = new TournamentGroupBuilder()
                .withId(1L)
                .withName("GruppeA")
                .build();
        TournamentTeam tournamentTeam = new TournamentTeamBuilder()
                .withId(1L)
                .withName("Russland")
                .withGroup(tournamentGroup)
                .build();
        TournamentTeam tournamentTeam2 = new TournamentTeamBuilder()
                .withId(2L)
                .withName("Saudi-Arabien")
                .withGroup(tournamentGroup)
                .build();
        TournamentTeam tournamentTeam3 = new TournamentTeamBuilder()
                .withId(3L)
                .withName("Ägypten")
                .withGroup(tournamentGroup)
                .build();
        TournamentTeam tournamentTeam4 = new TournamentTeamBuilder()
                .withId(4L)
                .withName("Uruguay")
                .withGroup(tournamentGroup)
                .build();
        List<Game> games = new ArrayList<>();
        LocalDateTime localDateTime = LocalDateTime.of(2018, 06, 14, 18, 00, 00);
        Game game = new GameBuilder()
                .withId(1L)
                .withHomeTeam(tournamentTeam)
                .withAwayTeam(tournamentTeam2)
                .withLocation("Moskau")
                .withPhase("Gruppenphase")
                .withDateTime(localDateTime)
                .build();
        LocalDateTime localDateTime2 = LocalDateTime.of(2018, 06, 15, 17, 00, 00);
        Game game2 = new GameBuilder()
                .withId(1L)
                .withHomeTeam(tournamentTeam)
                .withAwayTeam(tournamentTeam2)
                .withLocation("Jekaterinburg")
                .withPhase("Gruppenphase")
                .withDateTime(localDateTime2)
                .build();
        games.add(game);
        games.add(game2);

        when(gameRepositoryMock.findAll()).thenReturn(games);

        List<GameDTO> result = gameService.getAllGames();
        assertEquals((long)game.getHomeTeam().getId(), result.get(0).getHomeTeamId());
        assertEquals((long)game.getAwayTeam().getId(), result.get(0).getAwayTeamId());
        assertEquals(game.getHomeTeam().getName(), result.get(0).getHomeTeamName());
        assertEquals(game.getAwayTeam().getName(), result.get(0).getAwayTeamName());
        assertEquals(game.getLocation().getName(), result.get(0).getLocationName());
        assertEquals(game.getPhase().getName(), result.get(0).getPhaseName());
        assertEquals((long)game2.getHomeTeam().getId(), result.get(1).getHomeTeamId());
        assertEquals((long)game2.getAwayTeam().getId(), result.get(1).getAwayTeamId());
        assertEquals(game2.getHomeTeam().getName(), result.get(1).getHomeTeamName());
        assertEquals(game2.getAwayTeam().getName(), result.get(1).getAwayTeamName());
        assertEquals(game2.getLocation().getName(), result.get(1).getLocationName());
        assertEquals(game2.getPhase().getName(), result.get(1).getPhaseName());
        assertEquals(games.size(), result.size());

        verify(gameRepositoryMock, times(1)).findAll();
    }

    @Test
    public void getGameById_ok() {
        TournamentGroup tournamentGroup = new TournamentGroupBuilder()
                .withId(1L)
                .withName("GruppeA")
                .build();
        TournamentTeam tournamentTeam = new TournamentTeamBuilder()
                .withId(1L)
                .withName("Russland")
                .withGroup(tournamentGroup)
                .build();
        TournamentTeam tournamentTeam2 = new TournamentTeamBuilder()
                .withId(2L)
                .withName("Saudi-Arabien")
                .withGroup(tournamentGroup)
                .build();
        LocalDateTime localDateTime = LocalDateTime.of(2018, 06, 14, 18, 00, 00);
        Game game = new GameBuilder()
                .withId(1L)
                .withHomeTeam(tournamentTeam)
                .withAwayTeam(tournamentTeam2)
                .withLocation("Moskau")
                .withPhase("Gruppenphase")
                .withDateTime(localDateTime)
                .build();
        when(gameRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(game));

        GameDTO result = gameService.getGameById(1L);
        assertEquals((long)game.getHomeTeam().getId(), result.getHomeTeamId());
        assertEquals((long)game.getAwayTeam().getId(), result.getAwayTeamId());
        assertEquals(game.getHomeTeam().getName(), result.getHomeTeamName());
        assertEquals(game.getAwayTeam().getName(), result.getAwayTeamName());
        assertEquals(game.getLocation().getName(), result.getLocationName());
        assertEquals(game.getPhase().getName(), result.getPhaseName());

        verify(gameRepositoryMock, times(1)).findById(1L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getGameById_notFound() {
        TournamentGroup tournamentGroup = new TournamentGroupBuilder()
                .withId(1L)
                .withName("GruppeA")
                .build();
        TournamentTeam tournamentTeam = new TournamentTeamBuilder()
                .withId(1L)
                .withName("Russland")
                .withGroup(tournamentGroup)
                .build();
        TournamentTeam tournamentTeam2 = new TournamentTeamBuilder()
                .withId(2L)
                .withName("Saudi-Arabien")
                .withGroup(tournamentGroup)
                .build();
        LocalDateTime localDateTime = LocalDateTime.of(2018, 06, 14, 18, 00, 00);
        Game game = new GameBuilder()
                .withId(1L)
                .withHomeTeam(tournamentTeam)
                .withAwayTeam(tournamentTeam2)
                .withLocation("Moskau")
                .withPhase("Gruppenphase")
                .withDateTime(localDateTime)
                .build();
        when(gameRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        gameService.getGameById(1L);

        verify(gameRepositoryMock, times(1)).findById(1L);
    }

    @Test
    public void addGame_ok() {
        RestGameDTO restGameDTO = new RestGameDTOBuilder()
                .withHomeTeamId(1L)
                .withAwayTeamId(2L)
                .withLocationId(1L)
                .withPhaseId(1L)
                .withDate("2018-06-14")
                .withTime("18:00:00")
                .build();
        TournamentGroup tournamentGroup = new TournamentGroupBuilder()
                .withId(1L)
                .withName("GruppeA")
                .build();
        TournamentTeam tournamentTeam = new TournamentTeamBuilder()
                .withId(1L)
                .withName("Russland")
                .withGroup(tournamentGroup)
                .build();
        TournamentTeam tournamentTeam2 = new TournamentTeamBuilder()
                .withId(2L)
                .withName("Saudi-Arabien")
                .withGroup(tournamentGroup)
                .build();
        LocalDateTime localDateTime = LocalDateTime.of(2018, 06, 14, 18, 00, 00);
        Game game = new GameBuilder()
                .withId(1L)
                .withHomeTeam(tournamentTeam)
                .withAwayTeam(tournamentTeam2)
                .withLocation("Moskau")
                .withPhase("Gruppenphase")
                .withDateTime(localDateTime)
                .build();
        Location location = new LocationBuilder()
                .withId(1L)
                .withName("Moskau")
                .build();
        Phase phase = new PhaseBuilder()
                .withId(1L)
                .withName("Gruppenphase")
                .build();
        when(gameRepositoryMock.findById(1L)).thenReturn(Optional.empty());
        when(tournamentTeamRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(tournamentTeam));
        when(tournamentTeamRepositoryMock.findById(2L)).thenReturn(Optional.ofNullable(tournamentTeam2));
        when(locationRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(location));
        when(phaseRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(phase));
        when(gameRepositoryMock.save(any(Game.class))).thenReturn(game);

        GameDTO result = gameService.addGame(restGameDTO);
        assertEquals((long)game.getHomeTeam().getId(), result.getHomeTeamId());
        assertEquals((long)game.getAwayTeam().getId(), result.getAwayTeamId());
        assertEquals(game.getHomeTeam().getName(), result.getHomeTeamName());
        assertEquals(game.getAwayTeam().getName(), result.getAwayTeamName());
        assertEquals(game.getLocation().getName(), result.getLocationName());
        assertEquals(game.getPhase().getName(), result.getPhaseName());

        verify(gameRepositoryMock, times(1)).save(any(Game.class));
    }

    @Test(expected = IllegalActionException.class)
    public void addGame_exists() {
        RestGameDTO restGameDTO = new RestGameDTOBuilder()
                .withHomeTeamId(1L)
                .withAwayTeamId(2L)
                .withLocationId(1L)
                .withPhaseId(1L)
                .withDate("2018-06-14")
                .withTime("18:00:00")
                .build();
        TournamentGroup tournamentGroup = new TournamentGroupBuilder()
                .withId(1L)
                .withName("GruppeA")
                .build();
        TournamentTeam tournamentTeam = new TournamentTeamBuilder()
                .withId(1L)
                .withName("Russland")
                .withGroup(tournamentGroup)
                .build();
        TournamentTeam tournamentTeam2 = new TournamentTeamBuilder()
                .withId(2L)
                .withName("Saudi-Arabien")
                .withGroup(tournamentGroup)
                .build();
        LocalDateTime localDateTime = LocalDateTime.of(2018, 06, 14, 18, 00, 00);
        Game game = new GameBuilder()
                .withId(1L)
                .withHomeTeam(tournamentTeam)
                .withAwayTeam(tournamentTeam2)
                .withLocation("Moskau")
                .withPhase("Gruppenphase")
                .withDateTime(localDateTime)
                .build();
        Location location = new LocationBuilder()
                .withId(1L)
                .withName("Moskau")
                .build();
        Phase phase = new PhaseBuilder()
                .withId(1L)
                .withName("Gruppenphase")
                .build();
        when(gameRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(game));
        when(tournamentTeamRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(tournamentTeam));
        when(tournamentTeamRepositoryMock.findById(2L)).thenReturn(Optional.ofNullable(tournamentTeam2));
        when(locationRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(location));
        when(phaseRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(phase));
        when(gameRepositoryMock.existsGameByHomeTeamAndAwayTeamAndDateTimeEquals(tournamentTeam, tournamentTeam2, localDateTime)).thenReturn(true);

        gameService.addGame(restGameDTO);

        verify(gameRepositoryMock, times(1)).findById(1L);
        verify(gameRepositoryMock, times(0)).save(any(Game.class));
    }

    @Test
    public void updateGame_ok() {
        RestGameDTO restGameDTO = new RestGameDTOBuilder()
                .withHomeTeamId(1L)
                .withAwayTeamId(2L)
                .withLocationId(1L)
                .withPhaseId(1L)
                .withDate("2018-06-14")
                .withTime("23:00:00")
                .build();
        TournamentGroup tournamentGroup = new TournamentGroupBuilder()
                .withId(1L)
                .withName("GruppeA")
                .build();
        TournamentTeam tournamentTeam = new TournamentTeamBuilder()
                .withId(1L)
                .withName("Russland")
                .withGroup(tournamentGroup)
                .build();
        TournamentTeam tournamentTeam2 = new TournamentTeamBuilder()
                .withId(2L)
                .withName("Saudi-Arabien")
                .withGroup(tournamentGroup)
                .build();
        LocalDateTime localDateTime = LocalDateTime.of(2018, 06, 14, 23, 00, 00);
        Game game = new GameBuilder()
                .withId(1L)
                .withHomeTeam(tournamentTeam)
                .withAwayTeam(tournamentTeam2)
                .withLocation("Moskau")
                .withPhase("Gruppenphase")
                .withDateTime(localDateTime)
                .build();
        Location location = new LocationBuilder()
                .withId(1L)
                .withName("Moskau")
                .build();
        Phase phase = new PhaseBuilder()
                .withId(1L)
                .withName("Gruppenphase")
                .build();
        when(gameRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(game));
        when(tournamentTeamRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(tournamentTeam));
        when(tournamentTeamRepositoryMock.findById(2L)).thenReturn(Optional.ofNullable(tournamentTeam2));
        when(locationRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(location));
        when(phaseRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(phase));
        when(gameRepositoryMock.save(any())).thenReturn(game);

        GameDTO result = gameService.updateGame(1L, restGameDTO);
        assertEquals((long)game.getHomeTeam().getId(), result.getHomeTeamId());
        assertEquals((long)game.getAwayTeam().getId(), result.getAwayTeamId());
        assertEquals(game.getHomeTeam().getName(), result.getHomeTeamName());
        assertEquals(game.getAwayTeam().getName(), result.getAwayTeamName());
        assertEquals(game.getLocation().getName(), result.getLocationName());
        assertEquals(game.getPhase().getName(), result.getPhaseName());

        verify(gameRepositoryMock, times(1)).findById(1L);
        verify(gameRepositoryMock, times(1)).save(any());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void updateGame_notFound() {
        RestGameDTO restGameDTO = new RestGameDTOBuilder()
                .withHomeTeamId(1L)
                .withAwayTeamId(2L)
                .withLocationId(1L)
                .withPhaseId(1L)
                .withDate("2018-06-14")
                .withTime("18:00:00")
                .build();
        when(gameRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        gameService.updateGame(1L, restGameDTO);

        verify(gameRepositoryMock, times(1)).findById(1L);
        verify(gameRepositoryMock, times(0)).save(any());
    }

    @Test
    public void deleteGame_ok() {
        when(gameRepositoryMock.existsById(1L)).thenReturn(true);

        gameService.deleteGame(1L);

        verify(gameRepositoryMock, times(1)).existsById(1L);
        verify(gameRepositoryMock, times(1)).deleteById(1L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void deleteGame_notFound() {
        when(gameRepositoryMock.existsById(1L)).thenReturn(false);

        gameService.deleteGame(1L);

        verify(gameRepositoryMock, times(1)).existsById(1L);
        verify(gameRepositoryMock, times(0)).deleteById(1L);
    }

}

