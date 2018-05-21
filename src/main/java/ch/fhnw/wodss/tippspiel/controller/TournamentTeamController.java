package ch.fhnw.wodss.tippspiel.controller;

import ch.fhnw.wodss.tippspiel.domain.TournamentTeam;
import ch.fhnw.wodss.tippspiel.dto.RestTournamentTeamDTO;
import ch.fhnw.wodss.tippspiel.dto.TournamentTeamDTO;
import ch.fhnw.wodss.tippspiel.service.TournamentTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/tournamentTeams")
@PreAuthorize("hasRole('USER')")
public class TournamentTeamController {

    private final TournamentTeamService service;

    @Autowired
    public TournamentTeamController(TournamentTeamService service) {
        this.service = service;
    }

    @GetMapping(produces = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<TournamentTeamDTO>> getAllTournamentTeams() {
        return new ResponseEntity<>(service.getAllTournamentTeams(), HttpStatus.OK);
    }

    @Cacheable(value = "tournamentTeams", key = "#id", unless = "#result.statusCode != 200")
    @GetMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TournamentTeamDTO> getTournamentTeamById(@PathVariable Long id) {
        TournamentTeamDTO team = service.getTournamentTeamById(id);
        return new ResponseEntity<>(team, HttpStatus.OK);
    }

    @GetMapping(value = "/name/{name}", produces = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TournamentTeamDTO> getTournamentTeamByName(@PathVariable String name) {
        TournamentTeamDTO team = service.getTournamentTeamByName(name);
        return new ResponseEntity<>(team, HttpStatus.OK);
    }

    @CachePut(value = "tournamentTeams", key ="#tournamentTeam.id", unless = "#result.statusCode != 201")
    @PostMapping(produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TournamentTeamDTO> addTournamentTeam(@Valid @RequestBody RestTournamentTeamDTO restTournamentTeamDTO, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        TournamentTeamDTO newTeam = service.addTournamentTeam(restTournamentTeamDTO);
        return new ResponseEntity<>(newTeam, HttpStatus.CREATED);
    }

    @CachePut(value = "tournamentTeams", key = "#id", unless = "#result.statusCode != 200")
    @PutMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TournamentTeamDTO> updateTournamentTeam(@Valid @RequestBody RestTournamentTeamDTO restTournamentTeamDTO, @PathVariable Long id, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        TournamentTeamDTO newTeam = service.updateTournamentTeam(id, restTournamentTeamDTO);
        return new ResponseEntity<>(newTeam, HttpStatus.OK);
    }

    @CacheEvict(value = "tournamentTeams", key = "#id")
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteTournamentTeam(@PathVariable Long id) {
        service.deleteTournamentTeam(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
