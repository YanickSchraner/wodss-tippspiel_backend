package ch.fhnw.wodss.tippspiel.controller;

import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.dto.BetDTO;
import ch.fhnw.wodss.tippspiel.dto.RestBetDTO;
import ch.fhnw.wodss.tippspiel.service.BetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
    public ResponseEntity<BetDTO> getBetById(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return new ResponseEntity<>(service.getBetById(id, user), HttpStatus.OK);
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BetDTO> addBet(@AuthenticationPrincipal User user, @Valid @RequestBody RestBetDTO restBetDTO, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BetDTO newBet = service.addBet(restBetDTO, user);
        return new ResponseEntity<>(newBet, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BetDTO> updateBet(@AuthenticationPrincipal User user, @Valid @RequestBody RestBetDTO restBetDTO, @PathVariable Long id, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BetDTO newBet = service.updateBet(id, restBetDTO, user);
        return new ResponseEntity<>(newBet, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> deleteBet(@AuthenticationPrincipal User user, @PathVariable Long id) {
        service.deleteBet(id, user);
        return new ResponseEntity<>("Bet deleted", HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/user", produces = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BetDTO>> getBetsForUser(@AuthenticationPrincipal User user, @PathVariable Long userId) {
        return new ResponseEntity<>(service.getBetsForUser(userId, user), HttpStatus.OK);
    }

}
