package ch.fhnw.wodss.tippspiel.Web;

import ch.fhnw.wodss.tippspiel.Domain.TournamentGroup;
import ch.fhnw.wodss.tippspiel.Services.TournamentGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/tournamentGroups")
public class TournamentGroupController {

    private final TournamentGroupService service;

    @Autowired
    public TournamentGroupController(TournamentGroupService service) {
        this.service = service;
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<TournamentGroup>> getAllTournamentGroups() {
        return new ResponseEntity<>(service.getAllTournamentGroups(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<TournamentGroup> getTournamentGroupById(@PathVariable Long id) {
        TournamentGroup group = service.getTournamentGroupById(id);
        if (null == group) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @GetMapping(value = "/name/{name}", produces = "application/json")
    public ResponseEntity<TournamentGroup> getTournamentGroupByName(@PathVariable String name) {
        TournamentGroup group = service.getTournamentGroupByName(name);
        if (null == group) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<TournamentGroup> createTournamentGroup(@Valid @RequestBody TournamentGroup tournamentGroup, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        TournamentGroup newGroup = service.addTournamentGroup(tournamentGroup);
        if (null == newGroup) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(newGroup, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<TournamentGroup> updateTournamentGroup(@Valid @RequestBody TournamentGroup tournamentGroup, @PathVariable Long id, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        TournamentGroup newGroup = service.updateTournamentGroup(id, tournamentGroup);
        if (null == newGroup) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(newGroup, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteTournamentGroup(@PathVariable Long id) {
        service.deleteTournamentGroup(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
