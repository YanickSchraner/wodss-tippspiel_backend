package ch.fhnw.wodss.tippspiel.Web;

import ch.fhnw.wodss.tippspiel.Domain.TournamentGroup;
import ch.fhnw.wodss.tippspiel.Persistance.TournamentGroupRepository;
import ch.fhnw.wodss.tippspiel.Services.TournamentGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/tournamentGroups")
public class TournamentGroupController {

    @Autowired
    private TournamentGroupService service;

    @GetMapping(produces = "application/json")
    @ResponseBody
    public List<TournamentGroup> getAllTournamentGroups() {
        return null;
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    public TournamentGroup getTournamentGroupById(@PathVariable Long id) {
        return null;
    }

    @GetMapping(value = "/name/{name}", produces = "application/json")
    @ResponseBody
    public TournamentGroup getTournamentGroupByName(@PathVariable String name) {
        return null;
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    @ResponseBody
    public TournamentGroup createTournamentGroup(@Valid @RequestBody TournamentGroup tournamentGroup, BindingResult result) {
        return null;
    }

    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    @ResponseBody
    public TournamentGroup updateTournamentGroup(@Valid @RequestBody TournamentGroup tournamentGroup, @PathVariable Long id, BindingResult result) {
        return null;
    }

    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public String deleteTournamentGroup(@PathVariable Long id) {
        return null;
    }

}
