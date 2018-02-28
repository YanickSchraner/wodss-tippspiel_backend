package ch.fhnw.wodss.tippspiel.Web;

import ch.fhnw.wodss.tippspiel.DTOs.TippGroupDTO;
import ch.fhnw.wodss.tippspiel.DTOs.UserAllTippGroupDTO;
import ch.fhnw.wodss.tippspiel.Domain.TippGroup;
import ch.fhnw.wodss.tippspiel.Persistance.TippGroupRepository;
import ch.fhnw.wodss.tippspiel.Services.TippGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/tippGroup")
public class TippGroupController {

    @Autowired
    private TippGroupRepository repository;

    @Autowired
    private TippGroupService service;

    @GetMapping
    public ResponseEntity<List<TippGroupDTO>> getAllTippGroups() {
        return null;
    }

    @GetMapping(value = "/{id}/users")
    public ResponseEntity<List<UserAllTippGroupDTO>> getAllUsersInTippGroup(@PathVariable Long id) {
        return null;
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<TippGroup> getTippGroupById(@PathVariable Long id) {
        return null;
    }

    @GetMapping(value = "/name/{name}")
    public ResponseEntity<TippGroup> getTippGroupByName(@PathVariable String name){
        return null;
    }

    @PostMapping
    public ResponseEntity<TippGroup> addTippGroup(@Valid @RequestBody TippGroup tippGroup, BindingResult result){
        return null;
    }

    @PutMapping
    public ResponseEntity<TippGroup> addUserToTippGroup(String raw){
        return null;
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteTippGroup(@PathVariable Long id){
        return null;
    }


}
