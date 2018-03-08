package ch.fhnw.wodss.tippspiel.controller;

import ch.fhnw.wodss.tippspiel.domain.TournamentGroup;
import ch.fhnw.wodss.tippspiel.service.TournamentGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/tournamentGroups")
@PreAuthorize("hasRole('USER')")
public class TournamentGroupController {

    private final TournamentGroupService service;

    @Autowired
    public TournamentGroupController(TournamentGroupService service) {
        this.service = service;
    }

    @GetMapping(produces = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<TournamentGroup>> getAllTournamentGroups() {
        return new ResponseEntity<>(service.getAllTournamentGroups(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TournamentGroup> getTournamentGroupById(@PathVariable Long id) {
        TournamentGroup group = service.getTournamentGroupById(id);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @GetMapping(value = "/name/{name}", produces = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TournamentGroup> getTournamentGroupByName(@PathVariable String name) {
        TournamentGroup group = service.getTournamentGroupByName(name);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TournamentGroup> createTournamentGroup(@Valid @RequestBody TournamentGroup tournamentGroup, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        TournamentGroup newGroup = service.addTournamentGroup(tournamentGroup);
        return new ResponseEntity<>(newGroup, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TournamentGroup> updateTournamentGroup(@Valid @RequestBody TournamentGroup tournamentGroup, @PathVariable Long id, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        TournamentGroup newGroup = service.updateTournamentGroup(id, tournamentGroup);
        return new ResponseEntity<>(newGroup, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteTournamentGroup(@PathVariable Long id) {
        service.deleteTournamentGroup(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
