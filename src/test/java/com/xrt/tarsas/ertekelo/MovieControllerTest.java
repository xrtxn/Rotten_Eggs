package com.xrt.tarsas.ertekelo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.xrt.tarsas.ertekelo.service.MovieController;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MovieController.class)
public class MovieControllerTest {

    private static final String MOVIE_1_NAME = "The Shawshank Redemption";
    private static final String MOVIE_2_NAME = "The Godfather";
    private static final String DIRECTOR_1_FIRST_NAME = "Frank";
    private static final String DIRECTOR_1_LAST_NAME = "Darabont";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieRepository repository;

    @MockitoBean
    private DirectorRepository directorRepository;

    private Director testDirector;
    private Movie testMovie1;
    private Movie testMovie2;
    private List<Director> directorList;

    @BeforeEach
    void setUp() {
        testDirector = new Director(
            1L,
            DIRECTOR_1_FIRST_NAME,
            DIRECTOR_1_LAST_NAME,
            "1959"
        );

        testMovie1 = new Movie();
        testMovie1.setId(1L);
        testMovie1.setTitle(MOVIE_1_NAME);
        testMovie1.setDirector(testDirector);
        testMovie1.setReleaseYear("1994");

        testMovie2 = new Movie();
        testMovie2.setId(2L);
        testMovie2.setTitle(MOVIE_2_NAME);
        testMovie2.setDirector(testDirector);
        testMovie2.setReleaseYear("1972");

        directorList = Arrays.asList(testDirector);

        when(directorRepository.findById(1L)).thenReturn(
            Optional.of(testDirector)
        );
        when(directorRepository.findAll()).thenReturn(directorList);
    }

    @Test
    public void testListMovies() throws Exception {
        when(repository.findAll()).thenReturn(
            Arrays.asList(testMovie1, testMovie2)
        );

        mockMvc
            .perform(get("/movies"))
            .andExpect(status().isOk())
            .andExpect(view().name("movies"))
            .andExpect(model().attributeExists("movies"))
            .andExpect(
                model()
                    .attribute("movies", Arrays.asList(testMovie1, testMovie2))
            );
    }

    @Test
    public void testViewMovieFound() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(testMovie1));

        mockMvc
            .perform(get("/movies/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("details"))
            .andExpect(model().attributeExists("movie"))
            .andExpect(model().attribute("movie", testMovie1));
    }

    @Test
    public void testViewMovieWithNullDirector() throws Exception {
        Movie movieWithNullDirector = new Movie();
        movieWithNullDirector.setId(3L);
        movieWithNullDirector.setTitle("Movie with no director");

        when(repository.findById(3L)).thenReturn(
            Optional.of(movieWithNullDirector)
        );

        mockMvc
            .perform(get("/movies/3"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/movies/edit/3"))
            .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    public void testViewMovieNotFound() throws Exception {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        mockMvc
            .perform(get("/movies/999"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/movies"));
    }

    @Test
    public void testSaveMovie() throws Exception {
        when(repository.save(any(Movie.class))).thenAnswer(invocation ->
            invocation.getArgument(0)
        );

        mockMvc
            .perform(
                post("/movies/save")
                    .param("title", MOVIE_1_NAME)
                    .param("releaseYear", "1994")
                    .param("directorId", "1")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/movies"))
            .andExpect(flash().attributeExists("successMessage"));

        verify(repository, times(1)).save(any(Movie.class));
        verify(directorRepository, times(1)).findById(1L);
    }

    @Test
    public void testEditMovieFormFound() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(testMovie1));

        mockMvc
            .perform(get("/movies/edit/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("form"))
            .andExpect(model().attributeExists("movie"))
            .andExpect(model().attributeExists("directors"))
            .andExpect(model().attribute("movie", testMovie1));
    }

    @Test
    public void testEditMovieFormNotFound() throws Exception {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        mockMvc
            .perform(get("/movies/edit/999"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/movies"))
            .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    public void testDeleteMovie() throws Exception {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        mockMvc
            .perform(get("/movies/delete/1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/movies"));

        verify(repository, times(1)).existsById(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteMovieNotFound() throws Exception {
        when(repository.existsById(999L)).thenReturn(false);

        mockMvc
            .perform(get("/movies/delete/999"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/movies"))
            .andExpect(flash().attributeExists("errorMessage"));

        verify(repository, never()).deleteById(anyLong());
    }
}
