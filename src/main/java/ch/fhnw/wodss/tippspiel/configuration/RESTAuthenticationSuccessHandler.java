package ch.fhnw.wodss.tippspiel.configuration;

import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.dto.UserDTO;
import ch.fhnw.wodss.tippspiel.persistance.UserRepository;
import ch.fhnw.wodss.tippspiel.service.UserService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RESTAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        Gson gson = new Gson();
        User user = userRepository.findUserByEmailEquals(username).orElse(new User());
        UserDTO userDTO = userService.convertUserToUserDTO(user);
        response.getWriter().write(gson.toJson(userDTO));
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
