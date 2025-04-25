package com.xrt.tarsas.ertekelo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public final class ErtekeloApplication {

    private ErtekeloApplication() {
        // Private constructor to hide the implicit public one
    }

    public static void main(String[] args) {
        SpringApplication.run(ErtekeloApplication.class, args);
    }
}
