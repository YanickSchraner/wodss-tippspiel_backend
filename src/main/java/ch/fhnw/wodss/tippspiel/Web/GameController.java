package ch.fhnw.wodss.tippspiel.Web;

import ch.fhnw.wodss.tippspiel.Domain.Game;
import ch.fhnw.wodss.tippspiel.Persistance.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {
    @Autowired
    private GameRepository repository;

    @GetMapping
    public ResponseEntity<List<Game>> getAllGames(){
        return null;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable Long id){
        return null;
    }

    @PostMapping
    public ResponseEntity<Game> addGame(@Valid @RequestBody Game game, BindingResult result){
        return null;
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Game> updateGame(@Valid @RequestBody Game newGame, BindingResult result, @PathVariable Long id){
        return null;
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteGame(@PathVariable Long id){
        return null;
    }
}
