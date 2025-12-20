package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemCreateRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
public class RequestIntegrationTest {
    private Long userId;
    private Long itemRequestId;

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

        ItemCreateRequestDto itemCreateRequestDto = new ItemCreateRequestDto("request description");
        MvcResult requestResult = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsBytes(itemCreateRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String requestResponse = requestResult.getResponse().getContentAsString();
        JsonNode requestJson = objectMapper.readTree(requestResponse);
        itemRequestId = requestJson.get("id").asLong();
    }

    @Test
    public void getOwnRequests() throws Exception {
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestId))
                .andExpect(jsonPath("$[0].description").value("request description"));
    }

    @Test
    public void getAllRequests() throws Exception {
        UserDto userDto2 = new UserDto(null, "user2", "user2@mail.ru");
        MvcResult userResult = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsBytes(userDto2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String userResponse = userResult.getResponse().getContentAsString();
        JsonNode userJson = objectMapper.readTree(userResponse);
        Long userId2 = userJson.get("id").asLong();

        ItemCreateRequestDto itemCreateRequestDto2 = new ItemCreateRequestDto("request2 description");
        MvcResult requestResult = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId2)
                        .content(objectMapper.writeValueAsBytes(itemCreateRequestDto2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String requestResponse = requestResult.getResponse().getContentAsString();
        JsonNode requestJson = objectMapper.readTree(requestResponse);
        Long itemRequestId2 = requestJson.get("id").asLong();

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestId2))
                .andExpect(jsonPath("$[0].description").value("request2 description"));
    }

    @Test
    public void getRequestById() throws Exception {
        mockMvc.perform(get("/requests/{id}", itemRequestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$." + "id").value(itemRequestId))
                .andExpect(jsonPath("$." + "description").value("request description"));
    }

}
