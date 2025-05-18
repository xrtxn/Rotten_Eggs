package com.xrt.tarsas.ertekelo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.xrt.tarsas.ertekelo.service.DirectorController;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DirectorController.class)
public class DirectorControllerTest {

    private static final String DIRECTOR_1_FIRST_NAME = "Frank";
    private static final String DIRECTOR_1_LAST_NAME = "Darabont";
    private static final String DIRECTOR_2_FIRST_NAME = "Francis Ford";
    private static final String DIRECTOR_2_LAST_NAME = "Coppola";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DirectorRepository repository;

    private Director testDirector1;
    private Director testDirector2;
    private List<Director> directorList;

    @BeforeEach
    void setUp() {
        testDirector1 = new Director(
            1L,
            DIRECTOR_1_FIRST_NAME,
            DIRECTOR_1_LAST_NAME,
            "1959"
        );
        testDirector2 = new Director(
            2L,
            DIRECTOR_2_FIRST_NAME,
            DIRECTOR_2_LAST_NAME,
            "1939"
        );
        directorList = Arrays.asList(testDirector1, testDirector2);
    }

    @Test
    public void testListDirectors() throws Exception {
        when(repository.findAll()).thenReturn(directorList);

        mockMvc
            .perform(get("/directors"))
            .andExpect(status().isOk())
            .andExpect(view().name("directors"))
            .andExpect(model().attributeExists("directors"))
            .andExpect(model().attribute("directors", directorList));
    }

    @Test
    public void testViewDirectorFound() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(testDirector1));

        mockMvc
            .perform(get("/directors/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("director-details"))
            .andExpect(model().attributeExists("director"))
            .andExpect(model().attribute("director", testDirector1));
    }

    @Test
    public void testViewDirectorNotFound() throws Exception {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        mockMvc
            .perform(get("/directors/999"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/directors"));
    }

    @Test
    public void testNewDirectorForm() throws Exception {
        mockMvc
            .perform(get("/directors/new"))
            .andExpect(status().isOk())
            .andExpect(view().name("director-form"))
            .andExpect(model().attributeExists("director"));
    }

    @Test
    public void testSaveDirector() throws Exception {
        when(repository.save(any(Director.class))).thenAnswer(invocation ->
            invocation.getArgument(0)
        );

        mockMvc
            .perform(
                post("/directors/save")
                    .param("firstName", DIRECTOR_1_FIRST_NAME)
                    .param("lastName", DIRECTOR_1_LAST_NAME)
                    .param("birthYear", "1959")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/directors"))
            .andExpect(flash().attributeExists("successMessage"));

        verify(repository, times(1)).save(any(Director.class));
    }

    @Test
    public void testEditDirectorFormFound() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(testDirector1));

        mockMvc
            .perform(get("/directors/edit/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("director-form"))
            .andExpect(model().attributeExists("director"))
            .andExpect(model().attribute("director", testDirector1));
    }

    @Test
    public void testEditDirectorFormNotFound() throws Exception {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        mockMvc
            .perform(get("/directors/edit/999"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/directors"))
            .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    public void testDeleteDirector() throws Exception {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        mockMvc
            .perform(get("/directors/delete/1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/directors"))
            .andExpect(flash().attributeExists("successMessage"));

        verify(repository, times(1)).existsById(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteDirectorNotFound() throws Exception {
        when(repository.existsById(999L)).thenReturn(false);

        mockMvc
            .perform(get("/directors/delete/999"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/directors"))
            .andExpect(flash().attributeExists("errorMessage"));

        verify(repository, never()).deleteById(anyLong());
    }
}
