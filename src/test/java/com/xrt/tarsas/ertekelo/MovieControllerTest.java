package com.xrt.tarsas.ertekelo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.xrt.tarsas.ertekelo.service.MovieController;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MovieController.class)
public class MovieControllerTest {

    private static final String MOVIE_1_NAME = "The Shawshank Redemption";
    private static final String MOVIE_2_NAME = "The Godfather";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieRepository repository;

    @Test
    public void testListMovies() throws Exception {
        Movie movie1 = new Movie();
        movie1.setId(1L);
        movie1.setTitle(MOVIE_1_NAME);

        Movie movie2 = new Movie();
        movie2.setId(2L);
        movie2.setTitle(MOVIE_2_NAME);

        when(repository.findAll()).thenReturn(Arrays.asList(movie1, movie2));

        mockMvc
            .perform(get("/movies"))
            .andExpect(status().isOk())
            .andExpect(view().name("movies"))
            .andExpect(model().attributeExists("movies"))
            .andExpect(
                model().attribute("movies", Arrays.asList(movie1, movie2))
            );
    }

    @Test
    public void testViewMovie_Found() throws Exception {
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle(MOVIE_1_NAME);

        when(repository.findById(1L)).thenReturn(Optional.of(movie));

        mockMvc
            .perform(get("/movies/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("details"))
            .andExpect(model().attributeExists("movie"))
            .andExpect(model().attribute("movie", movie));
    }

    @Test
    public void testViewMovie_NotFound() throws Exception {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc
            .perform(get("/movies/999"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/movies"));
    }

    @Test
    public void testNewMovieForm() throws Exception {
        mockMvc
            .perform(get("/movies/new"))
            .andExpect(status().isOk())
            .andExpect(view().name("form"))
            .andExpect(model().attributeExists("movie"));
    }

    @Test
    public void testSaveMovie() throws Exception {
        Movie movie = new Movie();
        movie.setTitle(MOVIE_1_NAME);

        when(repository.save(any(Movie.class))).thenReturn(movie);

        mockMvc
            .perform(post("/movies/save").param("title", MOVIE_1_NAME))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/movies"));

        verify(repository, times(1)).save(any(Movie.class));
    }

    @Test
    public void testEditMovieForm_Found() throws Exception {
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle(MOVIE_1_NAME);

        when(repository.findById(1L)).thenReturn(Optional.of(movie));

        mockMvc
            .perform(get("/movies/edit/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("form"))
            .andExpect(model().attributeExists("movie"))
            .andExpect(model().attribute("movie", movie));
    }

    @Test
    public void testEditMovieForm_NotFound() throws Exception {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc
            .perform(get("/movies/edit/999"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/movies"));
    }

    @Test
    public void testDeleteMovie() throws Exception {
        when(repository.existsById(1L)).thenReturn(true);

        mockMvc
            .perform(get("/movies/delete/1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/movies"));

        verify(repository, times(1)).existsById(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteMovie_NotFound() throws Exception {
        mockMvc
            .perform(get("/movies/delete/999"))
            .andExpect(flash().attributeExists("errorMessage"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/movies"));
    }
}
