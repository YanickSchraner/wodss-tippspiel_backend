package ch.fhnw.wodss.tippspiel.controller;

import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.dto.RestUserDTO;
import ch.fhnw.wodss.tippspiel.dto.UserDTO;
import ch.fhnw.wodss.tippspiel.dto.UserRankingDTO;
import ch.fhnw.wodss.tippspiel.service.BetService;
import ch.fhnw.wodss.tippspiel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;
    private final BetService betService;

    @Autowired
    public UserController(UserService service, BetService betService) {
        this.service = service;
        this.betService = betService;
    }

    @GetMapping(produces = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return new ResponseEntity<>(service.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping(value = "/ranking", produces = "application/json")
    public ResponseEntity<List<UserRankingDTO>> getAllUsersForRanking() {
        return new ResponseEntity<>(service.getAllUsersForRanking(), HttpStatus.OK);
    }

    @Cacheable(value = "users", key = "#id", unless = "#result.statusCode != 200")
    @GetMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = service.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(value = "/name/{name}", produces = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDTO> getUserByName(@PathVariable String name) {
        UserDTO user = service.getUserByName(name);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<UserDTO> addUser(@Valid @RequestBody RestUserDTO restUserDTO, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        UserDTO newUser = service.addUser(restUserDTO);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PutMapping(value = "/passwordReset", consumes = "text/plain")
    public ResponseEntity<String> resetUserPassword(@RequestBody String email) {
        service.resetPassword(email);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody RestUserDTO restUserDTO, @AuthenticationPrincipal User user, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (user != null && !id.equals(user.getId())) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        UserDTO newUser = service.updateUser(user, restUserDTO);
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @CacheEvict(value = "users", key = "#id")
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id, @AuthenticationPrincipal User user) {
        service.deleteUser(id, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
