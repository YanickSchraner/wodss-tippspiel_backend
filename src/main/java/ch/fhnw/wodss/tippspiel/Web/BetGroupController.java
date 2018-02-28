package ch.fhnw.wodss.tippspiel.Web;

import ch.fhnw.wodss.tippspiel.DTOs.BetGroupDTO;
import ch.fhnw.wodss.tippspiel.DTOs.UserAllBetGroupDTO;
import ch.fhnw.wodss.tippspiel.Domain.BetGroup;
import ch.fhnw.wodss.tippspiel.Domain.User;
import ch.fhnw.wodss.tippspiel.Services.BetGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/betroups")
public class BetGroupController {

    @Autowired
    private BetGroupService service;

    @GetMapping(produces = "application/json")
    @ResponseBody
    public List<BetGroupDTO> getAllBetGroups() {
        return null;
    }

    @GetMapping(value = "/{id}/users", produces = "application/json")
    @ResponseBody
    public List<UserAllBetGroupDTO> getAllUsersInBetGroup(@PathVariable Long id) {
        return null;
    }


    @GetMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    public BetGroup getBetGroupById(@PathVariable Long id) {
        return null;
    }

    @GetMapping(value = "/name/{name}", produces = "application/json")
    @ResponseBody
    public BetGroup getBetGroupByName(@PathVariable String name) {
        return null;
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    @ResponseBody
    public BetGroup addBetGroup(@Valid @RequestBody BetGroup betGroup, BindingResult result) {
        return null;
    }

    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    @ResponseBody
    public BetGroup addUserBetGroup(@PathVariable Long id, @Valid @RequestBody User user, BindingResult result) {
        return null;
    }

    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public String deleteBetGroup(@PathVariable Long id) {
        return null;
    }


}
