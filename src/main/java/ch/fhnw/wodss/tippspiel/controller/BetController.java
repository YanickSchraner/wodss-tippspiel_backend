package ch.fhnw.wodss.tippspiel.controller;

import ch.fhnw.wodss.tippspiel.domain.Bet;
import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.service.BetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @Cacheable(value = "bets", key = "#id", unless = "#result == null")
    @GetMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Bet> getBetById(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return new ResponseEntity<>(service.getBetById(id), HttpStatus.OK);
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Bet> addBet(@AuthenticationPrincipal User user, @Valid @RequestBody Bet bet, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Bet newBet = service.addBet(bet);
        return new ResponseEntity<>(newBet, HttpStatus.CREATED);
    }

    @CachePut(value = "bets", key = "#id", unless = "#result == null")
    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Bet> updateBet(@AuthenticationPrincipal User user, @Valid @RequestBody Bet bet, @PathVariable Long id, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Bet newBet = service.updateBet(id, bet);
        return new ResponseEntity<>(newBet, HttpStatus.OK);
    }

    @CacheEvict(value = "bets", key = "#id")
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> deleteBet(@AuthenticationPrincipal User user, @PathVariable Long id) {
        service.deleteBet(id);
        return new ResponseEntity<>("Bet deleted", HttpStatus.OK);
    }
}
