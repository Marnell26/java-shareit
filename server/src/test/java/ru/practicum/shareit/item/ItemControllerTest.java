package ru.practicum.shareit.item;

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
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = ShareItApp.class)
class ItemControllerTest {

    private static final String HDR = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTest() throws Exception {
        ItemDto request = new ItemDto();
        request.setName("Item");
        request.setDescription("Description");
        request.setAvailable(true);

        ItemDto response = new ItemDto();
        response.setId(1L);
        response.setName("Item");
        response.setDescription("Description");
        response.setAvailable(true);

        Mockito.when(itemService.addItem(eq(1L), any(ItemCreateDto.class))).thenReturn(response);

        mvc.perform(post("/items")
                        .header(HDR, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Item"));
    }

    @Test
    void updateTest() throws Exception {
        ItemDto patch = new ItemDto();
        patch.setDescription("New Description");

        ItemDto response = new ItemDto();
        response.setId(2L);
        response.setName("Item2");
        response.setDescription("New Description");
        response.setAvailable(true);

        Mockito.when(itemService.updateItem(eq(1L), eq(2L), any(ItemDto.class))).thenReturn(response);

        mvc.perform(patch("/items/{itemId}", 2L)
                        .header(HDR, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.description").value("New Description"));
    }

    @Test
    void getByIdTest() throws Exception {
        ItemFullDto response = new ItemFullDto();
        response.setId(3L);
        response.setName("Item3");
        response.setDescription("Description");
        response.setAvailable(true);

        Mockito.when(itemService.getItem(eq(3L))).thenReturn(response);

        mvc.perform(get("/items/{itemId}", 3L)
                        .header(HDR, 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Item3"));
    }

    @Test
    void getByIdNotFoundTest() throws Exception {
        Mockito.when(itemService.getItem(eq(999L)))
                .thenThrow(new NotFoundException("Item not found"));

        mvc.perform(get("/items/{itemId}", 999L)
                        .header(HDR, 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOwnerItemsTest() throws Exception {
        ItemFullDto i1 = new ItemFullDto();
        i1.setId(1L);
        i1.setName("A");
        i1.setDescription("d1");
        i1.setAvailable(true);

        ItemFullDto i2 = new ItemFullDto();
        i2.setId(2L);
        i2.setName("B");
        i2.setDescription("d2");
        i2.setAvailable(true);

        Mockito.when(itemService.getItemsByOwner(eq(7L)))
                .thenReturn(List.of(i1, i2));

        mvc.perform(get("/items")
                        .header(HDR, 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void searchTest() throws Exception {
        ItemDto it = new ItemDto();
        it.setId(4L);
        it.setName("Item test");
        it.setDescription("Powerful");
        it.setAvailable(true);

        Mockito.when(itemService.search(eq("test")))
                .thenReturn(List.of(it));

        mvc.perform(get("/items/search")
                        .header(HDR, 1L)
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Item test"));
    }

    @Test
    void searchEmptyTextTest() throws Exception {
        Mockito.when(itemService.search(eq(""))).thenReturn(List.of());

        mvc.perform(get("/items/search")
                        .header(HDR, 1L)
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void addCommentTest() throws Exception {
        CommentCreateDto req = new CommentCreateDto();
        req.setText("Good");

        CommentDto resp = new CommentDto();
        resp.setId(11L);
        resp.setText("Good");
        resp.setAuthorName("Author");

        Mockito.when(itemService.addComment(eq(2L), eq(3L), any(CommentCreateDto.class)))
                .thenReturn(resp);

        mvc.perform(post("/items/{itemId}/comment", 3L)
                        .header(HDR, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11L))
                .andExpect(jsonPath("$.authorName").value("Author"));
    }

    @Test
    void addCommentValidationFailTest() throws Exception {
        CommentCreateDto req = new CommentCreateDto();
        req.setText("bad");

        Mockito.when(itemService.addComment(eq(2L), eq(3L), any(CommentCreateDto.class)))
                .thenThrow(new ValidationException("User has no approved bookings"));

        mvc.perform(post("/items/{itemId}/comment", 3L)
                        .header(HDR, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}