package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemCreateRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItemsList;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItemsList;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = ShareItApp.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemRequestService requestService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTest() throws Exception {
        ItemCreateRequestDto request = new ItemCreateRequestDto();
        request.setDescription("Need an item");

        ItemRequestDto response = new ItemRequestDto();
        response.setId(1L);
        response.setDescription("Need an item");
        response.setCreated(LocalDateTime.now());

        Mockito.when(requestService.addRequest(eq(1L), any(ItemCreateRequestDto.class)))
                .thenReturn(response);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need an item"));
    }

    @Test
    void getOwnRequestsTest() throws Exception {
        ItemRequestDtoWithItemsList ItemRequestDtoWithItemsList = new ItemRequestDtoWithItemsList();
        ItemRequestDtoWithItemsList.setId(1L);
        ItemRequestDtoWithItemsList.setDescription("Need an item");
        ItemRequestDtoWithItemsList.setCreated(LocalDateTime.now());

        Mockito.when(requestService.getOwnRequests(1L))
                .thenReturn(List.of(ItemRequestDtoWithItemsList));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Need an item"));
    }

    @Test
    void getAllRequestsTest() throws Exception {
        ItemRequestDtoWithItemsList ItemRequestDtoWithItemsList = new ItemRequestDtoWithItemsList();
        ItemRequestDtoWithItemsList.setId(2L);
        ItemRequestDtoWithItemsList.setDescription("Need an item");
        ItemRequestDtoWithItemsList.setCreated(LocalDateTime.now());

        Mockito.when(requestService.getAllRequests(2L))
                .thenReturn(List.of(ItemRequestDtoWithItemsList));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 2L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].description").value("Need an item"));
    }

    @Test
    void getByIdTest() throws Exception {
        ItemRequestDtoWithItemsList ItemRequestDtoWithItemsList = new ItemRequestDtoWithItemsList();
        ItemRequestDtoWithItemsList.setId(5L);
        ItemRequestDtoWithItemsList.setDescription("Need an item");
        ItemRequestDtoWithItemsList.setCreated(LocalDateTime.now());

        Mockito.when(requestService.getRequestById(5L)).thenReturn(ItemRequestDtoWithItemsList);

        mvc.perform(get("/requests/{requestId}", 5L)
                        .header("X-Sharer-User-Id", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.description").value("Need an item"));
    }

    @Test
    void getByIdNotFound() throws Exception {
        Mockito.when(requestService.getRequestById(999L))
                .thenThrow(new NotFoundException("Request not found"));

        mvc.perform(get("/requests/{requestId}", 999L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }
}
