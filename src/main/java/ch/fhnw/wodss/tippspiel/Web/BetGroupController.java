package ch.fhnw.wodss.tippspiel.Web;

import ch.fhnw.wodss.tippspiel.DTOs.BetGroupDTO;
import ch.fhnw.wodss.tippspiel.DTOs.UserAllBetGroupDTO;
import ch.fhnw.wodss.tippspiel.Domain.BetGroup;
import ch.fhnw.wodss.tippspiel.Domain.User;
import ch.fhnw.wodss.tippspiel.Services.BetGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/betroups")
public class BetGroupController {

    private final BetGroupService service;

    @Autowired
    public BetGroupController(BetGroupService service) {
        this.service = service;
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<BetGroupDTO>> getAllBetGroups() {
        return new ResponseEntity<>(service.getAllBetGroups(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/users", produces = "application/json")
    public ResponseEntity<List<UserAllBetGroupDTO>> getAllUsersInBetGroup(@PathVariable Long id) {
        List<UserAllBetGroupDTO> result = service.getAllUsersInBetGroup(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<BetGroup> getBetGroupById(@PathVariable Long id) {
        BetGroup betGroup = service.getBetGroupById(id);
        return new ResponseEntity<>(betGroup, HttpStatus.OK);
    }

    @GetMapping(value = "/name/{name}", produces = "application/json")
    public ResponseEntity<BetGroup> getBetGroupByName(@PathVariable String name) {
        BetGroup betGroup = service.getBetGroupByName(name);
        return new ResponseEntity<>(betGroup, HttpStatus.OK);
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<BetGroup> addBetGroup(@Valid @RequestBody BetGroup betGroup, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BetGroup newBetGroup = service.createBetGroup(betGroup);
        return new ResponseEntity<>(newBetGroup, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<BetGroup> addUserBetGroup(@PathVariable Long id, @Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        BetGroup betGroup = service.addUser(id, user);
        return new ResponseEntity<>(betGroup, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteBetGroup(@PathVariable Long id) {
        service.deleteBetGroup(id);
        return new ResponseEntity<>("Bet Group deleted", HttpStatus.OK);
    }


}
