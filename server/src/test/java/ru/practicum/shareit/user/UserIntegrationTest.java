package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
public class UserIntegrationTest {
    private Long userId;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    public void beforeEach() throws Exception {
        UserDto userDto = new UserDto(null, "user", "user@mail.ru");
        MvcResult userResult = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsBytes(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String userResponse = userResult.getResponse().getContentAsString();
        JsonNode userJson = objectMapper.readTree(userResponse);
        userId = userJson.get("id").asLong();
    }

    @Test
    public void updateUser() throws Exception {
        UserDto userUpdateDto = new UserDto();
        userUpdateDto.setEmail("updatedEmail@mail.ru");

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$." + "email").value("updatedEmail@mail.ru"));
    }

    @Test
    public void getUserById() throws Exception {
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$." + "id").value(userId))
                .andExpect(jsonPath("$." + "name").value("user"))
                .andExpect(jsonPath("$." + "email").value("user@mail.ru"));
    }

    @Test
    public void getUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userId))
                .andExpect(jsonPath("$[0].name").value("user"))
                .andExpect(jsonPath("$[0].email").value("user@mail.ru"));
    }

    @Test
    public void deleteUser() throws Exception {
        mockMvc.perform(delete("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

}
