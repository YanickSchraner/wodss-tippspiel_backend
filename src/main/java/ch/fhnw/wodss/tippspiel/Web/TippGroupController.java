package ch.fhnw.wodss.tippspiel.Web;

import ch.fhnw.wodss.tippspiel.DTOs.TippGroupDTO;
import ch.fhnw.wodss.tippspiel.DTOs.UserAllTippGroupDTO;
import ch.fhnw.wodss.tippspiel.Domain.TippGroup;
import ch.fhnw.wodss.tippspiel.Domain.User;
import ch.fhnw.wodss.tippspiel.Persistance.TippGroupRepository;
import ch.fhnw.wodss.tippspiel.Services.TippGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/tippGroups")
public class TippGroupController {

    @Autowired
    private TippGroupRepository repository;

    @Autowired
    private TippGroupService service;

    @GetMapping(produces = "application/json")
    @ResponseBody
    public List<TippGroupDTO> getAllTippGroups() {
        return null;
    }

    @GetMapping(value = "/{id}/users", produces = "application/json")
    @ResponseBody
    public List<UserAllTippGroupDTO> getAllUsersInTippGroup(@PathVariable Long id) {
        return null;
    }


    @GetMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    public TippGroup getTippGroupById(@PathVariable Long id) {
        return null;
    }

    @GetMapping(value = "/name/{name}", produces = "application/json")
    @ResponseBody
    public TippGroup getTippGroupByName(@PathVariable String name) {
        return null;
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    @ResponseBody
    public TippGroup addTippGroup(@Valid @RequestBody TippGroup tippGroup, BindingResult result) {
        return null;
    }

    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    @ResponseBody
    public TippGroup addUserToTippGroup(@PathVariable Long id, @Valid @RequestBody User user, BindingResult result) {
        return null;
    }

    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public String deleteTippGroup(@PathVariable Long id) {
        return null;
    }


}
