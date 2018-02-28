package ch.fhnw.wodss.tippspiel.Web;

import ch.fhnw.wodss.tippspiel.Domain.Tipp;
import ch.fhnw.wodss.tippspiel.Persistance.TippRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/tipps")
public class TippController {
    @Autowired
    TippRepository repository;

    @GetMapping(value = "/{id}")
    public ResponseEntity<Tipp> getTippById(@PathVariable Long id){
        return null;
    }

    @PostMapping
    public ResponseEntity<Tipp> addTipp(@Valid @RequestBody Tipp tipp, BindingResult result){
        return null;
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Tipp> updateTipp(@Valid @RequestBody Tipp newTipp, BindingResult result, @PathVariable Long id){
        return null;
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteTipp(@PathVariable Long id){
        return null;
    }
}
