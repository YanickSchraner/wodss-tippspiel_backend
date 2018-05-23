package ch.fhnw.wodss.tippspiel.service;

import ch.fhnw.wodss.tippspiel.builder.*;
import ch.fhnw.wodss.tippspiel.domain.*;
import ch.fhnw.wodss.tippspiel.dto.BetGroupDTO;
import ch.fhnw.wodss.tippspiel.dto.RestBetGroupDTO;
import ch.fhnw.wodss.tippspiel.dto.UserAllBetGroupDTO;
import ch.fhnw.wodss.tippspiel.exception.IllegalActionException;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.persistance.BetGroupRepository;
import ch.fhnw.wodss.tippspiel.persistance.UserRepository;
import ch.fhnw.wodss.tippspiel.security.Argon2PasswordEncoder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@WebMvcTest(BetGroupService.class)
public class BetGroupServiceTest {

    @Autowired
    BetGroupService betGroupService;
    @MockBean
    Argon2PasswordEncoder argon2PasswordEncoder;

    @MockBean
    private BetGroupRepository betGroupRepositoryMock;

    @MockBean
    private UserRepository userRepositoryMock;

    @Before
    public void setup() {
        Mockito.reset(betGroupRepositoryMock, userRepositoryMock);
    }

    @Test
    public void getAllUsersInBetGroup_ok() {
        List<User> users = new ArrayList<>();
        User user = new UserBuilder()
                .withId(1L)
                .withName("Tom")
                .withEmail("tom.ohme@gmx.ch")
                .build();
        User user2 = new UserBuilder()
                .withId(1L)
                .withName("Tom2")
                .withEmail("tom2.ohme@gmx.ch")
                .build();
        users.add(user);
        users.add(user2);
        BetGroup betGroup = new BetGroupBuilder()
                .withId(1L)
                .withName("FHNW")
                .withScore(0)
                .withMember(user)
                .withMember(user2)
                .withPassword("test123test123test123")
                .build();

        when(betGroupRepositoryMock.getUserInBetGroup(1L)).thenReturn(users);

        List<UserAllBetGroupDTO> result = betGroupService.getAllUsersInBetGroup(1L);
        assertEquals(user.getId(), result.get(0).getId());
        assertEquals(user.getName(), result.get(0).getName());
        assertEquals(user2.getId(), result.get(1).getId());
        assertEquals(user2.getName(), result.get(1).getName());
        assertEquals(users.size(), result.size());

        verify(betGroupRepositoryMock, times(1)).getUserInBetGroup(1L);
    }

    @Test
    public void getAllBetGroups_ok() {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Tom")
                .withEmail("tom.ohme@gmx.ch")
                .build();
        List<BetGroup> betGroups = new ArrayList<>();
        BetGroup betGroup = new BetGroupBuilder()
                .withId(1L)
                .withName("FHNW")
                .withScore(0)
                .withMember(user)
                .withPassword("test123test123test123")
                .build();
        BetGroup betGroup2 = new BetGroupBuilder()
                .withId(2L)
                .withName("FHNW2")
                .withScore(1)
                .withMember(user)
                .withPassword("test123test123test123")
                .build();
        betGroups.add(betGroup);
        betGroups.add(betGroup2);

        when(betGroupRepositoryMock.findAll()).thenReturn(betGroups);

        List<BetGroupDTO> result = betGroupService.getAllBetGroups();
        assertEquals(betGroup.getId(), result.get(0).getId());
        assertEquals(betGroup.getName(), result.get(0).getName());
        assertEquals(betGroup.getScore(), result.get(0).getScore());
        assertEquals(betGroup.getMembers().get(0).getId(), result.get(0).getUserIds().get(0));
        assertEquals(betGroup2.getId(), result.get(1).getId());
        assertEquals(betGroup2.getName(), result.get(1).getName());
        assertEquals(betGroup2.getScore(), result.get(1).getScore());
        assertEquals(betGroup2.getMembers().get(0).getId(), result.get(1).getUserIds().get(0));
        assertEquals(betGroups.size(), result.size());

        verify(betGroupRepositoryMock, times(1)).findAll();
    }

