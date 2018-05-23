package ch.fhnw.wodss.tippspiel.service;

import ch.fhnw.wodss.tippspiel.persistance.TournamentGroupRepository;
import ch.fhnw.wodss.tippspiel.persistance.TournamentTeamRepository;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@WebMvcTest(TournamentGroupService.class)
public class TournamentGroupServiceTest {

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
}
