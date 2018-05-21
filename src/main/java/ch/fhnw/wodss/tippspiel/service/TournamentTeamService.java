package ch.fhnw.wodss.tippspiel.service;

import ch.fhnw.wodss.tippspiel.domain.TournamentGroup;
import ch.fhnw.wodss.tippspiel.domain.TournamentTeam;
import ch.fhnw.wodss.tippspiel.dto.RestTournamentTeamDTO;
import ch.fhnw.wodss.tippspiel.dto.TournamentTeamDTO;
import ch.fhnw.wodss.tippspiel.exception.IllegalActionException;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.persistance.TournamentGroupRepository;
import ch.fhnw.wodss.tippspiel.persistance.TournamentTeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
public class TournamentTeamService {

    private final TournamentTeamRepository repository;
    private final TournamentGroupRepository tournamentGroupRepository;

    @Autowired
    public TournamentTeamService(TournamentTeamRepository repository, TournamentGroupRepository tournamentGroupRepository) {
        this.repository = repository;
        this.tournamentGroupRepository = tournamentGroupRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<TournamentTeamDTO> getAllTournamentTeams() {
        List<TournamentTeam> tournamentTeams = repository.findAll();
        List<TournamentTeamDTO> tournamentTeamDTOS = new ArrayList<>();
        for (TournamentTeam tournamentTeam : tournamentTeams) {
            tournamentTeamDTOS.add(convertTournamentTeamToTournamanetTeamDTO(tournamentTeam));
        }
        return tournamentTeamDTOS;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public TournamentTeamDTO getTournamentTeamById(Long id) {
        TournamentTeam tournamentTeam = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can't find a tournament team with id: " + id));
        return convertTournamentTeamToTournamanetTeamDTO(tournamentTeam);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public TournamentTeamDTO getTournamentTeamByName(String name) {
        TournamentTeam tournamentTeam = repository.findTournamentTeamByNameEquals(name)
                .orElseThrow(() -> new ResourceNotFoundException("Can't find a tournament team with name: " + name));
        return convertTournamentTeamToTournamanetTeamDTO(tournamentTeam);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TournamentTeamDTO addTournamentTeam(RestTournamentTeamDTO restTournamentTeamDTO) {
        if (repository.findTournamentTeamByNameEquals(restTournamentTeamDTO.getName()).isPresent()) {
            throw new IllegalActionException("There is already a tournament team with the name: " + restTournamentTeamDTO.getName());
        }
        TournamentTeam tournamentTeam = new TournamentTeam();
        tournamentTeam.setGroup(tournamentGroupRepository.findById(restTournamentTeamDTO.getTournamentGroupId()).orElseThrow(() -> new ResourceNotFoundException("Tournament group with id " + restTournamentTeamDTO.getTournamentGroupId() + " not found!")));
        tournamentTeam.setName(restTournamentTeamDTO.getName());
        repository.save(tournamentTeam);
        return convertTournamentTeamToTournamanetTeamDTO(tournamentTeam);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TournamentTeamDTO updateTournamentTeam(Long id, RestTournamentTeamDTO restTournamentTeamDTO) {
        TournamentTeam tournamentTeam = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No tournament team with id " + id + " exists!"));
        tournamentTeam.setId(id);
        tournamentTeam.setGroup(tournamentGroupRepository.findById(restTournamentTeamDTO.getTournamentGroupId()).orElseThrow(() -> new ResourceNotFoundException("Tournament group with id " + restTournamentTeamDTO.getTournamentGroupId() + " not found!")));
        tournamentTeam.setName(restTournamentTeamDTO.getName());
        repository.save(tournamentTeam);
        return convertTournamentTeamToTournamanetTeamDTO(tournamentTeam);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteTournamentTeam(Long id) {
        if (repository.hasGames(id)) {
            throw new IllegalActionException("Can't delete a tournament team with open games.");
        }
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Can't find a tournament team with id: " + id + " to delete.");
        }
    }

    private TournamentTeamDTO convertTournamentTeamToTournamanetTeamDTO(TournamentTeam tournamentTeam) {
        TournamentTeamDTO tournamentTeamDTO = new TournamentTeamDTO();
        tournamentTeamDTO.setName(tournamentTeam.getName());
        tournamentTeamDTO.setTournamentGroupName(tournamentTeam.getGroup().getName());
        return new TournamentTeamDTO();
    }

}
