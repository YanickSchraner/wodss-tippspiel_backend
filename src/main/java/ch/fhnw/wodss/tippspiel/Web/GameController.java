package ch.fhnw.wodss.tippspiel.Web;

import ch.fhnw.wodss.tippspiel.Domain.Game;
import ch.fhnw.wodss.tippspiel.Persistance.GameRepository;
import ch.fhnw.wodss.tippspiel.Services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {
    @Autowired
    private GameRepository repository;
    @Autowired
    private GameService service;

    @GetMapping(produces = "application/json")
    @ResponseBody
    public List<Game> getAllGames() {
        return null;
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    public Game getGameById(@PathVariable Long id) {
        return null;
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    @ResponseBody
    public Game addGame(@Valid @RequestBody Game game, BindingResult result) {
        return null;
    }

    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    @ResponseBody
    public Game updateGame(@Valid @RequestBody Game newGame, BindingResult result, @PathVariable Long id) {
        return null;
    }

    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public String deleteGame(@PathVariable Long id) {
        return null;
    }
}
