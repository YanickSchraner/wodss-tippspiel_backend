package ch.fhnw.wodss.tippspiel;

import ch.fhnw.wodss.tippspiel.domain.*;
import ch.fhnw.wodss.tippspiel.persistance.*;
import ch.fhnw.wodss.tippspiel.security.Argon2PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Component
public class TestData {
    @Autowired
    BetGroupRepository betGroupRepository;
    @Autowired
    BetRepository betRepository;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    TournamentGroupRepository tournamentGroupRepository;
    @Autowired
    TournamentTeamRepository tournamentTeamRepository;
    @Autowired
    UserRepository userRepository;

    public void initData() {
        Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder();
        String password = argon2PasswordEncoder.encode("password");
        Role user = new Role("ROLE_USER");
        Role admin = new Role("ROLE_ADMIN");
        roleRepository.save(user);
        roleRepository.save(admin);
        Set<Role> adminRoles = new HashSet<>();
        Set<Role> userRoles = new HashSet<>();
        adminRoles.add(user);
        userRoles.add(user);
        adminRoles.add(admin);
        User yanick = new User("Yanick", password, "yanick.schraner@students.fhnw.ch", new ArrayList<>(), new ArrayList<>(), true, true, adminRoles);
        User tom = new User("Tom", password, "tom.ohme@students.fhnw.ch", new ArrayList<>(), new ArrayList<>(), true, true, userRoles);
        User beni = new User("Beni", password, "benjamin.zumbrunn@students.fhnw.ch", new ArrayList<>(), new ArrayList<>(), true, true, userRoles);
        userRepository.save(yanick);
        userRepository.save(tom);
        userRepository.save(beni);
        BetGroup betGroup = new BetGroup("FHNW", password, 0, new ArrayList<>());
        betGroupRepository.save(betGroup);
        Phase groupPhase = new Phase("Gruppenphase");
        Location location = new Location("Moskau");
        TournamentGroup tournamentGroup = new TournamentGroup("E");
        TournamentTeam switzerland = new TournamentTeam("Schweiz", tournamentGroup);
        TournamentTeam brasil = new TournamentTeam("Brasilien", tournamentGroup);
        TournamentTeam costarica = new TournamentTeam("Costa Rica", tournamentGroup);
        TournamentTeam serbia = new TournamentTeam("Serbien", tournamentGroup);
        tournamentGroupRepository.save(tournamentGroup);
        tournamentTeamRepository.save(switzerland);
        tournamentTeamRepository.save(brasil);
        tournamentTeamRepository.save(costarica);
        tournamentTeamRepository.save(serbia);
        Game game1 = new Game(LocalDateTime.now(), 3, 0, switzerland, brasil, location, groupPhase);
        Game game2 = new Game(LocalDateTime.now().plusDays(10L), 0, 5, brasil, serbia, location, groupPhase);
        gameRepository.save(game1);
    }
}
