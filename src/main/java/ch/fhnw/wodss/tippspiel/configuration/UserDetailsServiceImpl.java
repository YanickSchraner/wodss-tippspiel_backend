package ch.fhnw.wodss.tippspiel.configuration;

import ch.fhnw.wodss.tippspiel.domain.Role;
import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.persistance.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Value("${security.login.errormessage}")
    private String errorMessage;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByNameEquals(username)
                .orElseThrow(() -> new UsernameNotFoundException(errorMessage));
        HashSet<GrantedAuthority> authorities = new HashSet<>();
        if(user.getRoles() != null){
            user.getRoles().stream()
                    .map(Role::getName)
                    .map(SimpleGrantedAuthority::new)
                    .forEach(authorities::add);
        }
        return new org.springframework.security.core.userdetails.User(user.getName(),user.getPassword(), authorities);
    }
}
