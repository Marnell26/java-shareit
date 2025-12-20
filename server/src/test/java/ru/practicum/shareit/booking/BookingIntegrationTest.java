package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
public class BookingIntegrationTest {
    private Long userId;
    private Long itemId;
    private Long bookingId;

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

        BookingCreateDto bookingCreateDto = new BookingCreateDto(itemId, LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 1, 2, 0, 0));
        MvcResult bookingResult = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsBytes(bookingCreateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String bookingResponse = bookingResult.getResponse().getContentAsString();
        JsonNode bookingJson = objectMapper.readTree(bookingResponse);
        bookingId = bookingJson.get("id").asLong();
    }

    @Test
    public void updateBookingTest() throws Exception {
        mockMvc.perform(patch("/bookings/{id}?approved=true", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$." + "status").value("APPROVED"));
    }

    @Test
    public void getBookingByIdTest() throws Exception {
        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$." + "id").value(bookingId));
    }

    @Test
    public void getUserBookingsTest() throws Exception {
        mockMvc.perform(get("/bookings?state=ALL")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingId))
                .andExpect(jsonPath("$[0].booker.id").value(userId))
                .andExpect(jsonPath("$[0].item.id").value(itemId));
        ;
    }

    @Test
    public void getOwnerBookings() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingId))
                .andExpect(jsonPath("$[0].booker.id").value(userId))
                .andExpect(jsonPath("$[0].item.id").value(itemId));
        ;
    }
}
