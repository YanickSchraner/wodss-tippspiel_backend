package ch.fhnw.wodss.tippspiel.Web;

import ch.fhnw.wodss.tippspiel.Domain.Game;
import ch.fhnw.wodss.tippspiel.Services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService service;

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<Game>> getAllGames() {
        return new ResponseEntity<>(service.getAllGames(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Game> getGameById(@PathVariable Long id) {
        Game game = service.getGameById(id);
        if(game == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<Game> addGame(@Valid @RequestBody Game game, BindingResult result) {
        return null;
    }

    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Game> updateGame(@Valid @RequestBody Game newGame, BindingResult result, @PathVariable Long id) {
        return null;
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteGame(@PathVariable Long id) {
        return null;
    }
}
