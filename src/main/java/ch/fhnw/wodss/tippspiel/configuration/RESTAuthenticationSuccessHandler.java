package ch.fhnw.wodss.tippspiel.configuration;

import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.persistance.UserRepository;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class RESTAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        Gson gson = new Gson();
        User user = userRepository.findUserByEmailEquals(username).orElse(new User());
        User small = new User(user.getName(), "", user.getEmail(), user.getBets(), new ArrayList<>(), user.isReminders(), user.isDailyResults(), user.getRoles());
        response.getWriter().write(gson.toJson(small));
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
