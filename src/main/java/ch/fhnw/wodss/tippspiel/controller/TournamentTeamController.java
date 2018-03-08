package ch.fhnw.wodss.tippspiel.controller;

import ch.fhnw.wodss.tippspiel.domain.TournamentTeam;
import ch.fhnw.wodss.tippspiel.service.TournamentTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/tournamentTeams")
public class TournamentTeamController {

    private final TournamentTeamService service;

    @Autowired
    public TournamentTeamController(TournamentTeamService service) {
        this.service = service;
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<TournamentTeam>> getAllTournamentTeams() {
        return new ResponseEntity<>(service.getAllTournamentTeams(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<TournamentTeam> getTournamentTeamById(@PathVariable Long id) {
        TournamentTeam team = service.getTournamentTeamById(id);
        return new ResponseEntity<>(team, HttpStatus.OK);
    }

    @GetMapping(value = "/name/{name}", produces = "application/json")
    public ResponseEntity<TournamentTeam> getTournamentTeamByName(@PathVariable String name) {
        TournamentTeam team = service.getTournamentTeamByName(name);
        return new ResponseEntity<>(team, HttpStatus.OK);
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<TournamentTeam> addTournamentTeam(@Valid @RequestBody TournamentTeam tournamentTeam, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        TournamentTeam newTeam = service.addTournamentTeam(tournamentTeam);
        return new ResponseEntity<>(newTeam, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<TournamentTeam> updateTournamentTeam(@Valid @RequestBody TournamentTeam tournamentTeam, @PathVariable Long id, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        TournamentTeam newTeam = service.updateTournamentTeam(id, tournamentTeam);
        return new ResponseEntity<>(newTeam, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteTournamentTeam(@PathVariable Long id) {
        service.deleteTournamentTeam(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
