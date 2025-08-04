package br.com.a3sitsolutions.tempo_box;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TempBoxApplication {

	public static void main(String[] args) {
		SpringApplication.run(TempBoxApplication.class, args);
	}

}
