package com.xrt.tarsas.ertekelo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<MovieRepository> findByTitle(String title);
    List<MovieRepository> findByDirector(String director);
    List<MovieRepository> findByReleaseYear(String releaseYear);
}
