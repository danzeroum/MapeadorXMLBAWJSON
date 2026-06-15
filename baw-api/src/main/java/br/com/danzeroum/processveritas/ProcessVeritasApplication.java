package br.com.danzeroum.processveritas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ProcessVeritasApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProcessVeritasApplication.class, args);
    }
}
