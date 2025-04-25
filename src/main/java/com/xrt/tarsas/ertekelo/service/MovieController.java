package com.xrt.tarsas.ertekelo.service;

import com.xrt.tarsas.ertekelo.Movie;
import com.xrt.tarsas.ertekelo.MovieRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/movies")
public final class MovieController {

    @Autowired
    private MovieRepository repository;

    @GetMapping
    public String listMovies(Model model) {
        List<Movie> movies = repository.findAll();

        model.addAttribute("movies", movies);

        return "movies";
    }

    @GetMapping("/{id}")
    public String viewMovie(@PathVariable("id") Long id, Model model) {
        Optional<Movie> movie = repository.findById(id);
        if (movie.isPresent()) {
            model.addAttribute("movie", movie.get());
            return "details";
        } else {
            return "redirect:/movies";
        }
    }

    @GetMapping("/new")
    public String newMovieForm(Model model) {
        model.addAttribute("movie", new Movie());
        return "form";
    }

    @PostMapping("/save")
    public String saveMovie(
        @ModelAttribute Movie movie,
        RedirectAttributes redirectAttributes
    ) {
        repository.save(movie);
        redirectAttributes.addFlashAttribute(
            "successMessage",
            "Movie saved successfully!"
        );
        return "redirect:/movies";
    }

    @GetMapping("/edit/{id}")
    public String editMovieForm(
        @PathVariable("id") Long id,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        Optional<Movie> movie = repository.findById(id);
        if (movie.isPresent()) {
            model.addAttribute("movie", movie.get());
            return "form";
        } else {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Movie doesn't exist!"
            );
            return "redirect:/movies";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteMovie(
        @PathVariable("id") Long id,
        RedirectAttributes redirectAttributes
    ) {
        if (!repository.existsById(id)) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Movie doesn't exist!"
            );
            return "redirect:/movies";
        }
        repository.deleteById(id);
        return "redirect:/movies";
    }
}
