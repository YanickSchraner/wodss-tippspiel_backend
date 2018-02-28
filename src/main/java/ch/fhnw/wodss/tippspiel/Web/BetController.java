package ch.fhnw.wodss.tippspiel.Web;

import ch.fhnw.wodss.tippspiel.Domain.Bet;
import ch.fhnw.wodss.tippspiel.Services.BetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/bets")
public class BetController {

    private final BetService service;

    @Autowired
    public BetController(BetService service) {
        this.service = service;
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    public Bet getBetById(@PathVariable Long id) {
        return null;
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    @ResponseBody
    public Bet addBet(@Valid @RequestBody Bet bet, BindingResult result) {
        return null;
    }

    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    @ResponseBody
    public Bet updateBet(@Valid @RequestBody Bet newBet, BindingResult result, @PathVariable Long id) {
        return null;
    }

    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public String deleteBet(@PathVariable Long id) {
        return null;
    }
}
