package com.xrt.tarsas.ertekelo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<MovieRepository> findByTitle(String title);
}
