package ch.fhnw.wodss.tippspiel.service;

import ch.fhnw.wodss.tippspiel.builder.*;
import ch.fhnw.wodss.tippspiel.domain.Bet;
import ch.fhnw.wodss.tippspiel.domain.Game;
import ch.fhnw.wodss.tippspiel.domain.TournamentTeam;
import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.dto.BetDTO;
import ch.fhnw.wodss.tippspiel.dto.RestBetDTO;
import ch.fhnw.wodss.tippspiel.exception.IllegalActionException;
import ch.fhnw.wodss.tippspiel.exception.ResourceAlreadyExistsException;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotAllowedException;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.persistance.BetRepository;
import ch.fhnw.wodss.tippspiel.persistance.GameRepository;
import ch.fhnw.wodss.tippspiel.persistance.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(BetService.class)
public class BetServiceTest {

    @Autowired
    BetService betService;

    @MockBean
    private BetRepository betRepositoryMock;

    @MockBean
    private GameRepository gameRepositoryMock;

    @MockBean
    private UserRepository userRepositoryMock;

    @Before
    public void setup() {
        Mockito.reset(betRepositoryMock, gameRepositoryMock, userRepositoryMock);
    }

    @Test
    public void getById_ok() {
        LocalDateTime calendar = LocalDateTime.now(ZoneId.of("Europe/Paris")).plusMonths(1);
        TournamentTeam homeTeam = new TournamentTeamBuilder()
                .withId(1L)
                .build();
        TournamentTeam awayTeam = new TournamentTeamBuilder()
                .withId(2L)
                .build();
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .withEmail("yanick.schraner@students.fhnw.ch")
                .build();
        Game game = new GameBuilder()
                .withId(1L)
                .withDateTime(calendar)
                .withHomeTeam(homeTeam)
                .withAwayTeam(awayTeam)
                .withLocation("Moskau")
                .withPhase("Gruppenphase")
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(3)
                .build();
        Bet bet = new BetBuilder()
                .withId(1L)
                .withScore(10)
                .withAwayTeamGoals(1)
                .withHomeTeamGoals(0)
                .withUser(user)
                .withGame(game)
                .build();
        when(betRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(bet));
        BetDTO result = betService.getBetById(1L, user);
        Assert.assertEquals((long)bet.getId(), result.getId());
        Assert.assertEquals(game.getAwayTeamGoals(), result.getActualAwayTeamGoals());
        Assert.assertEquals((long)game.getAwayTeam().getId(), result.getAwayTeamId());
        Assert.assertEquals((int)bet.getAwayTeamGoals(), result.getBettedAwayTeamGoals());
        Assert.assertEquals(game.getHomeTeamGoals(), result.getActualHomeTeamGoals());
        Assert.assertEquals((int)bet.getHomeTeamGoals(), result.getBettedHomeTeamGoals());
        Assert.assertEquals((long)game.getHomeTeam().getId(), result.getHomeTeamId());
        Assert.assertEquals((long)game.getId(), result.getGameId());
        Assert.assertEquals((long)bet.getUser().getId(), result.getUserId());
        Assert.assertEquals(bet.getScore(), result.getScore());
        Assert.assertEquals(bet.getUser().getUsername(), result.getUsername());
        Assert.assertEquals(bet.getGame().getLocation().getName(), result.getLocation());
        Assert.assertEquals(bet.getGame().getPhase().getName(), result.getPhase());
        Mockito.verify(betRepositoryMock, times(1)).findById(1L);
    }