    @Test
    public void getBetGroupById_ok() {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Tom")
                .withEmail("tom.ohme@gmx.ch")
                .build();
        BetGroup betGroup = new BetGroupBuilder()
                .withId(1L)
                .withName("FHNW")
                .withScore(0)
                .withMember(user)
                .withPassword("test123test123test123")
                .build();
        when(betGroupRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(betGroup));

        BetGroupDTO result = betGroupService.getBetGroupById(1L);
        Assert.assertEquals(betGroup.getId(), result.getId());
        Assert.assertEquals(betGroup.getName(), result.getName());
        Assert.assertEquals(betGroup.getScore(), result.getScore());
        Assert.assertEquals(betGroup.getMembers().get(0).getId(), result.getUserIds().get(0));

        Mockito.verify(betGroupRepositoryMock, times(1)).findById(1L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getBetGroupById_notFound() {
        when(betGroupRepositoryMock.findById(1L)).thenReturn(Optional.empty());
        betGroupService.getBetGroupById(1L);
        Mockito.verify(betGroupRepositoryMock, times(1)).findById(1L);
    }

    @Test
    public void getBetGroupByName_ok() {
        //TODO
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getBetGroupByName_notFound() {
        //TODO
    }

    @Test
    public void addUserToBetGroup_ok() {
        //TODO
    }

    @Test(expected = IllegalActionException.class)
    public void addUserToBetGroup_exists() {
        //TODO
    }

    @Test
    public void removeUserFromBetGroup_ok() {
        //TODO
    }

    @Test
    public void createBetGroup_ok() {
        RestBetGroupDTO restBetGroupDTO = new RestBetGroupDTOBuilder()
                .withName("FHNW")
                .withPassword("test123test123test123")
                .build();
        User user = new UserBuilder()
                .withId(1L)
                .withName("Tom")
                .withEmail("tom.ohme@gmx.ch")
                .build();
        BetGroup betGroup = new BetGroupBuilder()
                .withId(1L)
                .withName("FHNW")
                .withScore(0)
                .withMember(user)
                .withPassword("test123test123test123")
                .build();
        when(betGroupRepositoryMock.findBetGroupByNameEquals("FHNW")).thenReturn(Optional.empty());
        when(betGroupRepositoryMock.save(any())).thenReturn(betGroup);

        BetGroupDTO result = betGroupService.createBetGroup(restBetGroupDTO);
        assertEquals(betGroup.getId(), result.getId());
        assertEquals(betGroup.getName(), result.getName());
        Assert.assertEquals(betGroup.getScore(), result.getScore());
        Assert.assertEquals(betGroup.getMembers().get(0).getId(), result.getUserIds().get(0));

        verify(betGroupRepositoryMock, times(1)).findBetGroupByNameEquals("FHNW");
        verify(betGroupRepositoryMock, times(1)).save(any());
    }

    @Test(expected = IllegalActionException.class)
    public void createBetGroup_exists() {
        RestBetGroupDTO restBetGroupDTO = new RestBetGroupDTOBuilder()
                .withName("FHNW")
                .withPassword("test123test123test123")
                .build();
        User user = new UserBuilder()
                .withId(1L)
                .withName("Tom")
                .withEmail("tom.ohme@gmx.ch")
                .build();
        BetGroup betGroup = new BetGroupBuilder()
                .withId(1L)
                .withName("FHNW")
                .withScore(0)
                .withMember(user)
                .withPassword("test123test123test123")
                .build();
        when(betGroupRepositoryMock.findBetGroupByNameEquals("FHNW")).thenReturn(Optional.ofNullable(betGroup));

        betGroupService.createBetGroup(restBetGroupDTO);

        verify(betGroupRepositoryMock, times(1)).findBetGroupByNameEquals("FHNW");
        verify(betGroupRepositoryMock, times(0)).save(any());
    }

    @Test
    public void deleteBetGroup_ok() {
        //TODO
    }

    @Test(expected = IllegalActionException.class)
    public void deleteBetGroup_hasMembers() {
        //TODO
    }

    @Test(expected = ResourceNotFoundException.class)
    public void deleteBetGroup_notFound() {
        //TODO
    }

}
