package ch.fhnw.wodss.tippspiel.Web;

import ch.fhnw.wodss.tippspiel.Domain.TournamentTeam;
import ch.fhnw.wodss.tippspiel.Persistance.TournamentTeamRepository;
import ch.fhnw.wodss.tippspiel.Services.TournamentTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/tournamentTeams")
public class TournamentTeamController {

    @Autowired
    private TournamentTeamService service;

    @GetMapping(produces = "application/json")
    @ResponseBody
    public List<TournamentTeam> getAllTournamentTeams() {
        return null;
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    public TournamentTeam getTournamentTeamById() {
        return null;
    }

    @GetMapping(value = "/name/{name}", produces = "application/json")
    @ResponseBody
    public TournamentTeam getTournamentTeamByName(@PathVariable String name) {
        return null;
    }

    @PostMapping(produces = "application/json")
    @ResponseBody
    public TournamentTeam addTournamentTeam(@Valid @RequestBody TournamentTeam tournamentTeam, BindingResult result) {
        return null;
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    public TournamentTeam updateTournamentTeam(@Valid @RequestBody TournamentTeam tournamentTeam, @PathVariable Long id, BindingResult result) {
        return null;
    }

    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public String deleteTournamentTeam(@PathVariable Long id) {
        return null;
    }
}
