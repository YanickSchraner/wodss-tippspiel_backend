package ch.fhnw.wodss.tippspiel.controller;

import ch.fhnw.wodss.tippspiel.dto.GameDTO;
import ch.fhnw.wodss.tippspiel.dto.RestGameDTO;
import ch.fhnw.wodss.tippspiel.service.GameService;
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
@RequestMapping("/games")
@PreAuthorize("hasRole('USER')")
public class GameController {

    private final GameService service;

    @Autowired
    public GameController(GameService service) {
        this.service = service;
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<GameDTO>> getAllGames() {
        return new ResponseEntity<>(service.getAllGames(), HttpStatus.OK);
    }

    @Cacheable(value = "games", key = "#id", unless = "#result.statusCode != 200")
    @GetMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GameDTO> getGameById(@PathVariable Long id) {
        GameDTO game = service.getGameById(id);
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    @CachePut(value = "games", key = "#game.id", unless = "#result.statusCode != 201")
    @PostMapping(produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GameDTO> addGame(@Valid @RequestBody RestGameDTO restGameDTO, BindingResult result) {
        GameDTO newGame = service.addGame(restGameDTO);
        return new ResponseEntity<>(newGame, HttpStatus.CREATED);
    }

    @CachePut(value = "games", key = "#id", unless = "#result.statusCode != 200")
    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GameDTO> updateGame(@Valid @RequestBody RestGameDTO restGameDTO, BindingResult result, @PathVariable Long id) {
        GameDTO game = service.updateGame(id, restGameDTO);
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    @CacheEvict(value = "games", key = "#id")
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteGame(@PathVariable Long id) {
        service.deleteGame(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
