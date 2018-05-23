package ch.fhnw.wodss.tippspiel.controller;

import ch.fhnw.wodss.tippspiel.builder.BetGroupDTOBuilder;
import ch.fhnw.wodss.tippspiel.builder.UserBuilder;
import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.dto.BetGroupDTO;
import ch.fhnw.wodss.tippspiel.persistance.UserRepository;
import ch.fhnw.wodss.tippspiel.service.BetGroupService;
import ch.fhnw.wodss.tippspiel.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BetGroupMembershipControllerTest {

    @Value("${security.cors.allowedOrigins}")
    private String corsAllowedOrigins;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private BetGroupService betGroupServiceMock;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Before
    public void mockUserService() {
        User user = new UserBuilder()
                .withId(1L)
                .withName("Yanick")
                .withEmail("yanick.schraner@students.fhnw.ch")
                .build();
        when(userRepository.findUserByEmailEquals(any())).thenReturn(Optional.of(user));
    }

    @Before
    public void setUp() {
        Mockito.reset(betGroupServiceMock);
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }


    @Test
    @WithMockUser(roles = "USER")
    public void addUser_UserAddedToBetGroup_ShouldReturnAdded() throws Exception {
        List<Long> userIds = new ArrayList<>();
        userIds.add(1L);
        BetGroupDTO betGroupDTO = new BetGroupDTOBuilder()
                .withId(1L)
                .withName("FHNW")
                .withScore(0)
                .withUserIds(userIds)
                .build();
        User user = new UserBuilder()
                .withId(2L)
                .withName("Tom2")
                .withRole("ROLE_USER")
                .withPassword("passwordpassword")
                .withEmail("tom2.ohme@gmx.ch")
                .withReminders(true)
                .withDailyResults(true)
                .build();
        when(betGroupServiceMock.addUser(eq(1L), eq("test123"), any())).thenReturn(betGroupDTO);
        mockMvc.perform(post("/betgroupmemberships/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType("text/plain")
                .content("test123")
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("FHNW")))
                .andExpect(jsonPath("$.score", equalTo(0)));
        Mockito.verify(betGroupServiceMock, times(1)).addUser(eq(1L), eq("test123"), any());
    }

    @Test
    @WithMockUser(roles = {"UNVERIFIED"})
    public void addUser_asRoleUnverified_accessDenied() throws Exception {
        List<Long> userIds = new ArrayList<>();
        userIds.add(1L);
        BetGroupDTO betGroupDTO = new BetGroupDTOBuilder()
                .withId(1L)
                .withName("FHNW")
                .withScore(0)
                .withUserIds(userIds)
                .build();
        User user = new UserBuilder()
                .withId(2L)
                .withName("Tom2")
                .withRole("ROLE_USER")
                .withPassword("passwordpassword")
                .withEmail("tom2.ohme@gmx.ch")
                .withReminders(true)
                .withDailyResults(true)
                .build();
        mockMvc.perform(post("/betgroupmemberships/{id}", 1L)
                .headers(buildCORSHeaders())
                .header("Accept", "application/json")
                .contentType("text/plain")
                .content("test123"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "Yanick", roles = "USER")
    public void removeUserFromBetGroup() throws Exception {
        mockMvc.perform(delete("/betgroupmemberships/{id}", 1L)
                .headers(buildCORSHeaders()))
                .andExpect(status().isOk());
        verify(betGroupServiceMock, times(1)).removeUserFromBetGroup(eq(1L), any());
    }

    @Test
    @WithMockUser(roles = {"UNVERIFIED"})
    public void removeUserFromBetGroup_asRoleUnverified_accessDenied() throws Exception {
        mockMvc.perform(delete("/betgroupmemberships/{id}", 1L)
                .headers(buildCORSHeaders()))
                .andExpect(status().isForbidden());
    }

    private HttpHeaders buildCORSHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Requested-With", "JUNIT");
        httpHeaders.add("Origin", corsAllowedOrigins);
        return httpHeaders;
    }
}
