package ch.fhnw.wodss.tippspiel.service;

import ch.fhnw.wodss.tippspiel.builder.*;
import ch.fhnw.wodss.tippspiel.domain.Bet;
import ch.fhnw.wodss.tippspiel.domain.Game;
import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.dto.BetDTO;
import ch.fhnw.wodss.tippspiel.dto.RestBetDTO;
import ch.fhnw.wodss.tippspiel.exception.IllegalActionException;
import ch.fhnw.wodss.tippspiel.exception.ResourceAlreadyExistsException;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotAllowedException;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.persistance.BetRepository;
import ch.fhnw.wodss.tippspiel.persistance.GameRepository;
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
import java.util.Optional;

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

    @Before
    public void setup() {
        Mockito.reset(betRepositoryMock, gameRepositoryMock);
    }

    @Test
    public void getById_ok() {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .build();
        Bet bet = new BetBuilder()
                .withId(1L)
                .withScore(10)
                .withAwayTeamGoals(1)
                .withHomeTeamGoals(0)
                .withUser(user)
                .build();
        when(betRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(bet));
        BetDTO result = betService.getBetById(1L, user);
        Assert.assertEquals(bet, result);
        Assert.assertEquals((long) 1, (long) result.getActualAwayTeamGoals());
        Assert.assertEquals((long) 0, (long) result.getActualHomeTeamGoals());
        Assert.assertEquals((long) 10, (long) result.getScore());
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
        BetDTO result = betService.addBet(restBet, user);
        Assert.assertEquals((long)bet.getId(), result.getId());
        Mockito.verify(gameRepositoryMock, times(1)).findById(1L);
        Mockito.verify(betRepositoryMock, times(1)).existsBetByUser_IdAndGame_Id(1L, 1L);
        Mockito.verify(betRepositoryMock, times(1)).save(bet);
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

    @Test(expected = IllegalActionException.class)
    public void addBet_UserIdMismatch() {
        LocalDateTime calendar = LocalDateTime.now(ZoneId.of("Europe/Paris")).plusMonths(1);
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .build();
        User intercepter = new UserBuilder()
                .withId(2L)
                .withName("Bob")
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
        betService.addBet(restBet, intercepter);
        Mockito.verify(gameRepositoryMock, times(1)).findById(1L);
        Mockito.verify(betRepositoryMock, times(0)).existsBetByUser_IdAndGame_Id(1L, 1L);
        Mockito.verify(betRepositoryMock, times(0)).save(bet);
    }

    @Test
    public void updateBet_ok() {
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
        when(betRepositoryMock.existsById(1L)).thenReturn(true);
        BetDTO result = betService.updateBet(1L, restBet, user);
        Assert.assertEquals((long)bet.getId(), result.getId());
        Mockito.verify(gameRepositoryMock, times(1)).findById(1L);
        Mockito.verify(betRepositoryMock, times(1)).existsBetByUser_IdAndGame_Id(1L, 1L);
        Mockito.verify(betRepositoryMock, times(1)).save(bet);
        Mockito.verify(betRepositoryMock, times(1)).existsById(1L);
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
        when(betRepositoryMock.existsById(1L)).thenReturn(true);
        betService.updateBet(1L, restBet, user);
        Mockito.verify(gameRepositoryMock, times(1)).findById(1L);
        Mockito.verify(betRepositoryMock, times(1)).existsBetByUser_IdAndGame_Id(1L, 1L);
        Mockito.verify(betRepositoryMock, times(0)).save(bet);
        Mockito.verify(betRepositoryMock, times(1)).existsById(1L);
    }

    @Test(expected = IllegalActionException.class)
    public void updateBet_gameAlreadyStarted() {
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
        when(betRepositoryMock.existsBetByUser_IdAndGame_Id(1L, 1L)).thenReturn(true);
        when(betRepositoryMock.save(bet)).thenReturn(bet);
        when(betRepositoryMock.existsById(1L)).thenReturn(true);
        betService.updateBet(1L, restBet, user);
        Mockito.verify(gameRepositoryMock, times(1)).findById(1L);
        Mockito.verify(betRepositoryMock, times(1)).existsBetByUser_IdAndGame_Id(1L, 1L);
        Mockito.verify(betRepositoryMock, times(0)).save(bet);
        Mockito.verify(betRepositoryMock, times(1)).existsById(1L);
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

}
