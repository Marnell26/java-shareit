package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
public class ItemIntegrationTest {
    private Long userId;
    private Long itemId;

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

        ItemCreateDto itemCreateDto = new ItemCreateDto("item", "description", true, null);
        MvcResult itemResult = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsBytes(itemCreateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String itemResponse = itemResult.getResponse().getContentAsString();
        JsonNode itemJson = objectMapper.readTree(itemResponse);
        itemId = itemJson.get("id").asLong();

    }

    @Test
    public void updateItemTest() throws Exception {
        ItemDto itemUpdateDto = new ItemDto();
        itemUpdateDto.setDescription("Updated description");

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(itemUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$." + "description").value("Updated description"));
    }


    @Test
    public void getItemsByOwnerTest() throws Exception {
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemId))
                .andExpect(jsonPath("$[0].name").value("item"));
    }

    @Test
    public void getItemTest() throws Exception {
        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$." + "id").value(itemId))
                .andExpect(jsonPath("$." + "name").value("item"));
    }

    @Test
    public void searchTest() throws Exception {
        String text = "description";

        mockMvc.perform(get("/items/search" + "?text=" + text)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemId))
                .andExpect(jsonPath("$[0].name").value("item"));
    }

    @Test
    public void addCommentTest() throws Exception {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(itemId, LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 1, 2, 0, 0));
        MvcResult bookingResult = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsBytes(bookingCreateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String bookingResponse = bookingResult.getResponse().getContentAsString();
        JsonNode bookingJson = objectMapper.readTree(bookingResponse);
        Long bookingId = bookingJson.get("id").asLong();

        mockMvc.perform(patch("/bookings/{id}?approved=true", bookingId)
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON));


        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("Good");
        mockMvc.perform(post("/items/{id}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsBytes(commentCreateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$." + "text").value("Good"))
                .andExpect(jsonPath("$." + "authorName").value("user"));

    }
}
