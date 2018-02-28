package ch.fhnw.wodss.tippspiel.Web;

import ch.fhnw.wodss.tippspiel.Domain.Tipp;
import ch.fhnw.wodss.tippspiel.Persistance.TippRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/tipps")
public class TippController {
    @Autowired
    TippRepository repository;

    @GetMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    public Tipp getTippById(@PathVariable Long id) {
        return null;
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    @ResponseBody
    public Tipp addTipp(@Valid @RequestBody Tipp tipp, BindingResult result) {
        return null;
    }

    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    @ResponseBody
    public Tipp updateTipp(@Valid @RequestBody Tipp newTipp, BindingResult result, @PathVariable Long id) {
        return null;
    }

    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public String deleteTipp(@PathVariable Long id) {
        return null;
    }
}
