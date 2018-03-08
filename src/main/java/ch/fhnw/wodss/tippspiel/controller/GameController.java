package ch.fhnw.wodss.tippspiel.controller;

import ch.fhnw.wodss.tippspiel.domain.Game;
import ch.fhnw.wodss.tippspiel.service.GameService;
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

    private final GameService service;

    @Autowired
    public GameController(GameService service) {
        this.service = service;
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<Game>> getAllGames() {
        return new ResponseEntity<>(service.getAllGames(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Game> getGameById(@PathVariable Long id) {
        Game game = service.getGameById(id);
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<Game> addGame(@Valid @RequestBody Game game, BindingResult result) {
        Game newGame = service.addGame(game);
        return new ResponseEntity<>(newGame, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Game> updateGame(@Valid @RequestBody Game newGame, BindingResult result, @PathVariable Long id) {
        Game game = service.updateGame(id, newGame);
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteGame(@PathVariable Long id) {
        service.deleteGame(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
