package ch.fhnw.wodss.tippspiel.controller;

import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.dto.BetDTO;
import ch.fhnw.wodss.tippspiel.service.BetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("hasRole('USER')")
@RequestMapping("/userbets")
public class UserBetsController {

    private final BetService betService;

    @Autowired
    public UserBetsController(BetService betService) {
        this.betService = betService;
    }

    @GetMapping(produces = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BetDTO>> getBetsForUser(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(betService.getBetsForUser(user), HttpStatus.OK);
    }
}
