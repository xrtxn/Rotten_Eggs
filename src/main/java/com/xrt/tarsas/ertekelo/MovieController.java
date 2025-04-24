package com.xrt.tarsas.ertekelo;

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

@Controller
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    private MovieRepository repository;

    @GetMapping
    public String listMovies(Model model) {
        // Create a list of movies to display
        List<Movie> movies = repository.findAll();

        // Add the list to the model to be accessed in the view
        model.addAttribute("movies", movies);

        return "index";
    }

    @GetMapping("/{id}")
    public String viewMovie(@PathVariable Long id, Model model) {
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
    public String saveMovie(@ModelAttribute Movie movie) {
        repository.save(movie);
        return "redirect:/movies";
    }

    @GetMapping("/edit/{id}")
    public String editMovieForm(@PathVariable Long id, Model model) {
        Optional<Movie> movie = repository.findById(id);
        if (movie.isPresent()) {
            model.addAttribute("movie", movie.get());
            return "form";
        } else {
            return "redirect:/movies";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteMovie(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/movies";
    }
}
