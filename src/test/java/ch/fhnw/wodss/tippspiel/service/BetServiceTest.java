package ch.fhnw.wodss.tippspiel.service;

import ch.fhnw.wodss.tippspiel.builder.BetBuilder;
import ch.fhnw.wodss.tippspiel.builder.GameBuilder;
import ch.fhnw.wodss.tippspiel.builder.UserBuilder;
import ch.fhnw.wodss.tippspiel.domain.*;
import ch.fhnw.wodss.tippspiel.persistance.BetRepository;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(BetService.class)
public class BetServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BetRepository betRepositoryMock;

    //@Test
    public void findById_BetFound_ShouldReturnFound() throws Exception {
        Bet bet = new BetBuilder(0, 1, 10, new Game(), new User())
                .id(1L)
                .build();
        when(betRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(bet));

        Game game = new GameBuilder(new Date(), 0, 0, new TournamentTeam(), new TournamentTeam(), new Location(), new Phase())
                .id(1L)
                .build();
        Bet bet1 = new BetBuilder(0, 1, 10, game, new User())
                .id(1L)
                .build();
        List<Bet> bets = new ArrayList<>();
        bets.add(bet1);
        User user = new UserBuilder().withUsername("Yanick").withRole("USER").withId(1L).build();
        bet1.setUser(user);

        mockMvc.perform(get("/bets/{id}", 1L)
                .header("Accept", "application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.homeTeamGoals", equalTo(0)))
                .andExpect(jsonPath("$.awayTeamGoals", equalTo(1)))
                .andExpect(jsonPath("$.score", equalTo(10)))
                .andExpect(jsonPath("$.game.id", equalTo(1L)))
                .andExpect(jsonPath("$.user.id", equalTo(1L)))
                .andExpect(jsonPath("$.user.name", equalTo("Yanick")));
        Mockito.verify(betRepositoryMock, times(1)).findById(1L);
    }

    //@Test
    public void findById_BetNotExisting_ShouldReturnNotFound() throws Exception {
        when(betRepositoryMock.findById(2L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/bets/{id}", 2L)
                .header("Accept", "application/json")
        )
                .andExpect(status().isNotFound());
        Mockito.verify(betRepositoryMock, times(0)).findById(1L);
    }



}
