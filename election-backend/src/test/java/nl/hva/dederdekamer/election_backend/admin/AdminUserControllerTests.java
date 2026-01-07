// ...existing code...
package nl.hva.dederdekamer.election_backend.admin;

import nl.hva.dederdekamer.election_backend.controller.AdminUserController;
import nl.hva.dederdekamer.election_backend.dto.UserResponse;
import nl.hva.dederdekamer.election_backend.security.JwtRequestFilter;
import nl.hva.dederdekamer.election_backend.service.AdminUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AdminUserController.class)
public class AdminUserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminUserService adminUserService;

    @MockitoBean
    private JwtRequestFilter jwtRequestFilter;

    @Test
    @DisplayName("GET /api/admin/users returns list of users")
    void listUsers_returnsList() throws Exception {
        var u1 = new UserResponse("1", "Alice", "a@x.com", null, LocalDateTime.of(2023,1,1,0,0), Set.of(), Set.of("ROLE_USER"), null);
        var u2 = new UserResponse("2", "Bob", "b@x.com", null, LocalDateTime.of(2023,2,1,0,0), Set.of(), Set.of("ROLE_ADMIN"), null);
        when(adminUserService.listUsers()).thenReturn(List.of(u1, u2));

        mockMvc.perform(get("/api/admin/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].username").value("Alice"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].username").value("Bob"));
    }

    @Test
    @DisplayName("DELETE /api/admin/users/{id} returns 204 when deletion succeeds")
    void deleteUser_success() throws Exception {
        doNothing().when(adminUserService).deleteUser(1L);

        mockMvc.perform(delete("/api/admin/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/admin/users/{id} returns 500 when user not found")
    void deleteUser_serverErrorWhenNotFound() throws Exception {
        doThrow(new RuntimeException("User not found")).when(adminUserService).deleteUser(999L);

        mockMvc.perform(delete("/api/admin/users/999"))
                .andExpect(status().isInternalServerError());
    }
}

