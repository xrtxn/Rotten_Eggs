package com.xrt.tarsas.ertekelo.service;

import com.xrt.tarsas.ertekelo.Director;
import com.xrt.tarsas.ertekelo.DirectorRepository;
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
@RequestMapping("/directors")
public final class DirectorController {

    @Autowired
    private DirectorRepository repository;

    @GetMapping
    public String listDirectors(Model model) {
        List<Director> directors = repository.findAll();
        model.addAttribute("directors", directors);
        return "directors";
    }

    @GetMapping("/{id}")
    public String viewDirector(@PathVariable("id") Long id, Model model) {
        Optional<Director> director = repository.findById(id);
        if (director.isPresent()) {
            model.addAttribute("director", director.get());
            return "director-details";
        } else {
            return "redirect:/directors";
        }
    }

    @GetMapping("/new")
    public String newDirectorForm(Model model) {
        model.addAttribute("director", new Director());
        return "director-form";
    }

    @PostMapping("/save")
    public String saveDirector(
        @ModelAttribute Director director,
        RedirectAttributes redirectAttributes
    ) {
        repository.save(director);
        redirectAttributes.addFlashAttribute(
            "successMessage",
            "Director saved successfully!"
        );
        return "redirect:/directors";
    }

    @GetMapping("/edit/{id}")
    public String editDirectorForm(
        @PathVariable("id") Long id,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        Optional<Director> director = repository.findById(id);
        if (director.isPresent()) {
            model.addAttribute("director", director.get());
            return "director-form";
        } else {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Director doesn't exist!"
            );
            return "redirect:/directors";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteDirector(
        @PathVariable("id") Long id,
        RedirectAttributes redirectAttributes
    ) {
        if (!repository.existsById(id)) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Director doesn't exist!"
            );
            return "redirect:/directors";
        }

        try {
            repository.deleteById(id);
            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Director deleted successfully!"
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Cannot delete director. This director is associated with one or more movies."
            );
        }
        return "redirect:/directors";
    }
}
