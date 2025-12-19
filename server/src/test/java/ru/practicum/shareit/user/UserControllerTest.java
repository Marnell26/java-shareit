package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Test
    void createTest() throws Exception {
        UserDto request = new UserDto(null, "user", "user@mail.ru");
        UserDto response = new UserDto(1L, "user", "user@mail.ru");

        Mockito.when(userService.addUser(Mockito.any(UserDto.class))).thenReturn(response);

        mvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("user@mail.ru"));
    }

    @Test
    void updateWithConflictEmailTest() throws Exception {
        Mockito.doThrow(new ConflictException("Email уже используется"))
                .when(userService).updateUser(Mockito.eq(2L), Mockito.any(UserDto.class));

        mvc.perform(MockMvcRequestBuilders.patch("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"usr@mail.ru\"}"))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }


    @Test
    void getByIdTest() throws Exception {
        UserDto response = new UserDto(1L, "user", "user@mail.ru");
        Mockito.when(userService.getUserById(1L)).thenReturn(response);

        mvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("user"));
    }

    @Test
    void getByIdNotFoundTest() throws Exception {
        Mockito.when(userService.getUserById(99L))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mvc.perform(MockMvcRequestBuilders.get("/users/99"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void deleteTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Mockito.verify(userService, Mockito.times(1)).deleteUser(1L);
    }

    @Test
    void getUsersTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }
}