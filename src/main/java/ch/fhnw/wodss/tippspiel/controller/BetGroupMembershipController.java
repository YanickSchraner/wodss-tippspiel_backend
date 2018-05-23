package ch.fhnw.wodss.tippspiel.controller;

import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.dto.BetGroupDTO;
import ch.fhnw.wodss.tippspiel.service.BetGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/betgroupmemberships")
@PreAuthorize("hasRole('USER')")
public class BetGroupMembershipController {

    private final BetGroupService service;

    @Autowired
    public BetGroupMembershipController(BetGroupService service) {
        this.service = service;
    }

    @PostMapping(value = "/{id}", produces= "application/json", consumes = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BetGroupDTO> addUserToBetGroup(@PathVariable Long id, @AuthenticationPrincipal User user, @RequestBody String password) {
        BetGroupDTO betGroup = service.addUser(id, password, user);
        return new ResponseEntity<>(betGroup, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> removeUserFromBetGroup(@PathVariable Long id, @AuthenticationPrincipal User user) {
        service.removeUserFromBetGroup(id, user);
        return new ResponseEntity<>("User from bet group removed", HttpStatus.OK);

    }
}
