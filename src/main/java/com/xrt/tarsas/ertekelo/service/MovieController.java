package com.xrt.tarsas.ertekelo.service;

import com.xrt.tarsas.ertekelo.DirectorRepository;
import com.xrt.tarsas.ertekelo.Movie;
import com.xrt.tarsas.ertekelo.MovieRepository;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/movies")
public final class MovieController {

    @Autowired
    private MovieRepository repository;

    @Autowired
    private DirectorRepository directorRepository;

    @GetMapping
    public String listMovies(Model model) {
        List<Movie> allMovies = repository.findAll();

        List<Movie> filteredMovies = new ArrayList<>();
        for (Movie movie : allMovies) {
            if (movie.getDirector() != null) {
                filteredMovies.add(movie);
            }
        }

        model.addAttribute("movies", filteredMovies);

        return "movies";
    }

    @GetMapping("/{id}")
    public String viewMovie(
        @PathVariable("id") Long id,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        Optional<Movie> movie = repository.findById(id);
        if (movie.isPresent()) {
            if (movie.get().getDirector() == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Movie has no director, please add one!");
                return "redirect:/movies/edit/" + id;
            }
            model.addAttribute("movie", movie.get());
            return "details";
        } else {
            return "redirect:/movies";
        }
    }

    @GetMapping("/new")
    public String newMovieForm(Model model) {
        model.addAttribute("movie", new Movie());
        model.addAttribute("directors", directorRepository.findAll());
        return "form";
    }

    @PostMapping("/save")
    public String saveMovie(
        @ModelAttribute Movie movie,
        @RequestParam("directorId") Long directorId,
        RedirectAttributes redirectAttributes
    ) {
        directorRepository.findById(directorId).ifPresent(movie::setDirector);

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
            model.addAttribute("directors", directorRepository.findAll());
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
