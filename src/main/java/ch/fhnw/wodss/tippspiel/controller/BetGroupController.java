package ch.fhnw.wodss.tippspiel.controller;

import ch.fhnw.wodss.tippspiel.domain.BetGroup;
import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.dto.BetGroupDTO;
import ch.fhnw.wodss.tippspiel.dto.RestBetGroupDTO;
import ch.fhnw.wodss.tippspiel.dto.UserAllBetGroupDTO;
import ch.fhnw.wodss.tippspiel.service.BetGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/betgroups")
@PreAuthorize("hasRole('USER')")
public class BetGroupController {

    private final BetGroupService service;

    @Autowired
    public BetGroupController(BetGroupService service) {
        this.service = service;
    }

    @GetMapping(produces = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BetGroupDTO>> getAllBetGroups() {
        return new ResponseEntity<>(service.getAllBetGroups(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/users", produces = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<UserAllBetGroupDTO>> getAllUsersInBetGroup(@PathVariable Long id) {
        List<UserAllBetGroupDTO> result = service.getAllUsersInBetGroup(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Cacheable(value = "betGroups", key = "#id", unless = "#result.statusCode != 200")
    @GetMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BetGroup> getBetGroupById(@PathVariable Long id) {
        BetGroup betGroup = service.getBetGroupById(id);
        return new ResponseEntity<>(betGroup, HttpStatus.OK);
    }

    @Cacheable(value = "betGroupsName", key = "#name", unless = "#result.statusCode != 200")
    @GetMapping(value = "/name/{name}", produces = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BetGroup> getBetGroupByName(@PathVariable String name) {
        BetGroup betGroup = service.getBetGroupByName(name);
        return new ResponseEntity<>(betGroup, HttpStatus.OK);
    }

    @Caching(put = {
            @CachePut(value = "betGroups", key = "#betGroup.id", unless = "#result.statusCode != 201"),
            @CachePut(value = "betGroupsName", key = "#betGroup.name", unless = "#result.statusCode != 201")
    })
    @PostMapping(produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BetGroupDTO> addBetGroup(@Valid @RequestBody RestBetGroupDTO restBetGroupDTO, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BetGroupDTO newBetGroup = service.createBetGroup(restBetGroupDTO);
        return new ResponseEntity<>(newBetGroup, HttpStatus.CREATED);
    }

    @Caching(put = {
            @CachePut(value = "betGroups", key = "#id", unless = "#result.statusCode != 201"),
            @CachePut(value = "betGroupsName", key = "#result.body.name", unless = "#result.statusCode != 201")
    })
    @PutMapping(value = "/{id}/addUser", produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BetGroup> addUserToBetGroup(@PathVariable Long id, @Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BetGroup betGroup = service.addUser(id, user);
        return new ResponseEntity<>(betGroup, HttpStatus.CREATED);
    }

    // Todo
    @Caching(evict = {
            @CacheEvict(value = "betGroups", key = "#id"),
            @CacheEvict(value = "betGroupsName", key = "#result.body.name")
    })
    @PutMapping(value = "/{id}/removeUser", produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BetGroup> removeUserFromBetGroup(@PathVariable Long id, @Valid @RequestBody User user, BindingResult result) {
        return null;
    }
}
