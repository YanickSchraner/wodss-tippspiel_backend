package ch.fhnw.wodss.tippspiel.Web;

import ch.fhnw.wodss.tippspiel.Domain.User;
import ch.fhnw.wodss.tippspiel.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping(produces = "application/json")
    @ResponseBody
    public List<User> getAllUsers() {
        return null;
    }

    @GetMapping(value = "/ranking", produces = "application/json")
    @ResponseBody
    public List<User> getAllUsersForRanking() {
        return null;
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    public User getUserById(@PathVariable Long id) {
        return null;
    }

    @GetMapping(value = "/name/{name}", produces = "application/json")
    @ResponseBody
    public User getUserByName(@PathVariable String name) {
        return null;
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    @ResponseBody
    public User addUser(@Valid @RequestBody User user, BindingResult result) {
        return null;
    }

    @PutMapping(value = "/{id}/email", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public User updateUserEmail(@Valid @RequestBody User user, @PathVariable Long id, BindingResult result) {
        return null;
    }

    @PutMapping(value = "/{id}/passwordReset")
    @ResponseBody
    public String resetUserPassword(@PathVariable Long id) {
        return null;
    }

    @PutMapping(value = "/{id}/passwordChange", consumes = "application/json")
    @ResponseBody
    public String changeUserPassword(@RequestParam("old") String oldPassword, @RequestParam("new") String newPassword, @PathVariable Long id, BindingResult result) {
        return null;
    }

    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public String deleteUser(@PathVariable Long id) {
        return null;
    }
}
