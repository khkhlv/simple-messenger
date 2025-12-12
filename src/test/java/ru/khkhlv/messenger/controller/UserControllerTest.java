package ru.khkhlv.messenger.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.khkhlv.messenger.dto.UserDto;
import ru.khkhlv.messenger.model.User;
import ru.khkhlv.messenger.repository.UserRepository;
import ru.khkhlv.messenger.service.UserService;
import ru.khkhlv.messenger.util.SecurityContextHelper;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContextHelper securityContextHelper;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @WithMockUser
    void searchUsers_ShouldReturnUserDtos_WhenQueryProvided() throws Exception {
        String query = "test";
        UserDto userDto = new UserDto(1L, "testuser", "test@example.com");

        when(userService.searchUsers(eq(query))).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users/search")
                        .param("query", query)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    @WithMockUser
    void getCurrentUser_ShouldReturnCurrentUser_WhenAuthenticated() throws Exception {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("currentuser");
        user.setEmail("current@example.com");

        when(securityContextHelper.getCurrentUserId()).thenReturn(userId);
        when(userRepository.findById(eq(userId))).thenReturn(java.util.Optional.of(user));

        mockMvc.perform(get("/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value("currentuser"))
                .andExpect(jsonPath("$.email").value("current@example.com"));
    }
}
