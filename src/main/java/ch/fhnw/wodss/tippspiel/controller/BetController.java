package ch.fhnw.wodss.tippspiel.controller;

import ch.fhnw.wodss.tippspiel.domain.Bet;
import ch.fhnw.wodss.tippspiel.service.BetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/bets")
@PreAuthorize("hasRole('USER')")
public class BetController {

    private final BetService service;

    @Autowired
    public BetController(BetService service) {
        this.service = service;
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Bet> getBetById(@PathVariable Long id) {
        Bet bet = service.getBetById(id);
        return new ResponseEntity<>(service.getBetById(id), HttpStatus.OK);
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Bet> addBet(@Valid @RequestBody Bet bet, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Bet newBet = service.addBet(bet);
        return new ResponseEntity<>(newBet, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Bet> updateBet(@Valid @RequestBody Bet bet, @PathVariable Long id, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Bet newBet = service.updateBet(id, bet);
        return new ResponseEntity<>(newBet, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> deleteBet(@PathVariable Long id) {
        service.deleteBet(id);
        return new ResponseEntity<>("Bet deleted", HttpStatus.OK);
    }
}