    @Test(expected = ResourceNotAllowedException.class)
    public void getById_notAllowed() {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .build();
        User intercepter = new UserBuilder()
                .withId(2L)
                .withName("Bob")
                .build();
        Bet bet = new BetBuilder()
                .withId(1L)
                .withScore(10)
                .withAwayTeamGoals(1)
                .withHomeTeamGoals(0)
                .withUser(user)
                .build();
        when(betRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(bet));
        betService.getBetById(1L, intercepter);
        Mockito.verify(betRepositoryMock, times(1)).findById(1L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getById_notFound() {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .build();
        when(betRepositoryMock.findById(1L)).thenReturn(Optional.empty());
        betService.getBetById(1L, user);
        Mockito.verify(betRepositoryMock, times(1)).findById(1L);
    }

    @Test
    public void addBet_ok() {
        LocalDateTime calendar = LocalDateTime.now(ZoneId.of("Europe/Paris")).plusMonths(1);
        TournamentTeam homeTeam = new TournamentTeamBuilder()
                .withId(1L)
                .build();
        TournamentTeam awayTeam = new TournamentTeamBuilder()
                .withId(2L)
                .build();
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .withEmail("yanick.schraner@students.fhnw.ch")
                .build();
        Game game = new GameBuilder()
                .withId(1L)
                .withDateTime(calendar)
                .withHomeTeam(homeTeam)
                .withAwayTeam(awayTeam)
                .withLocation("Moskau")
                .withPhase("Gruppenphase")
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(3)
                .build();
        Bet bet = new BetBuilder()
                .withId(1L)
                .withScore(10)
                .withAwayTeamGoals(1)
                .withHomeTeamGoals(0)
                .withUser(user)
                .withGame(game)
                .build();
        RestBetDTO restBet = new RestBetDTOBuilder()
                .withGameId(1L)
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(1)
                .build();
        when(gameRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(game));
        when(betRepositoryMock.existsBetByUser_IdAndGame_Id(1L, 1L)).thenReturn(false);
        when(betRepositoryMock.save(any())).thenReturn(bet);
        when(userRepositoryMock.save(user)).thenReturn(user);
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(user));
        BetDTO result = betService.addBet(restBet, user);
        Assert.assertEquals((long)bet.getId(), result.getId());
        Mockito.verify(gameRepositoryMock, times(1)).findById(1L);
        Mockito.verify(betRepositoryMock, times(1)).existsBetByUser_IdAndGame_Id(1L, 1L);
        Mockito.verify(betRepositoryMock, times(1)).save(any(Bet.class));
        Mockito.verify(userRepositoryMock, times(1)).save(user);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void addBet_gameNotFound() {
        LocalDateTime calendar = LocalDateTime.now(ZoneId.of("Europe/Paris")).plusMonths(1);
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .build();
        Game game = new GameBuilder()
                .withId(1L)
                .withDateTime(calendar)
                .build();
        Bet bet = new BetBuilder()
                .withId(1L)
                .withScore(10)
                .withAwayTeamGoals(1)
                .withHomeTeamGoals(0)
                .withUser(user)
                .withGame(game)
                .build();
        RestBetDTO restBet = new RestBetDTOBuilder()
                .withGameId(1L)
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(1)
                .build();
        when(gameRepositoryMock.findById(1L)).thenReturn(Optional.empty());
        when(betRepositoryMock.existsBetByUser_IdAndGame_Id(1L, 1L)).thenReturn(false);
        when(betRepositoryMock.save(bet)).thenReturn(bet);
        betService.addBet(restBet, user);
        Mockito.verify(gameRepositoryMock, times(1)).findById(1L);
        Mockito.verify(betRepositoryMock, times(0)).existsBetByUser_IdAndGame_Id(1L, 1L);
        Mockito.verify(betRepositoryMock, times(0)).save(bet);
    }

    @Test(expected = ResourceAlreadyExistsException.class)
    public void addBet_betAlreadyExists() {
        LocalDateTime calendar = LocalDateTime.now(ZoneId.of("Europe/Paris")).plusMonths(1);
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .build();
        Game game = new GameBuilder()
                .withId(1L)
                .withDateTime(calendar)
                .build();
        Bet bet = new BetBuilder()
                .withId(1L)
                .withScore(10)
                .withAwayTeamGoals(1)
                .withHomeTeamGoals(0)
                .withUser(user)
                .withGame(game)
                .build();
        RestBetDTO restBet = new RestBetDTOBuilder()
                .withGameId(1L)
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(1)
                .build();
        when(gameRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(game));
        when(betRepositoryMock.existsBetByUser_IdAndGame_Id(1L, 1L)).thenReturn(true);
        when(betRepositoryMock.save(bet)).thenReturn(bet);
        betService.addBet(restBet, user);
        Mockito.verify(gameRepositoryMock, times(1)).findById(1L);
        Mockito.verify(betRepositoryMock, times(1)).existsBetByUser_IdAndGame_Id(1L, 1L);
        Mockito.verify(betRepositoryMock, times(0)).save(bet);
    }

    @Test(expected = IllegalActionException.class)
    public void addBet_gameAlreadyStarted() {
        LocalDateTime calendar = LocalDateTime.now(ZoneId.of("Europe/Paris")).minusMonths(1);
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .build();
        Game game = new GameBuilder()
                .withId(1L)
                .withDateTime(calendar)
                .build();
        Bet bet = new BetBuilder()
                .withId(1L)
                .withScore(10)
                .withAwayTeamGoals(1)
                .withHomeTeamGoals(0)
                .withUser(user)
                .withGame(game)
                .build();
        RestBetDTO restBet = new RestBetDTOBuilder()
                .withGameId(1L)
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(1)
                .build();
        when(gameRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(game));
        when(betRepositoryMock.existsBetByUser_IdAndGame_Id(1L, 1L)).thenReturn(false);
        when(betRepositoryMock.save(bet)).thenReturn(bet);
        betService.addBet(restBet, user);
        Mockito.verify(gameRepositoryMock, times(1)).findById(1L);
        Mockito.verify(betRepositoryMock, times(1)).existsBetByUser_IdAndGame_Id(1L, 1L);
        Mockito.verify(betRepositoryMock, times(0)).save(bet);
    }

    @Test
    public void updateBet_ok() {
        LocalDateTime calendar = LocalDateTime.now(ZoneId.of("Europe/Paris")).plusMonths(1);
        TournamentTeam homeTeam = new TournamentTeamBuilder()
                .withId(1L)
                .build();
        TournamentTeam awayTeam = new TournamentTeamBuilder()
                .withId(2L)
                .build();
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .withEmail("yanick.schraner@students.fhnw.ch")
                .build();
        Game game = new GameBuilder()
                .withId(1L)
                .withDateTime(calendar)
                .withHomeTeam(homeTeam)
                .withAwayTeam(awayTeam)
                .withLocation("Moskau")
                .withPhase("Gruppenphase")
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(3)
                .build();
        Bet bet = new BetBuilder()
                .withId(1L)
                .withScore(10)
                .withAwayTeamGoals(1)
                .withHomeTeamGoals(0)
                .withUser(user)
                .withGame(game)
                .build();
        RestBetDTO restBet = new RestBetDTOBuilder()
                .withGameId(1L)
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(1)
                .build();
        when(gameRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(game));
        when(betRepositoryMock.existsBetByUser_IdAndGame_Id(1L, 1L)).thenReturn(true);
        when(betRepositoryMock.save(bet)).thenReturn(bet);
        when(betRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(bet));
        BetDTO result = betService.updateBet(1L, restBet, user);
        Assert.assertEquals((long)bet.getId(), result.getId());
        Assert.assertEquals(game.getAwayTeamGoals(), result.getActualAwayTeamGoals());
        Assert.assertEquals((long)game.getAwayTeam().getId(), result.getAwayTeamId());
        Assert.assertEquals((int)bet.getAwayTeamGoals(), result.getBettedAwayTeamGoals());
        Assert.assertEquals(game.getHomeTeamGoals(), result.getActualHomeTeamGoals());
        Assert.assertEquals((int)bet.getHomeTeamGoals(), result.getBettedHomeTeamGoals());
        Assert.assertEquals((long)game.getHomeTeam().getId(), result.getHomeTeamId());
        Assert.assertEquals((long)game.getId(), result.getGameId());
        Assert.assertEquals((long)bet.getUser().getId(), result.getUserId());
        Assert.assertEquals(bet.getScore(), result.getScore());
        Assert.assertEquals(bet.getUser().getUsername(), result.getUsername());
        Assert.assertEquals(bet.getGame().getLocation().getName(), result.getLocation());
        Assert.assertEquals(bet.getGame().getPhase().getName(), result.getPhase());
        Mockito.verify(gameRepositoryMock, times(1)).findById(1L);
        Mockito.verify(betRepositoryMock, times(1)).existsBetByUser_IdAndGame_Id(1L, 1L);
        Mockito.verify(betRepositoryMock, times(1)).save(bet);
        Mockito.verify(betRepositoryMock, times(1)).findById(1L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void updateBet_betNotExists() {
        LocalDateTime calendar = LocalDateTime.now(ZoneId.of("Europe/Paris")).plusMonths(1);
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .build();
        Game game = new GameBuilder()
                .withId(1L)
                .withDateTime(calendar)
                .build();
        Bet bet = new BetBuilder()
                .withId(1L)
                .withScore(10)
                .withAwayTeamGoals(1)
                .withHomeTeamGoals(0)
                .withUser(user)
                .withGame(game)
                .build();
        RestBetDTO restBet = new RestBetDTOBuilder()
                .withGameId(1L)
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(1)
                .build();
        when(gameRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(game));
        when(betRepositoryMock.existsBetByUser_IdAndGame_Id(1L, 1L)).thenReturn(true);
        when(betRepositoryMock.save(bet)).thenReturn(bet);
        when(betRepositoryMock.existsById(1L)).thenReturn(false);
        betService.updateBet(1L, restBet, user);
        Mockito.verify(gameRepositoryMock, times(0)).findById(1L);
        Mockito.verify(betRepositoryMock, times(0)).existsBetByUser_IdAndGame_Id(1L, 1L);
        Mockito.verify(betRepositoryMock, times(0)).save(bet);
        Mockito.verify(betRepositoryMock, times(1)).existsById(1L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void updateBet_gameNotExists() {
        LocalDateTime calendar = LocalDateTime.now(ZoneId.of("Europe/Paris")).plusMonths(1);
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .build();
        Game game = new GameBuilder()
                .withId(1L)
                .withDateTime(calendar)
                .build();
        Bet bet = new BetBuilder()
                .withId(1L)
                .withScore(10)
                .withAwayTeamGoals(1)
                .withHomeTeamGoals(0)
                .withUser(user)
                .withGame(game)
                .build();
        RestBetDTO restBet = new RestBetDTOBuilder()
                .withGameId(1L)
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(1)
                .build();
        when(gameRepositoryMock.findById(1L)).thenReturn(Optional.empty());
        when(betRepositoryMock.existsBetByUser_IdAndGame_Id(1L, 1L)).thenReturn(true);
        when(betRepositoryMock.save(bet)).thenReturn(bet);
        when(betRepositoryMock.existsById(1L)).thenReturn(true);
        betService.updateBet(1L, restBet, user);
        Mockito.verify(gameRepositoryMock, times(1)).findById(1L);
        Mockito.verify(betRepositoryMock, times(0)).existsBetByUser_IdAndGame_Id(1L, 1L);
        Mockito.verify(betRepositoryMock, times(0)).save(bet);
        Mockito.verify(betRepositoryMock, times(1)).existsById(1L);
    }

    @Test(expected = IllegalActionException.class)
    public void updateBet_gameNotBettedByUser() {
        LocalDateTime calendar = LocalDateTime.now(ZoneId.of("Europe/Paris")).plusMonths(1);
        TournamentTeam homeTeam = new TournamentTeamBuilder()
                .withId(1L)
                .build();
        TournamentTeam awayTeam = new TournamentTeamBuilder()
                .withId(2L)
                .build();
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .withEmail("yanick.schraner@students.fhnw.ch")
                .build();
        Game game = new GameBuilder()
                .withId(1L)
                .withDateTime(calendar)
                .withHomeTeam(homeTeam)
                .withAwayTeam(awayTeam)
                .withLocation("Moskau")
                .withPhase("Gruppenphase")
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(3)
                .build();
        Bet bet = new BetBuilder()
                .withId(1L)
                .withScore(10)
                .withAwayTeamGoals(1)
                .withHomeTeamGoals(0)
                .withUser(user)
                .withGame(game)
                .build();
        RestBetDTO restBet = new RestBetDTOBuilder()
                .withGameId(1L)
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(1)
                .build();
        when(gameRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(game));
        when(betRepositoryMock.existsBetByUser_IdAndGame_Id(1L, 1L)).thenReturn(false);
        when(betRepositoryMock.save(bet)).thenReturn(bet);
        when(betRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(bet));
        betService.updateBet(1L, restBet, user);
        Mockito.verify(gameRepositoryMock, times(1)).findById(1L);
        Mockito.verify(betRepositoryMock, times(1)).existsBetByUser_IdAndGame_Id(1L, 1L);
        Mockito.verify(betRepositoryMock, times(0)).save(bet);
        Mockito.verify(betRepositoryMock, times(1)).findById(1L);
    }

    @Test(expected = IllegalActionException.class)
    public void updateBet_gameAlreadyStarted() {
        LocalDateTime calendar = LocalDateTime.now(ZoneId.of("Europe/Paris")).minusMonths(1);
        TournamentTeam homeTeam = new TournamentTeamBuilder()
                .withId(1L)
                .build();
        TournamentTeam awayTeam = new TournamentTeamBuilder()
                .withId(2L)
                .build();
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .withEmail("yanick.schraner@students.fhnw.ch")
                .build();
        Game game = new GameBuilder()
                .withId(1L)
                .withDateTime(calendar)
                .withHomeTeam(homeTeam)
                .withAwayTeam(awayTeam)
                .withLocation("Moskau")
                .withPhase("Gruppenphase")
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(3)
                .build();
        Bet bet = new BetBuilder()
                .withId(1L)
                .withScore(10)
                .withAwayTeamGoals(1)
                .withHomeTeamGoals(0)
                .withUser(user)
                .withGame(game)
                .build();
        RestBetDTO restBet = new RestBetDTOBuilder()
                .withGameId(1L)
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(1)
                .build();
        when(gameRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(game));
        when(betRepositoryMock.existsBetByUser_IdAndGame_Id(1L, 1L)).thenReturn(true);
        when(betRepositoryMock.save(bet)).thenReturn(bet);
        when(betRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(bet));
        betService.updateBet(1L, restBet, user);
        Mockito.verify(gameRepositoryMock, times(1)).findById(1L);
        Mockito.verify(betRepositoryMock, times(1)).existsBetByUser_IdAndGame_Id(1L, 1L);
        Mockito.verify(betRepositoryMock, times(0)).save(bet);
        Mockito.verify(betRepositoryMock, times(1)).findById(1L);
    }

    @Test
    public void deleteBet_ok() {
        LocalDateTime calendar = LocalDateTime.now(ZoneId.of("Europe/Paris")).plusMonths(1);
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .build();
        Game game = new GameBuilder()
                .withId(1L)
                .withDateTime(calendar)
                .build();
        Bet bet = new BetBuilder()
                .withId(1L)
                .withScore(10)
                .withAwayTeamGoals(1)
                .withHomeTeamGoals(0)
                .withUser(user)
                .withGame(game)
                .build();
        when(betRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(bet));
        when(gameRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(game));
        when(betRepositoryMock.existsBetByUser_IdAndGame_Id(1L, 1L)).thenReturn(true);
        betService.deleteBet(1L, user);
        Mockito.verify(betRepositoryMock, times(1)).findById(1L);
        Mockito.verify(betRepositoryMock, times(1)).existsBetByUser_IdAndGame_Id(1L, 1L);
        Mockito.verify(gameRepositoryMock, times(1)).findById(1L);
        Mockito.verify(betRepositoryMock, times(1)).deleteById(1L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void deleteBet_betNotFound() {
        LocalDateTime calendar = LocalDateTime.now(ZoneId.of("Europe/Paris")).plusMonths(1);
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .build();
        Game game = new GameBuilder()
                .withId(1L)
                .withDateTime(calendar)
                .build();
        Bet bet = new BetBuilder()
                .withId(1L)
                .withScore(10)
                .withAwayTeamGoals(1)
                .withHomeTeamGoals(0)
                .withUser(user)
                .withGame(game)
                .build();
        when(betRepositoryMock.findById(1L)).thenReturn(Optional.empty());
        betService.deleteBet(1L, user);
        Mockito.verify(betRepositoryMock, times(1)).findById(1L);
        Mockito.verify(betRepositoryMock, times(0)).existsBetByUser_IdAndGame_Id(1L, 1L);
        Mockito.verify(gameRepositoryMock, times(0)).findById(1L);
        Mockito.verify(betRepositoryMock, times(0)).deleteById(1L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void deleteBet_gameNotFound() {
        LocalDateTime calendar = LocalDateTime.now(ZoneId.of("Europe/Paris")).plusMonths(1);
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .build();
        Game game = new GameBuilder()
                .withId(1L)
                .withDateTime(calendar)
                .build();
        Bet bet = new BetBuilder()
                .withId(1L)
                .withScore(10)
                .withAwayTeamGoals(1)
                .withHomeTeamGoals(0)
                .withUser(user)
                .withGame(game)
                .build();
        when(betRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(bet));
        when(gameRepositoryMock.findById(1L)).thenReturn(Optional.empty());
        when(betRepositoryMock.existsBetByUser_IdAndGame_Id(1L, 1L)).thenReturn(true);
        betService.deleteBet(1L, user);
        Mockito.verify(betRepositoryMock, times(1)).findById(1L);
        Mockito.verify(betRepositoryMock, times(0)).existsBetByUser_IdAndGame_Id(1L, 1L);
        Mockito.verify(gameRepositoryMock, times(1)).findById(1L);
        Mockito.verify(betRepositoryMock, times(0)).deleteById(1L);
    }

    @Test(expected = IllegalActionException.class)
    public void deleteBet_gameNotBettedByUser() {
        LocalDateTime calendar = LocalDateTime.now(ZoneId.of("Europe/Paris")).plusMonths(1);
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .build();
        Game game = new GameBuilder()
                .withId(1L)
                .withDateTime(calendar)
                .build();
        Bet bet = new BetBuilder()
                .withId(1L)
                .withScore(10)
                .withAwayTeamGoals(1)
                .withHomeTeamGoals(0)
                .withUser(user)
                .withGame(game)
                .build();
        when(betRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(bet));
        when(gameRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(game));
        when(betRepositoryMock.existsBetByUser_IdAndGame_Id(1L, 1L)).thenReturn(false);
        betService.deleteBet(1L, user);
        Mockito.verify(betRepositoryMock, times(1)).findById(1L);
        Mockito.verify(betRepositoryMock, times(1)).existsBetByUser_IdAndGame_Id(1L, 1L);
        Mockito.verify(gameRepositoryMock, times(1)).findById(1L);
        Mockito.verify(betRepositoryMock, times(0)).deleteById(1L);
    }

    @Test(expected = IllegalActionException.class)
    public void deleteBet_gameAlreadyStarted() {
        LocalDateTime calendar = LocalDateTime.now(ZoneId.of("Europe/Paris")).minusMonths(1);
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .build();
        Game game = new GameBuilder()
                .withId(1L)
                .withDateTime(calendar)
                .build();
        Bet bet = new BetBuilder()
                .withId(1L)
                .withScore(10)
                .withAwayTeamGoals(1)
                .withHomeTeamGoals(0)
                .withUser(user)
                .withGame(game)
                .build();
        when(betRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(bet));
        when(gameRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(game));
        when(betRepositoryMock.existsBetByUser_IdAndGame_Id(1L, 1L)).thenReturn(true);
        betService.deleteBet(1L, user);
        Mockito.verify(betRepositoryMock, times(1)).findById(1L);
        Mockito.verify(betRepositoryMock, times(1)).existsBetByUser_IdAndGame_Id(1L, 1L);
        Mockito.verify(gameRepositoryMock, times(1)).findById(1L);
        Mockito.verify(betRepositoryMock, times(0)).deleteById(1L);
    }

    @Test
    public void getBetsForUser_ok() {
        LocalDateTime calendar = LocalDateTime.now(ZoneId.of("Europe/Paris")).minusMonths(1);
        TournamentTeam homeTeam = new TournamentTeamBuilder()
                .withId(1L)
                .build();
        TournamentTeam awayTeam = new TournamentTeamBuilder()
                .withId(2L)
                .build();
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .withEmail("yanick.schraner@students.fhnw.ch")
                .build();
        Game game = new GameBuilder()
                .withId(1L)
                .withDateTime(calendar)
                .withHomeTeam(homeTeam)
                .withAwayTeam(awayTeam)
                .withLocation("Moskau")
                .withPhase("Gruppenphase")
                .withHomeTeamGoals(0)
                .withAwayTeamGoals(3)
                .build();
        Bet bet = new BetBuilder()
                .withId(1L)
                .withScore(10)
                .withAwayTeamGoals(1)
                .withHomeTeamGoals(0)
                .withUser(user)
                .withGame(game)
                .build();
        Bet bet2 = new BetBuilder()
                .withId(2L)
                .withScore(10)
                .withAwayTeamGoals(1)
                .withHomeTeamGoals(0)
                .withUser(user)
                .withGame(game)
                .build();
        List<Bet> bets = new ArrayList<>();
        bets.add(bet);
        bets.add(bet2);

        when(betRepositoryMock.getBetsForUser(user)).thenReturn(bets);
        List<BetDTO> result = betService.getBetsForUser(user);
        Assert.assertEquals(bets.size(), result.size());
        Assert.assertEquals((long)bet.getId(), result.get(0).getId());
        Assert.assertEquals((long)bet2.getId(), result.get(1).getId());
        Mockito.verify(betRepositoryMock, times(1)).getBetsForUser(user);
    }

}
